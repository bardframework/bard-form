package org.bardframework.form.flow;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bardframework.commons.web.utils.WebUtils;
import org.bardframework.form.Form;
import org.bardframework.form.FormTemplate;
import org.bardframework.form.FormUtils;
import org.bardframework.form.exception.InvalidateFlowException;
import org.bardframework.form.field.input.InputFieldTemplate;
import org.bardframework.form.flow.repository.FlowDataRepository;
import org.bardframework.form.processor.FormProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public abstract class FlowHandlerAbstract implements FlowHandler {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FlowHandlerAbstract.class);
    protected final FlowDataRepository<FlowData> flowDataRepository;
    protected boolean failOnUnknownSubmitFields = true;

    public FlowHandlerAbstract(FlowDataRepository<FlowData> flowDataRepository) {
        this.flowDataRepository = flowDataRepository;
    }

    @Override
    public FlowResponse<String> start(String flowToken, Locale locale, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
        FlowData flowData = new FlowData();
        flowData.setLocale(locale);
        /*
            اجرای پیش پردازش های فلو
         */
        this.process(this.getPreProcessors(), flowToken, flowData, Map.of(), httpRequest, httpResponse);
        return this.processNextForm(flowToken, flowData, Map.of(), flowToken, httpRequest, httpResponse);
    }

    @Override
    public FlowResponse<String> submit(String flowToken, Map<String, String> formData, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws Exception {
        FlowData flowData = this.getFlowDataRepository().get(flowToken);
        FormTemplate currentFormTemplate = this.getCurrentFormTemplate(flowData);
        this.fillFlowData(flowToken, flowData, formData, currentFormTemplate);
        try {
            this.process(currentFormTemplate.getPostProcessors(), flowToken, flowData, formData, httpRequest, httpResponse);
            return this.processNextForm(flowToken, flowData, formData, null, httpRequest, httpResponse);
        } catch (InvalidateFlowException ex) {
            this.onException(ex);
            throw ex;
        } catch (Exception ex) {
            /*
                در تمامی حالات (حالاتی که استثنا رخ دهد یا خیر) باید دیتای تغیر یافته را ذخیره کنیم؛
                برخی کنترل های امنیتی مانند غیر معتبر کردن کپچای استفاده شده، با پاک کردن از دیتا انجام می شود
                در مثال ذکر شده و در حالتی که یک مرحله پس از کنترل کپچا استثنایی رخ دهد باید دیتا را ذخیره کنیم تا ناسازگاری در فرایند ایجاد نشود.
                توضیحات فوق، بیانگر اهمیت و دلیل ذخیره ی دیتا در بلاک finally است
             */
            this.getFlowDataRepository().put(flowToken, flowData);
            throw ex;
        }
    }

    @Override
    public void action(String flowToken, String action, Map<String, String> formData, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws Exception {
        LOGGER.debug("start action[{}] processing, flow token [{}], form data [{}]", action, flowToken, formData);
        FlowData flowData = this.getFlowDataRepository().get(flowToken);
        FormTemplate currentFormTemplate = this.getCurrentFormTemplate(flowData);
        if (StringUtils.isBlank(action)) {
            LOGGER.warn("null action can't process, flow token [{}], form [{}]", flowToken, currentFormTemplate.getName());
            httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            if (!this.processAction(flowToken, action, flowData, formData, currentFormTemplate, httpRequest, httpResponse)) {
                /*
                    در صورتی که عملیات نامناسب درخواست شود (هیچ پردازشگری یافت نشود)، فلو را پاک می کنیم
                 */
                LOGGER.warn("no processor exist to handle action [{}], flow token [{}], form [{}]", action, flowToken, currentFormTemplate.getName());
                throw new InvalidateFlowException(flowToken, "invalid action");
            }
        } catch (InvalidateFlowException ex) {
            this.onException(ex);
            throw ex;
        } finally {
            this.getFlowDataRepository().put(flowToken, flowData);
        }
    }

    /**
     * این متد فقط فرم جاری را برمیگرداند و پیش پردازش های آن را اجرا نمی کند
     */
    @Override
    public FlowResponse<String> getCurrent(String flowToken, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws Exception {
        FlowData flowData = this.getFlowDataRepository().get(flowToken);
        FormTemplate currentFormTemplate = this.getCurrentFormTemplate(flowData);
        return this.toResponse(currentFormTemplate, flowData, null);
    }

    protected boolean processAction(String flowToken, String action, FlowData flowData, Map<String, String> formData, FormTemplate currentFormTemplate, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
        for (InputFieldTemplate<?, ?> inputFieldTemplate : currentFormTemplate.getInputFieldTemplates()) {
            /*
                تلاش برای پردازش با پردازشگرهای اینپوت ها
             */
            if (this.process(inputFieldTemplate.getActionProcessors().get(action), flowToken, flowData, formData, httpRequest, httpResponse)) {
                return true;
            }
        }
        /*
            تلاش برای پردازش با پردازشگرهای فرم
         */
        if (this.process(currentFormTemplate.getActionProcessors().get(action), flowToken, flowData, formData, httpRequest, httpResponse)) {
            return true;
        }
        /*
            تلاش برای پردازش با پردازشگرهای فلو
         */
        return this.process(this.getActionProcessors().get(action), flowToken, flowData, formData, httpRequest, httpResponse);
    }

    /**
     * اعتبارسنجی داده های ارسالی و افزودن به فلو دیتا
     */
    protected void fillFlowData(String flowToken, FlowData flowData, Map<String, String> formData, FormTemplate currentFormTemplate) throws Exception {
        Set<String> allowedFieldNames = currentFormTemplate.getAllowedInputFields(flowData.getData(), flowData.getLocale());
        Set<String> illegalFields = new HashSet<>(formData.keySet());
        illegalFields.removeAll(allowedFieldNames);
        if (!illegalFields.isEmpty()) {
            if (this.isFailOnUnknownSubmitFields()) {
                throw new InvalidateFlowException(flowToken, "illegal fields: " + illegalFields);
            } else {
                LOGGER.warn("illegal fields[{}] sent to flow[{}].", illegalFields, flowToken);
            }
        }
        currentFormTemplate.validate(formData, flowData.getData(), flowData.getLocale());
        for (InputFieldTemplate<?, ?> inputFieldTemplate : currentFormTemplate.getInputFieldTemplates()) {
            if (!inputFieldTemplate.isPersistentValue()) {
                continue;
            }
            String value = formData.remove(inputFieldTemplate.getName());
            if (StringUtils.isNotBlank(value)) {
                flowData.getData().put(inputFieldTemplate.getName(), WebUtils.escapeString(value));
            }
        }
    }

    /**
     * 1. محاسبه فرم بعدی
     * <br>
     * 2. اجرای پیش پردازش های فرم
     * <br>
     * 3. ذخیره  داده های فلو
     */
    protected FlowResponse<String> processNextForm(String flowToken, FlowData flowData, Map<String, String> formData, String responseData, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
        FormTemplate nextFormTemplate = this.getNextFormTemplate(flowData);
        if (null != nextFormTemplate) {
            this.process(nextFormTemplate.getPreProcessors(), flowToken, flowData, formData, httpRequest, httpResponse);
        }
        FlowResponse<String> response = this.toResponse(nextFormTemplate, flowData, responseData);
        if (Boolean.TRUE.equals(response.getFinished())) {
            this.onFinished(flowToken, flowData, formData, httpRequest, httpResponse);
        }
        this.getFlowDataRepository().put(flowToken, flowData);
        return response;
    }

    protected FormTemplate getNextFormTemplate(FlowData flowData) throws Exception {
        if (this.getForms().size() <= flowData.getNextStepIndex()) {
            return null;
        }
        FormTemplate formTemplate = this.getForms().get(flowData.getNextStepIndex());
        /*
            increase index after fetch form, because step index start from 1, and form list index start from 0
         */
        flowData.setNextStepIndex(flowData.getNextStepIndex() + 1);
        return formTemplate;
    }

    protected FormTemplate getCurrentFormTemplate(FlowData flowData) throws Exception {
        return this.getForms().get(flowData.getNextStepIndex() - 1);
    }

    protected void onFinished(String flowToken, FlowData flowData, Map<String, String> formData, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
        this.process(this.getPostProcessors(), flowToken, flowData, formData, httpRequest, httpResponse);
        this.cleanFlowData(flowToken);
    }

    protected void onException(InvalidateFlowException ex) {
        /*
            در برخی موارد مانند تلاش زیاد برای تولید کپچا و ... خطای InvalidateFlowException تولید می شود و باید داده های مربوط به فلو پاک شوند
         */
        this.cleanFlowData(ex.getFlowToken());
    }

    /**
     * تبدیل به فرم
     */
    protected FlowResponse<String> toResponse(FormTemplate formTemplate, FlowData flowData, String responseData) throws Exception {
        FlowResponse<String> response = new FlowResponse<>(responseData);
        if (null != formTemplate) {
            /*
                convert and fill form
             */
            Form nextForm = FormUtils.toForm(formTemplate, flowData.getLocale(), flowData.getData(), flowData.getData());
            response.setForm(nextForm);
            response.setSteps(this.getStepsCounts());
            response.setCurrent(flowData.getNextStepIndex());
        }
        if (null == formTemplate || formTemplate.isFinished()) {
            response.setFinished(Boolean.TRUE);
        }
        return response;
    }

    protected int getStepsCounts() {
        return this.getForms().size();
    }

    protected void cleanFlowData(String flowToken) {
        this.getFlowDataRepository().remove(flowToken);
    }

    protected boolean process(List<FormProcessor> processors, String flowToken, FlowData flowData, Map<String, String> formData, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
        if (CollectionUtils.isEmpty(processors)) {
            return false;
        }
        for (FormProcessor processor : processors) {
            processor.process(flowToken, flowData.getData(), formData, flowData.getLocale(), httpRequest, httpResponse);
        }
        return true;
    }

    protected boolean isStarted(String flowToken) throws InvalidateFlowException {
        FlowData flowData = this.getFlowDataRepository().get(flowToken);
        return null != flowData && flowData.getNextStepIndex() > 0;
    }

    public FlowDataRepository<FlowData> getFlowDataRepository() {
        return flowDataRepository;
    }

    public boolean isFailOnUnknownSubmitFields() {
        return failOnUnknownSubmitFields;
    }

    public void setFailOnUnknownSubmitFields(boolean failOnUnknownSubmitFields) {
        this.failOnUnknownSubmitFields = failOnUnknownSubmitFields;
    }

    protected abstract List<FormTemplate> getForms();

    protected abstract List<FormProcessor> getPreProcessors();

    protected abstract List<FormProcessor> getPostProcessors();

    protected abstract Map<String, List<FormProcessor>> getActionProcessors();
}

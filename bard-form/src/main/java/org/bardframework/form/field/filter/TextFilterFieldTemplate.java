package org.bardframework.form.field.filter;

import org.apache.commons.lang3.StringUtils;
import org.bardframework.form.FormTemplate;
import org.bardframework.form.FormUtils;
import org.bardframework.form.field.input.InputFieldTemplate;
import org.bardframework.form.model.filter.StringFilter;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class TextFilterFieldTemplate extends InputFieldTemplate<TextFilterField, StringFilter> {

    protected TextFilterFieldTemplate(String name) {
        super(name);
    }

    @Override
    public boolean isValid(String flowToken, TextFilterField field, StringFilter filter, Map<String, String> flowData) {
        if (null == filter || StringUtils.isBlank(filter.getContains())) {
            if (Boolean.TRUE.equals(field.getRequired())) {
                log.debug("StringFilter [{}] is required, but it's value is empty", field.getName());
                return false;
            }
            return true;
        }
        if (null != field.getMinLength() && filter.getContains().length() < field.getMinLength()) {
            log.debug("StringFilter [{}] min length is [{}], but it's value[{}] length is smaller than minimum", field.getName(), field.getMinLength(), filter);
            return false;
        }
        if (null != field.getMaxLength() && filter.getContains().length() > field.getMaxLength()) {
            log.debug("StringFilter [{}] max length is [{}], but it's value[{}] length is greater than maximum", field.getName(), field.getMaxLength(), filter);
            return false;
        }
        if (StringUtils.isNotBlank(field.getRegex()) && !Pattern.matches(field.getRegex(), filter.getContains())) {
            log.debug("StringFilter [{}] regex is [{}], but it's value[{}] not match with it", field.getName(), field.getRegex(), filter);
            return false;
        }
        return true;
    }

    @Override
    public void fill(FormTemplate formTemplate, TextFilterField field, Map<String, String> values, Locale locale) throws Exception {
        super.fill(formTemplate, field, values, locale);
        field.setRegex(FormUtils.getFieldStringProperty(formTemplate, this, "regex", locale, values, this.getDefaultValues().getRegex()));
        field.setMask(FormUtils.getFieldStringProperty(formTemplate, this, "mask", locale, values, this.getDefaultValues().getMask()));
        field.setMinLength(FormUtils.getFieldIntegerProperty(formTemplate, this, "minLength", locale, values, this.getDefaultValues().getMinLength()));
        field.setMaxLength(FormUtils.getFieldIntegerProperty(formTemplate, this, "maxLength", locale, values, this.getDefaultValues().getMaxLength()));
    }

    @Override
    public StringFilter toValue(String value) {
        return StringUtils.isBlank(value) ? null : new StringFilter().setContains(value);
    }
}

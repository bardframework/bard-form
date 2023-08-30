package org.bardframework.form.field.input;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bardframework.form.FormTemplate;
import org.bardframework.form.FormUtils;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

@Getter
public class DateFieldTemplate extends InputFieldTemplate<DateField, LocalDate> {
    private boolean minIsNow;
    private boolean maxIsNow;

    protected DateFieldTemplate(String name) {
        super(name);
    }

    @Override
    public boolean isValid(String flowToken, DateField field, LocalDate value, Map<String, String> flowData) {
        if (null == value) {
            if (Boolean.TRUE.equals(field.getRequired())) {
                log.debug("field [{}] is required, but it's value is empty", field.getName());
                return false;
            }
            return true;
        }
        if (null != field.getMinValue() && value.isBefore(field.getMinValue())) {
            log.debug("field [{}] min value is [{}], but it's value is less than minimum", field.getName(), field.getMinValue());
            return false;
        }
        if (null != field.getMaxValue() && value.isAfter(field.getMaxValue())) {
            log.debug("field [{}] max value is [{}], but it's value is greater than maximum", field.getName(), field.getMaxValue());
            return false;
        }
        return true;
    }

    @Override
    public void fill(FormTemplate formTemplate, DateField field, Map<String, String> values, Locale locale) throws Exception {
        super.fill(formTemplate, field, values, locale);
        field.setMinValue(FormUtils.getFieldLocalDateProperty(formTemplate, this, "minValue", locale, values, this.getDefaultValues().getMinValue()));
        field.setMaxValue(FormUtils.getFieldLocalDateProperty(formTemplate, this, "maxValue", locale, values, this.getDefaultValues().getMaxValue()));
        if (null == field.getMinValue()) {
            field.setMinValue(this.getMinValue());
        }
        if (null == field.getMaxValue()) {
            field.setMaxValue(this.getMaxValue());
        }
    }

    @Override
    public LocalDate toValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return LocalDate.parse(value);
    }

    public LocalDate getMinValue() {
        return minIsNow ? LocalDate.now() : null;
    }

    public LocalDate getMaxValue() {
        return maxIsNow ? LocalDate.now() : null;
    }
}
package org.bardframework.form.field.filter;

import org.apache.commons.lang3.StringUtils;
import org.bardframework.form.FormTemplate;
import org.bardframework.form.FormUtils;
import org.bardframework.form.field.input.InputField;
import org.bardframework.form.field.input.InputFieldTemplate;
import org.bardframework.form.model.filter.LongFilter;

import java.util.Locale;
import java.util.Map;

public class NumberFilterFieldTemplate extends InputFieldTemplate<NumberFilterField, LongFilter> {

    protected NumberFilterFieldTemplate(String name) {
        super(name);
    }

    @Override
    public boolean isValid(String flowToken, NumberFilterField field, LongFilter filter, Map<String, String> flowData) {
        if (null == filter || (null == filter.getFrom() && null == filter.getTo())) {
            if (Boolean.TRUE.equals(field.getRequired())) {
                log.debug("filterField [{}] is required, but it's value is empty", field.getName());
                return false;
            }
            return true;
        }
        if (null != field.getMinValue()) {
            if (null != filter.getFrom() && filter.getFrom() < field.getMinValue()) {
                log.debug("field [{}] min value is [{}], but it's value is less than minimum", field.getName(), field.getMinValue());
                return false;
            }
            if (null != filter.getTo() && filter.getTo() < field.getMinValue()) {
                log.debug("field [{}] min value is [{}], but it's value is less than minimum", field.getName(), field.getMinValue());
                return false;
            }
        }
        if (null != field.getMaxValue()) {
            if (null != filter.getFrom() && filter.getFrom() > field.getMaxValue()) {
                log.debug("field [{}] max value is [{}], but it's value is greater than maximum", field.getName(), field.getMaxValue());
                return false;
            }
            if (null != filter.getTo() && filter.getTo() > field.getMaxValue()) {
                log.debug("field [{}] max value is [{}], but it's value is greater than maximum", field.getName(), field.getMaxValue());
                return false;
            }
        }
        long length = (null == filter.getFrom() || null == filter.getTo()) ? Long.MAX_VALUE : filter.getTo() - filter.getFrom();
        if (length < 0) {
            log.debug("values[{}] of range field[{}] is invalid, from is greater than to", filter, field.getName());
            /*
                from > to
             */
            return false;
        }
        if (null != field.getMinLength() && length < field.getMinLength()) {
            log.debug("field [{}] min length is [{}], but it's value length is smaller than minimum", field.getName(), field.getMaxLength());
            return false;
        }
        if (null != field.getMaxLength() && length > field.getMaxLength()) {
            log.debug("field [{}] max length is [{}], but it's value length is greater than maximum", field.getName(), field.getMaxLength());
            return false;
        }
        return true;
    }

    @Override
    public void fill(FormTemplate formTemplate, NumberFilterField field, Map<String, String> values, Locale locale) throws Exception {
        super.fill(formTemplate, field, values, locale);
        field.setMinLength(FormUtils.getFieldLongProperty(formTemplate, this, "minLength", locale, values, this.getDefaultValues().getMinLength()));
        field.setMaxLength(FormUtils.getFieldLongProperty(formTemplate, this, "maxLength", locale, values, this.getDefaultValues().getMaxLength()));
        field.setMinValue(FormUtils.getFieldLongProperty(formTemplate, this, "minValue", locale, values, this.getDefaultValues().getMinValue()));
        field.setMaxValue(FormUtils.getFieldLongProperty(formTemplate, this, "maxValue", locale, values, this.getDefaultValues().getMaxValue()));
    }


    @Override
    public LongFilter toValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String[] parts = value.split(InputField.SEPARATOR);
        if (parts.length != 2) {
            throw new IllegalStateException(value + " is not valid for range value");
        }
        LongFilter filter = new LongFilter();
        if (!parts[0].isBlank()) {
            filter.setFrom(Long.valueOf(parts[0]));
        }
        if (!parts[1].isBlank()) {
            filter.setTo(Long.valueOf(parts[1]));
        }
        return filter;
    }
}
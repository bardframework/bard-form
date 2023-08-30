package org.bardframework.form.field.filter;

import org.apache.commons.lang3.StringUtils;
import org.bardframework.form.FormTemplate;
import org.bardframework.form.FormUtils;
import org.bardframework.form.field.input.InputField;
import org.bardframework.form.field.input.InputFieldTemplate;
import org.bardframework.form.model.filter.LocalDateTimeFilter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;

public class DateTimeFilterFieldTemplate extends InputFieldTemplate<DateTimeFilterField, LocalDateTimeFilter> {
    private boolean minIsNow;
    private boolean maxIsNow;
    private ChronoUnit lengthUnit = ChronoUnit.SECONDS;

    protected DateTimeFilterFieldTemplate(String name) {
        super(name);
    }

    @Override
    public void fill(FormTemplate formTemplate, DateTimeFilterField field, Map<String, String> values, Locale locale) throws Exception {
        super.fill(formTemplate, field, values, locale);
        field.setMinLength(FormUtils.getFieldLongProperty(formTemplate, this, "minLength", locale, values, this.getDefaultValues().getMinLength()));
        field.setMaxLength(FormUtils.getFieldLongProperty(formTemplate, this, "maxLength", locale, values, this.getDefaultValues().getMaxLength()));
        field.setMinValue(FormUtils.getFieldLocalDateTimeProperty(formTemplate, this, "minValue", locale, values, this.getDefaultValues().getMinValue()));
        field.setMaxValue(FormUtils.getFieldLocalDateTimeProperty(formTemplate, this, "maxValue", locale, values, this.getDefaultValues().getMaxValue()));
        field.setLengthUnit(field.getLengthUnit());
        if (null == field.getMinValue()) {
            field.setMinValue(this.getMinValue());
        }
        if (null == field.getMaxValue()) {
            field.setMaxValue(this.getMaxValue());
        }
    }

    @Override
    public LocalDateTimeFilter toValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String[] parts = value.split(InputField.SEPARATOR);
        if (parts.length != 2) {
            throw new IllegalStateException(value + " is not valid for range value");
        }
        LocalDateTimeFilter filter = new LocalDateTimeFilter();
        if (!parts[0].isBlank()) {
            filter.setFrom(LocalDateTime.parse(parts[0]));
        }
        if (!parts[1].isBlank()) {
            filter.setTo(LocalDateTime.parse(parts[1]));
        }
        return filter;
    }

    @Override
    public boolean isValid(String flowToken, DateTimeFilterField field, LocalDateTimeFilter filter, Map<String, String> flowData) {
        if (null == filter || (null == filter.getFrom() && null == filter.getTo())) {
            if (Boolean.TRUE.equals(field.getRequired())) {
                log.debug("filterField [{}] is required, but it's value is empty", field.getName());
                return false;
            }
            return true;
        }
        if (null != field.getMinValue()) {
            if (null != filter.getFrom() && filter.getFrom().isBefore(this.getMinValue())) {
                log.debug("field [{}] min value is [{}], but it's value is less than minimum", field.getName(), field.getMinValue());
                return false;
            }
            if (null != filter.getTo() && filter.getTo().isBefore(this.getMinValue())) {
                log.debug("field [{}] min value is [{}], but it's value is less than minimum", field.getName(), field.getMinValue());
                return false;
            }
        }
        if (null != field.getMaxValue()) {
            if (null != filter.getFrom() && filter.getFrom().isAfter(this.getMaxValue())) {
                log.debug("field [{}] max value is [{}], but it's value is greater than maximum", field.getName(), field.getMaxValue());
                return false;
            }
            if (null != filter.getTo() && filter.getTo().isAfter(this.getMaxValue())) {
                log.debug("field [{}] max value is [{}], but it's value is greater than maximum", field.getName(), field.getMaxValue());
                return false;
            }
        }
        long length = null == filter.getFrom() || null == filter.getTo() ? Long.MAX_VALUE : Duration.between(filter.getFrom(), filter.getTo()).get(this.getLengthUnit()) + 1;
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

    public LocalDateTime getMinValue() {
        return minIsNow ? LocalDateTime.now() : null;
    }

    public LocalDateTime getMaxValue() {
        return maxIsNow ? LocalDateTime.now() : null;
    }

    public boolean isMinIsNow() {
        return minIsNow;
    }

    public void setMinIsNow(boolean minIsNow) {
        this.minIsNow = minIsNow;
    }

    public boolean isMaxIsNow() {
        return maxIsNow;
    }

    public void setMaxIsNow(boolean maxIsNow) {
        this.maxIsNow = maxIsNow;
    }

    public ChronoUnit getLengthUnit() {
        return lengthUnit;
    }

    public void setLengthUnit(ChronoUnit lengthUnit) {
        this.lengthUnit = lengthUnit;
    }
}
package org.bardframework.form.field;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bardframework.form.FormTemplate;
import org.bardframework.form.FormUtils;
import org.bardframework.form.common.FormField;
import org.bardframework.form.field.base.FormFieldTemplate;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListFieldTemplate extends FormFieldTemplate<ListField, List<String>> {

    protected ListFieldTemplate(String name) {
        super(name);
    }

    @Override
    public boolean isValid(ListField field, List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            if (Boolean.TRUE.equals(field.getRequired())) {
                LOGGER.debug("field [{}] is required, but it's value is empty", field.getName());
                return false;
            }
            return true;
        }
        if (values.size() > field.getMaxCount()) {
            LOGGER.debug("data count[{}] of field[{}] is greater than maximum[{}]", values.size(), field.getName(), field.getMaxCount());
            return false;
        }
        if (null != field.getMinLength() && values.stream().anyMatch(value -> value.length() < field.getMinLength())) {
            LOGGER.debug("field [{}] min length is [{}], but one of it's values length is smaller than minimum", field.getName(), field.getMinLength());
            return false;
        }
        if (null != field.getMaxLength() && values.stream().anyMatch(value -> value.length() > field.getMaxLength())) {
            LOGGER.debug("field [{}] max length is [{}], but one of it's values length is greater than maximum", field.getName(), field.getMaxLength());
            return false;
        }
        return true;
    }

    @Override
    public void fill(FormTemplate formTemplate, ListField field, Map<String, String> args, Locale locale) throws Exception {
        super.fill(formTemplate, field, args, locale);
        field.setMinLength(FormUtils.getFieldIntegerProperty(formTemplate, this.getName(), "minLength", locale, args, null));
        field.setMaxLength(FormUtils.getFieldIntegerProperty(formTemplate, this.getName(), "maxLength", locale, args, null));
        field.setMaxCount(FormUtils.getFieldIntegerProperty(formTemplate, this.getName(), "maxCount", locale, args, null));
    }

    @Override
    public List<String> toValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return List.of(value.split(FormField.SEPARATOR));
    }
}
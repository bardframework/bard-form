package org.bardframework.form.field.input;

import org.apache.commons.lang3.StringUtils;

public class SingleCheckboxFieldTemplate extends InputFieldTemplate<SingleCheckBoxField, Boolean> {

    protected SingleCheckboxFieldTemplate(String name) {
        super(name);
    }

    @Override
    public boolean isValid(SingleCheckBoxField field, Boolean value) {
        return true;
    }

    @Override
    public Boolean toValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }

}
package org.bardframework.form.field.input;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class LocationSelectFieldTemplate extends InputFieldTemplate<LocationSelectField, String> {

    public LocationSelectFieldTemplate(String name) {
        super(name);
    }

    @Override
    public boolean isValid(String flowToken, LocationSelectField field, String value, Map<String, String> flowData) {
        if (StringUtils.isBlank(value)) {
            if (Boolean.TRUE.equals(field.getRequired())) {
                log.debug("field [{}] is required, but it's value is empty", field.getName());
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public String toValue(String value) {
        return value;
    }
}

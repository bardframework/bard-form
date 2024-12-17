package org.bardframework.form.field.input;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.LoggerFactory;

public interface InputFieldDataProvider<F extends InputField<T>, T> {

    T getValidValue(F field) throws Exception;

    T getInvalidValue(F field) throws Exception;

    void set(ObjectNode objectNode, String property, T value);

    boolean supports(InputField<?> field);

    default void setValidValue(ObjectNode objectNode, F field) throws Exception {
        this.set(objectNode, field.getName(), this.getValidValue(field));
    }

    default void setInvalidValue(ObjectNode objectNode, F field) throws Exception {
        T invalidValue = this.getInvalidValue(field);
        LoggerFactory.getLogger(this.getClass()).info("setting invalid value [{}] for field [{}]", invalidValue, field.getName());
        this.set(objectNode, field.getName(), invalidValue);
    }

}
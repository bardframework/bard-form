package org.bardframework.form.field.view;

import org.bardframework.form.field.Field;
import org.bardframework.form.field.FieldType;

public class PaymentField extends Field {

    private Long amount;
    private String description;

    public PaymentField() {
    }

    @Override
    public FieldType getType() {
        return ViewFieldType.PAYMENT;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
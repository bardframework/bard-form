package org.bardframework.form.field.filter;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bardframework.form.FormTemplate;
import org.bardframework.form.field.input.InputFieldTemplate;
import org.bardframework.form.field.option.OptionDataSource;
import org.bardframework.form.model.filter.IdFilter;

import java.util.Locale;
import java.util.Map;

@Getter
public class SingleSelectFilterFieldTemplate extends InputFieldTemplate<SingleSelectFilterField, IdFilter<String>> {

    protected final OptionDataSource optionDataSource;

    protected SingleSelectFilterFieldTemplate(String name, OptionDataSource optionDataSource) {
        super(name);
        this.optionDataSource = optionDataSource;
    }

    @Override
    public void fill(FormTemplate formTemplate, SingleSelectFilterField field, Map<String, String> values, Locale locale) throws Exception {
        super.fill(formTemplate, field, values, locale);
        field.setOptions(optionDataSource.getOptions(locale));
    }


    @Override
    public boolean isValid(String flowToken, SingleSelectFilterField field, IdFilter<String> filter, Map<String, String> flowData) {
        if (null == filter || StringUtils.isBlank(filter.getEquals())) {
            if (Boolean.TRUE.equals(field.getRequired())) {
                log.debug("filterField [{}] is required, but it's value is empty", field.getName());
                return false;
            }
            return true;
        }
        if (field.getOptions().stream().filter(option -> !Boolean.TRUE.equals(option.getDisable())).noneMatch(option -> option.getId().equals(filter.getEquals().trim()))) {
            log.debug("field [{}] is select type, but it's value[{}] dose not equal with select options", field.getName(), filter);
            return false;
        }
        return true;
    }

    @Override
    public IdFilter<String> toValue(String id) {
        return StringUtils.isBlank(id) ? null : new IdFilter<String>().setEquals(id);
    }
}
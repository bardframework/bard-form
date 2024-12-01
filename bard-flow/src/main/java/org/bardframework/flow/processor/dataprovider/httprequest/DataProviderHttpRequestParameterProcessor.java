package org.bardframework.flow.processor.dataprovider.httprequest;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * map http request parameter name to fieldName
 * join values with ',' separator if more than one value exist for a parameter.
 */
public class DataProviderHttpRequestParameterProcessor extends DataProviderHttpRequestProcessorAbstract {

    public DataProviderHttpRequestParameterProcessor(Map<String, Object> mapper) {
        super(mapper);
    }

    @Override
    protected List<String> getValues(String name, HttpServletRequest httpRequest) {
        String[] values = httpRequest.getParameterValues(name);
        if (ArrayUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        return List.of(values);
    }
}

package org.bardframework.flow.processor.message.creator;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public interface MessageProvider {

    String create(Map<String, Object> args, Locale locale) throws IOException;

}

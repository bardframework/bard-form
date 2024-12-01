package org.bardframework.table;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bardframework.form.FormUtils;
import org.bardframework.table.header.HeaderTemplate;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class TableUtils {

    public static TableModel toTable(TableTemplate tableTemplate, Map<String, Object> args, Locale locale, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
        if (null == tableTemplate) {
            return null;
        }
        return TableUtils.toTable(new TableModel(), tableTemplate, args, locale, httpRequest, httpResponse);
    }

    public static <T extends TableModel> T toTable(T table, TableTemplate tableTemplate, Map<String, Object> args, Locale locale, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
        table.setName(tableTemplate.getName());
        table.setTitle(TableUtils.getTableStringValue(tableTemplate, "title", locale, args, tableTemplate.getTitle()));
        table.setDescription(TableUtils.getTableStringValue(tableTemplate, "description", locale, args, tableTemplate.getDescription()));
        table.setDelete(TableUtils.getTableBooleanValue(tableTemplate, "delete", locale, args, tableTemplate.getDelete()));
        table.setPrint(TableUtils.getTableBooleanValue(tableTemplate, "print", locale, args, tableTemplate.getPrint()));
        table.setExport(TableUtils.getTableBooleanValue(tableTemplate, "export", locale, args, tableTemplate.getExport()));
        table.setPreload(TableUtils.getTableBooleanValue(tableTemplate, "preload", locale, args, tableTemplate.getPreload()));
        table.setPageable(TableUtils.getTableBooleanValue(tableTemplate, "pageable", locale, args, tableTemplate.getPageable()));
        table.setCollapseFilterForm(TableUtils.getTableBooleanValue(tableTemplate, "collapseFilterForm", locale, args, tableTemplate.getCollapseFilterForm()));
        table.setHideColumn(TableUtils.getTableBooleanValue(tableTemplate, "hideColumn", locale, args, tableTemplate.getHideColumn()));
        table.setFetchSize(TableUtils.getTableIntegerValue(tableTemplate, "fetchSize", locale, args, tableTemplate.getFetchSize()));
        table.setFilterForm(FormUtils.toForm(tableTemplate.getFilterFormTemplate(), Map.of(), args, locale, httpRequest, httpResponse));
        table.setSaveForm(FormUtils.toForm(tableTemplate.getSaveFormTemplate(), Map.of(), args, locale, httpRequest, httpResponse));
        table.setUpdateForm(FormUtils.toForm(tableTemplate.getUpdateFormTemplate(), Map.of(), args, locale, httpRequest, httpResponse));

        for (HeaderTemplate<?, ?, ?> headerTemplate : tableTemplate.getHeaderTemplates()) {
            table.addHeader(headerTemplate.toHeader(tableTemplate, args, locale));
        }
        return table;
    }

    /**
     * @return false if we can't find
     */
    public static Boolean getTableBooleanValue(TableTemplate tableTemplate, String property, Locale locale, Map<String, Object> args, Boolean defaultValue) {
        String value = TableUtils.getTableStringValue(tableTemplate, property, locale, args, null);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            log.error("error reading [{}] of [{}] as boolean", property, tableTemplate.getName(), e);
            return null;
        }
    }

    /**
     * @return null if we can't read property value
     */
    public static Integer getTableIntegerValue(TableTemplate tableTemplate, String property, Locale locale, Map<String, Object> args, Integer defaultValue) {
        String value = TableUtils.getTableStringValue(tableTemplate, property, locale, args, null);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            log.error("error reading [{}] of [{}] as Integer", property, tableTemplate, e);
            return null;
        }
    }

    public static String getTableStringValue(TableTemplate tableTemplate, String property, Locale locale, Map<String, Object> args, String defaultValue) {
        return TableUtils.getTableValue(tableTemplate.getName(), property, locale, args, defaultValue, tableTemplate.getMessageSource());
    }

    public static String getTableValue(String tableName, String property, Locale locale, Map<String, Object> args, String defaultValue, MessageSource messageSource) {
        return FormUtils.getString("table", property, List.of(tableName), locale, args, defaultValue, messageSource);
    }

    /**
     * @return false if we can't read property value
     */
    public static Boolean getHeaderBooleanValue(TableTemplate TableTemplate, HeaderTemplate<?, ?, ?> headerTemplate, String property, Locale locale, Map<String, Object> args, Boolean defaultValue) {
        String value = TableUtils.getHeaderStringValue(TableTemplate, headerTemplate, property, locale, args, null);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            log.error("error reading [{}] of [{}.{}] as Boolean", property, TableTemplate, headerTemplate.getName(), e);
            return null;
        }
    }

    public static List<String> getHeaderListValue(TableTemplate tableTemplate, HeaderTemplate<?, ?, ?> headerTemplate, String property, Locale locale, Map<String, Object> args, List<String> defaultValue) {
        String value = TableUtils.getHeaderStringValue(tableTemplate, headerTemplate, property, locale, args, null);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toList());
    }

    /**
     * @return null if we can't read property value
     */
    public static Double getHeaderDoubleValue(TableTemplate tableTemplate, HeaderTemplate<?, ?, ?> headerTemplate, String property, Locale locale, Map<String, Object> args, Double defaultValue) {
        String value = TableUtils.getHeaderStringValue(tableTemplate, headerTemplate, property, locale, args, null);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            log.error("error reading [{}] of [{}.{}] as Double", property, tableTemplate, headerTemplate.getName(), e);
            return null;
        }
    }

    /**
     * @return null if we can't read property value
     */
    public static Long getHeaderLongValue(TableTemplate tableTemplate, HeaderTemplate<?, ?, ?> headerTemplate, String property, Locale locale, Map<String, Object> args, Long defaultValue) {
        String value = TableUtils.getHeaderStringValue(tableTemplate, headerTemplate, property, locale, args, null);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            log.error("error reading [{}] of [{}.{}] as Long", property, tableTemplate, headerTemplate.getName(), e);
            return null;
        }
    }

    /**
     * @return null if we can't read property value
     */
    public static LocalDate getHeaderLocalDateValue(TableTemplate tableTemplate, HeaderTemplate<?, ?, ?> headerTemplate, String property, Locale locale, Map<String, Object> args, LocalDate defaultValue) {
        String value = TableUtils.getHeaderStringValue(tableTemplate, headerTemplate, property, locale, args, null);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            log.error("error reading [{}] of [{}.{}] as LocalDate", property, tableTemplate, headerTemplate.getName(), e);
            return null;
        }
    }

    public static LocalDateTime getHeaderLocalDateTimeValue(TableTemplate tableTemplate, HeaderTemplate<?, ?, ?> headerTemplate, String property, Locale locale, Map<String, Object> args, LocalDateTime defaultValue) {
        String value = TableUtils.getHeaderStringValue(tableTemplate, headerTemplate, property, locale, args, null);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (Exception e) {
            log.error("error reading [{}] of [{}.{}] as LocalDateTime", property, tableTemplate, headerTemplate.getName(), e);
            return null;
        }
    }

    public static LocalTime getHeaderLocalTimeValue(TableTemplate tableTemplate, HeaderTemplate<?, ?, ?> headerTemplate, String property, Locale locale, Map<String, Object> args, LocalTime defaultValue) {
        String value = TableUtils.getHeaderStringValue(tableTemplate, headerTemplate, property, locale, args, null);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return LocalTime.parse(value);
        } catch (Exception e) {
            log.error("error reading [{}] of [{}.{}] as LocalTime", property, tableTemplate, headerTemplate.getName(), e);
            return null;
        }
    }

    /**
     * @return null if we can't read property value
     */
    public static String getHeaderStringValue(TableTemplate tableTemplate, HeaderTemplate<?, ?, ?> headerTemplate, String property, Locale locale, Map<String, Object> args, String defaultValue) {
        return TableUtils.getHeaderStringValue(tableTemplate.getName(), headerTemplate, property, locale, args, defaultValue, tableTemplate.getMessageSource());
    }

    /**
     * @return null if we can't read property value
     */
    public static String getHeaderStringValue(String tableName, HeaderTemplate<?, ?, ?> headerTemplate, String property, Locale locale, Map<String, Object> args, String defaultValue, MessageSource messageSource) {
        return FormUtils.getString("header", property, List.of(tableName, headerTemplate.getName()), locale, args, defaultValue, messageSource);
    }
}

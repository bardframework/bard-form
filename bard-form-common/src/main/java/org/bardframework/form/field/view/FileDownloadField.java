package org.bardframework.form.field.view;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bardframework.form.field.Field;
import org.bardframework.form.field.FieldType;

@Getter
@Setter
@ToString
public class FileDownloadField extends Field {

    private String src;
    private String fileName;
    private String contentType;
    private Integer size;

    @Override
    public FieldType getType() {
        return ViewFieldType.FILE_DOWNLOAD;
    }
}
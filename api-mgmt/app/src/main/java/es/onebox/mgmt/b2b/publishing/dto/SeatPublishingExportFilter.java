package es.onebox.mgmt.b2b.publishing.dto;

import es.onebox.core.file.exporter.generator.export.Translation;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingsFilter;
import es.onebox.mgmt.export.enums.CharsetEncoding;
import es.onebox.mgmt.export.enums.CsvFractionDigitsFormat;
import es.onebox.mgmt.export.enums.CsvSeparatorFormat;
import es.onebox.mgmt.export.enums.FileFormat;

import java.util.List;
import java.util.Set;

public class SeatPublishingExportFilter extends SeatPublishingsFilter {

    private List<SeatPublishingExportFileField> fields;
    private FileFormat format;
    private String email;
    private Long userId;
    private String timeZone;
    private CharsetEncoding charset;
    private CsvSeparatorFormat csvSeparatorFormat;
    private CsvFractionDigitsFormat csvfractionDigitsSeparatorFormat;
    private String language;
    private Set<Translation> translations;


    public List<SeatPublishingExportFileField> getFields() {
        return fields;
    }


    public void setFields(List<SeatPublishingExportFileField> fields) {
        this.fields = fields;
    }

    public FileFormat getFormat() {
        return format;
    }

    public void setFormat(FileFormat format) {
        this.format = format;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public CharsetEncoding getCharset() {
        return charset;
    }

    public void setCharset(CharsetEncoding charset) {
        this.charset = charset;
    }

    public CsvSeparatorFormat getCsvSeparatorFormat() {
        return csvSeparatorFormat;
    }

    public void setCsvSeparatorFormat(CsvSeparatorFormat csvSeparatorFormat) {
        this.csvSeparatorFormat = csvSeparatorFormat;
    }

    public CsvFractionDigitsFormat getCsvfractionDigitsSeparatorFormat() {
        return csvfractionDigitsSeparatorFormat;
    }

    public void setCsvfractionDigitsSeparatorFormat(CsvFractionDigitsFormat csvfractionDigitsSeparatorFormat) {
        this.csvfractionDigitsSeparatorFormat = csvfractionDigitsSeparatorFormat;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Set<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(Set<Translation> translations) {
        this.translations = translations;
    }
}

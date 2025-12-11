package es.onebox.mgmt.seasontickets.dto.renewals;

import es.onebox.core.file.exporter.generator.export.Translation;
import es.onebox.mgmt.export.enums.CharsetEncoding;
import es.onebox.mgmt.export.enums.CsvFractionDigitsFormat;
import es.onebox.mgmt.export.enums.CsvSeparatorFormat;
import es.onebox.mgmt.export.enums.FileFormat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.Set;

public class SeasonTicketRenewalsExportFilter {

    private List<SeasonTicketRenewalsExportFileField> fields;
    private FileFormat format;
    private String email;
    private Long userId;
    private String timeZone;
    private CharsetEncoding charset;
    private CsvSeparatorFormat csvSeparatorFormat;
    private CsvFractionDigitsFormat csvfractionDigitsSeparatorFormat;
    private String language;
    private Set<Translation> translations;


    public List<SeasonTicketRenewalsExportFileField> getFields() {
        return fields;
    }

    public void setFields(List<SeasonTicketRenewalsExportFileField> fields) {
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}

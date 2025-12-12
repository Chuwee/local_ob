package es.onebox.internal.automaticsales.export.dto;

import es.onebox.internal.automaticsales.export.enums.FileFormat;
import es.onebox.core.file.exporter.generator.export.Translation;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class ExportFilter<T extends ExportFileField> implements Serializable {

    @Serial
    private static final long serialVersionUID = 3746445083778410163L;
    private List<T> fields;
    private FileFormat format;
    private String email;
    private Long userId;
    private String language;
    private String timeZone;
    private String q;
    private Set<Translation> translations;

    public ExportFilter() {
    }

    public ExportFilter(List<T> fields, FileFormat format, String email, Long userId, String language, String timeZone, String q) {
        this.fields = fields;
        this.format = format;
        this.email = email;
        this.userId = userId;
        this.language = language;
        this.timeZone = timeZone;
        this.q = q;
    }
    public ExportFilter(List<T> fields, FileFormat format, String email, Long userId, String language, String timeZone, String q, Set<Translation> translations) {
        this.fields = fields;
        this.format = format;
        this.email = email;
        this.userId = userId;
        this.language = language;
        this.timeZone = timeZone;
        this.q = q;
        this.translations = translations;
    }

    public List<T> getFields() {
        return fields;
    }

    public void setFields(List<T> fields) {
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
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

package es.onebox.mgmt.export.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.export.enums.CharsetEncoding;
import es.onebox.mgmt.export.enums.CsvFractionDigitsFormat;
import es.onebox.mgmt.export.enums.CsvSeparatorFormat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class ExportSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("charset_encoding")
    private CharsetEncoding charsetEncoding;
    @JsonProperty("csv_separator_format")
    private CsvSeparatorFormat csvSeparatorFormat;
    @JsonProperty("csv_fraction_digits_format")
    private CsvFractionDigitsFormat csvFractionDigitsFormat;

    public CharsetEncoding getCharsetEncoding() {
        return charsetEncoding;
    }

    public void setCharsetEncoding(CharsetEncoding charsetEncoding) {
        this.charsetEncoding = charsetEncoding;
    }

    public CsvSeparatorFormat getCsvSeparatorFormat() {
        return csvSeparatorFormat;
    }

    public void setCsvSeparatorFormat(CsvSeparatorFormat csvSeparatorFormat) {
        this.csvSeparatorFormat = csvSeparatorFormat;
    }

    public CsvFractionDigitsFormat getCsvFractionDigitsFormat() {
        return csvFractionDigitsFormat;
    }

    public void setCsvFractionDigitsFormat(CsvFractionDigitsFormat csvFractionDigitsFormat) {
        this.csvFractionDigitsFormat = csvFractionDigitsFormat;
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

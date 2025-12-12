package es.onebox.common.tickets.enums;

public enum ReportFormat {
    PDF("pdf", "application/pdf", "_pdf", 1);

    private String type;
    private String contentType;
    private String sufijo;
    private int typeMstr;

    public String getType() {
        return this.type;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getSufijo() {
        return this.sufijo;
    }

    public int getTypeMstr() {
        return this.typeMstr;
    }

    private ReportFormat(String type, String contentType, String sufijo, int typeMstr) {
        this.type = type;
        this.contentType = contentType;
        this.sufijo = sufijo;
        this.typeMstr = typeMstr;
    }
}

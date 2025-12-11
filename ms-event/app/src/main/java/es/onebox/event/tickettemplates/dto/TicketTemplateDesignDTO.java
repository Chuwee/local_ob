package es.onebox.event.tickettemplates.dto;

import java.io.Serial;
import java.io.Serializable;

public class TicketTemplateDesignDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 292123839625775749L;

    private Long id;
    private String name;
    private String description;
    private Integer format;
    private String printer;
    private String paperType;
    private String orientation;
    private String jasperFileName;
    private DesignType designType;

    public TicketTemplateDesignDTO() {
    }

    public TicketTemplateDesignDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFormat() {
        return format;
    }

    public void setFormat(Integer format) {
        this.format = format;
    }

    public String getPrinter() {
        return printer;
    }

    public void setPrinter(String printer) {
        this.printer = printer;
    }

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getJasperFileName() {
        return jasperFileName;
    }

    public void setJasperFileName(String jasperFileName) {
        this.jasperFileName = jasperFileName;
    }

    public DesignType getDesignType() {
        return designType;
    }

    public void setDesignType(DesignType designType) {
        this.designType = designType;
    }
}

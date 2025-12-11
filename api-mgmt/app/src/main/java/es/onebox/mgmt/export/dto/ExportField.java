package es.onebox.mgmt.export.dto;

public interface ExportField <T extends ExportField> {

    T getByCode(String code);

}

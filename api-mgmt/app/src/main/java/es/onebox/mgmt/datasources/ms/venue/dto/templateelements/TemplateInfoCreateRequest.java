package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;

public class TemplateInfoCreateRequest extends TemplateInfoDefault {

    private TemplateInfoCopyInfo copyInfo;

    public TemplateInfoCopyInfo getCopyInfo() {
        return copyInfo;
    }

    public void setCopyInfo(TemplateInfoCopyInfo copyInfo) {
        this.copyInfo = copyInfo;
    }
}

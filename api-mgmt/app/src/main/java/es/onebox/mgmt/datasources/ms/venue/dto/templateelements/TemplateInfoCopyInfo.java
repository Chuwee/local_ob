package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;

import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.TemplateInfoCopyMatchType;

public record TemplateInfoCopyInfo(Long source, TemplateInfoCopyMatchType matchType) {

}
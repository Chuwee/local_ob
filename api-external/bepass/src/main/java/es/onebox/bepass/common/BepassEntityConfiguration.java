package es.onebox.bepass.common;

import java.util.List;

public record BepassEntityConfiguration(Long entityId, String tenantId, String companyId, List<String> locationIds) {

}

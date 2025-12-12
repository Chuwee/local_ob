package es.onebox.fifaqatar.notification.mapping;

import es.onebox.fifaqatar.notification.mapping.entity.SessionBarcodeMapping;
import es.onebox.fifaqatar.notification.mapping.entity.SessionBarcodesMapping;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SessionMappingRepository {

    private final SessionMappingCouchDao mappingCouchDao;

    public SessionMappingRepository(SessionMappingCouchDao mappingCouchDao) {
        this.mappingCouchDao = mappingCouchDao;
    }

    public SessionBarcodesMapping get() {
        return mappingCouchDao.get("");
    }

    public SessionBarcodeMapping getBySessionId(Long sessionId) {
        var mapping = this.get();

        return mapping.stream().filter(session -> sessionId.equals(session.getSourceSessionId()))
                .findFirst().orElse(null);
    }

    public List<SessionBarcodeMapping> getByDestinationSessionId(Long destinationSessionId) {
        var mapping = this.get();

        return mapping.stream().filter(session -> destinationSessionId.equals(session.getDestinationSessionId())).collect(Collectors.toList());
    }
}

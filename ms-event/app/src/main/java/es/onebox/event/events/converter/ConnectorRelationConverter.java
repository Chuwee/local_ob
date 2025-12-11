package es.onebox.event.events.converter;

import es.onebox.event.datasources.integration.dispatcher.dto.ConnectorRelation;
import es.onebox.event.datasources.integration.dispatcher.enums.ConnectorRelationApiType;
import es.onebox.event.datasources.integration.dispatcher.enums.ConnectorRelationType;

public class ConnectorRelationConverter {

    private ConnectorRelationConverter() {}

    public static ConnectorRelation toIntegration(Integer eventId, Integer connectorId) {
        ConnectorRelation connectorRelation = new ConnectorRelation();
        connectorRelation.setConnectorId(connectorId);
        connectorRelation.setApiId(ConnectorRelationApiType.TICKETING.getId());
        connectorRelation.setRelationType(ConnectorRelationType.EVENT);
        connectorRelation.setRelationId(eventId);
        return connectorRelation;
    }
}

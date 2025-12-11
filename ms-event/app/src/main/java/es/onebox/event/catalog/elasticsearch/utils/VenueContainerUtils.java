package es.onebox.event.catalog.elasticsearch.utils;

import es.onebox.event.catalog.dao.venue.VenueSeatRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEnlaceRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaNoNumeradaRecord;

import java.util.List;
import java.util.stream.Collectors;

public class VenueContainerUtils {

    private VenueContainerUtils() {
        throw new UnsupportedOperationException();
    }

    public static List<CpanelEnlaceRecord> getLinksByContainerOriginId(List<CpanelEnlaceRecord> links, Integer containerId) {
        return links.stream().filter(l -> containerId.equals(l.getOrigen())).collect(Collectors.toList());
    }

    public static List<CpanelEnlaceRecord> getLinksByContainerDestinationId(List<CpanelEnlaceRecord> links, Integer containerId) {
        return links.stream().filter(l -> containerId.equals(l.getDestino())).collect(Collectors.toList());
    }

    public static List<CpanelZonaNoNumeradaRecord> getNnzsByContainerId(List<CpanelZonaNoNumeradaRecord> nnzs, Integer containerId) {
        return nnzs.stream().filter(nnz -> containerId.equals(nnz.getIdcontenedor())).collect(Collectors.toList());
    }

    public static List<VenueSeatRecord> getSeatsContainerId(List<VenueSeatRecord> seats, Integer containerId) {
        return seats.stream().filter(s -> containerId.equals(s.getContainerId())).collect(Collectors.toList());
    }
}

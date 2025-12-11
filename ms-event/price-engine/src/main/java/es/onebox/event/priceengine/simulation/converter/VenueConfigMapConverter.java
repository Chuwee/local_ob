package es.onebox.event.priceengine.simulation.converter;

import es.onebox.event.priceengine.simulation.domain.PriceZone;
import es.onebox.event.priceengine.simulation.domain.PriceZoneConfig;
import es.onebox.event.priceengine.simulation.domain.RateMap;
import es.onebox.event.priceengine.simulation.domain.VenueConfigMap;
import es.onebox.event.priceengine.simulation.record.PriceZoneRateVenueConfigCustomRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VenueConfigMapConverter {

    private VenueConfigMapConverter() {throw new UnsupportedOperationException("Cannot instantiate converter class");}

    public static Map<Integer, VenueConfigMap> convertToVenueConfigMap (List<PriceZoneRateVenueConfigCustomRecord> records) {
        Map<Integer, VenueConfigMap> venueConfigMap = new HashMap<>();
        List<Integer> venueConfigIds = getVenueConfigsId(records);
        venueConfigIds.forEach( id -> venueConfigMap.put(id, getVenueMapById(id, records)));
        return venueConfigMap;
    }

    private static VenueConfigMap getVenueMapById(Integer id, List<PriceZoneRateVenueConfigCustomRecord> records) {
        VenueConfigMap venueConfigMap = new VenueConfigMap();

        List<PriceZoneRateVenueConfigCustomRecord> resultsByVenueConfigId
                = records.stream().filter(record -> record.getVenueConfig().getIdconfiguracion().equals(id)).toList();

        venueConfigMap.setId(id.longValue());
        venueConfigMap.setName(resultsByVenueConfigId.get(0).getVenueConfig().getNombreconfiguracion());
        venueConfigMap.setRate(convertToRateMap(resultsByVenueConfigId));

        return venueConfigMap;
    }

    private static Map<Integer, RateMap> convertToRateMap(List<PriceZoneRateVenueConfigCustomRecord> resultsByVenueConfigId) {
        Map<Integer, RateMap> rateMap = new HashMap<>();
        List<Integer> listRatesId = getRatesId(resultsByVenueConfigId);
        listRatesId.forEach(id -> rateMap.put(id, getRatesById(id, resultsByVenueConfigId)));
        return rateMap;
    }

    private static RateMap getRatesById(Integer id, List<PriceZoneRateVenueConfigCustomRecord> resultsByVenueConfigId) {
        RateMap rateMap = new RateMap();
        List<PriceZoneRateVenueConfigCustomRecord> resultByRateId =
                resultsByVenueConfigId.stream().filter(record -> record.getRate().getIdtarifa().equals(id)).toList();
        rateMap.setId(id);
        rateMap.setName(resultByRateId.get(0).getRate().getNombre());
        rateMap.setPriceZones(getPriceZones(resultByRateId));
        return rateMap;
    }

    private static List<PriceZone> getPriceZones(List<PriceZoneRateVenueConfigCustomRecord> resultByRateId) {
        return resultByRateId.stream().map(VenueConfigMapConverter::convertToPriceType).toList();
    }

    private static PriceZone convertToPriceType(PriceZoneRateVenueConfigCustomRecord record) {
        PriceZone priceZone = new PriceZone();
        priceZone.setId(record.getIdzona().longValue());
        priceZone.setPrice(record.getPrecio());
        priceZone.setConfig(convertToPriceTypeConfig(record.getPriceZoneConfig()));
        return  priceZone;
    }

    private static PriceZoneConfig convertToPriceTypeConfig(CpanelZonaPreciosConfigRecord record) {
        PriceZoneConfig priceZoneConfig = new PriceZoneConfig();
        priceZoneConfig.setCode(record.getCodigo());
        priceZoneConfig.setDescription(record.getDescripcion());
        return priceZoneConfig;
    }

    private static List<Integer> getRatesId(List<PriceZoneRateVenueConfigCustomRecord> records) {
        return records.stream().map(PriceZoneRateVenueConfigCustomRecord::getRate)
                .map(CpanelTarifaRecord::getIdtarifa).distinct().toList();
    }

    private static List<Integer> getVenueConfigsId(List<PriceZoneRateVenueConfigCustomRecord> result) {
        return result.stream()
                .map(PriceZoneRateVenueConfigCustomRecord::getVenueConfig)
                .map(CpanelConfigRecintoRecord::getIdconfiguracion)
                .distinct()
                .toList();
    }

}

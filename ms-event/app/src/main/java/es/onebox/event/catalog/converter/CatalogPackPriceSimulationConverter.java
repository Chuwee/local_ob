package es.onebox.event.catalog.converter;

import es.onebox.event.catalog.dao.couch.CatalogPriceTaxes;
import es.onebox.event.catalog.dao.couch.CatalogSurcharge;
import es.onebox.event.catalog.dao.couch.CatalogTaxesBreakdown;
import es.onebox.event.catalog.dao.couch.packs.ChannelPackPrice;
import es.onebox.event.catalog.dao.couch.packs.ChannelPackPriceItemInfo;
import es.onebox.event.catalog.dao.couch.packs.ChannelPackPriceType;
import es.onebox.event.catalog.dao.couch.packs.ChannelPackPricesDocument;
import es.onebox.event.catalog.dao.couch.packs.ChannelPackRate;
import es.onebox.event.catalog.dao.couch.packs.ChannelPackTaxInfo;
import es.onebox.event.catalog.dao.couch.packs.ChannelPackVenueConfigPricesSimulation;
import es.onebox.event.catalog.dto.CatalogTaxInfoDTO;
import es.onebox.event.catalog.dto.packs.CatalogPackPriceDTO;
import es.onebox.event.catalog.dto.packs.CatalogPackPriceTypeDTO;
import es.onebox.event.catalog.dto.packs.CatalogPackPricesSimulationDTO;
import es.onebox.event.catalog.dto.packs.CatalogPackRateDTO;
import es.onebox.event.catalog.dto.packs.ChannelPackPriceItemInfoDTO;
import es.onebox.event.catalog.dto.price.CatalogPriceTaxesDTO;
import es.onebox.event.catalog.dto.price.CatalogSurchargeDTO;
import es.onebox.event.catalog.dto.price.CatalogTaxesBreakdownDTO;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.event.packs.enums.PackPricingType;
import es.onebox.event.priceengine.packs.PackPrice;
import es.onebox.event.priceengine.packs.PackPriceItemInfo;
import es.onebox.event.priceengine.packs.PackPriceType;
import es.onebox.event.priceengine.packs.PackRate;
import es.onebox.event.priceengine.packs.PackVenueConfigPricesSimulation;
import es.onebox.event.priceengine.taxes.domain.TaxInfo;
import es.onebox.event.priceengine.taxes.utils.TaxSimulationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

public class CatalogPackPriceSimulationConverter {

    // ToCouch

    public static ChannelPackPricesDocument toCouchChannelPackPricesDocument(Long channelId,
                                                                             Long packId,
                                                                             PackPricingType pricingType,
                                                                             PackVenueConfigPricesSimulation simulation) {
        ChannelPackPricesDocument doc = new ChannelPackPricesDocument();
        doc.setChannelId(channelId);
        doc.setPackId(packId);
        doc.setPricingType(pricingType);
        doc.setSimulation(toCouchChannelPackPricesDocument(simulation));
        doc.setTaxes(toCouchTaxes(simulation.getTaxes()));
        doc.setSurchargesTaxes(toCouchTaxes(simulation.getSurchargesTaxes()));
        return doc;
    }

    private static ChannelPackVenueConfigPricesSimulation toCouchChannelPackPricesDocument(PackVenueConfigPricesSimulation in) {
        ChannelPackVenueConfigPricesSimulation out = new ChannelPackVenueConfigPricesSimulation();
        out.setVenueConfig(in.getVenueConfig());
        out.setRates(toCouchRates(in.getRates()));
        return out;
    }

    private static List<ChannelPackRate> toCouchRates(List<PackRate> rates) {
        return rates.stream().map(CatalogPackPriceSimulationConverter::toCouchRate).toList();
    }

    private static ChannelPackRate toCouchRate(PackRate in) {
        ChannelPackRate out = new ChannelPackRate();
        out.setId(in.getId());
        out.setName(in.getName());
        out.setDefaultRate(in.isDefaultRate());
        out.setPriceTypes(toCouchPriceTypes(in.getPriceTypes()));
        return out;
    }

    private static List<ChannelPackPriceType> toCouchPriceTypes(List<PackPriceType> priceTypes) {
        return priceTypes.stream().map(CatalogPackPriceSimulationConverter::toCouchPriceType).toList();
    }

    private static ChannelPackPriceType toCouchPriceType(PackPriceType in) {
        ChannelPackPriceType out = new ChannelPackPriceType();
        out.setId(in.getId());
        out.setName(in.getName());
        out.setPrice(toCouchPrice(in.getPrice()));
        out.setSimulations(CatalogPriceSimulationConverter.convertToCatalogPriceSimulation(in.getSimulations()));
        return out;
    }

    private static ChannelPackPrice toCouchPrice(PackPrice in) {
        ChannelPackPrice out = new ChannelPackPrice();
        out.setTotal(in.getTotal());
        out.setItemsInfo(toCouchItemsInfo(in.getItemsInfo()));
        return out;
    }

    private static List<ChannelPackPriceItemInfo> toCouchItemsInfo(List<PackPriceItemInfo> itemsInfo) {
        return itemsInfo.stream().map(CatalogPackPriceSimulationConverter::toCouchItemsInfo).toList();
    }

    private static ChannelPackPriceItemInfo toCouchItemsInfo(PackPriceItemInfo in) {
        ChannelPackPriceItemInfo out = new ChannelPackPriceItemInfo();
        out.setItemId(in.getItemId());
        out.setType(PackItemType.getById(in.getType().getId()));
        out.setItemPrice(in.getItemPrice());
        out.setItemPackPrice(in.getItemPackPrice());
        return out;
    }

    private static List<ChannelPackTaxInfo> toCouchTaxes(List<TaxInfo> taxes) {
        if(isEmpty(taxes)) return null;

        return taxes.stream().map(CatalogPackPriceSimulationConverter::toCouchTax).toList();
    }

    private static ChannelPackTaxInfo toCouchTax(TaxInfo in) {
        ChannelPackTaxInfo out = new ChannelPackTaxInfo();
        out.setId(in.getId());
        out.setName(in.getName());
        out.setValue(in.getValue());
        out.setDescription(in.getDescription());
        return out;
    }

    // CouchToDTO

    public static CatalogPackPricesSimulationDTO couchToDTO(ChannelPackPricesDocument doc) {
        if (isNull(doc) || isNull(doc.getSimulation())) return null;

        ChannelPackVenueConfigPricesSimulation simulation = doc.getSimulation();

        CatalogPackPricesSimulationDTO dto = new CatalogPackPricesSimulationDTO();
        dto.setVenueConfig(simulation.getVenueConfig());
        dto.setPricingType(doc.getPricingType());
        dto.setRates(couchToRatesDTO(simulation.getRates()));
        dto.setTaxes(couchToDTO(doc.getTaxes()));
        dto.setSurchargesTaxes(couchToDTO(doc.getSurchargesTaxes()));
        return dto;
    }

    private static List<CatalogPackRateDTO> couchToRatesDTO(List<ChannelPackRate> rates) {
        if (isEmpty(rates)) return new ArrayList<>();

        return rates.stream()
                .map(CatalogPackPriceSimulationConverter::couchToDTO)
                .filter(Objects::nonNull)
                .toList();
    }

    private static CatalogPackRateDTO couchToDTO(ChannelPackRate doc) {
        if (isNull(doc)) return null;

        CatalogPackRateDTO dto = new CatalogPackRateDTO();
        Long rateId = doc.getId();
        dto.setId(rateId);
        dto.setName(doc.getName());
        dto.setDefaultRate(doc.isDefaultRate());
        dto.setPriceTypes(couchPriceTypesToDTO(doc.getPriceTypes()));
        return dto;
    }

    private static List<CatalogPackPriceTypeDTO> couchPriceTypesToDTO(List<ChannelPackPriceType> doc) {
        if (isEmpty(doc)) return new ArrayList<>();

        return doc.stream()
                .map(CatalogPackPriceSimulationConverter::couchToDTO)
                .filter(Objects::nonNull)
                .toList();
    }

    private static CatalogPackPriceTypeDTO couchToDTO(ChannelPackPriceType doc) {
        if (isNull(doc)) return null;

        CatalogPackPriceTypeDTO dto = new CatalogPackPriceTypeDTO();
        dto.setId(doc.getId());
        dto.setName(doc.getName());
        dto.setPrice(couchToDTO(doc.getPrice()));
        dto.setSimulations(CatalogPriceSimulationConverter.convertToCatalogPriceSimulationDTO(doc.getSimulations()));
        return dto;
    }

    private static CatalogPackPriceDTO couchToDTO(ChannelPackPrice doc) {
        if (isNull(doc)) return null;

        CatalogPackPriceDTO dto = new CatalogPackPriceDTO();
        dto.setNet(doc.getNet());
        dto.setTaxes(couchToDTO(doc.getTaxes()));
        dto.setTotal(doc.getTotal());
        dto.setSurcharges(couchToSurchargeDTO(doc.getSurcharges()));
        dto.setItemsInfo(couchToPriceItemInfoDTO(doc.getItemsInfo()));
        return dto;
    }

    private static CatalogPriceTaxesDTO couchToDTO(CatalogPriceTaxes doc) {
        if (isNull(doc)) return null;

        CatalogPriceTaxesDTO dto = new CatalogPriceTaxesDTO();
        dto.setTotal(doc.getTotal());
        dto.setBreakdown(couchToBreakdownDTO(doc.getBreakdown()));
        return dto;
    }

    private static List<CatalogTaxesBreakdownDTO> couchToBreakdownDTO(List<CatalogTaxesBreakdown> breakdowns) {
        if (isEmpty(breakdowns)) return null;

        return breakdowns.stream()
                .map(breakdown -> {
                    CatalogTaxesBreakdownDTO breakdownDTO = new CatalogTaxesBreakdownDTO();
                    breakdownDTO.setId(breakdown.getId());
                    breakdownDTO.setAmount(breakdown.getAmount());
                    return breakdownDTO;
                }).toList();
    }

    private static List<CatalogSurchargeDTO> couchToSurchargeDTO(List<CatalogSurcharge> surcharges) {
        if (isEmpty(surcharges)) return new ArrayList<>();

        return surcharges.stream()
                .map(CatalogPackPriceSimulationConverter::couchToDTO)
                .filter(Objects::nonNull)
                .toList();
    }

    private static CatalogSurchargeDTO couchToDTO(CatalogSurcharge doc) {
        if (isNull(doc)) return null;

        CatalogSurchargeDTO dto = new CatalogSurchargeDTO();
        dto.setType(doc.getType());
        dto.setValue(doc.getValue());
        dto.setNet(doc.getNet());
        dto.setTaxes(couchToDTO(doc.getTaxes()));
        return dto;
    }

    private static List<ChannelPackPriceItemInfoDTO> couchToPriceItemInfoDTO(List<ChannelPackPriceItemInfo> doc) {
        return doc.stream()
                .map(CatalogPackPriceSimulationConverter::couchToDTO)
                .filter(Objects::nonNull)
                .toList();
    }

    private static ChannelPackPriceItemInfoDTO couchToDTO(ChannelPackPriceItemInfo doc) {
        if (isNull(doc)) return null;

        ChannelPackPriceItemInfoDTO dto = new ChannelPackPriceItemInfoDTO();
        dto.setItemId(doc.getItemId());
        dto.setType(doc.getType());
        dto.setItemPrice(doc.getItemPrice());
        dto.setItemPackPrice(doc.getItemPackPrice());
        return dto;
    }

    private static List<CatalogTaxInfoDTO> couchToDTO(List<ChannelPackTaxInfo> taxes) {
        if (isEmpty(taxes)) return null;

        return taxes.stream()
                .map(tax -> TaxSimulationUtils.createTaxInfo(tax.getId(), tax.getValue(), tax.getName(), CatalogTaxInfoDTO::new))
                .toList();
    }

}
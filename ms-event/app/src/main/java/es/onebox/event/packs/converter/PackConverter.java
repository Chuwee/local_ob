package es.onebox.event.packs.converter;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dto.RateDTO;
import es.onebox.event.packs.dao.domain.PackRecord;
import es.onebox.event.packs.dto.CategoryDTO;
import es.onebox.event.packs.dto.CreatePackItemDTO;
import es.onebox.event.packs.dto.CreatePackRateDTO;
import es.onebox.event.packs.dto.PackCreateRequest;
import es.onebox.event.packs.dto.PackDTO;
import es.onebox.event.packs.dto.PackDetailDTO;
import es.onebox.event.packs.dto.PackItemDTO;
import es.onebox.event.packs.dto.PackItemPriceTypesResponseDTO;
import es.onebox.event.packs.dto.PackItemSubsetDTO;
import es.onebox.event.packs.dto.PackItemSubsetsFilter;
import es.onebox.event.packs.dto.PackItemSubsetsResponseDTO;
import es.onebox.event.packs.dto.PackPriceDTO;
import es.onebox.event.packs.dto.PackUpdateRequest;
import es.onebox.event.packs.dto.PacksFilterRequest;
import es.onebox.event.packs.dto.PacksResponse;
import es.onebox.event.packs.dto.PriceTypeRange;
import es.onebox.event.packs.dto.UpdatePackRateDTO;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.event.packs.enums.PackPricingType;
import es.onebox.event.packs.enums.PackRangeType;
import es.onebox.event.packs.enums.PackStatus;
import es.onebox.event.packs.enums.PackSubtype;
import es.onebox.event.packs.enums.PackType;
import es.onebox.event.packs.record.PackDetailRecord;
import es.onebox.event.events.dao.record.PriceRecord;
import es.onebox.event.packs.utils.PackUtils;
import es.onebox.event.sessions.domain.Session;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemZonaPrecioRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackZonaPrecioMappingRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.event.sessions.dao.record.SessionRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PackConverter {

    private static final String DEFAULT_RATE_DESC = "Rate pack ";

    private PackConverter() {
    }

    public static PackDetailDTO toPackDetailDTO(PackDetailRecord record) {
        PackDetailDTO dto = mapPackDTO(record, new PackDetailDTO());
        dto.setBaseCategory(toBaseCategory(record));
        dto.setCustomCategory(toCustomCategory(record));
        if (record.getTaxid() != null) {
            dto.setTax(new IdNameDTO(record.getTaxid().longValue(), record.getTaxName()));
        }
        return dto;
    }

    public static PackDTO toDTO(PackRecord record) {
        return mapPackDTO(record, new PackDTO());
    }

    public static <T extends PackDTO> T mapPackDTO(PackRecord record, T dto) {
        fillPackBaseInfo(dto, record, false, false, false);
        dto.setPriceIncrement(record.getIncremementoprecio());
        dto.setActive(PackStatus.ACTIVE.getId().equals(record.getEstado()));
        return dto;
    }

    public static PacksResponse convert(List<PackRecord> packs, Long count, PacksFilterRequest filter) {
        PacksResponse response = new PacksResponse();
        List<PackDTO> dtos = packs.stream().map(PackConverter::toDTO).toList();
        Metadata metadataDTO = MetadataBuilder.build(filter, count);
        response.setData(dtos);
        response.setMetadata(metadataDTO);
        return response;
    }

    public static void fillPackBaseInfo(PackDTO packBaseDTO,
                                        PackRecord packRecord,
                                        Boolean soldOut,
                                        Boolean forSale,
                                        Boolean onSale) {
        packBaseDTO.setId(packRecord.getIdpack().longValue());
        packBaseDTO.setName(packRecord.getNombre());
        packBaseDTO.setType(PackType.from(packRecord));
        packBaseDTO.setSubtype(PackSubtype.from(packRecord));
        packBaseDTO.setEntityId(packRecord.getIdentidad().longValue());
        packBaseDTO.setEntityName(packRecord.getEntity().getNombre());
        packBaseDTO.setSoldOut(BooleanUtils.isTrue(soldOut));
        packBaseDTO.setForSale(BooleanUtils.isTrue(forSale));
        packBaseDTO.setOnSale(BooleanUtils.isTrue(onSale));
        if (packRecord.getIdpromocion() != null) {
            packBaseDTO.setPromotionId(packRecord.getIdpromocion().longValue());
        }
        if (packRecord.getIdtipopricing() != null) {
            packBaseDTO.setPricingType(PackPricingType.getById(packRecord.getIdtipopricing()));
        }
        packBaseDTO.setPackRangeType(PackRangeType.getById(packRecord.getTiporangopack()));
        packBaseDTO.setCustomStartSaleDate(CommonUtils.timestampToZonedDateTime(packRecord.getFechainiciopack()));
        packBaseDTO.setCustomEndSaleDate(CommonUtils.timestampToZonedDateTime(packRecord.getFechafinpack()));

        packBaseDTO.setUnifiedPrice(ConverterUtils.isByteAsATrue(packRecord.getUnifiedprice()));
        packBaseDTO.setShowDate(ConverterUtils.isByteAsATrue(packRecord.getShowdate()));
        packBaseDTO.setShowDateTime(ConverterUtils.isByteAsATrue(packRecord.getShowdatetime()));
        packBaseDTO.setShowMainDate(ConverterUtils.isByteAsATrue(packRecord.getShowmaindate()));
        packBaseDTO.setShowMainVenue(ConverterUtils.isByteAsATrue(packRecord.getShowmainvenue()));
    }

    public static List<PackItemDTO> toItemsDTO(List<CpanelPackItemRecord> packItemRecords,
                                               Map<Integer, List<CpanelPackZonaPrecioMappingRecord>> priceTypeMappingsByTargetItem) {
        if (CollectionUtils.isEmpty(packItemRecords)) {
            return null;
        }
        return packItemRecords.stream()
                .map(i -> PackConverter.toDTO(i, priceTypeMappingsByTargetItem))
                .collect(Collectors.toList());
    }

    public static PackItemDTO toDTO(CpanelPackItemRecord packItemRecord,
                                    Map<Integer, List<CpanelPackZonaPrecioMappingRecord>> priceTypeMappingsByTargetItem) {
        PackItemDTO item = new PackItemDTO();
        item.setPackItemId(packItemRecord.getIdpackitem().longValue());
        item.setItemId(packItemRecord.getIditem().longValue());
        item.setType(PackUtils.getType(packItemRecord));
        item.setMain(packItemRecord.getPrincipal());
        item.setDisplayItemInChannels(CommonUtils.isTrue(packItemRecord.getMostraritemenchannels()));
        item.setInformativePrice(packItemRecord.getPrecioinformativo());

        switch (item.getType()) {
            case EVENT -> item.setVenueTemplateId(packItemRecord.getIdconfiguracion());
            case SESSION -> {
                if (CommonUtils.isTrue(packItemRecord.getZonapreciomapping())) {
                    List<CpanelPackZonaPrecioMappingRecord> mappings = priceTypeMappingsByTargetItem.get(packItemRecord.getIdpackitem());
                    Map<Integer, List<Integer>> mapping = getPriceTypeMapping(mappings);
                    item.setPriceTypeMapping(mapping);
                } else {
                    item.setPriceTypeId(packItemRecord.getIdzonaprecio());
                }
            }
            case PRODUCT -> {
                item.setVariantId(packItemRecord.getIdvariante());
                item.setDeliveryPointId(packItemRecord.getIdpuntoentrega());
                item.setSharedBarcode(packItemRecord.getCodigodebarrascompartido());
            }
        }
        return item;
    }

    public static Map<Integer, List<Integer>> getPriceTypeMapping(List<CpanelPackZonaPrecioMappingRecord> mappings) {
        if(mappings == null || mappings.isEmpty()) return null;

        Map<Integer, List<Integer>> mapping = new HashMap<>();
        for (CpanelPackZonaPrecioMappingRecord r : mappings) {
            if (!mapping.containsKey(r.getIdsourcezonaprecio())) {
                mapping.put(r.getIdsourcezonaprecio(), new ArrayList<>());
            }
            mapping.get(r.getIdsourcezonaprecio()).add(r.getIdtargetzonaprecio());
        }
        return mapping;
    }

    public static CpanelPackRecord toRecord(PackCreateRequest createRequest) {
        CpanelPackRecord packRecord = new CpanelPackRecord();
        packRecord.setNombre(createRequest.getName());
        packRecord.setEstado(PackStatus.INACTIVE.getId());
        packRecord.setTipo(PackSubtype.PROMOTER.getId());
        packRecord.setSubtipo(PackType.AUTOMATIC.getId());
        packRecord.setIdentidad(createRequest.getEntityId().intValue());
        packRecord.setTiporangopack(PackRangeType.AUTOMATIC.getId());
        packRecord.setIdtipopricing(PackPricingType.COMBINED.getId());
        packRecord.setTaxid(createRequest.getTaxId().intValue());

        //Change to createRequest.getUnifiedPrice()
        packRecord.setUnifiedprice(ConverterUtils.isTrueAsByte(Boolean.TRUE));
        return packRecord;
    }

    public static CpanelPackItemRecord toMainItemRecord(CreatePackItemDTO mainItemDTO, Integer packId) {
        CpanelPackItemRecord packMainItemRecord = new CpanelPackItemRecord();
        packMainItemRecord.setIdpack(packId);
        packMainItemRecord.setIditem(mainItemDTO.getItemId().intValue());
        packMainItemRecord.setTipoitem(mainItemDTO.getType().getId());
        packMainItemRecord.setPrincipal(Boolean.TRUE);
        if (PackItemType.EVENT.equals(mainItemDTO.getType())) {
            packMainItemRecord.setIdconfiguracion(mainItemDTO.getVenueTemplateId());
        }
        return packMainItemRecord;
    }

    public static void toRecord(CpanelPackRecord packRecord, PackUpdateRequest updateRequest) {
        if (updateRequest.getActive() != null) {
            packRecord.setEstado(updateRequest.getActive() ? PackStatus.ACTIVE.getId() : PackStatus.INACTIVE.getId());
        }
        ConverterUtils.updateField(packRecord::setNombre, updateRequest.getName());

        if (updateRequest.getPackRangeType() != null) {
            packRecord.setTiporangopack(updateRequest.getPackRangeType().getId());
        }
        if (updateRequest.getPricingType() != null) {
            packRecord.setIdtipopricing(updateRequest.getPricingType().getId());
        }
        ConverterUtils.updateField(packRecord::setIncremementoprecio, updateRequest.getPriceIncrement());
        ConverterUtils.updateField(packRecord::setFechainiciopack, CommonUtils.zonedDateTimeToTimestamp(updateRequest.getCustomStartSaleDate()));
        ConverterUtils.updateField(packRecord::setFechafinpack, CommonUtils.zonedDateTimeToTimestamp(updateRequest.getCustomEndSaleDate()));

        if (updateRequest.getShowDate() != null) {
            ConverterUtils.updateField(packRecord::setShowdate, ConverterUtils.isTrueAsByte(updateRequest.getShowDate()));
        }
        if (updateRequest.getShowDateTime() != null) {
            ConverterUtils.updateField(packRecord::setShowdatetime, ConverterUtils.isTrueAsByte(updateRequest.getShowDateTime()));
        }
        if (updateRequest.getShowMainDate() != null) {
            ConverterUtils.updateField(packRecord::setShowmaindate, ConverterUtils.isTrueAsByte(updateRequest.getShowMainDate()));
        }
        if (updateRequest.getShowMainVenue() != null) {
            ConverterUtils.updateField(packRecord::setShowmainvenue, ConverterUtils.isTrueAsByte(updateRequest.getShowMainVenue()));
        }
        if (updateRequest.getBaseCategoryId() != null) {
            packRecord.setTaxonomyid(updateRequest.getBaseCategoryId().intValue());
        }
        if (updateRequest.getCustomCategoryId() != null) {
            packRecord.setCustomtaxonomyid(updateRequest.getCustomCategoryId().intValue());
        }
        if (updateRequest.getTaxId() != null) {
            packRecord.setTaxid(updateRequest.getTaxId().intValue());
        }
        if (updateRequest.getUnifiedPrice() != null) {
            packRecord.setUnifiedprice(ConverterUtils.isTrueAsByte(updateRequest.getUnifiedPrice()));
        }
    }

    public static CpanelPackItemRecord toRecord(CreatePackItemDTO in, Long packId) {
        CpanelPackItemRecord out = new CpanelPackItemRecord();
        out.setIdpack(packId.intValue());
        out.setIditem(in.getItemId().intValue());
        out.setTipoitem(in.getType().getId());
        out.setPrincipal(Boolean.FALSE);
        out.setMostraritemenchannels(ConverterUtils.isTrueAsByte(in.getDisplayItemInChannels()));
        switch (in.getType()) {
            case SESSION -> {
                if (MapUtils.isNotEmpty(in.getPriceTypeMapping())) {
                    out.setZonapreciomapping((byte) 1);
                } else {
                    out.setIdzonaprecio(in.getPriceTypeId());
                }
            }
            case PRODUCT -> {
                out.setIdvariante(in.getVariantId());
                out.setIdpuntoentrega(in.getDeliveryPointId());
                out.setCodigodebarrascompartido(in.getSharedBarcode());
            }
        }
        return out;
    }

    public static List<RateDTO> toRatesDTO(Map<Integer, CpanelTarifaPackRecord> ratesById, List<CpanelTarifaRecord> rates) {
        if (CollectionUtils.isEmpty(rates)) {
            return null;
        }

        return rates.stream().map(rate -> PackConverter.toDTO(rate, ratesById.get(rate.getIdtarifa())))
                .collect(Collectors.toList());
    }

    public static RateDTO toDTO(CpanelTarifaRecord rate, CpanelTarifaPackRecord packRate) {
        if (rate == null || packRate == null) {
            return null;
        }
        RateDTO rateDTO = new RateDTO();
        rateDTO.setId(rate.getIdtarifa().longValue());
        rateDTO.setName(rate.getNombre());
        rateDTO.setDescription(rate.getDescripcion());
        rateDTO.setRestrictive(CommonUtils.isTrue(rate.getAccesorestrictivo()));
        rateDTO.setDefaultRate(packRate.getDefecto());

        return rateDTO;
    }

    public static CpanelTarifaRecord toRecord(CreatePackRateDTO createRequest, Long packId) {
        CpanelTarifaRecord tarifaRecord = new CpanelTarifaRecord();
        tarifaRecord.setNombre(createRequest.getName());
        if (createRequest.getDescription() != null) {
            tarifaRecord.setDescripcion(createRequest.getDescription());
        } else {
            tarifaRecord.setDescripcion(DEFAULT_RATE_DESC + packId);
        }
        tarifaRecord.setDefecto(createRequest.getDefaultRate() != null ?
                ConverterUtils.isTrueAsByte(createRequest.getDefaultRate()) : ConverterUtils.isTrueAsByte(Boolean.FALSE));
        tarifaRecord.setAccesorestrictivo(ConverterUtils.isTrueAsByte(createRequest.getRestrictive()));
        return tarifaRecord;
    }

    public static void updateRecord(CpanelTarifaRecord tarifaRecord, UpdatePackRateDTO updateRequest) {
        ConverterUtils.updateField(tarifaRecord::setNombre, updateRequest.getName());
        ConverterUtils.updateField(tarifaRecord::setDescripcion, updateRequest.getDescription());
        ConverterUtils.updateField(tarifaRecord::setAccesorestrictivo, ConverterUtils.isTrueAsByte(updateRequest.getRestrictive()));
        if (updateRequest.getDefaultRate() != null) {
            ConverterUtils.updateField(tarifaRecord::setDefecto, ConverterUtils.isTrueAsByte(updateRequest.getDefaultRate()));
        }
    }

    public static void updateRecord(CpanelTarifaPackRecord tarifaPackRecord, UpdatePackRateDTO updateRequest) {
        ConverterUtils.updateField(tarifaPackRecord::setDefecto, updateRequest.getDefaultRate());
    }

    public static CpanelTarifaPackRecord toRecord(CreatePackRateDTO request, List<CpanelTarifaPackRecord> rates,
                                                  Integer rateId, Long packId) {
        CpanelTarifaPackRecord tarifaPackRecord = new CpanelTarifaPackRecord();
        tarifaPackRecord.setIdtarifa(rateId);
        tarifaPackRecord.setIdpack(packId.intValue());
        if (CollectionUtils.isEmpty(rates)) {
            tarifaPackRecord.setDefecto(Boolean.TRUE);
        } else {
            tarifaPackRecord.setDefecto(BooleanUtils.isTrue(request.getDefaultRate()));
        }
        tarifaPackRecord.setIdtarifaevento(request.getRelatedRateId());
        return tarifaPackRecord;
    }

    public static List<PackPriceDTO> toPricesDTO(List<PriceRecord> priceRecords) {
        if (CollectionUtils.isEmpty(priceRecords)) {
            return null;
        }
        return priceRecords.stream().map(PackConverter::toDTO).collect(Collectors.toList());
    }

    public static PackPriceDTO toDTO(PriceRecord priceRecord) {
        PackPriceDTO priceDTO = new PackPriceDTO();
        priceDTO.setPrice(priceRecord.getPrice());
        priceDTO.setRateId(priceRecord.getRateId());
        priceDTO.setRateName(priceRecord.getRateName());
        priceDTO.setPriceTypeId(priceRecord.getPriceZoneId().longValue());
        priceDTO.setPriceTypeCode(priceRecord.getPriceZoneCode());
        priceDTO.setPriceTypeDescription(priceRecord.getPriceZoneDescription());
        return priceDTO;
    }

    public static PackItemPriceTypesResponseDTO toPackItemPriceTypesResponseDTO(List<CpanelPackItemZonaPrecioRecord> packItemZonaPrecioRecords,
                                                                                List<IdNameCodeDTO> priceTypes) {
        if (CollectionUtils.isEmpty(packItemZonaPrecioRecords) || CollectionUtils.isEmpty(priceTypes)) {
            return null;
        }
        PackItemPriceTypesResponseDTO response = new PackItemPriceTypesResponseDTO();
        List<IdNameDTO> packItemPriceTypes = new ArrayList<>();
        packItemZonaPrecioRecords.forEach(record -> {
            priceTypes.stream()
                    .filter(pt -> record.getIdzonaprecio().equals(pt.getId().intValue()))
                    .findFirst().ifPresent(priceType ->
                            packItemPriceTypes.add(toDTO(record, priceType)));

        });
        response.setPriceTypes(packItemPriceTypes);
        response.setSelectionType(PriceTypeRange.RESTRICTED);

        return response;
    }


    public static IdNameDTO toDTO(CpanelPackItemZonaPrecioRecord packItemZonaPrecio, IdNameCodeDTO priceType) {
        IdNameDTO priceZoneDTO = new IdNameDTO();
        priceZoneDTO.setId(Long.valueOf(packItemZonaPrecio.getIdzonaprecio()));
        priceZoneDTO.setName(priceType.getName());

        return priceZoneDTO;
    }

    public static PackItemSubsetsResponseDTO toPackItemSubsetsResponseDTO(List<PackItemSubsetDTO> packItemSubsetDTOS, Long total, PackItemSubsetsFilter filter) {
        PackItemSubsetsResponseDTO response = new PackItemSubsetsResponseDTO();
        Metadata metadataDTO = MetadataBuilder.build(filter, total);
        response.setData(packItemSubsetDTOS);
        response.setMetadata(metadataDTO);
        return response;
    }

    public static PackItemSubsetDTO toPackItemSubsetDTO(Session session) {
        PackItemSubsetDTO dto = new PackItemSubsetDTO();
        dto.setId(session.getSessionId());
        dto.setName(session.getName());
        dto.setStartDate(session.getSessionStartDate());
        return dto;
    }

    public static List<PackItemSubsetDTO> toPackItemSubsetDTOList(List<Session> sessions) {
        if (CollectionUtils.isEmpty(sessions)) {
            return List.of();
        }
        return sessions.stream()
                .map(PackConverter::toPackItemSubsetDTO)
                .toList();
    }

    private static CategoryDTO toBaseCategory(PackDetailRecord record) {
        if (record.getTaxonomyid() == null) {
            return null;
        }

        CategoryDTO category = new CategoryDTO();
        category.setId(record.getTaxonomyid().longValue());
        category.setCode(record.getBaseCategoryCode());
        category.setDescription(record.getBaseCategoryDescription());
        return category;
    }

    private static CategoryDTO toCustomCategory(PackDetailRecord record) {
        if (record.getCustomtaxonomyid() == null) {
            return null;
        }

        CategoryDTO customCategory = new CategoryDTO();
        customCategory.setId(record.getCustomtaxonomyid().longValue());
        customCategory.setCode(record.getCustomCategoryCode());
        customCategory.setDescription(record.getCustomCategoryDescription());
        return customCategory;
    }
}

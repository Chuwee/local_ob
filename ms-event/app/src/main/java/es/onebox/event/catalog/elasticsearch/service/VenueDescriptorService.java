package es.onebox.event.catalog.elasticsearch.service;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.DateUtils;
import es.onebox.couchbase.core.Key;
import es.onebox.event.catalog.dao.VenueDescriptorCouchDao;
import es.onebox.event.catalog.dao.venue.VenueConfigurationDao;
import es.onebox.event.catalog.dao.venue.VenueContainerDao;
import es.onebox.event.catalog.dto.venue.container.VenueContainerDTO;
import es.onebox.event.catalog.dto.venue.container.VenueContainerDTOBuilder;
import es.onebox.event.catalog.dto.venue.container.VenueContainerLink;
import es.onebox.event.catalog.dto.venue.container.VenueContainerNnz;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptorBuilder;
import es.onebox.event.catalog.dto.venue.container.pricetype.VenuePriceType;
import es.onebox.event.catalog.dto.venue.container.pricetype.VenuePriceTypeCommElement;
import es.onebox.event.catalog.dto.venue.container.VenueQuota;
import es.onebox.event.catalog.dto.venue.container.VenueSector;
import es.onebox.event.catalog.dto.venue.container.tier.VenueTier;
import es.onebox.event.catalog.dto.venue.container.tier.VenueTierCommElement;
import es.onebox.event.catalog.elasticsearch.utils.VenueContainerUtils;
import es.onebox.event.config.LocalCache;
import es.onebox.event.datasources.ms.venue.dto.CommunicationElementType;
import es.onebox.event.events.dao.TierConfigCouchDao;
import es.onebox.event.events.dao.TierDao;
import es.onebox.event.events.dao.record.TierRecord;
import es.onebox.event.events.domain.TierConfig;
import es.onebox.event.events.dto.TierCondition;
import es.onebox.event.venues.dao.PriceTypeCouchDao;
import es.onebox.event.venues.domain.PriceTypeConfig;
import es.onebox.event.venues.domain.PriceTypeTranslation;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static es.onebox.event.datasources.ms.venue.dto.CommunicationElementType.DESCRIPTION;
import static es.onebox.event.datasources.ms.venue.dto.CommunicationElementType.NAME;

@Component
public class VenueDescriptorService {

    private final VenueConfigurationDao venueConfigurationDao;
    private final VenueContainerDao venueContainerDao;
    private final VenueDescriptorCouchDao venueDescriptorCouchDao;
    private final PriceTypeCouchDao priceTypeCouchDao;
    private final TierDao tierDao;
    private final TierConfigCouchDao tierConfigCouchDao;
    private final CacheRepository localCacheRepository;

    @Autowired
    public VenueDescriptorService(VenueConfigurationDao venueConfigurationDao,
                                  VenueContainerDao venueContainerDao,
                                  VenueDescriptorCouchDao venueDescriptorCouchDao,
                                  PriceTypeCouchDao priceTypeCouchDao, TierDao tierDao, TierConfigCouchDao tierConfigCouchDao,
                                  CacheRepository localCacheRepository) {
        this.venueConfigurationDao = venueConfigurationDao;
        this.venueContainerDao = venueContainerDao;
        this.venueDescriptorCouchDao = venueDescriptorCouchDao;
        this.priceTypeCouchDao = priceTypeCouchDao;
        this.tierDao = tierDao;
        this.tierConfigCouchDao = tierConfigCouchDao;
        this.localCacheRepository = localCacheRepository;
    }

    public VenueDescriptor getVenueDescriptor(Long venueTemplateId) {
        return this.venueDescriptorCouchDao.get(String.valueOf(venueTemplateId));
    }

    public List<VenueDescriptor> create(Long eventId, List<Long> venueTemplateIds, Boolean userTiers) {
        List<CpanelConfigRecintoRecord> venueConfigs = venueConfigurationDao.getByIds(venueTemplateIds.stream().map(Long::intValue).collect(Collectors.toList()));
        return venueConfigs.stream()
                .map(vc -> this.build(eventId, vc, userTiers))
                .collect(Collectors.toList());
    }

    private VenueDescriptor build(Long eventId, CpanelConfigRecintoRecord vc, Boolean useTiers) {
        return VenueDescriptorBuilder.builder()
                .venueConfigId(vc.getIdconfiguracion())
                .eventId(eventId.intValue())
                .graphic(vc.getEsgrafica() != 0)
                .type(vc.getTipoplantilla())
                .priceTypes(this.getPriceTypes(vc.getIdconfiguracion()))
                .quotas(this.getQuotas(vc.getIdconfiguracion()))
                .sectors(this.getSectors(vc.getIdconfiguracion()))
                .containers(vc.getEsgrafica() != 0 ? this.getContainers(vc.getIdconfiguracion()) : null)
                .name(vc.getNombreconfiguracion())
                .tiers(Boolean.TRUE.equals(useTiers) ? getTiers(vc.getIdconfiguracion()) : Collections.emptyList())
                .build();
    }

    private List<VenuePriceType> getPriceTypes(Integer venueConfigId) {
        List<CpanelZonaPreciosConfigRecord> templatePriceTypes = venueConfigurationDao.getPriceZonesByVenueConfigId(venueConfigId);

        Map<Long, PriceTypeTranslation> comElementsByPriceType = getPriceTypesComElements(templatePriceTypes);

        return templatePriceTypes.stream().map(zp -> {
            Long priceTypeId = zp.getIdzona().longValue();
            VenuePriceType venuePriceType = new VenuePriceType();
            venuePriceType.setId(priceTypeId);
            venuePriceType.setName(zp.getDescripcion());
            venuePriceType.setCode(zp.getCodigo());
            venuePriceType.setColor(zp.getColor());
            venuePriceType.setOrder(zp.getPrioridad());
            venuePriceType.setCommElements(getComElements(comElementsByPriceType.get(priceTypeId)));
            return venuePriceType;
        }).collect(Collectors.toList());
    }

    private List<VenueQuota> getQuotas(Integer venueConfigId) {
        return venueConfigurationDao.getQuotasByConfigId(venueConfigId)
                .stream().map(q -> {
                    var venueQuota = new VenueQuota();
                    venueQuota.setId(q.getIdcupo());
                    venueQuota.setName(q.getDescripcion());
                    venueQuota.setCode(q.getCodigo());
                    venueQuota.setDefaultQuota(CommonUtils.isTrue(q.getDefecto()));
                    return venueQuota;
                }).collect(Collectors.toList());
    }

    private List<VenueSector> getSectors(Integer venueConfigId) {
        return localCacheRepository.cached(LocalCache.TEMPLATE_SECTORS, LocalCache.TEMPLATE_TTL, TimeUnit.SECONDS,
                () -> venueContainerDao.geSectorsByVenueConfigId(venueConfigId)
                        .stream().map(s -> {
                            var venueSector = new VenueSector();
                            venueSector.setId(s.getIdsector());
                            venueSector.setCode(s.getCodigo());
                            venueSector.setDescription(s.getDescripcion());
                            return venueSector;
                        }).collect(Collectors.toList()),
                new Object[]{venueConfigId});
    }

    private List<VenueTier> getTiers(Integer venueConfigId) {
        List<VenueTier> venueTiers = new ArrayList<>();
        List<TierRecord> tiersByVenueTemplate = this.tierDao.findByVenueTemplate(venueConfigId);
        if (CollectionUtils.isNotEmpty(tiersByVenueTemplate)) {
            var tiersIds = tiersByVenueTemplate.stream().map(t -> t.getIdtier().longValue()).collect(Collectors.toList());
            Map<Long, TierConfig> tierConfigs = this.tierConfigCouchDao.bulkGet(tiersIds).stream().collect(Collectors.toMap(TierConfig::getTierId, Function.identity()));
            tiersByVenueTemplate.forEach(t -> {
                var venueTier = new VenueTier();
                venueTier.setId(t.getIdtier().longValue());
                venueTier.setName(t.getNombre());
                venueTier.setStartDate(DateUtils.fromMillis(t.getFechaInicio().getTime()));
                venueTier.setOlsonId(t.getTimeZoneOlsonId());
                venueTier.setPriceTypeId(t.getIdzona().longValue());
                venueTier.setPrice(t.getPrecio());
                venueTier.setActive(CommonUtils.isTrue(t.getVenta()));
                venueTier.setMaxCapacity(t.getLimite() != null ? t.getLimite().longValue() : null);
                var condition = TierCondition.getById(t.getCondicion());
                venueTier.setCondition(condition != null ? condition.name() : null);
                venueTier.setCommElements(new ArrayList<>());
                var tierConfig = tierConfigs.get(t.getIdtier().longValue());
                if (tierConfig != null && tierConfig.getTierTranslation() != null) {
                    var translation = tierConfig.getTierTranslation();
                    venueTier.getCommElements().addAll(getTierCommElements(NAME, translation.getName()));
                    venueTier.getCommElements().addAll(getTierCommElements(DESCRIPTION, translation.getDescription()));
                }
                venueTiers.add(venueTier);
            });
        }
        return venueTiers;
    }

    private List<VenueTierCommElement> getTierCommElements(CommunicationElementType type, Map<String, String> comm) {
        if (MapUtils.isNotEmpty(comm)) {
            return comm.entrySet().stream().map(entry -> {
                var commElement = new VenueTierCommElement();
                commElement.setType(type.name());
                commElement.setLang(entry.getKey());
                commElement.setValue(entry.getValue());

                return commElement;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<VenueContainerDTO> getContainers(Integer venueConfigId) {
        Object[] templateKey = {venueConfigId};
        var containers = localCacheRepository.cached(LocalCache.TEMPLATE_CONTAINERS, LocalCache.TEMPLATE_TTL, TimeUnit.SECONDS,
                () -> venueContainerDao.getByVenueConfigId(venueConfigId), templateKey);
        var nnzs = localCacheRepository.cached(LocalCache.TEMPLATE_NNZS, LocalCache.TEMPLATE_TTL, TimeUnit.SECONDS,
                () -> venueContainerDao.getZnnByVenueConfigId(venueConfigId), templateKey);
        var links = localCacheRepository.cached(LocalCache.TEMPLATE_LINKS, LocalCache.TEMPLATE_TTL, TimeUnit.SECONDS,
                () -> venueContainerDao.getLinksByVenueConfigId(venueConfigId), templateKey);

        return containers.stream()
                .map(c -> {
                    Integer containerId = c.getIdcontenedor();
                    var builder = VenueContainerDTOBuilder.builder()
                            .id(containerId)
                            .venueConfigId(c.getIdconfiguracion())
                            .name(c.getNombrecontenedor())
                            .description(c.getDescripcion())
                            .root(c.getEsvistaprincipal() != 0)
                            .svg(c.getUrlrepresentacion());

                    var containerNnzs = VenueContainerUtils.getNnzsByContainerId(nnzs, containerId)
                            .stream().map(nnz -> {
                                var nnzContainer = new VenueContainerNnz();
                                nnzContainer.setId(nnz.getIdzona().longValue());
                                nnzContainer.setName(nnz.getNombre());
                                nnzContainer.setSectorId(nnz.getSector());

                                return nnzContainer;
                            }).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(containerNnzs)) {
                        builder.nnzs(containerNnzs);
                    }

                    var containerLinks = VenueContainerUtils.getLinksByContainerOriginId(links, containerId)
                            .stream().map(l -> {
                                var venueContainerLink = new VenueContainerLink();
                                venueContainerLink.setId(l.getIdenlace().longValue());
                                venueContainerLink.setRefId(l.getRefid());
                                venueContainerLink.setDestinationId(l.getDestino());
                                venueContainerLink.setOriginId(l.getOrigen());

                                return venueContainerLink;
                            }).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(containerLinks)) {
                        builder.links(containerLinks);
                    }
                    return builder.build();
                }).collect(Collectors.toList());
    }

    private Map<Long, PriceTypeTranslation> getPriceTypesComElements(List<CpanelZonaPreciosConfigRecord> templatePriceTypes) {
        List<Key> keys = templatePriceTypes.stream().map(p -> {
            Key key = new Key();
            key.setKey(new String[]{String.valueOf(p.getIdzona())});
            return key;
        }).collect(Collectors.toList());
        List<PriceTypeConfig> priceTypeConfigs = priceTypeCouchDao.bulkGet(keys);

        return priceTypeConfigs.stream().
                filter(p -> p.getPriceTypeTranslation() != null).
                collect(Collectors.toMap(PriceTypeConfig::getPriceTypeId, PriceTypeConfig::getPriceTypeTranslation));
    }

    private List<VenuePriceTypeCommElement> getComElements(PriceTypeTranslation comElems) {
        if (comElems != null) {
            List<VenuePriceTypeCommElement> comElements = new ArrayList<>();
            if (MapUtils.isNotEmpty(comElems.getName())) {
                for (Map.Entry<String, String> elem : comElems.getName().entrySet()) {
                    comElements.add(new VenuePriceTypeCommElement(NAME.name(), elem.getKey(), elem.getValue()));
                }
            }
            if (MapUtils.isNotEmpty(comElems.getDescription())) {
                for (Map.Entry<String, String> elem : comElems.getDescription().entrySet()) {
                    comElements.add(new VenuePriceTypeCommElement(DESCRIPTION.name(), elem.getKey(), elem.getValue()));
                }
            }
            return comElements;
        }
        return null;
    }

}

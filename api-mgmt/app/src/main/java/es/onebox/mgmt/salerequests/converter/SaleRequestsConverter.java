package es.onebox.mgmt.salerequests.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsChannelSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsEntitySaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsEventSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsTaxonomySaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsVenueSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.dto.BaseCategorySaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.BaseSaleRequestsDTO;
import es.onebox.mgmt.salerequests.dto.CategoriesSaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.CategoriesSaleRequestExtendedDTO;
import es.onebox.mgmt.salerequests.dto.ChannelSaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.ChannelSaleRequestDetailDTO;
import es.onebox.mgmt.salerequests.dto.ContactPersonDTO;
import es.onebox.mgmt.salerequests.dto.EventSaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.EventSaleRequestDetailDTO;
import es.onebox.mgmt.salerequests.dto.LocationSaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.SaleRequestDetailDTO;
import es.onebox.mgmt.salerequests.dto.SearchSaleRequestsResponse;
import es.onebox.mgmt.salerequests.dto.SubscriptionListSalesRequestDTO;
import es.onebox.mgmt.salerequests.dto.TaxModeDTO;
import es.onebox.mgmt.salerequests.dto.VenueSaleRequestDTO;
import es.onebox.mgmt.salerequests.enums.SaleRequestEventType;
import es.onebox.mgmt.salerequests.enums.SaleRequestsStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class SaleRequestsConverter {

    private SaleRequestsConverter() {throw new UnsupportedOperationException("Cannot instantiate converter class");}

    public static SearchSaleRequestsResponse fromMsChannelsResponse(MsSaleRequestsResponseDTO msResponse) {
        SearchSaleRequestsResponse dto = new SearchSaleRequestsResponse();
        dto.setMetadata(msResponse.getMetadata());
        dto.setData(msResponse.getData()
                .stream()
                .map(SaleRequestsConverter::fromMsChannelObject)
                .collect(Collectors.toList()));
        return dto;
    }

    public static SaleRequestDetailDTO fromMsChannelToSaleRequestDetail(MsSaleRequestDTO msSaleRequests, ChannelLanguagesDTO languages,
                                                                        List<Currency> currencies ) {
        SaleRequestDetailDTO result = new SaleRequestDetailDTO();

        result.setId(msSaleRequests.getId());
        result.setStatus(SaleRequestsStatus.fromMsChannelEnum(msSaleRequests.getStatus()));
        result.setDate(msSaleRequests.getDate());
        result.setLanguages(languages);

        result.setChannel(channelDetailConvert(msSaleRequests.getChannel()));

        if (nonNull(msSaleRequests.getEvent())) {
            EventSaleRequestDetailDTO event = new EventSaleRequestDetailDTO();
            event.setId(msSaleRequests.getEvent().getId());
            event.setName(msSaleRequests.getEvent().getName());
            event.setEventType(SaleRequestEventType.valueOf(msSaleRequests.getEvent().getEventType().name()));
            event.setStartDate(msSaleRequests.getEvent().getStartDate());
            event.setEntity(entityConvert(msSaleRequests.getEvent().getEntity()));
            event.setVenues(convert(msSaleRequests.getEvent().getVenues()));

            if (nonNull(msSaleRequests.getEvent().getPersonContactName()) || nonNull(msSaleRequests.getEvent().getPersonContactSurname()) ||
                    nonNull(msSaleRequests.getEvent().getPersonContactEmail()) || nonNull(msSaleRequests.getEvent().getPersonContactPhone())) {
                ContactPersonDTO contactPersonDTO = new ContactPersonDTO();
                contactPersonDTO.setName(msSaleRequests.getEvent().getPersonContactName());
                contactPersonDTO.setSurname(msSaleRequests.getEvent().getPersonContactSurname());
                contactPersonDTO.setEmail(msSaleRequests.getEvent().getPersonContactEmail());
                contactPersonDTO.setPhone(msSaleRequests.getEvent().getPersonContactPhone());
                event.setContactPerson(contactPersonDTO);
            }
            fillCategoriesEventSaleRequest(event, msSaleRequests.getEvent());
            if (msSaleRequests.getEvent().getCurrencyId()!=null) {
                event.setCurrency(getCurrencyCode(msSaleRequests.getEvent().getCurrencyId(), currencies));
            }
            event.setTaxMode(TaxModeDTO.fromMs(msSaleRequests.getEvent().getTaxMode()));
            result.setEvent(event);
        }

        if (nonNull(msSaleRequests.getSubscriptionList())) {
            SubscriptionListSalesRequestDTO subscriptionList = new SubscriptionListSalesRequestDTO();
            subscriptionList.setEnable(msSaleRequests.getSubscriptionList().getEnableSubscriptionList());
            if (Boolean.TRUE.equals(msSaleRequests.getSubscriptionList().getEnableSubscriptionList())) {
                subscriptionList.setId(msSaleRequests.getSubscriptionList().getSubscriptionListId());
            }
            result.setSubscriptionList(subscriptionList);
        }

        return result;
    }

    private static void fillCategoriesEventSaleRequest(EventSaleRequestDetailDTO event, MsEventSaleRequestDTO msSaleRequestsEvent) {
        if (Objects.nonNull(msSaleRequestsEvent.getTaxonomy())) {
            event.setCategory(new CategoriesSaleRequestExtendedDTO());
            event.getCategory().setParent(convertToTaxonomySaleRequestDTO(msSaleRequestsEvent.getTaxonomy().getParentTaxonomy()));
            event.getCategory().setCustom(convertToTaxonomySaleRequestDTO(msSaleRequestsEvent.getTaxonomy().getCustomTaxonomy()));
            fillCategoryBase(event.getCategory(), msSaleRequestsEvent.getTaxonomy());
        }
    }

    private static void fillCategoryBase(BaseCategorySaleRequestDTO category, MsTaxonomySaleRequestDTO taxonomy) {
        category.setId(taxonomy.getId());
        category.setCode(taxonomy.getCode());
        category.setDescription(taxonomy.getDescription());
    }

    private static BaseCategorySaleRequestDTO convertToTaxonomySaleRequestDTO(MsTaxonomySaleRequestDTO taxonomy) {
        if (Objects.nonNull(taxonomy)) {
            BaseCategorySaleRequestDTO taxonomySaleRequestDTO = new BaseCategorySaleRequestDTO();
            taxonomySaleRequestDTO.setId(taxonomy.getId());
            taxonomySaleRequestDTO.setCode(taxonomy.getCode());
            taxonomySaleRequestDTO.setDescription(taxonomy.getDescription());
            return taxonomySaleRequestDTO;
        }
        return null;
    }

    private static BaseSaleRequestsDTO fromMsChannelObject(MsSaleRequestDTO msSaleRequests) {
        BaseSaleRequestsDTO baseSaleRequests = new BaseSaleRequestsDTO();

        baseSaleRequests.setId(msSaleRequests.getId());
        baseSaleRequests.setStatus(SaleRequestsStatus.fromMsChannelEnum(msSaleRequests.getStatus()));
        baseSaleRequests.setDate(msSaleRequests.getDate());

        baseSaleRequests.setChannel(channelConvert(msSaleRequests.getChannel()));

        if (nonNull(msSaleRequests.getEvent())) {
            EventSaleRequestDTO event = new EventSaleRequestDTO();
            event.setId(msSaleRequests.getEvent().getId());
            event.setName(msSaleRequests.getEvent().getName());
            event.setStartDate(msSaleRequests.getEvent().getStartDate());
            event.setEntity(entityConvert(msSaleRequests.getEvent().getEntity()));
            event.setVenues(convert(msSaleRequests.getEvent().getVenues()));

            if (nonNull(msSaleRequests.getEvent().getEventType())) {
                event.setEventType(SaleRequestEventType.valueOf(msSaleRequests.getEvent().getEventType().name()));
            }

            baseSaleRequests.setEvent(event);
        }

        return baseSaleRequests;
    }

    private static List<VenueSaleRequestDTO> convert(List<MsVenueSaleRequestDTO> msVenues) {
        if (CollectionUtils.isEmpty(msVenues)) {
            return null;
        }

        return msVenues.stream()
                .map(SaleRequestsConverter::convert)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static VenueSaleRequestDTO convert(MsVenueSaleRequestDTO msVenue) {
        if (isNull(msVenue)) {
            return null;
        }

        VenueSaleRequestDTO venue = new VenueSaleRequestDTO();
        venue.setId(msVenue.getId());
        venue.setName(msVenue.getName());
        if (nonNull(msVenue.getLocation())) {
            LocationSaleRequestDTO location = new LocationSaleRequestDTO();
            location.setCity(msVenue.getLocation().getCity());
            venue.setLocation(location);
        }

        return venue;
    }

    private static ChannelSaleRequestDTO channelConvert(MsChannelSaleRequestDTO channel) {
        if (nonNull(channel)) {
            ChannelSaleRequestDTO result = new ChannelSaleRequestDTO();
            result.setId(channel.getId());
            result.setName(channel.getName());
            if (channel.getSubtype() != null) {
                result.setType(ChannelSubtype.getById(channel.getSubtype().getIdSubtipo()));
            }
            result.setEntity(entityConvert(channel.getEntity()));
            return result;
        }
        return null;
    }

    private static ChannelSaleRequestDetailDTO channelDetailConvert(MsChannelSaleRequestDTO channel) {
        if (nonNull(channel)) {
            ChannelSaleRequestDetailDTO result = new ChannelSaleRequestDetailDTO();
            result.setId(channel.getId());
            result.setName(channel.getName());
            result.setType(ChannelSubtype.getById(channel.getSubtype().getIdSubtipo()));
            result.setEntity(entityConvert(channel.getEntity()));
            fillCategoriesChannelSaleRequest(result, channel);
            return result;
        }
        return null;
    }

    private static void fillCategoriesChannelSaleRequest(ChannelSaleRequestDetailDTO result, MsChannelSaleRequestDTO channel) {
        if (Objects.nonNull(channel.getTaxonomy())) {
            result.setCategory(new CategoriesSaleRequestDTO());
            result.getCategory().setCustom(convertToTaxonomySaleRequestDTO(channel.getTaxonomy().getCustomTaxonomy()));
            result.getCategory().setParent(convertToTaxonomySaleRequestDTO(channel.getTaxonomy().getParentTaxonomy()));
        }
    }

    private static IdNameDTO entityConvert(MsEntitySaleRequestDTO entity) {
        if (nonNull(entity)) {
            IdNameDTO result = new IdNameDTO();
            result.setId(entity.getId());
            result.setName(entity.getName());
            return result;
        }
        return null;
    }

    private static String getCurrencyCode(Long currencyId, List<Currency> currencies) {
        String currencyCode = currencies.stream()
                .filter(currency -> currency.getId().equals(currencyId))
                .map(Currency::getCode)
                .findFirst().orElse(null);
        if (currencyCode == null) {
           throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_FOUND);
        }
        return currencyCode;
    }
}

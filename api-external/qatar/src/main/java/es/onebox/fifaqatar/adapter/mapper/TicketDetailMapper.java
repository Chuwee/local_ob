package es.onebox.fifaqatar.adapter.mapper;

import es.onebox.common.datasources.common.dto.Charges;
import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.event.dto.response.catalog.session.SessionCatalog;
import es.onebox.common.datasources.ms.venue.dto.VenueDTO;
import es.onebox.common.datasources.orderitems.dto.OrderItem;
import es.onebox.common.datasources.orderitems.dto.validation.ItemTicketValidation;
import es.onebox.common.datasources.orderitems.enums.OrderItemRelatedProductState;
import es.onebox.common.datasources.orderitems.enums.TicketAllocationType;
import es.onebox.common.datasources.orders.dto.ItemPrice;
import es.onebox.common.datasources.orders.dto.SalesPrice;
import es.onebox.common.utils.GeneratorUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.TicketCodeValidity;
import es.onebox.fifaqatar.config.config.DeliverySettings;
import es.onebox.fifaqatar.config.translation.TranslationUtils;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.PlaceCity;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.ReleaseConditionAction;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.TicketCode;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.TicketManagement;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.TicketPlace;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.TicketReleaseCondition;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.TicketSeatingSummary;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.fifaqatar.config.translation.TranslationKey.*;

public interface TicketDetailMapper {

    default BigDecimal mapTicketPrice(List<OrderItem> items) {
        var finalPrices = items.stream().map(item -> item.getPrice().getFinalAmount()).collect(Collectors.toList());
        return NumberUtils.sum(finalPrices.toArray(BigDecimal[]::new));
    }

    default BigDecimal mapDiscountApplied(List<OrderItem> items) {
        var promotions = items.stream().map(item -> {
            SalesPrice sales = item.getPrice().getSales();
            if (sales != null) {
                return NumberUtils.sum(sales.getAutomatic(), sales.getDiscount(), sales.getPromotion());
            }
            return BigDecimal.ZERO;
        }).collect(Collectors.toList());
        return NumberUtils.sum(promotions.toArray(BigDecimal[]::new));
    }

    default BigDecimal mapSurchargeApplied(List<ItemPrice> items) {
        var surcharges = items.stream().map(item -> {
            Charges charges = item.getCharges();
            if (charges != null) {
                return NumberUtils.sum(charges.getChannel(), charges.getPromoter());
            }
            return BigDecimal.ZERO;
        }).collect(Collectors.toList());
        return NumberUtils.sum(surcharges.toArray(BigDecimal[]::new));
    }

    default List<TicketCode> mapCodes(List<OrderItem> items, MapperContext mapperContext) {
        return items.stream()
                .filter(item -> !OrderItemRelatedProductState.SEC_MKT_SOLD.equals(item.getRelatedProductState()))
                .map(item -> {
                    var barcode = item.getTicket().getBarcode().getCode();
                    TicketCode ticketCode = new TicketCode();
                    ticketCode.setId(1);
                    ticketCode.setCode(barcode);
                    ticketCode.setImage(buildBarcodeUrl(mapperContext, barcode));
                    ticketCode.setValidity(buildCodeValidity(item));

                    return ticketCode;
                }).collect(Collectors.toList());
    }

    default TicketCodeValidity buildCodeValidity(OrderItem item) {
        if (item.getTicket() != null && CollectionUtils.isEmpty(item.getTicket().getValidations())) {
            return null;
        }
        ItemTicketValidation lastValidation = item.getTicket().getValidations().stream().max(Comparator.comparing(ItemTicketValidation::getDate)).orElse(null);
        if (lastValidation != null && "VALIDATED".equals(lastValidation.getStatus())) {
            TicketCodeValidity codeValidity = new TicketCodeValidity();
            codeValidity.setRemainingUses(0);
            return codeValidity;
        } else {
            return null;
        }
    }

    default List<TicketPlace> mapPlaces(VenueDTO venue, String currency) {
        var place = new TicketPlace();
        place.setId(venue.getId().intValue());
        place.setName(venue.getName());
        place.setCurrency(currency);
        place.setLocale("en");
        if (venue.getCoordinates() != null) {
            place.setLatitude(venue.getCoordinates().getLatitude());
            place.setLongitude(venue.getCoordinates().getLongitude());
        }
        place.setAddress(venue.getAddress());
        place.setHidden(Boolean.FALSE);
        place.setMetroStations(new ArrayList());
        var city = new PlaceCity();
        city.setId(12);
        city.setName(venue.getCity());
        city.setCountry(venue.getCountry().getCode());
        city.setCode("");
        if (venue.getCoordinates() != null) {
            city.setLatitude(venue.getCoordinates().getLatitude());
            city.setLongitude(venue.getCoordinates().getLongitude());
            city.setLocale("en");
        }
        place.setCity(city);

        return List.of(place);
    }

    default TicketManagement mapManagement(MapperContext mapperContext, Long sessionId, String orderCode) {
        TicketManagement management = new TicketManagement();
        management.setUrl(buildAccountTicketsUrl(mapperContext, sessionId, orderCode));
        management.setSubtitle(TranslationUtils.getText(MANAGEMENT_SUBTITLE, mapperContext.getCurrentLang(), mapperContext.getDictionary()));

        return management;
    }

    default TicketManagement mapTransferManagement(MapperContext mapperContext, Long sessionId, String orderCode) {
        TicketManagement management = new TicketManagement();
        management.setUrl(buildAccountTicketsTransferUrl(mapperContext, sessionId, orderCode));
        management.setSubtitle(TranslationUtils.getText(MANAGEMENT_SUBTITLE, mapperContext.getCurrentLang(), mapperContext.getDictionary()));

        return management;
    }

    default TicketManagement maSecMktManagement(MapperContext mapperContext) {
        if (Boolean.TRUE.equals(mapperContext.getMainConfig().getSecMktEnabled()) && mapperContext.getSessionSecMktConfig() != null && Boolean.TRUE.equals(mapperContext.getSessionSecMktConfig().getEnabled())) {
            TicketManagement secMktManagement = new TicketManagement();
            secMktManagement.setUrl(buildAccountSecMktUrl(mapperContext));

            secMktManagement.setTitle(TranslationUtils.getText(MANAGEMENT_SECMKT_TITLE, mapperContext.getCurrentLang(), mapperContext.getDictionary()));
            secMktManagement.setSubtitle(TranslationUtils.getText(MANAGEMENT_SECMKT_SUBTITLE, mapperContext.getCurrentLang(), mapperContext.getDictionary()));

            return secMktManagement;
        }
        return null;
    }

    default TicketReleaseCondition mapReleaseCondition(MapperContext mapperContext) {
        if (mustFillInfo(mapperContext.getCurrentCustomer())) {
            TicketReleaseCondition fillInfoCondition = new TicketReleaseCondition();
            String requiredInfoText = TranslationUtils.getText(RELEASE_REQUIRED_INFO_MESSAGE, mapperContext.getCurrentLang(), mapperContext.getDictionary());
            fillInfoCondition.setMessage(requiredInfoText);
            ReleaseConditionAction action = new ReleaseConditionAction();
            action.setCta(requiredInfoText);
            action.setEnabled(Boolean.TRUE);
            action.setUrl(buildProfileUrl(mapperContext));
            fillInfoCondition.setAction(action);

            return fillInfoCondition;
        } else if (isDeliveryActive(mapperContext)) {
            TicketReleaseCondition delayCondition = new TicketReleaseCondition();
            String requiredInfoText = TranslationUtils.getText(RELEASE_REQUIRED_DELIVERY_MESSAGE, mapperContext.getCurrentLang(), mapperContext.getDictionary());
            delayCondition.setMessage(requiredInfoText);

            return delayCondition;
        }
        return null;
    }

    default TicketReleaseCondition mapMaxTicketCondition(MapperContext mapperContext, Long sessionId, String orderCode) {
        String message = TranslationUtils.getText(RELEASE_MAX_TICKETS_MESSAGE, mapperContext.getCurrentLang(), mapperContext.getDictionary());
        String cta = TranslationUtils.getText(RELEASE_MAX_TICKETS_CTA, mapperContext.getCurrentLang(), mapperContext.getDictionary());

        TicketReleaseCondition condition = new TicketReleaseCondition();
        condition.setMessage(message);
        ReleaseConditionAction action = new ReleaseConditionAction();
        action.setCta(cta);
        action.setEnabled(Boolean.TRUE);
        action.setUrl(buildAccountTicketsUrl(mapperContext, sessionId, orderCode));
        condition.setAction(action);
        return condition;
    }

    default boolean isDeliveryActive(MapperContext mapperContext) {
        var deliverySettings = mapperContext.getMainConfig().getDeliverySettings();
        if (deliverySettings.getEnabled()) {
            Integer delayedHours = deliverySettings.getHoursBefore();

            var session = mapperContext.getSessionCatalog();

            if (MapperUtils.isSessionFinished(session)) {
                return false;
            }

            if (MapperUtils.isSessionInProgress(session)) {
                return false;
            }

            ZonedDateTime startDate = Instant.ofEpochMilli(session.getBeginSessionDate()).atZone(ZoneId.of("UTC"));
            Duration duration = Duration.between(ZonedDateTime.now(startDate.getZone()), startDate).abs();

            return duration.toHours() >= delayedHours.intValue();
        } else {
            return false;
        }

    }

    default boolean isSessionFinished(SessionCatalog sessionCatalog) {
        return MapperUtils.isSessionFinished(sessionCatalog);
    }

    default boolean mustFillInfo(Customer customer) {
        //TODO check all required
        HashMap<String, Object> data = customer.getAdditionalProperties();
        if (MapUtils.isEmpty(data)) {
            return true;
        }
        var nationality = data.get("nationality");
        var yourTeam = data.get("your_team") == null ? data.get("yourTeam") : data.get("your_team") == null;

        return nationality == null || yourTeam == null;
    }

    default TicketSeatingSummary mapSeatingSummary(List<OrderItem> items, MapperContext mapperContext) {
        TicketSeatingSummary summary = new TicketSeatingSummary();
        summary.setTitle("Seats");
        StringBuilder html = new StringBuilder();
        for (OrderItem item : items) {
            var allocation = item.getTicket().getAllocation();

            var gateName = allocation.getAccess() != null && allocation.getAccess().getName() != null ? allocation.getAccess().getName() : "-";
            var rowName = allocation.getRow() != null && allocation.getRow().getName() != null ? allocation.getRow().getName() : "-";
            var sectorName = allocation.getSector() != null && allocation.getSector().getName() != null ? allocation.getSector().getName() : "-";
            var seatName = allocation.getSeat() != null && allocation.getSeat().getName() != null ? allocation.getSeat().getName() : "-";

            String text = TranslationUtils.translateSeatingSummary(mapperContext.getCurrentLang(), mapperContext.getDictionary(),
                    gateName,
                    sectorName,
                    rowName,
                    seatName);

            String line = text + "<br>";
            html.append(line);

        }

        String extraInfo = mapExtraInfo(items, mapperContext);
        if (StringUtils.isNotBlank(extraInfo)) {
            html.append(extraInfo);
        }

        summary.setHtmlAssignations(html.toString());

        return summary;
    }

    default String mapInstructions(SessionCatalog sessionCatalog, String lang, String defaultLang) {
        //Dirty hack to use backup locales for instructions
        if (lang.startsWith("en")) {
            lang = "en-US";
        } else if (lang.startsWith("ar")) {
            lang = "ar-AE";
        }

        return MapperUtils.findCommElement("TEXT_SUMMARY_WEB", sessionCatalog.getCommunicationElements(), lang, defaultLang);
    }

    default String mapExtraInfo(List<OrderItem> items, MapperContext mapperContext) {
        OrderItem itemWithDisability = items.stream().filter(item -> {
            if (item.getTicket() != null && item.getTicket().getAllocation() != null) {
                String accessibility = item.getTicket().getAllocation().getAccessibility();
                return StringUtils.isNotBlank(accessibility) && "DISABILITY".equals(accessibility);
            }
            return false;
        }).findFirst().orElse(null);

        if (itemWithDisability != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<p>");
            stringBuilder.append(TranslationUtils.getText(DISABILITY_MESSAGE, mapperContext.getCurrentLang(), mapperContext.getDictionary()));
            stringBuilder.append("</p>");

            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    default String buildBarcodeUrl(MapperContext mapperContext, String barcode) {
        var signature = GeneratorUtils.getHashSHA256(barcode + mapperContext.getBarcodeSigningKey());
        return UriComponentsBuilder.fromHttpUrl(mapperContext.getBarcodeUrl())
                .pathSegment(barcode)
                .queryParam("signature", signature)
                .build().toString();
    }

    default String buildAccountTicketsUrl(MapperContext mapperContext, Long sessionId, String orderCode) {
        return UriComponentsBuilder.fromHttpUrl(mapperContext.getAccountTicketsUrl())
                .pathSegment(String.valueOf(sessionId))
                .queryParam("hl", mapperContext.getCurrentLang())
                .queryParam("orderCode", orderCode)
                .queryParam("token", mapperContext.getCustomerAccessToken())
                .build().toString();
    }

    default String buildAccountTicketsTransferUrl(MapperContext mapperContext, Long sessionId, String orderCode) {
        return UriComponentsBuilder.fromHttpUrl(mapperContext.getAccountTicketsTransferUrl())
                .pathSegment(String.valueOf(sessionId))
                .queryParam("hl", mapperContext.getCurrentLang())
                .queryParam("orderCode", orderCode)
                .queryParam("token", mapperContext.getCustomerAccessToken())
                .build().toString();
    }

    default String buildAccountSecMktUrl(MapperContext mapperContext) {
        return UriComponentsBuilder.fromHttpUrl(mapperContext.getAccountSecMktUrl())
                .queryParam("hl", mapperContext.getCurrentLang())
                .queryParam("token", mapperContext.getCustomerAccessToken())
                .build().toString();
    }

    default String buildProfileUrl(MapperContext mapperContext) {
        return UriComponentsBuilder.fromHttpUrl(mapperContext.getAccountProfileUrl())
                .queryParam("hl", mapperContext.getCurrentLang())
                .queryParam("token", mapperContext.getCustomerAccessToken())
                .build().toString();
    }
}

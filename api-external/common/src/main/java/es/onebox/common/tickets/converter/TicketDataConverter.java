package es.onebox.common.tickets.converter;

import es.onebox.common.datasources.common.enums.SessionType;
import es.onebox.common.datasources.ms.entity.dto.Language;
import es.onebox.common.datasources.ms.entity.dto.Producer;
import es.onebox.common.datasources.ms.event.enums.TicketCommunicationElementTag;
import es.onebox.common.datasources.ms.order.dto.ComElementDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.OrderPromotionDTO;
import es.onebox.common.datasources.ms.order.dto.OrderTicketDataDTO;
import es.onebox.common.datasources.ms.order.dto.SeatType;
import es.onebox.common.datasources.ms.venue.dto.PriceTypeTicketCommunicationElement;
import es.onebox.common.tickets.TicketData;
import es.onebox.common.tickets.dto.SeasonDate;
import es.onebox.common.tickets.dto.SessionData;
import es.onebox.common.tickets.enums.CurrencySign;
import es.onebox.common.tickets.enums.Visibility;
import es.onebox.common.utils.MemberValidationUtils;
import es.onebox.core.utils.common.EncryptionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class TicketDataConverter extends TicketConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter SEASON_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static final String EUSKERA_LOCALE = "es_EU";

    public static void fillAttendantInfo(OrderProductDTO orderProductDTO, TicketData ticketData) {
        if (nonNull(orderProductDTO.getAttendant())) {
            StringBuilder attendantName = new StringBuilder();
            if (nonNull(orderProductDTO.getAttendant().getFields().get(ATTENDANT_NAME))) {
                attendantName.append(orderProductDTO.getAttendant().getFields().get(ATTENDANT_NAME));
                ticketData.setAttendantName(attendantName.toString());
            }
            if (nonNull(orderProductDTO.getAttendant().getFields().get(ATTENDANT_SURNAME))) {
                attendantName.append(" ").append(orderProductDTO.getAttendant().getFields().get(ATTENDANT_SURNAME));
                ticketData.setAttendantName(attendantName.toString());
            }
            if (nonNull(orderProductDTO.getAttendant().getFields().get(ATTENDANT_ID_NUMBER))) {
                ticketData.setAttendantId(orderProductDTO.getAttendant().getFields().get(ATTENDANT_ID_NUMBER));
            }
            if (nonNull(orderProductDTO.getAttendant().getFields().get(ATTENDANT_MAIL))) {
                ticketData.setAttendantEmail(orderProductDTO.getAttendant().getFields().get(ATTENDANT_MAIL));
            }
        }
    }

    public static void fillAllocationData(OrderProductDTO orderProductDTO, TicketData ticketData, SessionData sessionData, EncryptionUtils encryptionUtils) {
        ticketData.setVenue(sessionData.getVenue().getName());
        ticketData.setVenueAddress(sessionData.getVenue().getAddress() +
                " - " + sessionData.getVenue().getCity());

        OrderTicketDataDTO productTicketData = orderProductDTO.getTicketData();
        String barcode = StringUtils.isNotBlank(productTicketData.getExternalBarcode()) ? productTicketData.getExternalBarcode() : productTicketData.getBarcode();
        String decodedBarcode = encryptionUtils.decode(barcode);
        ticketData.setBarcode(decodedBarcode);
        if (productTicketData.getAccessName() != null) {
            ticketData.setGate(productTicketData.getAccessName());
        }
        if (es.onebox.common.datasources.ms.order.dto.EventType.ACTIVITY.equals(orderProductDTO.getEventType())
                || es.onebox.common.datasources.ms.order.dto.EventType.THEME_PARK.equals(orderProductDTO.getEventType())) {
            ticketData.setZone(productTicketData.getPriceZoneName());
        } else if (nonNull(productTicketData.getSectorName())) {
            ticketData.setZone(productTicketData.getPriceZoneName());
            ticketData.setSector(productTicketData.getSectorName());

            if (SeatType.NUMBERED.equals(productTicketData.getSeatType())) {
                ticketData.setRow(productTicketData.getRowName());
                ticketData.setSeat(productTicketData.getNumSeat());
            } else if (nonNull(productTicketData.getSeatType())) {
                String sectorName = String.format("%s - %s", productTicketData.getSectorName(),
                        productTicketData.getNotNumberedAreaName());
                ticketData.setSector(sectorName);
            }
        }

        if (productTicketData.getVisibility() != null) {
            ticketData.setVisibility(Visibility.valueOf(productTicketData.getVisibility().name()).getType());
        }
    }

    public static void fillPricesData(OrderProductDTO orderProductDTO, TicketData ticketData, Language language, String currency) {
        BigDecimal fees = orderProductDTO.getPrice().getCharges().getChannel() != null ? BigDecimal.valueOf(orderProductDTO.getPrice().getCharges().getChannel()) : BigDecimal.ZERO;
        if (orderProductDTO.getPrice().getCharges().getPromoter() != null) {
            fees = fees.add(BigDecimal.valueOf(orderProductDTO.getPrice().getCharges().getPromoter()));
        }

        CurrencySign currencySign;
        if (orderProductDTO.getPrice().getCurrency() == null) {
            currencySign = CurrencySign.valueOf(currency);

        } else {
            currencySign = CurrencySign.valueOf(orderProductDTO.getPrice().getCurrency());
        }
        ticketData.setAdministrationFeesPrice(fees + " " + currencySign.getSign());
        ticketData.setItemPrice(calculateBasePrice(orderProductDTO).toString() + " " + currencySign.getSign());
        ticketData.setTotalPrice(orderProductDTO.getPrice().getFinalPrice().toString() + " " + currencySign.getSign());

        OrderPromotionDTO promotions = orderProductDTO.getPromotions();
        if (nonNull(promotions)) {
            if (nonNull(promotions.getPromotion()) && BooleanUtils.isTrue(promotions.getPromotion().getSelfManaged())) {
                ticketData.setPartnerId(MemberValidationUtils.getPromotionPartnerId(promotions.getPromotion().getCollectiveKey()));
            }
            if (nonNull(promotions.getDiscount()) && BooleanUtils.isTrue(promotions.getDiscount().getSelfManaged())) {
                ticketData.setPartnerId(MemberValidationUtils.getPromotionPartnerId(promotions.getDiscount().getCollectiveKey()));
            }

            if (nonNull(promotions.getDiscount()) && nonNull(promotions.getDiscount().getComElements())) {
                Optional<ComElementDTO> discountName = promotions.getDiscount().getComElements().stream().filter(di -> di.getLanguageCode().equals(language.getCode())).findFirst();
                ticketData.setDiscount(discountName.map(ComElementDTO::getValue).orElse(null));
                if (promotions.getDiscount() != null
                        && promotions.getDiscount().getCollectiveKey() != null) {
                    ticketData.setMembershipNumber(MemberValidationUtils.getPromotionPartnerId(promotions.getDiscount().getCollectiveKey()));
                }
            }
            if (nonNull(promotions.getPromotion()) && nonNull(promotions.getPromotion().getComElements())) {
                Optional<ComElementDTO> promotionName = promotions.getPromotion().getComElements().stream().filter(di -> di.getLanguageCode().equals(language.getCode())).findFirst();
                ticketData.setPromotion(promotionName.map(ComElementDTO::getValue).orElse(null));
                if (promotions.getPromotion() != null
                        && promotions.getPromotion().getCollectiveKey() != null) {
                    ticketData.setMembershipNumber(MemberValidationUtils.getPromotionPartnerId(promotions.getPromotion().getCollectiveKey()));
                }
            }
            if (nonNull(promotions.getAutomatic()) && nonNull(promotions.getAutomatic().getComElements())) {
                Optional<ComElementDTO> automaticName = promotions.getAutomatic().getComElements().stream().filter(di -> di.getLanguageCode().equals(language.getCode())).findFirst();
                ticketData.setAutomatic(automaticName.map(ComElementDTO::getValue).orElse(null));
            }
        }

    }


    public static void fillDates(OrderProductDTO orderProductDTO, SessionData sessionData, TicketData ticketData, Language language) {
        ZonedDateTime sessionDate = null;
        if (SessionType.SESSION.equals(sessionData.getSessionType())) {
            if (nonNull(sessionData.getSessionDate())
                    && nonNull(sessionData.getSessionDate().getStart())) {
                sessionDate = ZonedDateTime.ofInstant(
                        sessionData.getSessionDate().getStart().toInstant(),
                        ZoneId.of(sessionData.getVenue().getTimezone().getOlsonId()));
                ticketData.setSessionDate(sessionDate.format(DATE_TIME_FORMATTER));
            }
        } else {
            if (nonNull(sessionData.getSeasonsDate())) {
                SeasonDate seasonDate = sessionData.getSeasonsDate();
                if (seasonDate.getStart().toInstant().compareTo(seasonDate.getEnd().toInstant()) == 0) {
                    sessionDate = ZonedDateTime.ofInstant(seasonDate.getStart().toInstant(),
                            ZoneId.of(sessionData.getVenue().getTimezone().getOlsonId()));
                    ticketData.setSessionDate(sessionDate.format(SEASON_DATE_FORMATTER));
                } else {
                    sessionDate = ZonedDateTime.ofInstant(seasonDate.getStart().toInstant(),
                            ZoneId.of(sessionData.getVenue().getTimezone().getOlsonId()));
                    ticketData.setSessionDate(sessionDate.format(SEASON_DATE_FORMATTER) + " - " +
                            ZonedDateTime.ofInstant(seasonDate.getEnd().toInstant(),
                                    ZoneId.of(sessionData.getVenue().getTimezone().getOlsonId())).format(SEASON_DATE_FORMATTER));
                }
            }
        }
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of(sessionData.getVenue().getTimezone().getOlsonId()));
        ticketData.setPrintDate(zdt.format(DATE_TIME_FORMATTER));

        if (nonNull(sessionDate)) {
            ticketData.setHoraTexto(getHoraTexto(sessionDate));
            ticketData.setDiaSemanaTexto(getWeekDayName(sessionDate, language.getCode()));
            ticketData.setDiaMes(String.valueOf(sessionDate.getDayOfMonth()));
            ticketData.setMesTexto(getMonthName(sessionDate, language.getCode()));
        }
        ticketData.setZonaPrecio(orderProductDTO.getTicketData().getPriceZoneName());
    }


    public static void fillPromoterData(SessionData sessionData, TicketData ticketData) {
        if (nonNull(sessionData) && nonNull(sessionData.getPromoter())) {
            Producer producer = sessionData.getPromoter();
            if (nonNull(producer)) {
                StringBuilder promoterData = new StringBuilder();
                promoterData.append(producer.getSocialReason()).append(" - ").append("CIF: ").append(producer.getNif());
                if (nonNull(producer.getAddress())) {
                    promoterData.append(" - ").append(producer.getAddress());
                }
                if (nonNull(producer.getPostalCode())) {
                    promoterData.append(", ").append(producer.getPostalCode());
                }
                if (nonNull(producer.getCity())) {
                    promoterData.append(", ").append(producer.getCity());
                }
                ticketData.setPromotorData(promoterData.toString());
            }
        }
    }

    public static void fillTicketCommunicationElements(SessionData sessionData,
                                                       List<PriceTypeTicketCommunicationElement> priceTypeCommElements,
                                                       TicketData ticketData) {

        if (!CollectionUtils.isEmpty(sessionData.getEventCommElement())) {
            sessionData.getEventCommElement().forEach(ce -> {
                if (TicketCommunicationElementTag.TITLE.equals(ce.getTag())) {
                    ticketData.setTitle(ce.getValue());
                }
                if (TicketCommunicationElementTag.SUBTITLE.equals(ce.getTag())) {
                    ticketData.setSubtitle(ce.getValue());
                }
                if (TicketCommunicationElementTag.ADDITIONAL_DATA.equals(ce.getTag())) {
                    ticketData.setAdditionalData(ce.getValue());
                }
                if (TicketCommunicationElementTag.BODY.equals(ce.getTag())) {
                    ticketData.setPathImageEvent(ce.getValue());
                }
            });
        }
        if (!CollectionUtils.isEmpty(sessionData.getSessionCommElement())) {
            sessionData.getSessionCommElement().forEach(ce -> {
                if (TicketCommunicationElementTag.TITLE.equals(ce.getTag())) {
                    ticketData.setTitle(ce.getValue());
                }
                if (TicketCommunicationElementTag.BODY.equals(ce.getTag())) {
                    ticketData.setPathImageEvent(ce.getValue());
                }
            });
        }
        if (!CollectionUtils.isEmpty(priceTypeCommElements)) {
            priceTypeCommElements.forEach(ce -> {
                if (TicketCommunicationElementTag.TITLE.name().equals(ce.getType())) {
                    ticketData.setTitle(ce.getValue());
                }
                if (TicketCommunicationElementTag.SUBTITLE.name().equals(ce.getType())) {
                    ticketData.setSubtitle(ce.getValue());
                }
                if (TicketCommunicationElementTag.ADDITIONAL_DATA.name().equals(ce.getType())) {
                    ticketData.setAdditionalData(ce.getValue());
                }
                if (TicketCommunicationElementTag.BODY.name().equals(ce.getType())) {
                    ticketData.setPathImageEvent(ce.getValue());
                }
            });
        }

        if (isNull(ticketData.getTitle())) {
            ticketData.setTitle(sessionData.getSessionName());
        }
    }


    private static BigDecimal calculateBasePrice(OrderProductDTO orderProductDTO) {
        BigDecimal basePrice = BigDecimal.ZERO;
        if (nonNull(orderProductDTO.getPrice().getBasePrice())) {
            basePrice = basePrice.add(BigDecimal.valueOf(orderProductDTO.getPrice().getBasePrice()));
        }
        if (nonNull(orderProductDTO.getPromotions())) {
            if (nonNull(orderProductDTO.getPrice().getPromotions().getAutomatic())) {
                basePrice = basePrice.subtract(BigDecimal.valueOf(orderProductDTO.getPrice().getPromotions().getAutomatic()));
            }
            if (nonNull(orderProductDTO.getPrice().getPromotions().getPromotion())) {
                basePrice = basePrice.subtract(BigDecimal.valueOf(orderProductDTO.getPrice().getPromotions().getPromotion()));
            }
            if (nonNull(orderProductDTO.getPrice().getPromotions().getDiscount())) {
                basePrice = basePrice.subtract(BigDecimal.valueOf(orderProductDTO.getPrice().getPromotions().getDiscount()));
            }
        }

        return basePrice;
    }

    private static String getHoraTexto(ZonedDateTime sessionDate) {
        return sessionDate != null ? sessionDate.format(HOUR_FORMATTER) : "";
    }


    //TODO: This code is duplicated in ms-ticket. Create a new static class in core
    public static String getWeekDayName(ZonedDateTime date, String language) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (EUSKERA_LOCALE.equals(language)) {
            return switch (dayOfWeek) {
                case MONDAY -> "astelehena";
                case TUESDAY -> "asteartea";
                case WEDNESDAY -> "asteazkena";
                case THURSDAY -> "osteguna";
                case FRIDAY -> "ostirala";
                case SATURDAY -> "larunbata";
                case SUNDAY -> "igandea";
            };
        }
        String localeLang = language.substring(0, language.indexOf("_"));
        String localeCountry = language.substring(language.indexOf("_") + 1);
        return dayOfWeek.getDisplayName(TextStyle.FULL, new Locale(localeLang, localeCountry));
    }

    public static String getMonthName(ZonedDateTime date, String language) {
        Month month = date.getMonth();
        if (EUSKERA_LOCALE.equals(language)) {
            return switch (month) {
                case JANUARY -> "URTARRILA";
                case FEBRUARY -> "OTSAILA";
                case MARCH -> "MARTXOA";
                case APRIL -> "APIRILA";
                case MAY -> "MAIATZA";
                case JUNE -> "EKAINAK";
                case JULY -> "UZTAILA";
                case AUGUST -> "ABUZTUA";
                case SEPTEMBER -> "IRAILA";
                case OCTOBER -> "URRIA";
                case NOVEMBER -> "AZAROA";
                case DECEMBER -> "ABENDUA";
            };
        }
        String localeLang = language.substring(0, language.indexOf("_"));
        String localeCountry = language.substring(language.indexOf("_") + 1);
        return month.getDisplayName(TextStyle.FULL, new Locale(localeLang, localeCountry));
    }

}

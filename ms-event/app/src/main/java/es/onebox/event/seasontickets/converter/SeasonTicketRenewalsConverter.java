package es.onebox.event.seasontickets.converter;

import es.onebox.dal.dto.couch.enums.SeasonProductReleaseStatus;
import es.onebox.dal.dto.couch.enums.SeatType;
import es.onebox.dal.dto.couch.order.OrderProductDTO;
import es.onebox.dal.dto.couch.order.OrderSeasonSessionDTO;
import es.onebox.dal.dto.couch.order.OrderTicketDataDTO;
import es.onebox.event.datasources.ms.client.dto.CustomerExternalProduct;
import es.onebox.event.datasources.ms.client.dto.ExternalSeatType;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.RenewalExternalOriginSeat;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.RenewalSeasonTicketOriginSeat;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.RenewalSeasonTicketRenewalSeat;
import es.onebox.event.seasontickets.dao.couch.RenewalType;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketExternalSeat;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfig;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalCouchDocument;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalProduct;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalStatus;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketSeat;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketSeatType;
import es.onebox.event.seasontickets.dto.SearchSeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.seasontickets.dto.renewals.RenewalCandidateReasonSeasonTicketDTO;
import es.onebox.event.seasontickets.dto.renewals.RenewalCandidateSeasonTicketDTO;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalType;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalsConfigDTO;
import es.onebox.event.seasontickets.dto.renewals.UpdateSeasonTicketRenewalsConfigDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class SeasonTicketRenewalsConverter {

    private SeasonTicketRenewalsConverter(){ throw new UnsupportedOperationException("Cannot instantiate utilities class");}

    private static final String IBAN = "iban";
    private static final String BIC = "bic";
    private static final String AUTO_RENEWAL = "autoRenewal";

    public static RenewalCandidateSeasonTicketDTO getRenewalCandidateSeasonTicketDTO(SeasonTicketDTO seasonTicketDTO, SearchSeasonTicketDTO seasonTicket) {
        RenewalCandidateSeasonTicketDTO renewalCandidateSeasonTicketDTO = new RenewalCandidateSeasonTicketDTO();
        renewalCandidateSeasonTicketDTO.setId(seasonTicket.getId());
        renewalCandidateSeasonTicketDTO.setName(seasonTicket.getName());

        boolean isSeasonTicketOnSale = SeasonTicketStatusDTO.READY.equals(seasonTicket.getStatus());
        boolean isEqualMemberMandatory = seasonTicketDTO.getMemberMandatory().equals(seasonTicket.getMemberMandatory());
        if(!isSeasonTicketOnSale && isEqualMemberMandatory) {
            renewalCandidateSeasonTicketDTO.setCompatible(TRUE);
        } else {
            renewalCandidateSeasonTicketDTO.setCompatible(FALSE);
            List<RenewalCandidateReasonSeasonTicketDTO> reasons = new ArrayList<>();
            if(isSeasonTicketOnSale) {
                reasons.add(RenewalCandidateReasonSeasonTicketDTO.SEASON_TICKET_ON_SALE);
            }
            if(!isEqualMemberMandatory) {
                reasons.add(RenewalCandidateReasonSeasonTicketDTO.SEASON_TICKET_WITH_DIFFERENT_MEMBER_MANDATORY);
            }
            renewalCandidateSeasonTicketDTO.setReasons(reasons);
        }

        return renewalCandidateSeasonTicketDTO;
    }

    public static SeasonTicketRenewalCouchDocument createRenewalCouchDocument(Long originSeasonTicketId,
                                                                              Long renewalSeasonTicketId,
                                                                              RenewalSeasonTicketRenewalSeat renewalSeat,
                                                                              RenewalSeasonTicketOriginSeat originSeat,
                                                                              RenewalExternalOriginSeat externalOriginSeat,
                                                                              Map<Long, Long> relatedRatesMap,
                                                                              Boolean externalOrigin) {
        SeasonTicketRenewalCouchDocument seasonTicketRenewalCouchDocument = new SeasonTicketRenewalCouchDocument();

        if(externalOrigin) {
            seasonTicketRenewalCouchDocument.setUserId(externalOriginSeat.getUserId());
        } else {
            seasonTicketRenewalCouchDocument.setUserId(originSeat.getUserId());
        }

        Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap = new HashMap<>();
        SeasonTicketRenewalProduct seasonTicketRenewalProduct = createRenewalProduct(originSeasonTicketId,
                renewalSeat, originSeat, externalOriginSeat, relatedRatesMap, externalOrigin);
        List<SeasonTicketRenewalProduct> renewalProductList = new ArrayList<>();
        renewalProductList.add(seasonTicketRenewalProduct);
        seasonTicketProductMap.put(renewalSeasonTicketId, renewalProductList);
        seasonTicketRenewalCouchDocument.setSeasonTicketProductMap(seasonTicketProductMap);

        return seasonTicketRenewalCouchDocument;
    }

    public static SeasonTicketRenewalProduct createRenewalProduct(Long originSeasonTicketId,
                                                                  RenewalSeasonTicketRenewalSeat renewalSeat,
                                                                  RenewalSeasonTicketOriginSeat originSeat,
                                                                  RenewalExternalOriginSeat externalOriginSeat,
                                                                  Map<Long, Long> relatedRatesMap,
                                                                  Boolean externalOrigin) {
        SeasonTicketRenewalProduct seasonTicketRenewalProduct = new SeasonTicketRenewalProduct();
        seasonTicketRenewalProduct.setOriginSeasonTicketId(originSeasonTicketId);

        UUID uuid = UUID.randomUUID();
        seasonTicketRenewalProduct.setId(uuid.toString());

        String originSeatName;
        Long rateId;

        if (externalOrigin) {
            SeasonTicketExternalSeat externalSeat = createExternalSeat(externalOriginSeat);
            seasonTicketRenewalProduct.setOriginExternalSeat(externalSeat);
            seasonTicketRenewalProduct.setOriginRateId(externalOriginSeat.getRateId());
            seasonTicketRenewalProduct.setExternalOrigin(TRUE);
            seasonTicketRenewalProduct.setAutoRenewal(externalOriginSeat.getAutoRenewal());
            seasonTicketRenewalProduct.setIban(externalOriginSeat.getIban());
            seasonTicketRenewalProduct.setBic(externalOriginSeat.getBic());

            originSeatName = externalOriginSeat.getSeat();
            rateId = externalOriginSeat.getRateId();
        } else {
            SeasonTicketSeat originSeasonTicketSeat = createSeat(originSeat);
            seasonTicketRenewalProduct.setOriginSeasonTicketSeat(originSeasonTicketSeat);
            seasonTicketRenewalProduct.setOriginRateId(originSeat.getRateId());
            seasonTicketRenewalProduct.setBalance(originSeat.getBalance());
            seasonTicketRenewalProduct.setExternalOrigin(FALSE);
            seasonTicketRenewalProduct.setAutoRenewal(originSeat.getAutoRenewal());
            seasonTicketRenewalProduct.setIban(originSeat.getIban());
            seasonTicketRenewalProduct.setBic(originSeat.getBic());

            originSeatName = originSeat.getOriginSeatName();
            rateId = originSeat.getRateId();
        }

        if(renewalSeat != null) {
            SeasonTicketSeat renewalSeasonTicketSeat = createSeat(renewalSeat, originSeatName);
            seasonTicketRenewalProduct.setRenewalSeasonTicketSeat(renewalSeasonTicketSeat);
            seasonTicketRenewalProduct.setStatus(SeasonTicketRenewalStatus.PENDING_RENEWAL);
        } else {
            seasonTicketRenewalProduct.setStatus(SeasonTicketRenewalStatus.MAPPING_SEAT_NOT_FOUND);
        }

        seasonTicketRenewalProduct.setRenewalRateId(relatedRatesMap.get(rateId));

        return seasonTicketRenewalProduct;
    }

    private static SeasonTicketSeat createSeat(RenewalSeasonTicketOriginSeat originSeat) {
        SeasonTicketSeat seat = new SeasonTicketSeat();
        seat.setSectorId(originSeat.getOriginSectorId());
        seat.setSeatId(originSeat.getSeatId());
        seat.setPriceZoneId(originSeat.getPriceZoneId());

        if (SeasonTicketSeatType.NOT_NUMBERED.equals(originSeat.getSeatType())) {
            seat.setSeatType(SeasonTicketSeatType.NOT_NUMBERED);
            seat.setNotNumberedZoneId(originSeat.getOriginNotNumberedZoneId());
        } else {
            seat.setSeatType(SeasonTicketSeatType.NUMBERED);
            seat.setRowId(originSeat.getOriginRowId());
            seat.setSeatName(originSeat.getOriginSeatName());
        }
        return seat;
    }

    private static SeasonTicketSeat createSeat(RenewalSeasonTicketRenewalSeat renewalSeat, String seatName) {
        SeasonTicketSeat seat = new SeasonTicketSeat();
        seat.setSectorId(renewalSeat.getRenewalSectorId());
        seat.setSeatId(renewalSeat.getRenewalSeatId());
        seat.setPriceZoneId(renewalSeat.getRenewalPriceZoneId());

        if (SeasonTicketSeatType.NOT_NUMBERED.equals(renewalSeat.getSeatType())) {
            seat.setSeatType(SeasonTicketSeatType.NOT_NUMBERED);
            seat.setNotNumberedZoneId(renewalSeat.getRenewalNotNumberedZoneId());
        } else {
            seat.setSeatType(SeasonTicketSeatType.NUMBERED);
            seat.setRowId(renewalSeat.getRenewalRowId());
            seat.setSeatName(seatName);
        }
        return seat;
    }

    private static SeasonTicketExternalSeat createExternalSeat(RenewalExternalOriginSeat externalOriginSeat) {
        SeasonTicketExternalSeat seat = new SeasonTicketExternalSeat();
        seat.setSector(externalOriginSeat.getSector());
        seat.setPriceZone(externalOriginSeat.getPriceZone());

        if (SeasonTicketSeatType.NOT_NUMBERED.equals(externalOriginSeat.getSeatType())) {
            seat.setSeatType(SeasonTicketSeatType.NOT_NUMBERED);
            seat.setNotNumberedZone(externalOriginSeat.getNotNumberedZone());
        } else {
            seat.setSeatType(SeasonTicketSeatType.NUMBERED);
            seat.setRow(externalOriginSeat.getRow());
            seat.setSeat(externalOriginSeat.getSeat());
        }
        return seat;
    }

    public static RenewalSeasonTicketOriginSeat createRenewalSeasonTicketOriginSeat(OrderProductDTO orderProductDTO, Boolean includeBalance, Double maxEarnings) {
        OrderTicketDataDTO ticketData = orderProductDTO.getTicketData();
        String userId = orderProductDTO.getAdditionalData().getCustomer().getUserId();

        Integer sectorId = ticketData.getSectorId();
        Integer notNumberedZoneId = ticketData.getNotNumberedAreaId();
        Integer rowId = ticketData.getRowId();
        Long seatId = orderProductDTO.getId();
        String numSeat = ticketData.getNumSeat();
        Long priceZoneId = ticketData.getPriceZoneId().longValue();
        Long rateId = ticketData.getRateId().longValue();

        RenewalSeasonTicketOriginSeat renewalSeasonTicketOriginSeat = new RenewalSeasonTicketOriginSeat();
        renewalSeasonTicketOriginSeat.setUserId(userId);
        renewalSeasonTicketOriginSeat.setOriginSeatId(seatId);
        renewalSeasonTicketOriginSeat.setOriginSectorId(sectorId);
        renewalSeasonTicketOriginSeat.setPriceZoneId(priceZoneId);
        renewalSeasonTicketOriginSeat.setRateId(rateId);

        Map<String, Object> renewalDetails = orderProductDTO.getRenewalDetails();
        if (renewalDetails != null) {
            renewalSeasonTicketOriginSeat.setIban(renewalDetails.get(IBAN) != null ? renewalDetails.get(IBAN).toString() : null);
            renewalSeasonTicketOriginSeat.setBic(renewalDetails.get(BIC) != null ? renewalDetails.get(BIC).toString() : null);
            renewalSeasonTicketOriginSeat.setAutoRenewal(renewalDetails.get(AUTO_RENEWAL) != null && Boolean.parseBoolean(renewalDetails.get(AUTO_RENEWAL).toString()));
        }

        if (SeatType.NOT_NUMBERED.equals(ticketData.getSeatType())) {
            renewalSeasonTicketOriginSeat.setSeatType(SeasonTicketSeatType.NOT_NUMBERED);
            renewalSeasonTicketOriginSeat.setOriginNotNumberedZoneId(notNumberedZoneId);
        } else {
            renewalSeasonTicketOriginSeat.setSeatType(SeasonTicketSeatType.NUMBERED);
            renewalSeasonTicketOriginSeat.setOriginRowId(rowId);
            renewalSeasonTicketOriginSeat.setOriginSeatName(numSeat);
        }

        if (BooleanUtils.isTrue(includeBalance)) {
            if (orderProductDTO.getSeasonData() != null && CollectionUtils.isNotEmpty(orderProductDTO.getSeasonData().getSessionProducts())) {
                Double balance = orderProductDTO.getSeasonData().getSessionProducts().stream()
                        .map(OrderSeasonSessionDTO::getRelease)
                        .filter(Objects::nonNull)
                        .filter(release -> SeasonProductReleaseStatus.SOLD.equals(release.getStatus()))
                        .map(release -> calculateBalance(release.getData()))
                        .reduce(Double::sum).orElse(0D);
                balance = maxEarnings == null ? balance : Math.min(balance, maxEarnings);
                renewalSeasonTicketOriginSeat.setBalance(balance);
            }
        }

        return renewalSeasonTicketOriginSeat;
    }

    private static Double calculateBalance(Map<String, Object> releaseData) {
        if (releaseData !=null && releaseData.containsKey("price") && releaseData.containsKey("percentage")) {
            Double price = Double.parseDouble(releaseData.get("price").toString());
            Double percentage = Double.parseDouble(releaseData.get("percentage").toString());
            return price * percentage / 100.0;
        }
        return 0D;
    }

    public static RenewalExternalOriginSeat createRenewalExternalOriginSeat(CustomerExternalProduct customerExternalProduct,
                                                                            Long renewalProcessIdentifier,
                                                                            Long rateId) {
        if(customerExternalProduct == null) {
            return null;
        }
        RenewalExternalOriginSeat externalOriginSeat = new RenewalExternalOriginSeat();
        externalOriginSeat.setUserId(customerExternalProduct.getUserId());
        externalOriginSeat.setSector(customerExternalProduct.getSectorName());
        externalOriginSeat.setPriceZone(customerExternalProduct.getPriceZoneName());
        externalOriginSeat.setRenewalProcessIdentifier(renewalProcessIdentifier);
        externalOriginSeat.setRateId(rateId);
        externalOriginSeat.setAutoRenewal(customerExternalProduct.getAutoRenewal());
        externalOriginSeat.setIban(customerExternalProduct.getIban());
        externalOriginSeat.setBic(customerExternalProduct.getBic());

        if (ExternalSeatType.NOT_NUMBERED.equals(customerExternalProduct.getSeatType())) {
            externalOriginSeat.setSeatType(SeasonTicketSeatType.NOT_NUMBERED);
            externalOriginSeat.setNotNumberedZone(customerExternalProduct.getNotNumberedZoneName());
        } else {
            externalOriginSeat.setSeatType(SeasonTicketSeatType.NUMBERED);
            externalOriginSeat.setRow(customerExternalProduct.getRowName());
            externalOriginSeat.setSeat(customerExternalProduct.getSeatName());
        }
        return externalOriginSeat;
    }

    public static SeasonTicketRenewalsConfigDTO toDTO(SeasonTicketRenewalConfig source) {
        SeasonTicketRenewalsConfigDTO target = new SeasonTicketRenewalsConfigDTO();
        target.setRenewalType(SeasonTicketRenewalType.valueOf(source.getRenewalType().name()));
        target.setBankAccountId(source.getBankAccountId());
        target.setGroupByReference(source.getGroupByReference());
        return target;
    }

    public static void toDocument(SeasonTicketRenewalConfig document, UpdateSeasonTicketRenewalsConfigDTO update) {
        if (document == null) {
            document = new SeasonTicketRenewalConfig();
        }
        document.setRenewalType(RenewalType.valueOf(update.getRenewalType().name()));
        document.setBankAccountId(update.getBankAccountId());
        document.setGroupByReference(update.getGroupByReference());
    }
}

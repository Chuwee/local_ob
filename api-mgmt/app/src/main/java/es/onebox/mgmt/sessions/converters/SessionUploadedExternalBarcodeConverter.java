package es.onebox.mgmt.sessions.converters;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.order.dto.ProductBarcodesResponseDTO;
import es.onebox.mgmt.datasources.ms.order.dto.TicketBarcodeDTO;
import es.onebox.mgmt.sessions.dto.BarcodeDTO;
import es.onebox.mgmt.sessions.dto.BarcodeSessionDataDTO;
import es.onebox.mgmt.sessions.dto.ExternalBarcodesResponseDTO;
import es.onebox.mgmt.sessions.dto.SeatDataDTO;
import es.onebox.mgmt.sessions.enums.ExternalBarcodeValidationStatus;

import java.util.ArrayList;

public class SessionUploadedExternalBarcodeConverter {

    private SessionUploadedExternalBarcodeConverter() {
    }

    public static ExternalBarcodesResponseDTO toDTO(ProductBarcodesResponseDTO source) {
        ExternalBarcodesResponseDTO target = new ExternalBarcodesResponseDTO();

        if (source != null && source.getData() != null) {

            target.setData(new ArrayList<>());
            for (TicketBarcodeDTO ticketBarcodeDTO : source.getData()) {
                BarcodeDTO<ExternalBarcodeValidationStatus> barcodeDTO = toDto(ticketBarcodeDTO);
                target.getData().add(barcodeDTO);
            }
            target.setMetadata(source.getMetadata());
        }
        return target;
    }

    private static BarcodeDTO<ExternalBarcodeValidationStatus> toDto(TicketBarcodeDTO source) {
        BarcodeDTO<ExternalBarcodeValidationStatus> target = new BarcodeDTO<>();
        target.setBarcode(source.getBarcode());
        target.setEvent(source.getEvent());
        target.setPriceZone(source.getPriceZone());

        if (source.getOrder() != null) {
            target.setLocator(source.getOrder().getCode());
        }

        if (source.getStatus() != null) {
            target.setStatus(ExternalBarcodeValidationStatus.valueOf(source.getStatus().name()));
        }

        if (source.getSeat() != null) {
            SeatDataDTO seatData = new SeatDataDTO();

            seatData.setType(source.getSeat().getType());
            seatData.setSeat(source.getSeat().getSeat());

            if (isValidIdNameDTO(source.getSeat().getRow())) {
                seatData.setRow(source.getSeat().getRow());
            }
            if (isValidIdNameDTO(source.getSeat().getAccess())) {
                seatData.setAccess(source.getSeat().getAccess());
            }
            if (isValidIdNameDTO(source.getSeat().getSector())) {
                seatData.setSector(source.getSeat().getSector());
            }
            if (isValidIdNameDTO(source.getSeat().getNotNumberedArea())) {
                seatData.setNotNumberedArea(source.getSeat().getNotNumberedArea());
            }

            target.setSeatData(seatData);
        }

        if (source.getSession() != null) {
            BarcodeSessionDataDTO sessionData = new BarcodeSessionDataDTO();
            sessionData.setStart(source.getSession().getStart());
            sessionData.setId(source.getSession().getId());
            sessionData.setName(source.getSession().getName());
            target.setSession(sessionData);
        }

        return target;
    }

    private static boolean isValidIdNameDTO(IdNameDTO dto) {
        return dto != null && (dto.getId() != null || dto.getName() != null);
    }
}

package es.onebox.fifaqatar.tickets.converter;

import es.onebox.common.datasources.accesscontrol.dto.ACSeatDTO;
import es.onebox.common.datasources.accesscontrol.dto.ACTicketDTO;
import es.onebox.common.datasources.accesscontrol.dto.BarcodeInfo;
import es.onebox.common.datasources.accesscontrol.dto.SeatGateDTO;
import es.onebox.common.datasources.accesscontrol.dto.SeatNotNumberedZoneDTO;
import es.onebox.common.datasources.accesscontrol.dto.SeatSectorDTO;
import es.onebox.common.datasources.accesscontrol.dto.SeatType;
import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.SeasonSessionTransferDTO;
import es.onebox.fifaqatar.tickets.dto.HayyaTicket;
import es.onebox.fifaqatar.tickets.dto.NotNumberedZone;
import es.onebox.fifaqatar.tickets.dto.Seat;
import es.onebox.fifaqatar.tickets.dto.SeatGate;
import es.onebox.fifaqatar.tickets.dto.SeatSector;
import es.onebox.fifaqatar.tickets.dto.TicketHolder;
import es.onebox.fifaqatar.tickets.dto.ZucchettiTicket;

import java.util.List;

public class TicketConverter {

    public static void fillACTicket(HayyaTicket hayyaTicket, ACTicketDTO acTicketDTO) {
        hayyaTicket.setType(acTicketDTO.getType());
        hayyaTicket.setBarcode(acTicketDTO.getBarcode());
        hayyaTicket.setSession(acTicketDTO.getSession());
        hayyaTicket.setEvent(acTicketDTO.getEvent());
        hayyaTicket.setVenue(acTicketDTO.getVenue());
        hayyaTicket.setSeat(acTicketDTO.getSeat());
        hayyaTicket.setStatus(acTicketDTO.getStatus());
        hayyaTicket.setValidations(acTicketDTO.getValidations());
        hayyaTicket.setSales(acTicketDTO.getSales());
        hayyaTicket.setRate(acTicketDTO.getRate());
        hayyaTicket.setProvider(acTicketDTO.getProvider());
        hayyaTicket.setTicketItemType(acTicketDTO.getTicketItemType());
        hayyaTicket.setOrderCode(acTicketDTO.getOrderCode());
        hayyaTicket.setSessionId(acTicketDTO.getSessionId());
        hayyaTicket.setAdmissionDate(acTicketDTO.getAdmissionDate());
        hayyaTicket.setPrice(acTicketDTO.getPrice());
        hayyaTicket.setUpdatedDate(acTicketDTO.getUpdatedDate());
        hayyaTicket.setAttendant(acTicketDTO.getAttendant());
    }

    public static void fillCustomer(HayyaTicket hayyaTicket, Customer customer) {
        hayyaTicket.setTicketHolder(convert(customer));
    }

    public static void fillOwner(HayyaTicket hayyaTicket, OrderDTO order) {
        hayyaTicket.setTicketHolder(convert(order));
    }

    public static void fillTransferred(HayyaTicket hayyaTicket, OrderProductDTO product) {
        hayyaTicket.setTicketHolder(convert(product.getTransferData()));
    }

    public static List<ZucchettiTicket> convert(List<BarcodeInfo> whitelist) {
        return whitelist.stream()
                .map(TicketConverter::convert)
                .toList();
    }

    private static ZucchettiTicket convert(BarcodeInfo barcodeInfo) {
        ZucchettiTicket zucchettiTicket = new ZucchettiTicket();
        zucchettiTicket.setBarcode(barcodeInfo.getBarcode());
        zucchettiTicket.setSessionId(barcodeInfo.getSessionId());
        zucchettiTicket.setSeat(convert(barcodeInfo.getSeat()));
        zucchettiTicket.setType("SEAT");
        return zucchettiTicket;
    }

    private static Seat convert(ACSeatDTO seatDTO) {
        Seat seat = new Seat();
        seat.setSector(convert(seatDTO.getSector()));
        if (seatDTO.getNotNumberedZone() != null) {
            seat.setNotNumberedZone(convert(seatDTO.getNotNumberedZone()));
        } else {
            seat.setRow(seatDTO.getRow());
            seat.setSeat(seatDTO.getSeat());
        }
        seat.setGate(convert(seatDTO.getGate()));
        seat.setType(convert(seatDTO.getType()));

        return seat;
    }

    private static SeatSector convert(SeatSectorDTO sectorDTO) {
        SeatSector sector = new SeatSector();
        sector.setId(sectorDTO.getId());
        sector.setCode(sectorDTO.getCode());
        sector.setName(sectorDTO.getName());
        return sector;
    }

    private static es.onebox.fifaqatar.tickets.dto.SeatType convert(SeatType type) {
        if (type == null) {
            return null;
        }
        return es.onebox.fifaqatar.tickets.dto.SeatType.valueOf(type.name());
    }

    private static SeatGate convert(SeatGateDTO gateDTO) {
        SeatGate gate = new SeatGate();
        gate.setId(gateDTO.getId());
        gate.setName(gateDTO.getName());
        return gate;
    }

    private static NotNumberedZone convert(SeatNotNumberedZoneDTO notNumberedZoneDTO) {
        NotNumberedZone notNumberedZone = new NotNumberedZone();
        notNumberedZone.setId(notNumberedZoneDTO.getId());
        notNumberedZone.setName(notNumberedZoneDTO.getName());
        return notNumberedZone;
    }

    private static TicketHolder convert(OrderDTO order) {
        TicketHolder ticketHolder = new TicketHolder();
        ticketHolder.setName(order.getCustomer().getName());
        ticketHolder.setLastname(order.getCustomer().getSurname());
        ticketHolder.setEmail(order.getCustomer().getEmail());
        ticketHolder.setPhone(order.getCustomer().getPhone());
        ticketHolder.setCountry(order.getCustomer().getCountry());
        if (order.getCustomer().getAdditionalInfo() != null) {
            ticketHolder.setNationality((String) order.getCustomer().getAdditionalInfo().get("nationality"));
            ticketHolder.setDocNumber((String) order.getCustomer().getAdditionalInfo().get("identificationId"));
        }
        return ticketHolder;
    }

    private static TicketHolder convert(Customer customer) {
        TicketHolder ticketHolder = new TicketHolder();
        ticketHolder.setName(customer.getName());
        ticketHolder.setLastname(customer.getSurname());
        ticketHolder.setEmail(customer.getEmail());
        ticketHolder.setPhone(customer.getPhone());
        ticketHolder.setCountry(customer.getCountry());
        ticketHolder.setDocNumber(customer.getIdCard());
        if (customer.getAdditionalProperties() != null) {
            ticketHolder.setNationality((String) customer.getAdditionalProperties().get("nationality"));
        }
        return ticketHolder;
    }

    private static TicketHolder convert(SeasonSessionTransferDTO transferDTO) {
        TicketHolder ticketHolder = new TicketHolder();
        ticketHolder.setName(transferDTO.getData().get("name"));
        ticketHolder.setLastname(transferDTO.getData().get("surname"));
        ticketHolder.setEmail(transferDTO.getData().get("email"));
        return  ticketHolder;
    }
}

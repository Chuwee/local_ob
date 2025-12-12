package es.onebox.bepass.tickets.converter;

import es.onebox.bepass.datasources.bepass.dto.Ticket;
import es.onebox.bepass.datasources.bepass.dto.TicketToken;
import es.onebox.bepass.tickets.dto.TicketStatus;
import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.event.dto.AccessScheduleType;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.dto.SessionDateDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.OrderTicketDataDTO;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TicketsConverter {

    public static final String BARCODE = "barcode";
    private static final Long ADMISSION_START = 2L;
    private static final Long ADMISSION_END = 24L;

    private TicketsConverter() {
    }

    public static List<Ticket> toCreate(Customer customer, List<OrderProductDTO> products, String externalEventId,
                                        SessionDTO session, Function<String, String> decoder) {
        List<Ticket> request = new ArrayList<>();
        SessionRanges ranges = resolveSessionRanges(session);
        products.forEach(product -> {
            Ticket out = create(customer, product, externalEventId, session, ranges, decoder);
            request.add(out);
        });
        return request;
    }

    public static Ticket toCreate(Customer customer, OrderProductDTO product, String externalEventId,
                                  SessionDTO session, Function<String, String> decoder) {
        SessionRanges ranges = resolveSessionRanges(session);
        return create(customer, product, externalEventId, session, ranges, decoder);
    }

    public static Ticket toRefund(OrderProductDTO product, String externalEventId, Function<String, String> decoder) {
        return refund(product, externalEventId, decoder);
    }

    private static Ticket refund(OrderProductDTO product, String externalEventId, Function<String, String> decoder) {
        Ticket out = new Ticket();
        out.setExternalId(String.valueOf(product.getId()));
        out.setEventId(externalEventId);
        out.setExternalEventId(String.valueOf(product.getEventId()));
        out.setSessionId(String.valueOf(product.getSessionId()));
        TicketToken token = new TicketToken();
        token.setActive(Boolean.FALSE);
        token.setType(BARCODE);
        if (product.getTicketData() != null) {
            token.setToken(decoder.apply(product.getTicketData().getBarcode()));
        }
        out.setTokens(List.of(token));
        return out;
    }


    private static Ticket create(Customer customer, OrderProductDTO product,
                                 String externalEventId, SessionDTO session,
                                 SessionRanges sessionRanges,
                                 Function<String, String> decoder) {
        Ticket out = new Ticket();
        out.setExternalId(String.valueOf(product.getId()));
        out.setEventId(externalEventId);
        out.setExternalEventId(String.valueOf(product.getEventId()));
        out.setSessionId(String.valueOf(product.getSessionId()));
        if (sessionRanges != null) {
            ZonedDateTimeWithRelative admissionStart = sessionRanges.start;
            ZonedDateTimeWithRelative admissionEnd = sessionRanges.end;
            if (admissionStart != null && admissionEnd != null) {
                out.setValidFrom(admissionStart.absolute().toInstant().toString());
                out.setValidTo(admissionEnd.absolute().toInstant().toString());
            }
        }
        // user info
        out.setUserId(customer.getUserId());
        out.setUserFirstName(customer.getName());
        out.setUserLastName(customer.getSurname());
        out.setUserPhone(customer.getPhone());
        out.setUserDocument(customer.getIdCard());
        out.setUserEmail(customer.getEmail());
        // holder info
        out.setHolderFirstName(customer.getName());
        out.setHolderLastName(customer.getSurname());
        out.setHolderPhone(customer.getPhone());
        out.setHolderDocument(customer.getIdCard());
        out.setHolderEmail(customer.getEmail());
        out.setStatus(TicketStatus.ACTIVE.name());
        // allocation
        OrderTicketDataDTO ticketData = product.getTicketData();
        if (ticketData != null) {
            out.setRow(ticketData.getRowName());
            out.setSeat(ticketData.getNumSeat());
            out.setSectorId(ticketData.getSectorName());
            out.setGateId(ticketData.getAccessName());
        }
        TicketToken token = new TicketToken();
        token.setActive(Boolean.TRUE);
        token.setType(BARCODE);
        if (product.getTicketData() != null) {
            token.setToken(decoder.apply(product.getTicketData().getBarcode()));
        }
        out.setTokens(List.of(token));
        return out;
    }

    private static SessionRanges resolveSessionRanges(SessionDTO session) {
        SessionDateDTO sessionDate = session.getDate();
        if (sessionDate == null) {
            return null;
        }
        if (session.getAccessScheduleType() == null || AccessScheduleType.DEFAULT.equals(session.getAccessScheduleType())) {
            if (sessionDate.getStart() != null) {
                return new SessionRanges(ZonedDateTimeWithRelative.of(sessionDate.getStart().minusHours(ADMISSION_START)),
                        ZonedDateTimeWithRelative.of(sessionDate.getStart().plusHours(ADMISSION_END)));
            }
        } else if (AccessScheduleType.SPECIFIC.equals(session.getAccessScheduleType())) {
            ZonedDateTimeWithRelative start = null;
            if (sessionDate.getAdmissionStart() != null) {
                start = sessionDate.getAdmissionStart();
            } else if (sessionDate.getAdmissionStart() == null && sessionDate.getStart() != null) {
                start = ZonedDateTimeWithRelative.of(sessionDate.getStart().minusHours(ADMISSION_START));
            }
            ZonedDateTimeWithRelative end = null;
            if (sessionDate.getAdmissionEnd() != null) {
                end = sessionDate.getAdmissionEnd();
            }else if (sessionDate.getAdmissionEnd() == null && sessionDate.getStart() != null) {
                end = ZonedDateTimeWithRelative.of(sessionDate.getStart().plusHours(ADMISSION_END));
            }
            return new SessionRanges(start, end);
        }
        return null;
    }


    private record SessionRanges(ZonedDateTimeWithRelative start, ZonedDateTimeWithRelative end) {
    }
}

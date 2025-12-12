package es.onebox.common.tickets.converter;

import es.onebox.common.tickets.TicketData;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TicketParamMapConverter extends TicketConverter {

    //ticket Literals codes
    private static final String ZONE_CODE = "ZONE";
    private static final String SURCHARGE_CODE = "SURCHARGE";
    private static final String TOTAL_CODE = "TOTAL";

    //ticket comm element tags codes
    private static final String TERMS_TAG = "TERMS_AND_CONDITIONS";


    public static Map<String, Object> fillReportParams(TicketData ticketData) {
        HashMap<String, Object> params = new HashMap<>();

        params.put("LOCALIZADOR", ticketData.getOrderCode());
        params.put("FECHA_IMPRESION", ticketData.getPrintDate());
        params.put("RECINTO", ticketData.getVenue());
        params.put("DIRECCION_RECINTO", ticketData.getVenueAddress());

        params.put("DIA_SEMANA_TEXTO", ticketData.getDiaSemanaTexto());
        params.put("DIA_MES", ticketData.getDiaMes());
        params.put("DIA_TEXTO", ticketData.getDiaSemanaTexto());
        params.put("MES_TEXTO", ticketData.getMesTexto());
        params.put("HORA_TEXTO", ticketData.getHoraTexto());
        params.put("FECHA_NUMERO", ticketData.getPrintDate());
        params.put("ZONA_PRECIO", ticketData.getZonaPrecio());

        params.put("TITULO", ticketData.getTitle());
        params.put("SUBTITULO", ticketData.getSubtitle());
        params.put("SUBTITULO_1", ticketData.getTitle());
        params.put("SUBTITULO_2", ticketData.getSubtitle());
        params.put("ZONA", ticketData.getZone());
        params.put("SECTOR", ticketData.getSector());
        params.put("CODIGO_BARRAS", ticketData.getBarcode());
        params.put("CODIGO_BARRAS_EXCLUIDO", false);
        params.put("ACCESO", ticketData.getGate());
        params.put("PRECIO_ENTRADA", ticketData.getItemPrice());
        params.put("PRECIO", ticketData.getItemPrice());
        params.put("GASTOS_DISTRIBUCION", ticketData.getAdministrationFeesPrice());
        params.put("GGESTION", ticketData.getAdministrationFeesPrice());
        params.put("TOTAL", ticketData.getTotalPrice());
        params.put("FILA", ticketData.getRow());
        params.put("BUTACA", ticketData.getSeat());
        params.put("OTROS_DATOS", ticketData.getAdditionalData());
        params.put("FECHA_SESION", ticketData.getSessionDate());
        params.put("DATOS_PROMOTOR", ticketData.getPromotorData());
        params.put("PATH_IMAGEN_EVENT", ticketData.getPathImageEvent());
        params.put(ATTENDANT_NAME, ticketData.getAttendantName());
        params.put(ATTENDANT_ID_NUMBER, ticketData.getAttendantId());
        params.put(ATTENDANT_MAIL, ticketData.getAttendantEmail());
        params.put("DISCOUNT", ticketData.getDiscount());
        params.put("PROMOTION", ticketData.getPromotion());
        params.put("AUTOMATIC", ticketData.getAutomatic());
        String joinedPromotions = null;
        if(ticketData.getPromotion() != null) {
            joinedPromotions = ticketData.getPromotion();
        }
        if (ticketData.getDiscount() != null) {
            if(joinedPromotions != null) {
                joinedPromotions += " - " + ticketData.getDiscount();
            } else {
                joinedPromotions = ticketData.getDiscount();
            }
        }
        if (ticketData.getAutomatic() != null) {
            if(joinedPromotions != null) {
                joinedPromotions += " - " + ticketData.getAutomatic();
            } else {
                joinedPromotions = ticketData.getAutomatic();
            }
        }
        params.put("PROMOCION", joinedPromotions);
        params.put("VISIBILIDAD", ticketData.getVisibility());
        params.put("TARIFA", ticketData.getRate());
        params.put("TIPO_ENTRADA", ticketData.getTicketType());
        params.put("PATH_EXTERNAL_TICKET", ticketData.getExternalTicket());
        params.put("NUMERO_SOCIO", ticketData.getPartnerId());
        params.put("COMPOSED_DATE", ticketData.getComposedDate());


        // Ticket Literals
        Map<String, String> ticketLiteralsByCode = ticketData.getTicketLiteralsByCode();
        params.put("TITULO_ZONA", ticketLiteralsByCode.getOrDefault(ZONE_CODE, null));
        params.put("TITULO_SECTOR", ticketLiteralsByCode.getOrDefault(SECTOR_CODE, null));
        params.put("TITULO_GGESTION", ticketLiteralsByCode.getOrDefault(SURCHARGE_CODE, null));
        params.put("TITULO_TOTAL", ticketLiteralsByCode.getOrDefault(TOTAL_CODE, null));
        params.put("TITULO_TIPO_ENTRADA", ticketLiteralsByCode.getOrDefault("TICKET_TYPE", null));
        params.put("TITULO_ACCESO", ticketLiteralsByCode.getOrDefault("ACCESS", null));
        params.put("TITULO_LOCALIZADOR", ticketLiteralsByCode.getOrDefault("LOCATOR", null));
        params.put("TITULO_PRECIO", ticketLiteralsByCode.getOrDefault("PRICE", null));
        params.put("COPYRIGHT", ticketLiteralsByCode.getOrDefault("COPYRIGHT", null));
        params.put("MENSAJE_ENTRADA", ticketLiteralsByCode.getOrDefault("ACCESS_MSG", null));

        params.put("PATH_IMAGEN_CUERPO", ticketData.getPathImageEvent());

        // Ticket communication elements
        Map<String, String> ticketCommElementsByTagType = ticketData.getTicketCommElementsByTagType();
        params.put("CONDICIONES_USO", ticketCommElementsByTagType.get(TERMS_TAG));
        return params;
    }

}

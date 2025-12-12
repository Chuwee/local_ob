package es.onebox.ath.integration.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.onebox.ath.integration.dto.ConsultResponseDTO;
import es.onebox.ath.integration.dto.LoginRequestDTO;
import es.onebox.ath.integration.dto.LoginResponseDTO;
import es.onebox.ath.integration.dto.SeatManagementResponseDTO;
import es.onebox.ath.integration.dto.TransferMatchDTO;
import es.onebox.ath.integration.dto.TransferSeatDTO;
import es.onebox.common.datasources.webhook.dto.OrderNotificationMessageDTO;
import es.onebox.common.datasources.webhook.dto.OrderPayloadDTO;
import es.onebox.common.datasources.webhook.dto.ath.AthConsultPayloadDTO;
import es.onebox.common.datasources.webhook.dto.ath.AthConsultResponseDTO;
import es.onebox.common.datasources.webhook.dto.ath.AthCreatePayloadDTO;
import es.onebox.common.datasources.webhook.dto.ath.AthLoginPayloadDTO;
import es.onebox.common.datasources.webhook.dto.ath.AthLoginResponseDTO;
import es.onebox.common.datasources.webhook.dto.ath.AthModifyPayloadDTO;
import es.onebox.common.datasources.webhook.dto.ath.AthSeatManagementResponseDTO;
import es.onebox.common.datasources.webhook.dto.ath.CesionLocalidad;
import es.onebox.common.datasources.webhook.dto.ath.PartidoCesion;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BluewayMessageConverter {

    private static final String CLUB = "Club";
    private static final String ESTADO = "Cancelada";

    public static OrderNotificationMessageDTO convertLogin(LoginRequestDTO body, String username, String password) throws JsonProcessingException {
        AthLoginPayloadDTO payloadDTO = new AthLoginPayloadDTO();
        payloadDTO.setCodigoSocio(body.getUsername());
        payloadDTO.setClave(body.getPass());

        return convert(payloadDTO, username, password);
    }

    public static LoginResponseDTO convertLoginResponse(AthLoginResponseDTO athLoginResponseDTO) {
        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setToken(athLoginResponseDTO.getToken());
        responseDTO.setMessage(athLoginResponseDTO.getMensaje());
        responseDTO.setErrorCode(athLoginResponseDTO.getCodigoError());
        responseDTO.setAccountId(athLoginResponseDTO.getAccountId());
        return responseDTO;
    }

    public static OrderNotificationMessageDTO convertCreate(HashMap order, String token, String usernameOB, String passwordOB) throws JsonProcessingException {
        List<HashMap> items = (List<HashMap>) order.get("items");
        HashMap item = items.get(0);
        HashMap member = (HashMap) item.get("member");
        HashMap allocation = (HashMap) item.get("allocation");
        HashMap event = (HashMap) allocation.get("external_event");

        AthCreatePayloadDTO payloadDTO = new AthCreatePayloadDTO();
        payloadDTO.setToken(token);
        payloadDTO.setCodigoSocio(member.get("person_id").toString());
        payloadDTO.setPartido(item.get("season").toString() + event.get("id"));
        payloadDTO.setDestinatario(CLUB);
        payloadDTO.setNombre(member.get("name").toString());
        payloadDTO.setApellidos(member.get("surname").toString());
        payloadDTO.setEmail(member.get("email").toString());

        return convert(payloadDTO, usernameOB, passwordOB);
    }

    public static OrderNotificationMessageDTO convertModify(String cesionLocalidadId, String token, String usernameOB, String passwordOB) throws JsonProcessingException {
        AthModifyPayloadDTO payloadDTO = new AthModifyPayloadDTO();
        payloadDTO.setToken(token);
        payloadDTO.setCesionLocalidadId(cesionLocalidadId);
        payloadDTO.setEstado(ESTADO);

        return convert(payloadDTO, usernameOB, passwordOB);
    }

    public static SeatManagementResponseDTO convertSeatManagementResponse(AthSeatManagementResponseDTO athSeatManagementResponseDTO) {
        SeatManagementResponseDTO responseDTO = new SeatManagementResponseDTO();
        responseDTO.setToken(athSeatManagementResponseDTO.getToken());
        responseDTO.setErrorCode(athSeatManagementResponseDTO.getCodigoError());
        responseDTO.setMessage(athSeatManagementResponseDTO.getMensaje());
        responseDTO.setTransferSeatId(athSeatManagementResponseDTO.getCesionLocalidadId());

        return responseDTO;
    }

    public static OrderNotificationMessageDTO convertConsult(String username, String token, String usernameOB, String passwordOB) throws JsonProcessingException {
        AthConsultPayloadDTO payloadDTO = new AthConsultPayloadDTO();
        payloadDTO.setToken(token);
        payloadDTO.setCodigoSocio(username);

        return convert(payloadDTO, usernameOB, passwordOB);
    }

    public static ConsultResponseDTO convertConsultResponse(AthConsultResponseDTO athConsultResponseDTO) {
        ConsultResponseDTO responseDTO = new ConsultResponseDTO();
        responseDTO.setToken(athConsultResponseDTO.getToken());
        responseDTO.setMessage(athConsultResponseDTO.getMensaje());
        responseDTO.setErrorCode(athConsultResponseDTO.getCodigoError());
        responseDTO.setTransferSeats(convertTransferSeat(athConsultResponseDTO.getCesionesLocalidad()));

        return responseDTO;
    }

    private static OrderNotificationMessageDTO convert(OrderPayloadDTO payloadDTO, String username, String password) {
        Map<String, String> headers = generateHeaders(username, password);
        OrderNotificationMessageDTO messageDTO = new OrderNotificationMessageDTO();
        messageDTO.setHeaders(headers);
        messageDTO.setPayload(payloadDTO);
        return messageDTO;
    }

    private static Map<String, String> generateHeaders(String username, String pass) {
        Map<String, String> headers = new HashMap<>();
        String valueToEncode = username + ":" + pass;
        headers.put("auth", Base64.getEncoder().encodeToString(valueToEncode.getBytes()));
        return headers;
    }

    private static List<TransferSeatDTO> convertTransferSeat(List<CesionLocalidad> cesionesLocalidad){
        if (cesionesLocalidad != null && !cesionesLocalidad.isEmpty()) {
            List<TransferSeatDTO> transferSeatDTOList = new ArrayList<>();
            for (CesionLocalidad localidad : cesionesLocalidad) {
                TransferSeatDTO seatDTO = new TransferSeatDTO();
                seatDTO.setTransferMatch(convertTransferMatch(localidad.getPartidoCesion()));
                seatDTO.setTransferSeatId(localidad.getCesionLocalidadId());
                seatDTO.setName(localidad.getNombre());
                seatDTO.setSurnames(localidad.getApellidos());
                seatDTO.setEmail(localidad.getEmail());
                seatDTO.setState(localidad.getEstado());
                seatDTO.setReceiver(localidad.getDestinatario());
                seatDTO.setChannel(localidad.getCanal());
                transferSeatDTOList.add(seatDTO);
            }
            return transferSeatDTOList;
        }
        return null;
    }

    private static TransferMatchDTO convertTransferMatch(PartidoCesion partidoCesion){
        TransferMatchDTO transferMatchDTO = new TransferMatchDTO();
        transferMatchDTO.setSeasonDayNumber(partidoCesion.getNumeroJornada());
        transferMatchDTO.setMatch(partidoCesion.getPartido());
        transferMatchDTO.setMatchDate(partidoCesion.getFechaPartido());
        transferMatchDTO.setCompetitionId(partidoCesion.getIdCompeticion());
        transferMatchDTO.setRival(partidoCesion.getRival());
        transferMatchDTO.setSeason(partidoCesion.getTemporada());
        transferMatchDTO.setSeasonDay(partidoCesion.getJornada());
        return transferMatchDTO;
    }
}

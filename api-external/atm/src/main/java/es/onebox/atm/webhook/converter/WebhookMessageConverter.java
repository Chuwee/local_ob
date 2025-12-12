package es.onebox.atm.webhook.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.ms.entity.dto.CountrySubdivisionDTO;
import es.onebox.common.datasources.webhook.dto.OrderNotificationMessageDTO;
import es.onebox.common.datasources.webhook.dto.atm.ATMPayloadDTO;
import es.onebox.common.utils.GeneratorUtils;
import es.onebox.core.serializer.mapper.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebhookMessageConverter {
    private static final String SECTOR_TEXT = "sector";
    private static final String COUNTRY_SUBDIVISION = "country_subdivision";
    private static final String MEMBER = "member";
    private static final String UNDEFINED = "undefined";


    public static OrderNotificationMessageDTO convert(HashMap order, String apiKey,
                                                      Map<String, CountrySubdivisionDTO> subdivisionDTOMap,
                                                      Map<String, String> headers)
            throws JsonProcessingException {

        if (order.containsKey("buyer_data") && order.get("buyer_data") != null) {
            Map<String, Object> buyerData = (Map) order.get("buyer_data");
            if (buyerData.containsKey("external_client_id")) {
                if(UNDEFINED.equals(buyerData.get("external_client_id")) || StringUtils.isEmpty((String)buyerData.get("external_client_id"))){
                    buyerData.remove("external_client_id");
                    buyerData.remove("user_id");
                }else {
                    buyerData.remove("allow_commercial_mailing");
                    buyerData.put("user_id", buyerData.get("external_client_id"));
                }
            }else if (buyerData.containsKey("user_id")) {
                if(UNDEFINED.equals(buyerData.get("user_id")) || StringUtils.isEmpty((String)buyerData.get("user_id"))){
                    buyerData.remove("external_client_id");
                    buyerData.remove("user_id");
                }else {
                    buyerData.remove("allow_commercial_mailing");
                    buyerData.put("external_client_id", buyerData.get("user_id"));
                }
            }
        }
        // TODO remove when ATM change the data model https://oneboxtds.atlassian.net/browse/OB-26843 --- BEGIN
        if (order.get("items") != null) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");
            for (Map<String, Object> element : items) {
                if (element.get("allocation") != null) {
                    Map<String, Object> allocation = (Map<String, Object>) element.get("allocation");
                    if (allocation.containsKey(SECTOR_TEXT)) {
                        String sector = allocation.get(SECTOR_TEXT) != null ? allocation.get(SECTOR_TEXT).toString() : null;
                        allocation.remove(SECTOR_TEXT);

                        Map<String, Object> sectorMap = new HashMap<>();
                        sectorMap.put("id", null);
                        sectorMap.put("name", null);
                        sectorMap.put("code", sector);
                        allocation.put(SECTOR_TEXT, sectorMap);
                    }

                    if (allocation.get("external_properties") != null) {
                        Map<String, Object> external = (Map<String, Object>) allocation.get("external_properties");
                        String seatStr = external.get("avet_seat_id") != null ? external.get("avet_seat_id").toString() : null;
                        if (seatStr != null) {
                            external.put("avet_seat_id", Long.valueOf(seatStr));
                        }
                    }
                }
                if (element.get("previous_allocation") != null) {
                    Map<String, Object> allocation = (Map<String, Object>) element.get("previous_allocation");
                    if (allocation.containsKey(SECTOR_TEXT)) {
                        String sector = allocation.get(SECTOR_TEXT) != null ? allocation.get(SECTOR_TEXT).toString() : null;
                        allocation.remove(SECTOR_TEXT);

                        Map<String, Object> sectorMap = new HashMap<>();
                        sectorMap.put("id", null);
                        sectorMap.put("name", null);
                        sectorMap.put("code", sector);
                        allocation.put(SECTOR_TEXT, sectorMap);
                    }
                    if (allocation.get("external_properties") != null) {
                        Map<String, Object> external = (Map<String, Object>) allocation.get("external_properties");
                        String seatStr = external.get("avet_seat_id") != null ? external.get("avet_seat_id").toString() : null;
                        if (seatStr != null) {
                            Long seatLng = Long.valueOf(seatStr);
                            external.remove("avet_seat_id");
                            external.put("avet_seat_id", seatLng);
                        }
                    }
                }
                // Add state to each member
                if (element.get(MEMBER) != null) {
                    Map<String, Object> member = (Map<String, Object>) element.get(MEMBER);
                    String countrySubdivisionId = (String) member.get(COUNTRY_SUBDIVISION);
                    if (countrySubdivisionId != null) {
                        subdivisionDTOMap.values().stream()
                                .filter(subdivision -> subdivision.getId().equals(Integer.valueOf(countrySubdivisionId)))
                                .findFirst()
                                .ifPresent(subdivision -> member.put(COUNTRY_SUBDIVISION, subdivision.getName()));
                    }
                }
            }
        }
        // TODO remove when ATM change the data model https://oneboxtds.atlassian.net/browse/OB-26843k --- END
        ATMPayloadDTO payloadDTO = new ATMPayloadDTO();
        payloadDTO.setOrder(order);
        OrderNotificationMessageDTO messageDTO = new OrderNotificationMessageDTO();
        messageDTO.setHeaders(headers);
        messageDTO.setPayload(payloadDTO);
        messageDTO.setSignature(GeneratorUtils.getHashSHA256(JsonMapper.jacksonMapper().writeValueAsString(payloadDTO) + apiKey));

        return messageDTO;
    }

}

package es.onebox.fifaqatar.conciliation.utils;

import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.client.dto.request.CreateCustomerRequest;
import es.onebox.common.datasources.orders.dto.Order;
import es.onebox.common.datasources.orders.repository.OrdersRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.fifaqatar.conciliation.config.FifaQatarCustomerConciliationStopperDAO;
import es.onebox.fifaqatar.conciliation.dto.CreateCustomerRequestDTO;
import es.onebox.fifaqatar.conciliation.dto.UpdateCustomerRequestDTO;
import es.onebox.fifaqatar.config.config.FifaQatarConfigDocument;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Component
public class ConciliationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConciliationUtils.class);
    private final FifaQatarCustomerConciliationStopperDAO stopperDAO;

    @Autowired
    private OrdersRepository ordersRepository;

    public ConciliationUtils(FifaQatarCustomerConciliationStopperDAO stopperDAO) {
        this.stopperDAO = stopperDAO;
    }
    private static final Map<String, String> BUYER_TO_CUSTOMER_KEYS = java.util.Map.of(
            "nationality", "nationality",
            "your_team", "your_team"
    );

    public static String safeGetString(Map<String, Object> map, String key) {
        if (map == null) {
            return null;
        }
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }

    public static String safeGetStringGender(Map<String, Object> map, String key) {
        if (map == null) {
            return null;
        }
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        String gender = value.toString();
        return switch (gender) {
            case "MALE" -> "M";
            case "FEMALE" -> "F";
            default -> gender;
        };
    }

    public static String safeGetNestedString(Map<String, Object> map, String subKey) {
        if (map == null) {
            return null;
        }
        Object nested = map.get("international_phone");
        if (!(nested instanceof Map)){
            return null;
        }
        Map<String, Object> nestedMap = (Map<String, Object>) nested;
        return safeGetString(nestedMap, subKey);
    }

    public static CreateCustomerRequestDTO createCustomerRequest(String email, Long entityId, Map<String, Object> buyerData) {
        CreateCustomerRequestDTO request = new CreateCustomerRequestDTO();
        request.setEmail(email);
        request.setEntityId(entityId != null ? entityId.intValue() : null);
        request.setName(safeGetString(buyerData, "name"));
        request.setSurname(safeGetString(buyerData, "surname"));
        request.setStatus("ACTIVE");
        request.setType("MEMBER");
        request.setCountry(safeGetString(buyerData, "country"));
        request.setGender(safeGetString(buyerData, "gender"));
        request.setIdCard(safeGetString(buyerData, "id_number"));
        request.setIdCardType(safeGetString(buyerData, "id_number_type"));
        request.setPhone(safeGetNestedString(buyerData, "number"));
        request.setPhonePrefix(safeGetNestedString(buyerData, "prefix"));
        request.setCity(safeGetString(buyerData, "city"));
        request.setAdditionalProperties(buildAdditionalProperties(buyerData));
        return request;
    }

    private static HashMap<String, Object> buildAdditionalProperties(Map<String, Object> buyerData) {
        if (buyerData == null || buyerData.isEmpty()) {
            return new HashMap<>();
        }
        HashMap<String, Object> additionalData = new java.util.LinkedHashMap<>();
        BUYER_TO_CUSTOMER_KEYS.forEach((buyerKey, customerKey) -> {
            Object v = buyerData.get(buyerKey);
            if (v != null) {
                additionalData.put(customerKey, v);
            }
        });
        return additionalData;
    }

    public static boolean customerAlreadyUpdated(Customer customer, UpdateCustomerRequestDTO req) {
        if (req == null) return true;

        if (req.getName() != null && !Objects.equals(req.getName(), customer.getName())) return false;
        if (req.getSurname() != null && !Objects.equals(req.getSurname(), customer.getSurname())) return false;
        if (req.getCountry() != null && !Objects.equals(req.getCountry(), customer.getCountry())) return false;
        if (req.getGender() != null && !Objects.equals(req.getGender(), customer.getGender())) return false;
        if (req.getIdCard() != null && !Objects.equals(req.getIdCard(), customer.getIdCard())) return false;
        if (req.getIdCardType() != null && !Objects.equals(req.getIdCardType(), customer.getIdCardType())) return false;
        if (req.getPhone() != null && !Objects.equals(req.getPhone(), customer.getPhone())) return false;
        if (req.getPhonePrefix() != null && !Objects.equals(req.getPhonePrefix(), customer.getPhonePrefix())) return false;
        if (req.getCity() != null && !Objects.equals(req.getCity(), customer.getCity())) return false;

        Map<String, Object> reqAdditionalProperties = req.getAdditionalProperties();
        if (reqAdditionalProperties != null && !reqAdditionalProperties.isEmpty()) {
            Map<String, Object> additionalProperties = null;
            try {
                additionalProperties = customer.getAdditionalProperties();
            } catch (Exception ignored) {
            }
            for (Map.Entry<String, Object> e : reqAdditionalProperties.entrySet()) {
                Object custVal = (additionalProperties != null) ? additionalProperties.get(e.getKey()) : null;
                if (!Objects.equals(e.getValue(), custVal)) return false;
            }
        }

        return true;
    }

    public static UpdateCustomerRequestDTO updateCustomerRequest(Map<String, Object> buyerData) {
        UpdateCustomerRequestDTO request = new UpdateCustomerRequestDTO();
        request.setName(safeGetString(buyerData, "name"));
        request.setSurname(safeGetString(buyerData, "surname"));
        request.setCountry(safeGetString(buyerData, "country"));
        request.setGender(safeGetStringGender(buyerData, "gender"));
        request.setIdCard(safeGetString(buyerData, "identification_id"));
        request.setPhone(safeGetNestedString(buyerData, "number"));
        request.setPhonePrefix(safeGetNestedString(buyerData, "prefix"));
        request.setCity(safeGetString(buyerData, "city"));
        request.setAdditionalProperties(buildAdditionalProperties(buyerData));
        return request;
    }

    public static UpdateCustomerRequestDTO updateCustomerRequest(Customer existing, Map<String, Object> buyerData) {
        if (existing == null || buyerData == null || buyerData.isEmpty()) {
            return null;
        }

        UpdateCustomerRequestDTO req = new UpdateCustomerRequestDTO();
        int informedFieldsCount = 0;

        informedFieldsCount = fillFieldIfNotInformed(existing.getName(), safeGetString(buyerData, "name"), req::setName, informedFieldsCount);
        informedFieldsCount = fillFieldIfNotInformed(existing.getSurname(), safeGetString(buyerData, "surname"), req::setSurname, informedFieldsCount);
        informedFieldsCount = fillFieldIfNotInformed(existing.getCountry(), safeGetString(buyerData, "country"), req::setCountry, informedFieldsCount);
        informedFieldsCount = fillFieldIfNotInformed(existing.getGender(), safeGetStringGender(buyerData, "gender"), req::setGender, informedFieldsCount);
        informedFieldsCount = fillFieldIfNotInformed(existing.getIdCard(), safeGetString(buyerData, "identification_id"),req::setIdCard, informedFieldsCount);
        informedFieldsCount = fillFieldIfNotInformed(existing.getIdCardType(), safeGetString(buyerData, "id_number_type"), req::setIdCardType, informedFieldsCount);
        informedFieldsCount = fillFieldIfNotInformed(existing.getPhone(), safeGetNestedString(buyerData, "number"), req::setPhone, informedFieldsCount);
        informedFieldsCount = fillFieldIfNotInformed(existing.getPhonePrefix(), safeGetNestedString(buyerData, "prefix"), req::setPhonePrefix, informedFieldsCount);
        informedFieldsCount = fillFieldIfNotInformed(existing.getCity(), safeGetString(buyerData, "city"), req::setCity, informedFieldsCount);

        Map<String, Object> customerAdditionalProperties = existing.getAdditionalProperties();
        HashMap<String, Object> additionalProperties = new LinkedHashMap<>();
        BUYER_TO_CUSTOMER_KEYS.forEach((buyerKey, customerKey) -> {
            if (customerAdditionalProperties == null || !customerAdditionalProperties.containsKey(customerKey)) {
                Object v = buyerData.get(buyerKey);
                if (v != null) {
                    additionalProperties.put(customerKey, v);
                }
            }
        });
        if (!additionalProperties.isEmpty()) {
            req.setAdditionalProperties(additionalProperties);
            informedFieldsCount++;
        }

        return informedFieldsCount == 0 ? null : req;
    }

    private static int fillFieldIfNotInformed(String customerValue, String buyerDataValue, Consumer<String> setter, int informedFields) {
        if (StringUtils.isBlank(customerValue) && StringUtils.isNotBlank(buyerDataValue)) {
            setter.accept(buyerDataValue);
            return informedFields + 1;
        }
        return informedFields;
    }



    public static void processCsvLine(String line, Map<String, String> orderCodeToEmail) {
        String[] parts = line.split(",", -1);
        if (parts.length < 2) {
            return;
        }
        String code = unquote(parts[0].trim());
        String email = unquote(parts[1].trim().toLowerCase());
        if (code.isEmpty() || email.isEmpty()) {
            return;
        }
        orderCodeToEmail.putIfAbsent(code, email);
    }

    private static String unquote(String s) {
        if (s == null) {
            return "";
        }
        s = s.trim();
        while (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1).trim();
        }
        return s.replace("\"\"", "\"");
    }


    public static Map<String, String> readCsv(MultipartFile file) {
        Map<String, String> orderCodeToEmail = new HashMap<>();

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = CSVFormat.EXCEL
                     .withFirstRecordAsHeader()
                     .withQuote('"')
                     .withIgnoreSurroundingSpaces()
                     .withIgnoreEmptyLines()
                     .withTrim()
                     .parse(reader)) {

            Map<String, String> normalizedHeaderMap = new HashMap<>();
            for (String originalHeader : csvParser.getHeaderMap().keySet()) {
                String normalized = originalHeader
                        .replaceAll("[\"\\uFEFF]", "")
                        .trim()
                        .toLowerCase();
                normalizedHeaderMap.put(normalized, originalHeader);
            }

            String codeHeader = normalizedHeaderMap.get("transaction code");
            String emailHeader = normalizedHeaderMap.get("email");

            if (codeHeader == null || emailHeader == null) {
                LOGGER.error("CSV headers found: {}", csvParser.getHeaderMap().keySet());
                throw new IllegalArgumentException("CSV must contain 'Transaction code' and 'Email' headers");
            }

            for (CSVRecord record : csvParser) {
                String code = record.get(codeHeader);
                String email = record.get(emailHeader);

                if (code != null && email != null && !code.isBlank() && !email.isBlank()) {
                    orderCodeToEmail.put(code.trim(), email.trim());
                }
            }

        } catch (IOException e) {
            LOGGER.error("Failed to read CSV", e);
            return Map.of("status", "ERROR", "message", "CSV processing error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid CSV header", e);
            return Map.of("status", "ERROR", "message", "Invalid or missing CSV header: " + e.getMessage());
        }

        if (orderCodeToEmail.isEmpty()) {
            LOGGER.info("No valid rows found in CSV.");
        }

        return orderCodeToEmail;
    }



    public List<CreateCustomerRequest> createCustomerRequests(Map<String, String> parsedCsv, String accessToken, List<Long> channelIds, FifaQatarConfigDocument config) {
       Map<String, Map<String, Object>> codeToBuyerData = mapOrderCodeBuyerData(accessToken, channelIds);
       final Map<String, List<String>> emailToCodes = new LinkedHashMap<>();
       parsedCsv.forEach((code, email) ->
               emailToCodes.computeIfAbsent(email, k -> new ArrayList<>()).add(code)
       );
        long entityId = config.getEntityId().longValue();

        List<CreateCustomerRequest> requests = new ArrayList<>(emailToCodes.size());
        emailToCodes.forEach((email, codes) ->
                codes.stream()
                        .map(codeToBuyerData::get)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .map(buyerData -> ConciliationUtils.createCustomerRequest(email, entityId, buyerData))
                        .ifPresent(requests::add)
        );

        return requests;
   }


    public Map<String, Map<String, Object>> mapOrderCodeBuyerData(String accessToken, List<Long> channelIds) {
        final Instant start = Instant.now();

        List<Order> orders = ordersRepository.getOrders(accessToken, channelIds, null, null, false);
        Map<String, Map<String, Object>> codeToBuyerData = new HashMap<>(Math.max(16, orders.size()) * 2);

        for (Order o : orders) {
            if (o == null) {
                continue;
            }
            String code = o.getCode();
            if (code == null) {
                continue;
            }
            Map<String, Object> buyerData = o.getBuyerData();
            if (buyerData == null || buyerData.isEmpty()) {
                continue;
            }
            codeToBuyerData.putIfAbsent(code, buyerData);
        }
        long elapsedMs = Duration.between(start, Instant.now()).toMillis();
        LOGGER.info("Finished order retrieving, Elapsed {} ms, total orders: {}", elapsedMs, codeToBuyerData.size());
        return codeToBuyerData;
    }

    public void checkProcessShouldPause(Long entityId) throws InterruptedException {
        Long value = stopperDAO.get(entityId.toString());
        if (value == null) {
            throw new OneboxRestException();
        }
        boolean hasStopped = false;
        while (value != 1L) {
            hasStopped = true;
            LOGGER.info("[AUTOCREATE CUSTOMERS] Process paused in onebox-operative.fifaQatarCustomerConciliationStopper document set it to 1 to resume entityId: {}", entityId);
            Thread.sleep(10 * 1000L);
            value = stopperDAO.get(entityId.toString());
        }

        if (hasStopped) {
            LOGGER.info("[AUTOCREATE CUSTOMERS] Resuming process entityId: {}", entityId);
        }
    }

    public void checkProcessNotExistsForEntity(Long entityId) {
        Long l = stopperDAO.get(entityId.toString());
        if (l != null) {
            LOGGER.info("[AUTOASSIGN CUSTOMERS] Process exists for entityId: {}", entityId);
            throw new OneboxRestException(ApiExternalErrorCode.GENERIC_ERROR,
                    "Process exists for entityId: "+ entityId, new RuntimeException());
        }
        stopperDAO.createCounter(entityId.toString(), 1L);
    }

    public void cleanProcessLock(Long entityId) {
        stopperDAO.remove(entityId.toString());
    }


    public File buildCsv(Map<String, Object> result) throws IOException {
        Path path = Files.createTempFile("customers-", ".csv");
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            w.write("email,status,customer_id,error\n");

            result.keySet().stream().sorted().forEach(email -> {
                Object val = result.get(email);
                String status;
                String customerId = "";
                String error = "";

                if (val == null) {
                    status = "NOT_CREATED";
                    error = "NO_RESULT";
                } else if (val instanceof String s) {
                    status = "CREATED";
                    customerId = s;
                } else if (val instanceof Map<?, ?> mapVal) {
                    Object statusObj = mapVal.get("status");
                    status = (statusObj == null) ? "NOT_CREATED" : statusObj.toString();
                    Object err = mapVal.get("error");
                    if (err != null) error = err.toString();
                } else {
                    status = "UNKNOWN";
                    error = val.toString();
                }

                try {
                    w.append(csv(email)).append(',')
                            .append(csv(status)).append(',')
                            .append(csv(customerId)).append(',')
                            .append(csv(error)).append('\n');
                } catch (IOException ioe) {
                    // Convertimos en unchecked para poder usarla dentro del lambda
                    throw new RuntimeException(ioe);
                }
            });
            w.flush();
        } catch (RuntimeException re) {
            // Reempaquetar IOException lanzada desde el lambda
            if (re.getCause() instanceof IOException ioe) {
                try { Files.deleteIfExists(path); } catch (IOException ignore) {}
                throw ioe;
            }
            try { Files.deleteIfExists(path); } catch (IOException ignore) {}
            throw re;
        } catch (IOException e) {
            try { Files.deleteIfExists(path); } catch (IOException ignore) {}
            throw e;
        }
        return path.toFile();
    }

    private String csv(String s) {
        if (s == null) return "";
        boolean needsQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        if (needsQuote) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }



    public File buildUserUpdatesCsv(Map<String, Object> updates) throws IOException {
        Path path = Files.createTempFile("user-updates-", ".csv");
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            w.write("user_id,updated\n");
            updates.keySet().stream().sorted().forEach(userId -> {
                Boolean updated = (Boolean) updates.get(userId);
                String updatedStr = (updated != null && updated) ? "true" : "false";
                try {
                    w.append(csv(userId)).append(',').append(updatedStr).append('\n');
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            });
            w.flush();
        } catch (RuntimeException re) {
            if (re.getCause() instanceof IOException ioe) {
                try { Files.deleteIfExists(path); } catch (IOException ignore) {}
                throw ioe;
            }
            try { Files.deleteIfExists(path); } catch (IOException ignore) {}
            throw re;
        } catch (IOException e) {
            try { Files.deleteIfExists(path); } catch (IOException ignore) {}
            throw e;
        }
        return path.toFile();
    }






}

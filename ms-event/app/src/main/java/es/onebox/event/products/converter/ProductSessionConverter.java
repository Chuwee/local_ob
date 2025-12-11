package es.onebox.event.products.converter;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.products.domain.ProductSessionRecord;
import es.onebox.event.products.dto.ProductChannelSessionDTO;
import es.onebox.event.products.dto.ProductSessionBaseDTO;
import es.onebox.event.products.dto.ProductSessionDTO;
import es.onebox.event.products.dto.ProductSessionSearchFilter;
import es.onebox.event.products.dto.ProductSessionsDTO;
import es.onebox.event.products.dto.ProductSessionsPublishingDTO;
import es.onebox.event.products.enums.SelectionType;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionDateDTO;
import es.onebox.event.sessions.dto.SessionsDTO;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProductSessionConverter {
    private ProductSessionConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ProductSessionsPublishingDTO allSessionsScope() {
        ProductSessionsPublishingDTO out = new ProductSessionsPublishingDTO();
        out.setType(SelectionType.ALL);
        return out;
    }

    public static ProductSessionsPublishingDTO toProductSessions
            (List<ProductSessionRecord> productSessionRecords) {
        ProductSessionsPublishingDTO result = new ProductSessionsPublishingDTO();
        result.setSessions(productSessionRecords.stream()
                .map(ProductSessionConverter::toProductSession)
                .collect(Collectors.toSet()));
        result.setType(SelectionType.RESTRICTED);
        return result;
    }

    private static ProductSessionBaseDTO toProductSession(ProductSessionRecord productSessionRecord) {
        ProductSessionBaseDTO dto = new ProductSessionBaseDTO();
        dto.setId(productSessionRecord.getSessionid().longValue());
        dto.setName(productSessionRecord.getSession().getNombre());
        dto.setDates(toDate(productSessionRecord.getSession()));

        return dto;
    }


    private static SessionDateDTO toDate(CpanelSesionRecord session) {
        SessionDateDTO sessionDateDTO = new SessionDateDTO();
        sessionDateDTO.setStart(CommonUtils.timestampToZonedDateTime(session.getFechainiciosesion()));
        sessionDateDTO.setEnd(CommonUtils.timestampToZonedDateTime(session.getFecharealfinsesion()));
        return sessionDateDTO;
    }

    public static ProductSessionsDTO toProductSessionsResponse(SessionsDTO sessionsDTO) {
        ProductSessionsDTO sessions = new ProductSessionsDTO();
        sessions.setMetadata(sessionsDTO.getMetadata());
        sessions.setData(convertToSessionsData(sessionsDTO.getData()));
        return sessions;
    }

    public static ListWithMetadata<ProductChannelSessionDTO> toProductSessionResponse(SessionsDTO sessions) {
        ListWithMetadata<ProductChannelSessionDTO> response = new ListWithMetadata<>();
        response.setData(convertToProductChannelSessionsData(sessions.getData()));
        response.setMetadata(sessions.getMetadata());
        return response;
    }

    private static List<ProductSessionDTO> convertToSessionsData(List<SessionDTO> sessionDTOList) {
        return sessionDTOList == null ? new ArrayList<>() : sessionDTOList.stream()
                .map(ProductSessionConverter::convertToSessionData)
                .filter(Objects::nonNull)
                .toList();
    }

    private static ProductSessionDTO convertToSessionData(SessionDTO sessionDTO) {
        if (sessionDTO == null) {
            return null;
        }

        ProductSessionDTO sessionProduct = new ProductSessionDTO();
        sessionProduct.setIsSmartBooking(sessionDTO.getIsSmartBooking());
        return (ProductSessionDTO) convertToBaseSessionData(sessionDTO, sessionProduct);
    }

    private static ProductSessionBaseDTO convertToBaseSessionData(SessionDTO sessionDTO, ProductSessionBaseDTO sessionProduct) {
        sessionProduct.setId(sessionDTO.getId());
        sessionProduct.setName(sessionDTO.getName());
        SessionDateDTO dates = new SessionDateDTO();
        dates.setStart(sessionDTO.getDate().getStart());
        dates.setEnd(sessionDTO.getDate().getEnd());
        sessionProduct.setDates(dates);
        return sessionProduct;
    }

    public static SessionSearchFilter convertToSessionFilter(ProductSessionSearchFilter filter) {
        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setLimit(filter.getLimit());
        sessionFilter.setOffset(filter.getOffset());
        sessionFilter.setFreeSearch(filter.getFreeSearch());
        sessionFilter.setStartDate(filter.getStartDate());
        sessionFilter.setEndDate(filter.getEndDate());
        return sessionFilter;
    }

    private static List<ProductChannelSessionDTO> convertToProductChannelSessionsData(List<SessionDTO> sessions) {
        return sessions == null ? new ArrayList<>() : sessions.stream()
                .map(ProductSessionConverter::convertToProductChannelSessionData)
                .collect(Collectors.toList());
    }

    private static ProductChannelSessionDTO convertToProductChannelSessionData(SessionDTO sessionDTO) {
        if (sessionDTO == null) {
            return null;
        }

        ProductChannelSessionDTO sessionProduct = new ProductChannelSessionDTO();
        convertToBaseSessionData(sessionDTO, sessionProduct);
        sessionProduct.getDates().setChannelPublication(sessionDTO.getDate().getChannelPublication());
        sessionProduct.setStatus(sessionDTO.getStatus());
        return sessionProduct;
    }
}

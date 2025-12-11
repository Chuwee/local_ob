package es.onebox.mgmt.products.converter;

import es.onebox.mgmt.channels.utils.ChannelsUrlUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannelSession;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannelSessions;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductPublishingSessions;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSession;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSessionBase;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSessions;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSessions;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus;
import es.onebox.mgmt.products.dto.ProductChannelSessionLinkDTO;
import es.onebox.mgmt.products.dto.ProductPublishingSessionsDTO;
import es.onebox.mgmt.products.dto.ProductSessionBaseDTO;
import es.onebox.mgmt.products.dto.ProductSessionDTO;
import es.onebox.mgmt.products.dto.ProductSessionSmartBookingDTO;
import es.onebox.mgmt.products.dto.ProductSessionVariantDTO;
import es.onebox.mgmt.products.dto.ProductSessionsDTO;
import es.onebox.mgmt.products.dto.UpdateProductSessionsDTO;
import es.onebox.mgmt.products.enums.SelectionType;
import es.onebox.mgmt.sessions.enums.SessionSmartBookingType;
import org.apache.commons.collections4.CollectionUtils;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductSessionConverter {
    private ProductSessionConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static ProductPublishingSessionsDTO toProductPublishingSessionsDto(ProductPublishingSessions productSessions) {
        if (productSessions == null) {
            return null;
        }

        ProductPublishingSessionsDTO productSessionsDTO = new ProductPublishingSessionsDTO();
        productSessionsDTO.setType(productSessions.getType());

        if (productSessionsDTO.getType().equals(SelectionType.ALL)) {
            return productSessionsDTO;
        }

        Set<ProductSessionDTO> sessionDTOSet = new HashSet<>();
        if (productSessions.getSessions() != null) {
            for (ProductSessionBase session : productSessions.getSessions()) {
                ProductSessionDTO sessionDTO = new ProductSessionDTO();
                fillProductSessionBaseDTO(session, sessionDTO);
                sessionDTOSet.add(sessionDTO);
            }
        }
        productSessionsDTO.setSessions(sessionDTOSet);

        return productSessionsDTO;
    }

    private static ProductSessionDTO toProductSessionDto(ProductSession productSession) {
        if (productSession == null) {
            return null;
        }
        ProductSessionDTO productSessionDTO = new ProductSessionDTO();
        fillProductSessionBaseDTO(productSession, productSessionDTO);
        if (CollectionUtils.isNotEmpty(productSession.getVariants())) {
            productSessionDTO.setVariants(productSession.getVariants().stream().map(v -> {
                        ProductSessionVariantDTO variant = new ProductSessionVariantDTO();
                        variant.setId(v.getId());
                        variant.setUseCustomStock(v.getUseCustomStock());
                        variant.setStock(v.getStock());
                        variant.setUseCustomPrice(v.getUseCustomPrice());
                        variant.setPrice(v.getPrice());
                        return variant;
                    }
            ).toList());
        }
        //TODO remove after migration
        productSessionDTO.setStock(productSession.getStock());
        productSessionDTO.setUseCustomStock(productSession.getUseCustomStock());
        productSessionDTO.setSmartBooking(toSmartBookingDTO(productSession));

        return productSessionDTO;
    }

    private static void fillProductSessionBaseDTO(ProductSessionBase in, ProductSessionBaseDTO out) {
        out.setId(in.getId());
        out.setName(in.getName());
        out.setDates(in.getDates());
    }

    private static ProductSessionSmartBookingDTO toSmartBookingDTO(ProductSession in) {
        if (in.getIsSmartBooking() == null) {
            return null;
        }
        ProductSessionSmartBookingDTO smartBooking = new ProductSessionSmartBookingDTO();
        smartBooking.setType(in.getIsSmartBooking() ? SessionSmartBookingType.SMART_BOOKING : SessionSmartBookingType.SEAT_SELECTION);
        return smartBooking;
    }

    public static UpdateProductSessions toUpdateProductSessions(UpdateProductSessionsDTO request) {
        UpdateProductSessions update = new UpdateProductSessions();
        update.setSessions(request.getSessions());
        update.setType(request.getType());

        return update;
    }

    public static ProductSessionsDTO toProductSessionsDTO(ProductSessions productSessions) {

        ProductSessionsDTO productSessionsDTO = new ProductSessionsDTO();
        productSessionsDTO.setMetadata(productSessions.getMetadata());
        productSessionsDTO.setData(convertToProductSessionsDTO(productSessions.getData()));
        return productSessionsDTO;
    }

    private static List<ProductSessionDTO> convertToProductSessionsDTO(List<ProductSession> data) {
        return data.stream().map(ProductSessionConverter::toProductSessionDto).filter(Objects::nonNull).toList();
    }

    public static List<ProductChannelSessionLinkDTO> convertSessionToLinks(ProductChannelSessions sessions,
                                                                           ChannelConfig channelConfig, String language,
                                                                           String urlChannels) {
        return sessions.getData()
                .stream()
                .map(session -> convertSessionToLink(channelConfig, language, urlChannels, session))
                .collect(Collectors.toList());
    }

    private static ProductChannelSessionLinkDTO convertSessionToLink(ChannelConfig channelConfig, String language, String urlChannels, ProductChannelSession session) {
        ProductChannelSessionLinkDTO linkDTO = new ProductChannelSessionLinkDTO();
        linkDTO.setId(session.getId());
        linkDTO.setName(session.getName());
        boolean isPreview = session.getStatus().equals(SessionStatus.PREVIEW);
        linkDTO.setLink(ChannelsUrlUtils.buildUrlProductsBySession(urlChannels, channelConfig.getUrl(),
                session.getId(), language, isPreview, channelConfig.getChannelType()));
        linkDTO.setEnabled((SessionStatus.READY.equals(session.getStatus())
                && session.getDates().getChannelPublication().absolute().toInstant().compareTo(Instant.now()) <= 0)
                || SessionStatus.PREVIEW.equals(session.getStatus()));
        return linkDTO;
    }
}

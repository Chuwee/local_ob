package es.onebox.fever.service;

import es.onebox.common.datasources.ms.event.dto.ProductChannelDTO;
import es.onebox.common.datasources.ms.event.dto.ProductCommunicationElementsImagesDTO;
import es.onebox.common.datasources.ms.event.dto.ProductCommunicationElementsTextsDTO;
import es.onebox.common.datasources.ms.event.dto.ProductDTO;
import es.onebox.common.datasources.ms.event.dto.ProductEvents;
import es.onebox.common.datasources.ms.event.dto.ProductPublishingSessions;
import es.onebox.common.datasources.ms.event.dto.ProductLanguages;
import es.onebox.common.datasources.ms.event.dto.ProductSurchargeDTO;
import es.onebox.common.datasources.ms.event.dto.ProductVariants;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductDetailDTO;
import es.onebox.common.datasources.webhook.dto.fever.ProductUpdate;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.fever.converter.CommonConverter;
import es.onebox.fever.converter.ProductConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductWebhookService {
    private final MsEventRepository eventRepository;

    @Autowired
    public ProductWebhookService(MsEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public WebhookFeverDTO sendProductGeneralData(WebhookFeverDTO webhookFeverDTO) {

        ProductDTO product = eventRepository.getProduct(Long.valueOf(webhookFeverDTO.getNotificationMessage().getId()));
        ProductDetailDTO productDetailDTO = ProductConverter.toProductDetailDTO(product);

        ProductUpdate productUpdate = new ProductUpdate();
        productUpdate.setProductDetails(productDetailDTO);

        return updateFeverMessage(webhookFeverDTO, productUpdate);
    }

    public WebhookFeverDTO sendProductSurcharges(WebhookFeverDTO webhookFeverDTO) {
        ProductDTO product = eventRepository.getProduct(Long.valueOf(webhookFeverDTO.getNotificationMessage().getId()));
        List<ProductSurchargeDTO> productSurcharges = eventRepository.getProductSurcharges(product.getProductId());

        ProductUpdate productUpdate = new ProductUpdate();
        productUpdate.setProductSurcharges(ProductConverter.toProductSurchargeDTO(productSurcharges));

        return updateFeverMessage(webhookFeverDTO, productUpdate);
    }

    public WebhookFeverDTO sendProductConfiguration (WebhookFeverDTO webhookFeverDTO) {
        Long productId = Long.valueOf(webhookFeverDTO.getNotificationMessage().getId());
        ProductVariants productVariants = eventRepository.getProductVariants(productId);

        ProductUpdate productUpdate = new ProductUpdate();
        productUpdate.setProductVariants(ProductConverter.toProductVariantsDTO(productVariants));

        return updateFeverMessage(webhookFeverDTO, productUpdate);
    }

    public WebhookFeverDTO sendProductEvents(WebhookFeverDTO webhookFeverDTO) {
        Long productId = Long.valueOf(webhookFeverDTO.getNotificationMessage().getId());
        ProductEvents productEvents = eventRepository.getProductEvents(productId);

        ProductUpdate productUpdate = new ProductUpdate();
        productUpdate.setProductEvents(ProductConverter.toProductEventsDTO(productEvents));

        return updateFeverMessage(webhookFeverDTO, productUpdate);
    }

    public WebhookFeverDTO sendProductSessions(WebhookFeverDTO webhookFeverDTO) {
        Long productId = Long.valueOf(webhookFeverDTO.getNotificationMessage().getId());
        Long eventId = webhookFeverDTO.getNotificationMessage().getEventId();

        if (eventId == null) {
            throw new OneboxRestException(ApiExternalErrorCode.EVENT_ID_REQUIRED);
        }

        ProductPublishingSessions productSessions = eventRepository.geProductSessions(productId, eventId);

        ProductUpdate productUpdate = new ProductUpdate();
        productUpdate.setProductSessions(ProductConverter.toProductPublishingSessionsDTO(productSessions));

        return updateFeverMessage(webhookFeverDTO, productUpdate);
    }

    public WebhookFeverDTO sendProductLanguages(WebhookFeverDTO webhookFeverDTO) {
        Long productId = Long.valueOf(webhookFeverDTO.getNotificationMessage().getId());
        ProductLanguages productLanguages = eventRepository.productLanguages(productId);

        ProductUpdate productUpdate = new ProductUpdate();
        productUpdate.setProductLanguages(ProductConverter.toProductLanguagesDTO(productLanguages));

        return updateFeverMessage(webhookFeverDTO, productUpdate);
    }

    public WebhookFeverDTO sendProductChannelLiterals(WebhookFeverDTO webhookFeverDTO) {
        Long productId = Long.valueOf(webhookFeverDTO.getNotificationMessage().getId());
        ProductCommunicationElementsTextsDTO productLiterals = eventRepository.getProductCommunicationElementsTexts(productId);

        ProductUpdate productUpdate = new ProductUpdate();
        productUpdate.setProductCommunicationElementTexts(ProductConverter.toProductCommunicationElementTextsDTO(productLiterals));

        return updateFeverMessage(webhookFeverDTO, productUpdate);
    }

    public WebhookFeverDTO sendProductChannelImages(WebhookFeverDTO webhookFeverDTO) {
        Long productId = Long.valueOf(webhookFeverDTO.getNotificationMessage().getId());
        ProductCommunicationElementsImagesDTO productImages = eventRepository.getProductCommunicationElementImages(productId);

        ProductUpdate productUpdate = new ProductUpdate();
        productUpdate.setProductCommunicationElementImages(ProductConverter.toProductCommunicationElementImagesDTO(productImages));

        return updateFeverMessage(webhookFeverDTO, productUpdate);
    }

    public WebhookFeverDTO sendProductChannelsUpdate(WebhookFeverDTO webhookFeverDTO) {
        Long productId = Long.valueOf(webhookFeverDTO.getNotificationMessage().getId());
        List<ProductChannelDTO> productChannels = eventRepository.getProductChannels(productId);

        ProductUpdate productUpdate = new ProductUpdate();
        productUpdate.setProductChannels(ProductConverter.toProductChannelDTO(productChannels));

        return updateFeverMessage(webhookFeverDTO, productUpdate);
    }

    public WebhookFeverDTO sendProductChannelUpdateSale(WebhookFeverDTO webhookFeverDTO) {
        Long productId = Long.valueOf(webhookFeverDTO.getNotificationMessage().getId());
        Long channelId = webhookFeverDTO.getNotificationMessage().getChannelId();

        if (channelId == null) {
            throw new OneboxRestException(ApiExternalErrorCode.CHANNEL_ID_REQUIRED);
        }

        ProductChannelDTO productChannel = eventRepository.getProductChannel(productId, channelId);

        ProductUpdate productUpdate = new ProductUpdate();
        productUpdate.setProductChannel(ProductConverter.toProductChannelDTO(productChannel));

        return updateFeverMessage(webhookFeverDTO, productUpdate);
    }

    private WebhookFeverDTO updateFeverMessage(WebhookFeverDTO webhookFeverDTO, ProductUpdate productUpdate) {
        webhookFeverDTO.setFeverMessage(CommonConverter.convert(webhookFeverDTO.getNotificationMessage()));
        webhookFeverDTO.getFeverMessage().setProductUpdate(productUpdate);
        return webhookFeverDTO;
    }
}

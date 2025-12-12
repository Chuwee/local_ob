package es.onebox.common.datasources.webhook.dto.fever;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductCommunicationElementImageFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductCommunicationElementTextFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductPublishingSessionsFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductLanguageFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductChannelFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductDetailDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductEventFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductSurchargeFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.product.ProductVariantsFeverDTO;

import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public class ProductUpdate {

    private ProductDetailDTO productDetails;
    private List<ProductLanguageFeverDTO> productLanguages;
    private List<ProductSurchargeFeverDTO> productSurcharges;
    private ProductVariantsFeverDTO productVariants;
    private List<ProductCommunicationElementTextFeverDTO> productCommunicationElementTexts;
    private List<ProductCommunicationElementImageFeverDTO> productCommunicationElementImages;
    private List<ProductEventFeverDTO> productEvents;
    private ProductPublishingSessionsFeverDTO productSessions;
    private List<ProductChannelFeverDTO> productChannels;
    private ProductChannelFeverDTO productChannel;

    public ProductUpdate() {}

    public ProductDetailDTO getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(ProductDetailDTO productDetails) {
        this.productDetails = productDetails;
    }

    public List<ProductLanguageFeverDTO> getProductLanguages() { return productLanguages; }

    public void setProductLanguages(List<ProductLanguageFeverDTO> productLanguages) { this.productLanguages = productLanguages; }

    public List<ProductSurchargeFeverDTO> getProductSurcharges() {
        return productSurcharges;
    }

    public void setProductSurcharges(List<ProductSurchargeFeverDTO> productSurcharges) {
        this.productSurcharges = productSurcharges;
    }

    public ProductVariantsFeverDTO getProductVariants() { return productVariants; }

    public void setProductVariants(ProductVariantsFeverDTO productVariants) { this.productVariants = productVariants; }

    public List<ProductChannelFeverDTO> getProductChannels() { return productChannels; }

    public void setProductChannels(List<ProductChannelFeverDTO> productChannels) { this.productChannels = productChannels; }

    public List<ProductCommunicationElementTextFeverDTO> getProductCommunicationElementTexts() {
        return productCommunicationElementTexts;
    }

    public void setProductCommunicationElementTexts(List<ProductCommunicationElementTextFeverDTO> productCommunicationElementTexts) {
        this.productCommunicationElementTexts = productCommunicationElementTexts;
    }

    public List<ProductCommunicationElementImageFeverDTO> getProductCommunicationElementImages() {
        return productCommunicationElementImages;
    }

    public void setProductCommunicationElementImages(List<ProductCommunicationElementImageFeverDTO> productCommunicationElementImages) {
        this.productCommunicationElementImages = productCommunicationElementImages;
    }

    public List<ProductEventFeverDTO> getProductEvents() { return productEvents; }

    public void setProductEvents(List<ProductEventFeverDTO> productEvents) { this.productEvents = productEvents; }

    public ProductPublishingSessionsFeverDTO getProductSessions() { return productSessions; }

    public void setProductSessions(ProductPublishingSessionsFeverDTO productSessions) { this.productSessions = productSessions; }

    public ProductChannelFeverDTO getProductChannel() { return productChannel; }

    public void setProductChannel(ProductChannelFeverDTO productChannel) { this.productChannel = productChannel; }
}

package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentImagePassbookList;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentImagePdfList;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentTextList;
import es.onebox.mgmt.products.enums.ticketContent.ProductTicketContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProductTicketContentRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public ProductTicketContentRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public void createTicketContentPdfText(final Long productId, final ProductTicketContentTextList contentTextLists, ProductTicketContentType ticketType) {
        msEventDatasource.createProductTicketContentPdfText(productId, contentTextLists, ticketType);
    }

    public ProductTicketContentTextList getProductTicketContentsPdfTexts(Long productId) {
        return msEventDatasource.getProductTicketContentPdfText(productId);
    }

    public void createTicketContentPdfImage(Long productId, final ProductTicketContentImagePdfList contentImageList, ProductTicketContentType ticketType) {
        msEventDatasource.createProductTicketContentPdfImage(productId, contentImageList, ticketType);
    }

    public ProductTicketContentImagePdfList getProductTicketContentsPdfImages(Long productId) {
        return msEventDatasource.getProductTicketContentPdfImage(productId);
    }

    public void deleteTicketContentsPdfImage(Long productId, String language) {
        msEventDatasource.deleteProductTicketContentsPdfImage(productId, language);
    }

    public void createTicketContentPassbookText(final Long productId, final ProductTicketContentTextList contentTextLists, ProductTicketContentType ticketType) {
        msEventDatasource.createProductTicketContentPassbookText(productId, contentTextLists, ticketType);
    }

    public ProductTicketContentTextList getProductTicketContentsPassbookTexts(Long productId) {
        return msEventDatasource.getProductTicketContentPassbookText(productId);
    }

    public void createTicketContentPassbookImage(Long productId, final ProductTicketContentImagePassbookList contentImageList, ProductTicketContentType ticketType) {
        msEventDatasource.createProductTicketContentPassbookImage(productId, contentImageList, ticketType);
    }

    public ProductTicketContentImagePassbookList getProductTicketContentsPassbookImages(Long productId) {
        return msEventDatasource.getProductTicketContentPassbookImage(productId);
    }

    public void deleteTicketContentsPassbookImage(Long productId, String language) {
        msEventDatasource.deleteProductTicketContentsPassbookImage(productId, language);
    }
}
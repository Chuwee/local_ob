package es.onebox.mgmt.products.service;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentImagePassbookList;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentImagePdfList;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentTextList;
import es.onebox.mgmt.datasources.ms.event.repository.ProductTicketContentRepository;
import es.onebox.mgmt.products.converter.ProductTicketContentConverter;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentImagePassbookListDTO;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentImagePdfListDTO;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentTextPassbookListDTO;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentTextPdfListDTO;
import es.onebox.mgmt.products.enums.ticketContent.ProductTicketContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductTicketContentService {
    private final ProductTicketContentRepository productTicketContentsRepository;

    @Autowired
    public ProductTicketContentService(ProductTicketContentRepository productTicketContentsRepository) {
        this.productTicketContentsRepository = productTicketContentsRepository;
    }

    public void createPdfTexts(Long productId, ProductTicketContentTextPdfListDTO textPdfListDTO, ProductTicketContentType ticketType) {
        ProductTicketContentTextList ticketContentTextLists = ProductTicketContentConverter.toTicketContentTextPDFList(textPdfListDTO);
        productTicketContentsRepository.createTicketContentPdfText(productId, ticketContentTextLists, ticketType);
    }

    public ProductTicketContentTextPdfListDTO getPdfTexts(Long productId) {
        ProductTicketContentTextList productTicketContentTextList = productTicketContentsRepository.getProductTicketContentsPdfTexts(productId);
        return ProductTicketContentConverter.toTextsPdfDto(productTicketContentTextList);
    }

    public void createPdfImages(final Long productId, ProductTicketContentImagePdfListDTO contentsDTO, ProductTicketContentType ticketType) {
        ProductTicketContentImagePdfList ticketContentImageLists = ProductTicketContentConverter.toTicketContentImagePDFList(contentsDTO);
        productTicketContentsRepository.createTicketContentPdfImage(productId, ticketContentImageLists, ticketType);
    }

    public ProductTicketContentImagePdfListDTO getPdfImages(Long productId) {
        ProductTicketContentImagePdfList ticketContentImageLists = productTicketContentsRepository.getProductTicketContentsPdfImages(productId);
        return ProductTicketContentConverter.toImagePdfDto(ticketContentImageLists);
    }

    public void deletePdfImages(Long productId, String language) {
        productTicketContentsRepository.deleteTicketContentsPdfImage(productId, ConverterUtils.toLocale(language));
    }


    public void createPassbookTexts(final Long productId, ProductTicketContentTextPassbookListDTO contentsDTO, ProductTicketContentType ticketType) {
        ProductTicketContentTextList ticketContentTextLists = ProductTicketContentConverter.toTicketContentTextPASSBOOKList(contentsDTO);
        productTicketContentsRepository.createTicketContentPassbookText(productId, ticketContentTextLists, ticketType);
    }

    public ProductTicketContentTextPassbookListDTO getPassbookTexts(Long productId) {
        ProductTicketContentTextList productTicketContentTextList = productTicketContentsRepository.getProductTicketContentsPassbookTexts(productId);
        return ProductTicketContentConverter.toTextsPassbookDto(productTicketContentTextList);
    }

    public void createPassbookImages(final Long productId, ProductTicketContentImagePassbookListDTO contentsDTO, ProductTicketContentType ticketType) {
        ProductTicketContentImagePassbookList ticketContentImageLists = ProductTicketContentConverter.toTicketContentImagePASSBOOKList(contentsDTO);
        productTicketContentsRepository.createTicketContentPassbookImage(productId, ticketContentImageLists, ticketType);
    }

    public ProductTicketContentImagePassbookListDTO getPassbookImages(Long productId) {
        ProductTicketContentImagePassbookList ticketContentImageLists = productTicketContentsRepository.getProductTicketContentsPassbookImages(productId);
        return ProductTicketContentConverter.toImagePassbookDto(ticketContentImageLists);
    }

    public void deletePassbookImages(Long productId, String language) {
        productTicketContentsRepository.deleteTicketContentsPassbookImage(productId, ConverterUtils.toLocale(language));
    }
}

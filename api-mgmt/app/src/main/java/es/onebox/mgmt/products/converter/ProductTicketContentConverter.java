package es.onebox.mgmt.products.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentImagePassbook;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentImagePassbookList;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentImagePdf;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentImagePdfList;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentText;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentTextList;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentImagePassbookDTO;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentImagePassbookListDTO;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentImagePdfDTO;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentImagePdfListDTO;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentTextDTO;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentTextPassbookListDTO;
import es.onebox.mgmt.products.dto.ticketContent.ProductTicketContentTextPdfListDTO;
import es.onebox.mgmt.products.enums.ticketContent.ProductTicketContentTextType;

public class ProductTicketContentConverter {
    private ProductTicketContentConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static ProductTicketContentTextList toTicketContentTextPDFList(ProductTicketContentTextPdfListDTO contentsTextPDFListDTO) {
        ProductTicketContentTextList productTicketContentTextList = new ProductTicketContentTextList();
        for (ProductTicketContentTextDTO dto : contentsTextPDFListDTO) {
            ProductTicketContentText<ProductTicketContentTextType> ticketContentText = new ProductTicketContentText<>();
            ticketContentText.setType(dto.getType());
            ticketContentText.setLanguage(ConverterUtils.toLocale(dto.getLanguage()));
            ticketContentText.setValue(dto.getValue());

            productTicketContentTextList.add(ticketContentText);
        }
        return productTicketContentTextList;
    }

    public static ProductTicketContentImagePdfList toTicketContentImagePDFList(ProductTicketContentImagePdfListDTO contentsImagePDFListDTO) {
        ProductTicketContentImagePdfList ticketContentImagePdfList = new ProductTicketContentImagePdfList();
        for (ProductTicketContentImagePdfDTO dto : contentsImagePDFListDTO) {
            ProductTicketContentImagePdf ticketContentImagePdf = new ProductTicketContentImagePdf();
            ticketContentImagePdf.setType(dto.getType());
            ticketContentImagePdf.setLanguage(ConverterUtils.toLocale(dto.getLanguage()));
            ticketContentImagePdf.setImageUrl(dto.getImageUrl());

            ticketContentImagePdfList.add(ticketContentImagePdf);
        }
        return ticketContentImagePdfList;
    }

    public static ProductTicketContentTextList toTicketContentTextPASSBOOKList(ProductTicketContentTextPassbookListDTO contentsTextPASSBOOKListDTO) {
        ProductTicketContentTextList productTicketContentTextList = new ProductTicketContentTextList();
        for (ProductTicketContentTextDTO dto : contentsTextPASSBOOKListDTO) {
            ProductTicketContentText<ProductTicketContentTextType> ticketContentText = new ProductTicketContentText<>();
            ticketContentText.setType(dto.getType());
            ticketContentText.setLanguage(ConverterUtils.toLocale(dto.getLanguage()));
            ticketContentText.setValue(dto.getValue());

            productTicketContentTextList.add(ticketContentText);
        }
        return productTicketContentTextList;
    }

    public static ProductTicketContentImagePassbookList toTicketContentImagePASSBOOKList(ProductTicketContentImagePassbookListDTO contentsImagePassbookListDTO) {
        ProductTicketContentImagePassbookList ticketContentImagePassbookList = new ProductTicketContentImagePassbookList();
        for (ProductTicketContentImagePassbookDTO dto : contentsImagePassbookListDTO) {
            ProductTicketContentImagePassbook ticketContentImage = new ProductTicketContentImagePassbook();
            ticketContentImage.setType(dto.getType());
            ticketContentImage.setLanguage(ConverterUtils.toLocale(dto.getLanguage()));
            ticketContentImage.setImageUrl(dto.getImageUrl());

            ticketContentImagePassbookList.add(ticketContentImage);
        }
        return ticketContentImagePassbookList;
    }

    public static ProductTicketContentTextPdfListDTO toTextsPdfDto(ProductTicketContentTextList ticketContentTexts) {
        ProductTicketContentTextPdfListDTO ticketContentTextPdfListDTO = new ProductTicketContentTextPdfListDTO();

        for (ProductTicketContentText<ProductTicketContentTextType> ticketContentText : ticketContentTexts) {
            ProductTicketContentTextDTO ticketContentTextDTO = new ProductTicketContentTextDTO();
            ticketContentTextDTO.setType(ticketContentText.getType());
            ticketContentTextDTO.setLanguage(ConverterUtils.toLanguageTag(ticketContentText.getLanguage()));
            ticketContentTextDTO.setValue(ticketContentText.getValue());
            ticketContentTextPdfListDTO.add(ticketContentTextDTO);
        }
        return ticketContentTextPdfListDTO;
    }

    public static ProductTicketContentTextPassbookListDTO toTextsPassbookDto(ProductTicketContentTextList ticketContentTexts) {
        ProductTicketContentTextPassbookListDTO ticketContentTextPassbookListDTO = new ProductTicketContentTextPassbookListDTO();

        for (ProductTicketContentText<ProductTicketContentTextType> ticketContentText : ticketContentTexts) {
            ProductTicketContentTextDTO ticketContentTextDTO = new ProductTicketContentTextDTO();
            ticketContentTextDTO.setType(ticketContentText.getType());
            ticketContentTextDTO.setLanguage(ConverterUtils.toLanguageTag(ticketContentText.getLanguage()));
            ticketContentTextDTO.setValue(ticketContentText.getValue());
            ticketContentTextPassbookListDTO.add(ticketContentTextDTO);
        }
        return ticketContentTextPassbookListDTO;
    }

    public static ProductTicketContentImagePdfListDTO toImagePdfDto(ProductTicketContentImagePdfList ticketContentImages) {
        ProductTicketContentImagePdfListDTO ticketContentImagePdfListDTO = new ProductTicketContentImagePdfListDTO();

        for (ProductTicketContentImagePdf ticketContentImagePdf : ticketContentImages) {
            ProductTicketContentImagePdfDTO ticketContentImagePdfDTO = new ProductTicketContentImagePdfDTO();
            ticketContentImagePdfDTO.setType(ticketContentImagePdf.getType());
            ticketContentImagePdfDTO.setLanguage(ConverterUtils.toLanguageTag(ticketContentImagePdf.getLanguage()));
            ticketContentImagePdfDTO.setImageUrl(ticketContentImagePdf.getImageUrl());
            ticketContentImagePdfListDTO.add(ticketContentImagePdfDTO);
        }
        return ticketContentImagePdfListDTO;
    }

    public static ProductTicketContentImagePassbookListDTO toImagePassbookDto(ProductTicketContentImagePassbookList ticketContentImages) {
        ProductTicketContentImagePassbookListDTO ticketContentImagePassbookListDTO = new ProductTicketContentImagePassbookListDTO();

        for (ProductTicketContentImagePassbook productTicketContentImagePdf : ticketContentImages) {
            ProductTicketContentImagePassbookDTO ticketContentImagePassbookDTO = new ProductTicketContentImagePassbookDTO();
            ticketContentImagePassbookDTO.setType(productTicketContentImagePdf.getType());
            ticketContentImagePassbookDTO.setLanguage(ConverterUtils.toLanguageTag(productTicketContentImagePdf.getLanguage()));
            ticketContentImagePassbookDTO.setImageUrl(productTicketContentImagePdf.getImageUrl());
            ticketContentImagePassbookListDTO.add(ticketContentImagePassbookDTO);
        }
        return ticketContentImagePassbookListDTO;
    }
}

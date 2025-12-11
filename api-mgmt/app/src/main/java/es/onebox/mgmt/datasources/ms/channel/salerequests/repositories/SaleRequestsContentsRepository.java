package es.onebox.mgmt.datasources.ms.channel.salerequests.repositories;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.MsSaleRequestsDatasource;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.SaleRequestAgreement;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.SaleRequestCommunicationElementDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.TicketCommunicationElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SaleRequestsContentsRepository {

    private final MsSaleRequestsDatasource msSaleRequestsDatasource;

    @Autowired
    public SaleRequestsContentsRepository(MsSaleRequestsDatasource msSaleRequestsDatasource) {
        this.msSaleRequestsDatasource = msSaleRequestsDatasource;
    }

    public List<TicketCommunicationElement> getSaleRequestTicketPdfImages(Long saleRequestId, String language) {
        return this.msSaleRequestsDatasource.getSaleRequestTicketPdfImages(saleRequestId, language);
    }

    public void updateSaleRequestTicketPdfImages(Long saleRequestId, List<TicketCommunicationElement> comTicketElements) {
        this.msSaleRequestsDatasource.updateSaleRequestTicketPdfImages(saleRequestId, comTicketElements);
    }

    public void deleteSaleRequestTicketPdfImages(Long saleRequestId, TicketCommunicationElement comTicketElement) {
        this.msSaleRequestsDatasource.deleteSaleRequestTicketPdfImages(saleRequestId, comTicketElement);
    }

    public List<TicketCommunicationElement> getSaleRequestTicketPrinterImages(Long saleRequestId, String language) {
        return this.msSaleRequestsDatasource.getSaleRequestTicketPrinterImages(saleRequestId, language);
    }

    public void updateSaleRequestTicketPrinterImages(Long saleRequestId, List<TicketCommunicationElement> comTicketElements) {
        this.msSaleRequestsDatasource.updateSaleRequestTicketPrinterImages(saleRequestId, comTicketElements);
    }

    public void deleteSaleRequestTicketPrinterImages(Long saleRequestId, TicketCommunicationElement comTicketElement) {
        this.msSaleRequestsDatasource.deleteSaleRequestTicketPrinterImages(saleRequestId, comTicketElement);
    }

    public List<SaleRequestAgreement> getSaleRequestAgreements(Long saleRequestId) {
        return msSaleRequestsDatasource.getAgreements(saleRequestId);
    }

    public IdDTO createSaleRequestAgreement(Long saleRequestId, SaleRequestAgreement body) {
        return msSaleRequestsDatasource.createAgreement(saleRequestId, body);
    }

    public void updateSaleRequestAgreement(Long saleRequestId, Long channelAgreementId, SaleRequestAgreement body) {
        msSaleRequestsDatasource.updateAgreement(saleRequestId, channelAgreementId, body);
    }

    public void deleteSaleRequestAgreement(Long saleRequestId, Long agreementId) {
        msSaleRequestsDatasource.deleteAgreement(saleRequestId, agreementId);
    }

    public SaleRequestCommunicationElementDTO getCommunicationElements(Long saleRequestId) {
        return msSaleRequestsDatasource.getCommunicationElements(saleRequestId);
    }

    public void updateCommunicationElementsBySaleRequest(Long saleRequestId, SaleRequestCommunicationElementDTO comPurchaseElement) {
        msSaleRequestsDatasource.updateCommunicationElementsBySaleRequest(saleRequestId, comPurchaseElement);
    }

    public void deleteCommunicationElementsBySaleRequest(Long saleRequestId, SaleRequestCommunicationElementDTO comPurchaseElement) {
        msSaleRequestsDatasource.deleteCommunicationElementsBySaleRequest(saleRequestId, comPurchaseElement);
    }
}

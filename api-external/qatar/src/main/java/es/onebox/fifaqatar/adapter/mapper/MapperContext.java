package es.onebox.fifaqatar.adapter.mapper;

import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.event.dto.response.catalog.event.EventCatalog;
import es.onebox.common.datasources.ms.event.dto.response.catalog.session.SessionCatalog;
import es.onebox.common.datasources.ms.event.dto.response.session.passbook.SessionPassbookCommElement;
import es.onebox.common.datasources.ms.event.dto.response.session.secmkt.SessionSecMktConfig;
import es.onebox.fifaqatar.config.config.FifaQatarConfigDocument;
import es.onebox.fifaqatar.config.translation.FifaQatarTranslation;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class MapperContext implements Serializable {

    @Serial
    private static final long serialVersionUID = -3184121233115479338L;

    private String barcodeUrl;
    private String barcodeSigningKey;
    private String customerAccessToken;
    private String accountProfileUrl;
    private String accountTicketsUrl;
    private String accountTicketsTransferUrl;
    private String accountSecMktUrl;
    private String currentLang;
    private FifaQatarConfigDocument mainConfig;
    private FifaQatarTranslation dictionary;
    private Customer currentCustomer;

    private SessionCatalog sessionCatalog;
    private SessionSecMktConfig sessionSecMktConfig;
    private EventCatalog eventCatalog;
    private List<SessionPassbookCommElement> sessionPassbookCommElements;

    public String getBarcodeUrl() {
        return barcodeUrl;
    }

    public void setBarcodeUrl(String barcodeUrl) {
        this.barcodeUrl = barcodeUrl;
    }

    public String getCustomerAccessToken() {
        return customerAccessToken;
    }

    public void setCustomerAccessToken(String customerAccessToken) {
        this.customerAccessToken = customerAccessToken;
    }

    public String getBarcodeSigningKey() {
        return barcodeSigningKey;
    }

    public void setBarcodeSigningKey(String barcoderSigningKey) {
        this.barcodeSigningKey = barcoderSigningKey;
    }

    public String getAccountProfileUrl() {
        return accountProfileUrl;
    }

    public void setAccountProfileUrl(String accountProfileUrl) {
        this.accountProfileUrl = accountProfileUrl;
    }

    public String getAccountTicketsUrl() {
        return accountTicketsUrl;
    }

    public void setAccountTicketsUrl(String accountTicketsUrl) {
        this.accountTicketsUrl = accountTicketsUrl;
    }

    public String getAccountSecMktUrl() {
        return accountSecMktUrl;
    }

    public void setAccountSecMktUrl(String accountSecMktUrl) {
        this.accountSecMktUrl = accountSecMktUrl;
    }

    public FifaQatarConfigDocument getMainConfig() {
        return mainConfig;
    }

    public void setMainConfig(FifaQatarConfigDocument mainConfig) {
        this.mainConfig = mainConfig;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    public String getCurrentLang() {
        return currentLang;
    }

    public void setCurrentLang(String currentLang) {
        this.currentLang = currentLang;
    }

    public FifaQatarTranslation getDictionary() {
        return dictionary;
    }

    public void setDictionary(FifaQatarTranslation dictionary) {
        this.dictionary = dictionary;
    }

    public SessionCatalog getSessionCatalog() {
        return sessionCatalog;
    }

    public void setSessionCatalog(SessionCatalog sessionCatalog) {
        this.sessionCatalog = sessionCatalog;
    }

    public SessionSecMktConfig getSessionSecMktConfig() {
        return sessionSecMktConfig;
    }

    public void setSessionSecMktConfig(SessionSecMktConfig sessionSecMktConfig) {
        this.sessionSecMktConfig = sessionSecMktConfig;
    }

    public EventCatalog getEventCatalog() {
        return eventCatalog;
    }

    public void setEventCatalog(EventCatalog eventCatalog) {
        this.eventCatalog = eventCatalog;
    }

    public List<SessionPassbookCommElement> getSessionPassbookCommElements() {
        return sessionPassbookCommElements;
    }

    public void setSessionPassbookCommElements(List<SessionPassbookCommElement> sessionPassbookCommElements) {
        this.sessionPassbookCommElements = sessionPassbookCommElements;
    }

    public String getAccountTicketsTransferUrl() {
        return accountTicketsTransferUrl;
    }

    public void setAccountTicketsTransferUrl(String accountTicketsTransferUrl) {
        this.accountTicketsTransferUrl = accountTicketsTransferUrl;
    }
}

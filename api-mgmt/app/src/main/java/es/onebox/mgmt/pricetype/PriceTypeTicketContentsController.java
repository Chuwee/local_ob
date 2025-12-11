package es.onebox.mgmt.pricetype;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePassbookType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentImagePASSBOOKFilter;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentImagePDFFilter;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentImagePrinterFilter;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentTextFilter;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentTextPASSBOOKFilter;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsImagePASSBOOKListDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsImagePDFListDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsImagePrinterListDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsTextListDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsTextPASSBOOKListDTO;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ApiConfig.BASE_URL + "/venue-templates/{venueTemplateId}/price-types/{priceTypeId}/ticket-contents")
public class PriceTypeTicketContentsController {

    private static final String VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0 = "Venue Template Id must be above 0";
    private static final String PRICE_TYPE_ID_MUST_BE_ABOVE_0 = "Price Type Id must be above 0";
    private static final String AUDIT_COLLECTION = "PRICETYPE_TICKETCOMMUNICATIONELEMENTS";
    private static final String AUDIT_SUBCOLLECTION_PDF = "PRICETYPE_TICKETCOMMUNICATIONELEMENTS_PDF";
    private static final String AUDIT_SUBCOLLECTION_PRINTER = "PRICETYPE_TICKETCOMMUNICATIONELEMENTS_PRINTER";
    private static final String AUDIT_SUBCOLLECTION_PASSBOOK = "PRICETYPE_TICKETCOMMUNICATIONELEMENTS_PASSBOOK";

    private final PriceTypeTicketContentsService priceTypeTicketContentsService;

    @Autowired
    public PriceTypeTicketContentsController(PriceTypeTicketContentsService priceTypeTicketContentsService) {
        this.priceTypeTicketContentsService = priceTypeTicketContentsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PDF/texts")
    public PriceTypeTicketContentsTextListDTO getTicketsPDFTextContents(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                                                        @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                                                        @BindUsingJackson @Valid PriceTypeTicketContentTextFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_GET);
        return this.priceTypeTicketContentsService.getPriceTypeTicketContentsTexts(venueTemplateId, priceTypeId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PDF/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPDFTextContents(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                            @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                            @Valid @NotEmpty @RequestBody PriceTypeTicketContentsTextListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_UPDATE);
        this.priceTypeTicketContentsService.updatePriceTypeTicketContentsTexts(venueTemplateId, priceTypeId, contents);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PDF/images")
    public PriceTypeTicketContentsImagePDFListDTO getPriceTypeTicketsPDFImageContents(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                                                                      @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                                                                      @BindUsingJackson @Valid PriceTypeTicketContentImagePDFFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_GET);
        return this.priceTypeTicketContentsService.getPriceTypeTicketContentsPDFImages(venueTemplateId, priceTypeId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PDF/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updatePriceTypeTicketPDFImageContents(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                                      @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                                      @Valid @NotEmpty @RequestBody PriceTypeTicketContentsImagePDFListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_UPDATE);
        this.priceTypeTicketContentsService.updatePriceTypeTicketContentsPDFImages(venueTemplateId, priceTypeId, contents);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/PDF/images/languages/{language}/types/{type}")
    public void deletePriceTypeTicketPDFContentsImage(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                                      @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                                      @PathVariable @LanguageIETF String language,
                                                      @PathVariable TicketContentImagePDFType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_DELETE);
        this.priceTypeTicketContentsService.deletePriceTypeTicketContentPDFImage(venueTemplateId, priceTypeId, language, type);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PRINTER/texts")
    public PriceTypeTicketContentsTextListDTO getTicketsPrinterTextContents(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                                                            @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                                                            @BindUsingJackson @Valid PriceTypeTicketContentTextFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_GET);
        return this.priceTypeTicketContentsService.getPriceTypeTicketPrinterContentsTexts(venueTemplateId, priceTypeId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PRINTER/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPrinterTextContents(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                                @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                                @Valid @NotEmpty @RequestBody PriceTypeTicketContentsTextListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_UPDATE);
        this.priceTypeTicketContentsService.updatePriceTypeTicketPrinterContentsTexts(venueTemplateId, priceTypeId, contents);
    }


    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PRINTER/images")
    public PriceTypeTicketContentsImagePrinterListDTO getPriceTypeTicketsPrinterImageContents(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                                                                              @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                                                                              @BindUsingJackson @Valid PriceTypeTicketContentImagePrinterFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_GET);
        return this.priceTypeTicketContentsService.getPriceTypeTicketContentsPrinterImages(venueTemplateId, priceTypeId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PRINTER/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updatePriceTypeTicketPRINTERImageContents(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                                          @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                                          @Valid @NotEmpty @RequestBody PriceTypeTicketContentsImagePrinterListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_UPDATE);
        this.priceTypeTicketContentsService.updatePriceTypeTicketContentsPRINTERImages(venueTemplateId, priceTypeId, contents);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/PRINTER/images/languages/{language}/types/{type}")
    public void deletePriceTypeTicketPRINTERContentsImage(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                                          @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                                          @PathVariable @LanguageIETF String language, @PathVariable TicketContentImagePrinterType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_DELETE);
        this.priceTypeTicketContentsService.deletePriceTypeTicketContentPRINTERImage(venueTemplateId, priceTypeId, language, type);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PASSBOOK/texts")
    public PriceTypeTicketContentsTextPASSBOOKListDTO getTicketsPASSBOOKTextContents(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                                                                     @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                                                                     @BindUsingJackson @Valid PriceTypeTicketContentTextPASSBOOKFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_GET);
        return this.priceTypeTicketContentsService.getPriceTypeTicketPassbookContentsTexts(venueTemplateId, priceTypeId, filter, TicketCommunicationElementCategory.PASSBOOK);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PASSBOOK/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPASSBOOKTextContents(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                                 @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                                 @Valid @NotEmpty @RequestBody PriceTypeTicketContentsTextPASSBOOKListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_UPDATE);
        this.priceTypeTicketContentsService.updatePriceTypeTicketPassbookContentsTexts(venueTemplateId, priceTypeId, contents, TicketCommunicationElementCategory.PASSBOOK);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PASSBOOK/images")
    public PriceTypeTicketContentsImagePASSBOOKListDTO getPriceTypeTicketsPASSBOOKImageContents(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                                                                                @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                                                                                @BindUsingJackson @Valid PriceTypeTicketContentImagePASSBOOKFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_GET);
        return this.priceTypeTicketContentsService.getPriceTypeTicketContentsPASSBOOKImages(venueTemplateId, priceTypeId, filter, TicketCommunicationElementCategory.PASSBOOK);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PASSBOOK/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updatePriceTypeTicketPASSBOOKImageContents(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                                           @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                                           @Valid @NotEmpty @RequestBody PriceTypeTicketContentsImagePASSBOOKListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_UPDATE);
        this.priceTypeTicketContentsService.updatePriceTypeTicketContentsPASSBOOKImages(venueTemplateId, priceTypeId, contents, TicketCommunicationElementCategory.PASSBOOK);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/PASSBOOK/images/languages/{language}/types/{type}")
    public void deletePriceTypeTicketPASSBOOKContentsImage(@PathVariable @Min(value = 1, message = VENUE_TEMPLATE_ID_MUST_BE_ABOVE_0) Long venueTemplateId,
                                                           @PathVariable @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
                                                           @PathVariable @LanguageIETF String language, @PathVariable TicketContentImagePassbookType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_DELETE);
        this.priceTypeTicketContentsService.deletePriceTypeTicketContentPASSBOOKImage(venueTemplateId, priceTypeId, language, type, TicketCommunicationElementCategory.PASSBOOK);
    }

}

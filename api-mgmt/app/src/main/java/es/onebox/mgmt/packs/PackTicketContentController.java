package es.onebox.mgmt.packs;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentImagePDFFilter;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentImagePrinterFilter;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentTextPDFFilter;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentsImagePDFListDTO;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentsImagePrinterListDTO;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentsTextListDTO;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.packs.service.PacksContentsService;
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
@RequestMapping(value = PackTicketContentController.BASE_URI)
public class PackTicketContentController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/packs/{packId}/ticket-contents";

    private static final String PACK_ID_MUST_BE_ABOVE_0 = "Pack Id must be above 0";
    private static final String AUDIT_COLLECTION = "PACK_TICKET_CONTENTS";
    private static final String AUDIT_SUBCOLLECTION_PDF = "PDF";
    private static final String AUDIT_SUBCOLLECTION_PRINTER = "PRINTER";

    private final PacksContentsService packsContentsService;

    @Autowired
    public PackTicketContentController(PacksContentsService packsContentsService) {
        this.packsContentsService = packsContentsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PDF/texts")
    public PackTicketContentsTextListDTO getTicketsPDFTextContents(@PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                                                   @BindUsingJackson @Valid PackTicketContentTextPDFFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_GET);
        return packsContentsService.getPackTicketContentsTexts(packId, filter, TicketCommunicationElementCategory.PDF);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PDF/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPDFTextContents(@PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                            @Valid @NotEmpty @RequestBody PackTicketContentsTextListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_UPDATE);
        packsContentsService.updatePackTicketContentsTexts(packId, contents, TicketCommunicationElementCategory.PDF);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PDF/images")
    public PackTicketContentsImagePDFListDTO getTicketsPDFImageContents(@PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                                                        @BindUsingJackson @Valid PackTicketContentImagePDFFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_GET);
        return packsContentsService.getPackTicketContentsPDFImages(packId, filter, TicketCommunicationElementCategory.PDF);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PDF/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPDFImageContents(@PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                             @Valid @NotEmpty @RequestBody PackTicketContentsImagePDFListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_UPDATE);
        packsContentsService.updatePackTicketContentsPDFImages(packId, contents, TicketCommunicationElementCategory.PDF);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/PDF/images/languages/{language}/types/{type}")
    public void deleteTicketPDFContentsImage(@PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                             @PathVariable @LanguageIETF String language, @PathVariable TicketContentImagePDFType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PDF, AuditTag.AUDIT_ACTION_DELETE);
        packsContentsService.deletePackTicketContentPDFImage(packId, language, type, TicketCommunicationElementCategory.PDF);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PRINTER/texts")
    public PackTicketContentsTextListDTO getTicketsPrinterTextContents(@PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                                                       @BindUsingJackson @Valid PackTicketContentTextPDFFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_GET);
        return packsContentsService.getPackTicketContentsTexts(packId, filter, TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PRINTER/texts", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPrintersTextContents(@PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                                 @Valid @NotEmpty @RequestBody PackTicketContentsTextListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_UPDATE);
        packsContentsService.updatePackTicketContentsTexts(packId, contents, TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/PRINTER/images")
    public PackTicketContentsImagePrinterListDTO getTicketsPrinterImageContents(@PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                                                                @BindUsingJackson @Valid PackTicketContentImagePrinterFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_GET);
        return packsContentsService.getPackTicketContentsPrinterImages(packId, filter, TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/PRINTER/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTicketPrinterImageContents(@PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                                 @Valid @NotEmpty @RequestBody PackTicketContentsImagePrinterListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_UPDATE);
        packsContentsService.updatePackTicketContentsPrinterImages(packId, contents, TicketCommunicationElementCategory.TICKET_OFFICE);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/PRINTER/images/languages/{language}/types/{type}")
    public void deleteTicketPrinterContentsImage(@PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                                 @PathVariable @LanguageIETF String language, @PathVariable TicketContentImagePrinterType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_PRINTER, AuditTag.AUDIT_ACTION_DELETE);
        packsContentsService.deletePackTicketContentPrinterImage(packId, language, type, TicketCommunicationElementCategory.TICKET_OFFICE);
    }
}

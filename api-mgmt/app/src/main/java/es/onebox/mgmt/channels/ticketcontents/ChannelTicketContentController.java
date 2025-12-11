package es.onebox.mgmt.channels.ticketcontents;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPDFImageContentsDTO;
import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPassbookImageContentsDTO;
import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPassbookTextContentsDTO;
import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPrinterImageContentsDTO;
import es.onebox.mgmt.channels.ticketcontents.enums.ChannelTicketPDFImageContentType;
import es.onebox.mgmt.channels.ticketcontents.enums.ChannelTicketPassbookImageContentType;
import es.onebox.mgmt.channels.ticketcontents.enums.ChannelTicketPassbookTextContentType;
import es.onebox.mgmt.channels.ticketcontents.enums.ChannelTicketPrinterImageContentType;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelTicketContentController.BASE_URI)
public class ChannelTicketContentController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/ticket-contents";

    private static final String AUDIT_COLLECTION = "CHANNEL_TICKET_CONTENTS";
    private static final String AUDIT_SUB_COLLECTION_PDF = "PDF";
    private static final String AUDIT_SUB_COLLECTION_PASSBOOK = "PASSBOOK";

    private final ChannelTicketPDFContentService channelTicketPDFContentService;
    private final ChannelTicketPassbookContentService channelTicketPassbookContentService;

    private final ChannelTicketPrinterContentService channelTicketPrinterContentService;

    @Autowired
    public ChannelTicketContentController(ChannelTicketPDFContentService channelTicketPDFContentService,
                                          ChannelTicketPassbookContentService channelTicketPassbookContentService,
                                          ChannelTicketPrinterContentService channelTicketPrinterContentService) {
        this.channelTicketPDFContentService = channelTicketPDFContentService;
        this.channelTicketPassbookContentService = channelTicketPassbookContentService;
        this.channelTicketPrinterContentService = channelTicketPrinterContentService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping("/PDF/images")
    public ChannelTicketPDFImageContentsDTO getTicketPDFImageContent(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                                     @RequestParam(value = "language", required = false) @LanguageIETF String language,
                                                                     @RequestParam(value = "type", required = false) ChannelTicketPDFImageContentType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUB_COLLECTION_PDF, AuditTag.AUDIT_ACTION_GET);
        return this.channelTicketPDFContentService.getTicketContent(channelId, language, type);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/PDF/images")
    public void updateTicketPDFImageContent(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                       @RequestBody @Valid ChannelTicketPDFImageContentsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUB_COLLECTION_PDF, AuditTag.AUDIT_ACTION_CREATE);
        this.channelTicketPDFContentService.updateTicketContent(channelId, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/PDF/images/languages/{language}/types/{type}")
    public void deleteTicketPDFContent(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                    @PathVariable @LanguageIETF String language,
                                    @PathVariable ChannelTicketPDFImageContentType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUB_COLLECTION_PDF, AuditTag.AUDIT_ACTION_CREATE);
        this.channelTicketPDFContentService.deleteTicketContent(channelId, language, type);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping("/PASSBOOK/images")
    public ChannelTicketPassbookImageContentsDTO getTicketPassbookImageContent(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                                               @RequestParam(value = "language", required = false) @LanguageIETF String language,
                                                                               @RequestParam(value = "type", required = false) ChannelTicketPassbookImageContentType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUB_COLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_GET);
        return this.channelTicketPassbookContentService.getTicketImageContent(channelId, language, type);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/PASSBOOK/images")
    public void updateTicketPassbookImageContent(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                            @RequestBody @Valid ChannelTicketPassbookImageContentsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUB_COLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_CREATE);
        this.channelTicketPassbookContentService.updateTicketImageContent(channelId, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/PASSBOOK/images/languages/{language}/types/{type}")
    public void deleteTicketPassbookContent(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                       @PathVariable @LanguageIETF String language,
                                       @PathVariable ChannelTicketPassbookImageContentType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUB_COLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_CREATE);
        this.channelTicketPassbookContentService.deleteTicketImageContent(channelId, language, type);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping("/PASSBOOK/texts")
    public ChannelTicketPassbookTextContentsDTO getTicketPassbookTextContent(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                                             @RequestParam(value = "language", required = false) @LanguageIETF String language,
                                                                             @RequestParam(value = "type", required = false) ChannelTicketPassbookTextContentType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUB_COLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_GET);
        return this.channelTicketPassbookContentService.getTicketTextContent(channelId, language, type);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/PASSBOOK/texts")
    public void updateTicketPassbookTextContent(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                 @RequestBody @Valid ChannelTicketPassbookTextContentsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUB_COLLECTION_PASSBOOK, AuditTag.AUDIT_ACTION_CREATE);
        this.channelTicketPassbookContentService.updateTicketTextContent(channelId, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping("/PRINTER/images")
    public ChannelTicketPrinterImageContentsDTO getTicketPrinterImageContent(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                                             @RequestParam(value = "language", required = false) @LanguageIETF String language,
                                                                             @RequestParam(value = "type", required = false) ChannelTicketPrinterImageContentType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.channelTicketPrinterContentService.getTicketContent(channelId, language, type);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/PRINTER/images")
    public void updateTicketPrinterImageContent(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                @RequestBody @Valid ChannelTicketPrinterImageContentsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        this.channelTicketPrinterContentService.updateTicketContent(channelId, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/PRINTER/images/languages/{language}/types/{type}")
    public void deleteTicketPrinterContent(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                           @PathVariable @LanguageIETF String language,
                                           @PathVariable ChannelTicketPrinterImageContentType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        this.channelTicketPrinterContentService.deleteTicketContent(channelId, language, type);
    }
}

package es.onebox.mgmt.languages;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Valid
@RestController
@RequestMapping(LanguageController.BASE_URI)
public class LanguageController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/languages";

    private static final String AUDIT_COLLECTION = "LANGUAGES";

    private final LanguageService languageService;

    @Autowired
    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<CodeDTO> getLanguages(@RequestParam(value="platform_language", required = false) Boolean platformLanguage) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET_AVAILABLE);
        return languageService.getLanguages(platformLanguage);
    }
}


package es.onebox.mgmt.passbook;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.security.Roles;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueCodeDTO;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.ticket.dto.AvailablePassbookField;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookPreviewRequest;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookRequestFilter;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookTemplate;
import es.onebox.mgmt.datasources.ms.ticket.dto.UpdatePassbookTemplate;
import es.onebox.mgmt.datasources.ms.ticket.repository.PassbookRepository;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.mgmt.events.dto.TicketPrintResultDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtPassbookErrorCode;
import es.onebox.mgmt.passbook.converter.PassbookTemplateConverter;
import es.onebox.mgmt.passbook.dto.AvailablePassbookFieldDTO;
import es.onebox.mgmt.passbook.dto.CodeDTO;
import es.onebox.mgmt.passbook.dto.CreatePassbookTemplateDTO;
import es.onebox.mgmt.passbook.dto.PassbookRequestFilterDTO;
import es.onebox.mgmt.passbook.dto.PassbookTemplateDTO;
import es.onebox.mgmt.passbook.dto.PassbookTemplateListDTO;
import es.onebox.mgmt.passbook.dto.PassbookTemplateType;
import es.onebox.mgmt.passbook.dto.UpdatePassbookTemplateDTO;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PassbookTemplateService {

    private static final String TERMS_CONDITIONS_KEY = "TERMS_CONDITIONS_BODY";
    private static final Pattern PASSBOOK_CODE_PATTERN = Pattern.compile("[a-zA-Z0-9_]{3,25}");
    private static final String HTML_SCAPED_GREATER_THAN_SYMBOL = "&gt;";
    private static final String HTML_SCAPED_LOWER_THAN_SYMBOL = "&lt;";
    private static final String PLACEHOLDER_START = "{";
    private static final String PLACEHOLDER_END = "}";

    @Autowired
    private PassbookRepository passbookRepository;
    @Autowired
    private SecurityManager securityManager;
    @Autowired
    private MasterdataService masterdataService;
    @Autowired
    private EntitiesRepository entitiesRepository;
    @Autowired
    private ValidationService validationService;
    @Autowired
    private TicketsRepository ticketsRepository;

    public PassbookTemplateListDTO searchPassbookTemplates(PassbookRequestFilterDTO filter) {
        securityManager.checkEntityAccessible(filter);
        if (filter.getEntityId() == null && SecurityUtils.hasEntityType(EntityTypes.OPERATOR)) {
            filter.setOperatorId(SecurityUtils.getUserOperatorId());
        }
        PassbookRequestFilter msTicketRequest = PassbookTemplateConverter.convert(filter);
        return PassbookTemplateConverter.convert(passbookRepository.searchPassbookTemplates(msTicketRequest));
    }

    public void deletePassbookTemplate(String code, Long entityId) {
        validatePassbookTemplateCode(code);
        passbookRepository.deletePassbookTemplate(code, getAndCheckEntityId(entityId));
    }

    public PassbookTemplateDTO getPassbookTemplate(String code, Long entityId) {
        validatePassbookTemplateCode(code);
        entityId = getAndCheckEntityId(entityId);
        return PassbookTemplateConverter.convert(passbookRepository.getPassbookTemplate(code, entityId));
    }

    public CodeDTO createPassbookTemplate(CreatePassbookTemplateDTO cpt) {
        cpt.setEntityId(getAndCheckEntityId(cpt.getEntityId()));
        if (cpt.getOriginEntityId() == null) {
            cpt.setOriginEntityId(cpt.getEntityId());
        }
        cpt.setOriginEntityId(getAndCheckEntityId(cpt.getOriginEntityId()));
        cpt.setOperatorId(SecurityUtils.getUserOperatorId());
        if (StringUtils.isEmpty(cpt.getName())) {
            throw new OneboxRestException(ApiMgmtPassbookErrorCode.PASSBOOK_NAME_MANDATORY);
        }
        es.onebox.core.serializer.dto.common.CodeDTO result = passbookRepository.createPassbookTemplates(PassbookTemplateConverter.convert(cpt));
        return new CodeDTO(result.getCode());
    }

    public Map<String, String> getPassbookLiterals(Long entityId, String code, String langCode) {
        entityId = getAndCheckEntityId(entityId);
        validatePassbookTemplateCode(code);
        validateLang(langCode);
        PassbookTemplate template = passbookRepository.getPassbookTemplate(code, entityId);
        if (template == null) {
            throw new OneboxRestException(ApiMgmtPassbookErrorCode.PASSBOOK_TEMPLATE_NOT_FOUND);
        }
        return passbookRepository.getPassbookLiterals(entityId, code, ConverterUtils.toLocale(langCode));
    }

    public List<AvailablePassbookFieldDTO> availablePassbookFields(PassbookTemplateType type) {
        List<AvailablePassbookField> response = passbookRepository.availablePassbookFields(PassbookTemplateConverter.convertType(type));
        return PassbookTemplateConverter.convertAvailableFields(response);
    }

    private Long getAndCheckEntityId(Long entityId) {
        boolean missingEntityId = entityId == null || entityId <= 0;
        if ((SecurityUtils.hasEntityType(EntityTypes.OPERATOR) || SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)) && missingEntityId) {
            throw new OneboxRestException(ApiMgmtPassbookErrorCode.PASSBOOK_ENTITY_ID_MANDATORY);
        }
        if (missingEntityId) {
            return SecurityUtils.getUserEntityId();
        }
        securityManager.checkEntityAccessible(entityId);
        return entityId;
    }

    public void updatePassbookTemplate(String code, Long entityId, UpdatePassbookTemplateDTO updatePassbookTemplate) {
        validatePassbookTemplateCode(code);
        if (updatePassbookTemplate.getLanguages() != null) {
            validateLang(updatePassbookTemplate.getLanguages().getDefaultLanguage());
            updatePassbookTemplate.getLanguages().getSelected().forEach(this::validateLang);
        }
        UpdatePassbookTemplate request = PassbookTemplateConverter.convert(updatePassbookTemplate);
        passbookRepository.updatePassbookTemplate(code, getAndCheckEntityId(entityId), request);
    }

    public void updatePassbookLiterals(Long entityId, String passbookCode, String langCode, Map<String, String> literals) {
        entityId = getAndCheckEntityId(entityId);
        validatePassbookTemplateCode(passbookCode);
        validateLang(langCode);
        PassbookTemplate template = passbookRepository.getPassbookTemplate(passbookCode, entityId);
        if (template == null) {
            throw new OneboxRestException(ApiMgmtPassbookErrorCode.PASSBOOK_TEMPLATE_NOT_FOUND);
        }
        if (literals.containsKey(TERMS_CONDITIONS_KEY)) {
            String termsConditionsBody = literals.get(TERMS_CONDITIONS_KEY);
            if (termsConditionsBody.contains(HTML_SCAPED_LOWER_THAN_SYMBOL) || termsConditionsBody.contains(HTML_SCAPED_GREATER_THAN_SYMBOL)) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                        "'<' and '>' symbols are not allowed in terms and conditions literal", null);
            }
            literals.put(TERMS_CONDITIONS_KEY, HtmlUtils.htmlUnescape(termsConditionsBody));
        }
        passbookRepository.updatePassbookLiterals(passbookCode, entityId, ConverterUtils.toLocale(langCode), literals);
    }

    public TicketPrintResultDTO getPassbookPreview(String code, Long entityId, String language) {
        Entity entity = entitiesRepository.getEntity(getAndCheckEntityId(entityId));
        if (language == null) {
            language = ConverterUtils.toLanguageTag(entity.getLanguage().getCode());
        }
        if (entity.getSelectedLanguages() == null || entity.getSelectedLanguages().isEmpty()) {
            entity.setSelectedLanguages(Collections.singletonList(new IdValueCodeDTO(entity.getLanguage().getCode())));
        }
        language = validationService.convertAndCheckLanguageTag(language, entity.getSelectedLanguages()
                .stream()
                .map(IdValueCodeDTO::getCode)
                .collect(Collectors.toSet()));
        PassbookPreviewRequest request = new PassbookPreviewRequest();
        request.setLanguageCode(language);
        request.setEntityId(entity.getId());
        return new TicketPrintResultDTO(ticketsRepository.getPassbookPreview(request, code).getDownloadUrl());
    }

    private void validateLang(String langCode) {
        if (StringUtils.isEmpty(langCode)) {
            throw new OneboxRestException(ApiMgmtPassbookErrorCode.PASSBOOK_LANG_MANDATORY);
        }
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        if (!languages.containsKey(ConverterUtils.toLocale(langCode))) {
            throw new OneboxRestException(ApiMgmtPassbookErrorCode.PASSBOOK_INVALID_LANG);
        }
    }

    private void validatePassbookTemplateCode(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new OneboxRestException(ApiMgmtPassbookErrorCode.PASSBOOK_CODE_MANDATORY);
        } else if (!PASSBOOK_CODE_PATTERN.matcher(code).matches()) {
            throw new OneboxRestException(ApiMgmtPassbookErrorCode.PASSBOOK_CODE_UNACCEPTABLE);
        }
    }

    public List<String> availableDataPlaceholders(PassbookTemplateType type) {
        return passbookRepository.availableDataPlaceholders(PassbookTemplateConverter.convertType(type));
    }

    public List<String> availableLiteralKeys() {
        return passbookRepository.getPassbookAvailableLiteralKeys();
    }
}

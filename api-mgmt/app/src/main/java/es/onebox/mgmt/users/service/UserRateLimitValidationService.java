package es.onebox.mgmt.users.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.users.dto.ratelimit.UserRateLimitConfigDTO;
import es.onebox.mgmt.users.dto.ratelimit.UserRateLimitRuleDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class UserRateLimitValidationService {

    private static final String CONFIG_NOT_SET_PROPERLY = "Config must be unlimited or not. Not both";
    private static final String PATTERN_ALREADY_EXISTS = "Pattern should be unique for same configuration";
    private static final String CONFIG_MUST_BE_PRESENT = "Some config must be present";

    private UserRateLimitValidationService() {
    }

    public static void validate(UserRateLimitConfigDTO dto) {
        isUnlimitedNullAndDTOEmptyThrowsException(dto);
        isUnlimitedAndRulesAreNotEmptyThrowsException(dto);
        isNotUniquePatternThrowsException(dto);
    }


    private static void isNotUniquePatternThrowsException(UserRateLimitConfigDTO dto) {
        isNotUniquePatternInDTO(dto);
    }

    private static void isNotUniquePatternInDTO(UserRateLimitConfigDTO dto) {
        var rules = dto.getRules();
        if (rules == null) {
            return;
        }
        List<UserRateLimitRuleDTO> duplicatedRulePatterns = rules.stream()
            .collect(Collectors.groupingBy(UserRateLimitRuleDTO::getPattern))
            .entrySet().stream().filter(e -> e.getValue().size() > 1)
            .flatMap(e -> e.getValue().stream()).toList();
        if (duplicatedRulePatterns.size() > 1) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.USER_RATE_LIMIT_INVALID_CONFIG)
                .setMessage(PATTERN_ALREADY_EXISTS)
                .build();
        }
    }

    private static void isUnlimitedAndRulesAreNotEmptyThrowsException(UserRateLimitConfigDTO dto) {
        if (BooleanUtils.isTrue(dto.getUnlimited()) && CollectionUtils.isNotEmpty(dto.getRules())) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.USER_RATE_LIMIT_INVALID_CONFIG)
                .setMessage(CONFIG_NOT_SET_PROPERLY)
                .build();
        }
    }

    private static void isUnlimitedNullAndDTOEmptyThrowsException(UserRateLimitConfigDTO dto) {
        if (dto.getUnlimited() == null && CollectionUtils.isEmpty(dto.getRules())) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.USER_RATE_LIMIT_INVALID_CONFIG)
                .setMessage(CONFIG_MUST_BE_PRESENT)
                .build();
        }
    }
}

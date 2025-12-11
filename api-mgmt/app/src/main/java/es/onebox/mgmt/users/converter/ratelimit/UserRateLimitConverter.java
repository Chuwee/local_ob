package es.onebox.mgmt.users.converter.ratelimit;

import es.onebox.mgmt.datasources.ms.entity.dto.user.ratelimit.UserRateLimitConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.user.ratelimit.UserRateLimitQuota;
import es.onebox.mgmt.datasources.ms.entity.dto.user.ratelimit.UserRateLimitRule;
import es.onebox.mgmt.users.dto.ratelimit.UserRateLimitConfigDTO;
import es.onebox.mgmt.users.dto.ratelimit.UserRateLimitQuotaDTO;
import es.onebox.mgmt.users.dto.ratelimit.UserRateLimitRuleDTO;

import java.util.List;

public class UserRateLimitConverter {

    private UserRateLimitConverter() {
    }

    public static UserRateLimitConfigDTO domainToDTO(UserRateLimitConfig domain) {
        UserRateLimitConfigDTO dto = new UserRateLimitConfigDTO();
        dto.setUnlimited(domain.getUnlimited());
        if (domain.getRules() != null) {
            dto.setRules(domainListRuleToListDTO(domain.getRules()));
        }
        return dto;
    }

    public static UserRateLimitConfig dtoToDomain(UserRateLimitConfigDTO dto) {
        UserRateLimitConfig domain = new UserRateLimitConfig();
        domain.setUnlimited(dto.getUnlimited());
        if (dto.getRules() != null) {
            domain.setRules(dtoListRuleToListDomain(dto.getRules()));
        }
        return domain;
    }

    private static UserRateLimitQuotaDTO domainQuotaToDTO(UserRateLimitQuota userRateLimitQuota) {
        UserRateLimitQuotaDTO dto = new UserRateLimitQuotaDTO();
        dto.setTimeUnit(userRateLimitQuota.getTimeUnit());
        dto.setPeriod(userRateLimitQuota.getPeriod());
        dto.setLimit(userRateLimitQuota.getLimit());
        return dto;
    }

    private static UserRateLimitQuota dtoQuotaToDomain(UserRateLimitQuotaDTO dto) {
        UserRateLimitQuota domain = new UserRateLimitQuota();
        domain.setLimit(dto.getLimit());
        domain.setPeriod(dto.getPeriod());
        domain.setTimeUnit(dto.getTimeUnit());
        return domain;
    }

    private static UserRateLimitRuleDTO domainRuleToDTO(UserRateLimitRule domain) {
        UserRateLimitRuleDTO dto = new UserRateLimitRuleDTO();
        dto.setPattern(domain.getPattern());
        if (domain.getQuotas() != null) {
            dto.setQuotas(domainListQuotaToListDTO(domain.getQuotas()));
        }
        return dto;
    }

    private static UserRateLimitRule dtoRuleToDomain(UserRateLimitRuleDTO dto) {
        UserRateLimitRule domain = new UserRateLimitRule();
        domain.setPattern(dto.getPattern());
        if (dto.getQuotas() != null) {
            domain.setQuotas(dtoListQuotaToListDomain(dto.getQuotas()));
        }
        return domain;
    }

    private static List<UserRateLimitRuleDTO> domainListRuleToListDTO(
        List<UserRateLimitRule> domains) {
        return domains.stream().map(UserRateLimitConverter::domainRuleToDTO).toList();
    }

    private static List<UserRateLimitRule> dtoListRuleToListDomain(
        List<UserRateLimitRuleDTO> dtos) {
        return dtos.stream().map(UserRateLimitConverter::dtoRuleToDomain).toList();
    }

    private static List<UserRateLimitQuota> dtoListQuotaToListDomain(
        List<UserRateLimitQuotaDTO> dtos) {
        return dtos.stream().map(UserRateLimitConverter::dtoQuotaToDomain).toList();

    }

    private static List<UserRateLimitQuotaDTO> domainListQuotaToListDTO(
        List<UserRateLimitQuota> domains) {
        return domains.stream().map(UserRateLimitConverter::domainQuotaToDTO).toList();
    }


}

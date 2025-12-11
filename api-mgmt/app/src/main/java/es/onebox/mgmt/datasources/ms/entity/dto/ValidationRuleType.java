package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;

public enum ValidationRuleType implements Serializable {
    MIN_YEAR,
    MAX_YEAR,
    REQUIRED_WHEN_AGE_IS_BIGGER_THAN,
    ID_FIELD,
    REGEX;
}
package es.onebox.fever.exception;

import es.onebox.core.webmvc.exception.ApiErrorDTO;

public class ApiErrorRetryDTO extends ApiErrorDTO {

    private final Integer retryAfter;
    public ApiErrorRetryDTO(ApiErrorDTO apiErrorDTO, Integer retryAfter) {
        super(apiErrorDTO.getCode(),  apiErrorDTO.getMessage());
        this.retryAfter = retryAfter;
    }

    public Integer getRetryAfter() {
        return retryAfter;
    }
}

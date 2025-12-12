package es.onebox.flc.common;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.validation.MaxLimit;

@MaxLimit(50L)
public class GenericRequest extends BaseRequestFilter {
    private static final long serialVersionUID = -4090818102282292890L;
}

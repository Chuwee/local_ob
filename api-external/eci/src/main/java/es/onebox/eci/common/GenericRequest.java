package es.onebox.eci.common;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.validation.MaxLimit;

@MaxLimit(50L)
public class GenericRequest extends BaseRequestFilter {
    private static final long serialVersionUID = 6674054107724031819L;
}

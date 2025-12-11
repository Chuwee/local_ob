package es.onebox.event.common.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.stream.Stream;

public class UrlUtils {

    private UrlUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static String composeRelativePathNullable(Object... params) {
        if (ArrayUtils.isEmpty(params)) {
            return null;
        }
        return UriComponentsBuilder
                .newInstance()
                .pathSegment(
                        Stream.of(params)
                                .map(Object::toString)
                                .toArray(String[]::new))
                .build()
                .toString()
                .substring(1);
    }

    public static String composeAbsoluteUrl(final String url, Object... pathParams) {
        if (ArrayUtils.isEmpty(pathParams)) {
            return url;
        }
        return UriComponentsBuilder
                .fromHttpUrl(url)
                .pathSegment(
                        Stream.of(pathParams)
                                .map(Object::toString)
                                .toArray(String[]::new))
                .build()
                .toString();
    }
}

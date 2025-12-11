package es.onebox.mgmt.common;


import jakarta.servlet.http.HttpServletRequest;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class URLExtractorUtils {

    private URLExtractorUtils() {
        throw new UnsupportedOperationException();
    }

    public static String getFullURI(Request request) {
        HttpUrl url = request.url();
        StringBuilder requestURL = new StringBuilder(url.encodedPath());
        String queryString = url.encodedQuery();
        return concat(requestURL, queryString);
    }
    public static String getFullURI(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURI());
        String queryString = request.getQueryString();
        return concat(requestURL, queryString);
    }

    private static String concat(StringBuilder requestURL, String queryString) {
        if (queryString == null) {
            return requestURL.toString();
        }
        return requestURL.append('?').append(queryString).toString();
    }
}

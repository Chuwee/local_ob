package es.onebox.common.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.serializer.mapper.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utilities.class);

    private static ObjectMapper mapper;

    static {
        mapper = JsonMapper.jacksonMapper();
    }

    public static String serializeJson(Object basket) {
        String json = null;
        try {
            json = mapper.writeValueAsString(basket);
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot serialize basket to JSON", e);
        }
        return json;
    }

    public static <T> T deserializeJson(String text, Class<T> clazz) {
        T object = null;
        try {
            object = mapper.readValue(text, clazz);
        } catch (IOException e) {
            LOGGER.error("Cannot deserialize JSON string to object", e);
        }
        return object;
    }

    public static boolean checkUrlContextPath(ServletRequest request, String context) {
        String path = ((HttpServletRequest) request).getRequestURI();
        Matcher matcher = Pattern.compile(context).matcher(path);
        return matcher.find();
    }
}

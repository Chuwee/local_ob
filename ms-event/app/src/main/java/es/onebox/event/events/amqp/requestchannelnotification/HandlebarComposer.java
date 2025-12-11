package es.onebox.event.events.amqp.requestchannelnotification;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringSubstitutor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class HandlebarComposer {

    public static final String PROPERTIES_FILE_EXT = ".properties";
    public static final String PROPERTIES_FILE_NAME = "messages";
    public static final String TEMPLATE_FILE = "template.html";
    private Template template;
    private String resourcePath;
    private Map<Locale, Properties> cachedLocales;

    public HandlebarComposer(String resourcesPath) throws IOException {
        this(resourcesPath, TEMPLATE_FILE);
    }

    public HandlebarComposer(String resourcesPath, String templateFile) throws IOException {
        Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader());
        this.template = handlebars.compile(resourcesPath + "/" + templateFile);
        this.cachedLocales = new HashMap<>();
        this.resourcePath = resourcesPath;
    }

    public String compose(Map<String, Object> handlebarsParams) throws IOException {
        Context context = Context.newBuilder(handlebarsParams).build();
        return this.template.apply(context);
    }

    public String composeFromProperties(Locale locale, Map<String, Object> propertiesParams) throws IOException {
        return compose(replacePropertiesParams(locale, propertiesParams));
    }

    public String composeFromProperties(Locale locale, Map<String, Object> propertiesParams, Map<String, Object> handlebarsParams) throws IOException {
        handlebarsParams.putAll(replacePropertiesParams(locale, propertiesParams));
        return compose(handlebarsParams);
    }

    public String getPropertiesMessage(Locale locale, String key) throws IOException {
        Properties properties = getProperties(locale);
        return properties.getProperty(key, null);
    }

    public String getPropertiesMessage(Locale locale, String key, Map<String, Object> propertiesParams) throws IOException {
        Properties properties = getProperties(locale);
        String property = properties.getProperty(key, null);
        StringSubstitutor strSubstitutor = new StringSubstitutor(propertiesParams);
        return strSubstitutor.replace(property);
    }

    private Map<String, Object> replacePropertiesParams(Locale locale, Map<String, Object> propertiesParams) throws IOException {
        Map<String, Object> properties = new HashMap<>();
        for (Map.Entry<Object, Object> property : getProperties(locale).entrySet()) {
            properties.put(property.getKey().toString(), property.getValue());
        }
        StringSubstitutor strSubstitutor = new StringSubstitutor(propertiesParams);
        properties.replaceAll((k, v) -> strSubstitutor.replace(v));
        return properties;
    }

    private Properties getProperties(Locale locale) throws IOException {
        Properties properties = cachedLocales.get(locale);
        if (properties == null) {
            properties = getAndCacheProperties(locale);
        }
        return properties;
    }

    private Properties getAndCacheProperties(Locale locale) throws IOException {
        Properties properties = new Properties();
        InputStream is = null;
        try {
            is = getClass().getClassLoader().getResourceAsStream(getPropertiesFile(locale));
            if (is == null) {
                is = getClass().getClassLoader().getResourceAsStream(getPropertiesFile(null));
            }
            if (is == null) {
                throw new IOException("Could not load bundle with path" + resourcePath);
            }
            Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            properties.load(reader);
        } finally {
            IOUtils.closeQuietly(is);
        }
        cachedLocales.put(locale, properties);
        return properties;
    }

    private String getPropertiesFile(Locale locale) {
        if (locale == null) {
            return resourcePath + "/" + PROPERTIES_FILE_NAME + PROPERTIES_FILE_EXT;
        }
        return resourcePath + "/" + PROPERTIES_FILE_NAME + "_" + locale + PROPERTIES_FILE_EXT;
    }
}

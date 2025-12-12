package es.onebox.internal.automaticsales;

import es.onebox.core.serializer.mapper.JsonMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestUtils {

    public static <T> T getObjectFromFile (String fileName, Class<T> clazz) throws IOException {
        File file = new File(TestUtils.class.getClassLoader().getResource(fileName).getFile());
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        return  JsonMapper.jacksonMapper().readValue(json, clazz);
    }

}

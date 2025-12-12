package es.onebox.fifaqatar;

import com.fasterxml.jackson.core.type.TypeReference;
import es.onebox.common.datasources.orderitems.dto.OrderItem;
import es.onebox.core.serializer.mapper.JsonMapper;
import es.onebox.fifaqatar.config.translation.FifaQatarTranslation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BaseTest {


    protected FifaQatarTranslation getDictionary() throws IOException {
        String str = loadResourceFile("dictionary.json");

        return JsonMapper.jacksonMapper().readValue(str, FifaQatarTranslation.class);
    }

    protected List<OrderItem> loadMockedOrderItems(String fileSource) throws IOException {
        String str = loadResourceFile(fileSource);

        return JsonMapper.jacksonMapper().readValue(str, new TypeReference<List<OrderItem>>() {
        });
    }

    protected String loadResourceFile(String fileSource) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileSource).getFile());
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
    }
}

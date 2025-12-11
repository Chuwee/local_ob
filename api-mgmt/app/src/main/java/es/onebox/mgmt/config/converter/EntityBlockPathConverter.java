package es.onebox.mgmt.config.converter;

import es.onebox.mgmt.entities.contents.enums.EntityBlockCategory;
import org.springframework.core.convert.converter.Converter;

public class EntityBlockPathConverter implements Converter<String, EntityBlockCategory> {

    @Override
    public EntityBlockCategory convert(String source) {
        return EntityBlockCategory.fromPath(source);
    }

}

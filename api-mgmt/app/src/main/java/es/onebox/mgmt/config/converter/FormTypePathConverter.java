package es.onebox.mgmt.config.converter;

import es.onebox.mgmt.channels.forms.enums.FormType;
import org.springframework.core.convert.converter.Converter;

public class FormTypePathConverter implements Converter<String, FormType> {
        @Override
        public FormType convert(String source) {
            return FormType.fromName(source.toLowerCase());
        }
}

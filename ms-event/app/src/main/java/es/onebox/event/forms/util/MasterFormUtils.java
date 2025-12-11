package es.onebox.event.forms.util;

import es.onebox.event.forms.domain.FieldType;
import es.onebox.event.forms.domain.MasterFormField;
import es.onebox.event.forms.domain.MasterFormFields;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class MasterFormUtils {

    private MasterFormUtils() {
        throw new UnsupportedOperationException();
    }

    public static void overrideDescriptors(final MasterFormFields masterFields, final MasterFormFields entityFields) {
        if (CollectionUtils.isEmpty(entityFields)) {
            return;
        }
        masterFields.forEach(masterField -> overrideField(masterField, findField(masterField, entityFields)));
        addCustomFields(masterFields, entityFields);
    }

    private static void overrideField(MasterFormField masterField, MasterFormField entityField) {
        if (entityField != null) {
            FieldType type = entityField.getType();
            if (FieldType.LIST.equals(type)) {
                masterField.setValues(entityField.getValues());
            }
            if (FieldType.MULTI_KEY.equals(type)) {
                masterField.getFields().forEach(multiKeyField -> {
                    var multiKeyEntityField = entityField.getFields().stream().filter(field -> multiKeyField.getKey().equals(field.getKey())).findFirst().orElse(null);
                    if (multiKeyEntityField != null && FieldType.LIST.equals(multiKeyEntityField.getType())) {
                        multiKeyField.setValues(multiKeyEntityField.getValues());
                    }
                });
            }
        }
    }

    private static void addCustomFields(MasterFormFields masterFields, final MasterFormFields entityFields) {
        List<String> masterKeys = masterFields.stream().map(MasterFormField::getKey).toList();
        List<MasterFormField> customFields = entityFields.stream().filter(field -> !masterKeys.contains(field.getKey())).toList();
        if (CollectionUtils.isNotEmpty(customFields)) {
            masterFields.addAll(customFields);
        }
    }

    private static MasterFormField findField(MasterFormField masterField, MasterFormFields entityFields) {
        return entityFields.stream()
                .filter(field -> masterField.getType().equals(field.getType()) && masterField.getKey().equals(field.getKey()))
                .findFirst()
                .orElse(null);
    }
} 
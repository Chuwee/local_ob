package es.onebox.event.tickettemplates.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.tickettemplates.dao.TicketTemplateRecord;
import es.onebox.event.tickettemplates.dto.DesignType;
import es.onebox.event.tickettemplates.dto.TicketTemplateDTO;
import es.onebox.event.tickettemplates.dto.TicketTemplateDesignDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaPlantillaTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelModeloTicketRecord;

import java.util.ArrayList;
import java.util.List;

public class TicketTemplateConverter {
    public static final String SLASH = "/";

    private TicketTemplateConverter() {
    }

    public static TicketTemplateDTO convert(TicketTemplateRecord record, List<CpanelIdiomaPlantillaTicketRecord> langs) {
        if (record == null) {
            return null;
        }
        TicketTemplateDTO target = new TicketTemplateDTO();
        target.setId(record.getIdplantilla().longValue());
        target.setName(record.getNombre());
        if (record.getAsignacionautomatica() != null) {
            target.setDefault(ConverterUtils.isByteAsATrue(record.getAsignacionautomatica()));
        }
        if (record.getIdentidad() != null) {
            target.setEntity(new IdNameDTO(record.getIdentidad().longValue(), record.getEntityName()));
        }
        target.setExcludeBarcode(ConverterUtils.isByteAsATrue(record.getExcludebarcode()));
        target.setDesign(convertModel(record));

        if (langs != null) {
            target.setSelectedLanguageIds(new ArrayList<>());
            for (CpanelIdiomaPlantillaTicketRecord lang : langs) {
                target.getSelectedLanguageIds().add(lang.getIdidioma());
                if (CommonUtils.isTrue(lang.getDefecto())) {
                    target.setDefaultLanguage(lang.getIdidioma());
                }
            }
        }

        return target;
    }

    public static TicketTemplateDesignDTO convertModel(TicketTemplateRecord record) {
        TicketTemplateDesignDTO model = null;
        if (record.getIdmodelo() != null) {
            model = new TicketTemplateDesignDTO();
            model.setId(record.getIdmodelo().longValue());
            model.setName(record.getModelName());
            model.setDescription(record.getModelDescription());
            model.setFormat(record.getModelFormat());
            model.setPrinter(record.getModelPrinter());
            model.setPaperType(record.getModelPaper());
            model.setOrientation(record.getModelOrientation());
            model.setJasperFileName(record.getJasperModel().substring(record.getJasperModel().indexOf(SLASH)+1));
            model.setDesignType(DesignType.byValue(record.getModelType()));
        }
        return model;
    }

    public static TicketTemplateDesignDTO convertModel(CpanelModeloTicketRecord record) {
        TicketTemplateDesignDTO model = new TicketTemplateDesignDTO();
        model.setId(record.getIdmodelo().longValue());
        model.setName(record.getNombre());
        model.setFormat(record.getFormato().intValue());
        model.setPrinter(record.getTipoimpresora());
        model.setPaperType(record.getTipohoja());
        model.setOrientation(record.getOrientacion());
        return model;
    }

    public static void updateRecord(TicketTemplateRecord templateRecord, TicketTemplateDTO templateDTO) {
        ConverterUtils.updateField(templateRecord::setNombre, templateDTO.getName());
    }

}

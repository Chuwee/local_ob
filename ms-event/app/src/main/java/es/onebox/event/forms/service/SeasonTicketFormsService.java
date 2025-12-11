package es.onebox.event.forms.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.forms.converter.FormConverter;
import es.onebox.event.forms.dao.DefaultFormCouchDao;
import es.onebox.event.forms.dao.SeasonTicketFormsCouchDao;
import es.onebox.event.forms.dao.MasterFormsFieldsCouchDao;
import es.onebox.event.forms.domain.Form;
import es.onebox.event.forms.domain.MasterFormField;
import es.onebox.event.forms.domain.MasterFormFields;
import es.onebox.event.forms.dto.FormFieldDTO;
import es.onebox.event.forms.dto.UpdateFormDTO;
import es.onebox.event.forms.enums.FormTypeDTO;
import es.onebox.event.forms.util.MasterFormUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SeasonTicketFormsService {

    private final MasterFormsFieldsCouchDao masterFormsFieldsCouchDao;
    private final SeasonTicketFormsCouchDao seasonTicketFormsCouchDao;
    private final DefaultFormCouchDao defaultFormCouchDao;

    @Autowired
    public SeasonTicketFormsService(MasterFormsFieldsCouchDao masterFormsFieldsCouchDao,
                                  SeasonTicketFormsCouchDao seasonTicketFormsCouchDao,
                                  DefaultFormCouchDao defaultFormCouchDao) {
        this.masterFormsFieldsCouchDao = masterFormsFieldsCouchDao;
        this.seasonTicketFormsCouchDao = seasonTicketFormsCouchDao;
        this.defaultFormCouchDao = defaultFormCouchDao;
    }

    public boolean exists(Long seasonTicketId, FormTypeDTO formType) {
        return seasonTicketFormsCouchDao.exists(seasonTicketId, formType);
    }

    public List<List<FormFieldDTO>> getSeasonTicketForm(final Long seasonTicketId, FormTypeDTO formType) {
        Form form = this.seasonTicketFormsCouchDao.get(seasonTicketId, formType);
        if (form == null) {
            form = defaultFormCouchDao.getByFormType(formType);
        }
        return FormConverter.toDTO(form, getMasterFormFields(seasonTicketId));
    }

    public void updateSeasonTicketForm(Long seasonTicketId, FormTypeDTO formType, UpdateFormDTO updateForm) {
        if (CollectionUtils.isEmpty(updateForm)) {
            return;
        }
        this.seasonTicketFormsCouchDao.upsert(seasonTicketId, formType, FormConverter.toDomain(updateForm, getMasterFormFields(seasonTicketId)));
    }

    private Map<String, MasterFormField> getMasterFormFields(Long seasonTicketId) {
        MasterFormFields masterFields = masterFormsFieldsCouchDao.get();
        if (CollectionUtils.isEmpty(masterFields)) {
            throw new OneboxRestException(MsEventErrorCode.MASTER_FORM_NOT_FOUND);
        }
        var entityFields = masterFormsFieldsCouchDao.getByEntityId(seasonTicketId);
        MasterFormUtils.overrideDescriptors(masterFields, entityFields);
        return masterFields.stream().collect(Collectors.toMap(MasterFormField::getKey, Function.identity()));
    }
}
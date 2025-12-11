package es.onebox.mgmt.languages;

import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.mgmt.datasources.ms.entity.repository.MasterdataRepository;
import es.onebox.mgmt.languages.converter.LanguageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {

    private final MasterdataRepository masterdataRepository;

    @Autowired
    public LanguageService(MasterdataRepository masterdataRepository) {
        this.masterdataRepository = masterdataRepository;
    }

    public List<CodeDTO> getLanguages(Boolean platformLanguage) {
        return LanguageConverter.fromEntities(masterdataRepository.getLanguages(null, platformLanguage));
    }
}

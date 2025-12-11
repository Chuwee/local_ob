package es.onebox.mgmt.countries;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.countries.converter.InternationalPhonePrefixConverter;
import es.onebox.mgmt.countries.dto.InternationalPhonePrefixDTO;
import es.onebox.mgmt.datasources.ms.entity.repository.MasterdataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternationalPhonePrefixService {

    private final MasterdataRepository masterdataRepository;

    @Autowired
    public InternationalPhonePrefixService(MasterdataRepository masterdataRepository) {
        this.masterdataRepository = masterdataRepository;
    }


    public List<InternationalPhonePrefixDTO> getAllInternationalPhonePrefixes() {
        return InternationalPhonePrefixConverter.fromEntities(
            masterdataRepository.getAllInternationalPhonePrefixes()
                .stream()
                .filter(country -> !CommonUtils.isBlank(country.getInternationalPhonePrefix()))
                .toList()
        );
    }
}

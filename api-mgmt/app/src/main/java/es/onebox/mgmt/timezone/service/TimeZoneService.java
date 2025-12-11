package es.onebox.mgmt.timezone.service;

import es.onebox.mgmt.datasources.ms.entity.repository.MasterdataRepository;
import es.onebox.mgmt.timezone.converter.TimeZoneConverter;
import es.onebox.mgmt.timezone.dto.TimeZoneDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeZoneService {

    @Autowired
    private MasterdataRepository masterdataRepository;

    public List<TimeZoneDTO> getTimeZones() {
        return TimeZoneConverter.fromEntities(masterdataRepository.getTimeZones());
    }

}

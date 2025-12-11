package es.onebox.mgmt.currencies;

import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.mgmt.common.MasterdataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrenciesService {

    private MasterdataService masterdataService;

    @Autowired
    public CurrenciesService(MasterdataService masterdataService) {
        this.masterdataService = masterdataService;
    }
 
    public List<CodeDTO> getCurrencies() {
        return CurrencyConverter.toDTO(masterdataService.getCurrencies());
    }
}

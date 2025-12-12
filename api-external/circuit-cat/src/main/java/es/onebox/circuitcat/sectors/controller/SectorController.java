package es.onebox.circuitcat.sectors.controller;

import es.onebox.circuitcat.sectors.dto.SectorDTO;
import es.onebox.circuitcat.sectors.service.SectorService;
import es.onebox.common.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiConfig.CircuitApiConfig.BASE_URL + "/sectors")
public class SectorController {

    @Autowired
    private SectorService sectorService;

    @GetMapping()
    public List<SectorDTO> getSectorStatus(@RequestParam(value = "sector", required = true) String sectorCode,
                                           @RequestParam(value = "session_ids", required = true) List<Long> sessionIds,
                                           @RequestParam(value = "channel_id", required = false) Long channelId) {

        return sectorService.getSectorStatus(sectorCode, sessionIds, channelId);
    }
}

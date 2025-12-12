package es.onebox.circuitcat.seats.controller;

import es.onebox.circuitcat.seats.service.SeatService;
import es.onebox.common.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiConfig.CircuitApiConfig.BASE_URL + "/seats")
public class SeatController {


    @Autowired
    private SeatService seatService;

    @PutMapping("/block")
    public void block(@RequestParam(value = "sector") String sector,
                                 @RequestParam(value = "row") String row,
                                 @RequestParam(value = "seat") String seat,
                                 @RequestParam(value = "session_ids") List<Long> sessionIds) {

        seatService.block(sector, row, seat, sessionIds);
    }

    @PutMapping("/unblock")
    public void unblock(@RequestParam(value = "sector") String sector,
                      @RequestParam(value = "row") String row,
                      @RequestParam(value = "seat") String seat,
                      @RequestParam(value = "session_ids") List<Long> sessionIds) {

        seatService.unblock(sector, row, seat, sessionIds);
    }
}

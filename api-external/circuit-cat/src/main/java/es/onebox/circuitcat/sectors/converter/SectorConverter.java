package es.onebox.circuitcat.sectors.converter;

import es.onebox.circuitcat.common.dto.Seat;
import es.onebox.circuitcat.sectors.dto.Sector;
import es.onebox.circuitcat.sectors.dto.SectorDTO;

import java.util.ArrayList;
import java.util.List;

public class SectorConverter {
    public static List<SectorDTO> from(List<Sector> views) {
        List<SectorDTO> viewsDTO = new ArrayList<>();
        for (Sector view : views) {
            SectorDTO viewDTO = new SectorDTO();
            viewDTO.setCode(view.getCode());
            viewDTO.setSeats(new ArrayList<Seat>(view.getSeats().values()));
            viewsDTO.add(viewDTO);
        }
        return viewsDTO;
    }
}

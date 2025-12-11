package es.onebox.event.surcharges;

import es.onebox.event.surcharges.dto.RangeDTO;
import es.onebox.event.surcharges.dto.SurchargeTypeDTO;
import es.onebox.event.surcharges.dto.SurchargesDTO;

import java.util.ArrayList;
import java.util.List;

public class SurchargesMockFactory {

    public static SurchargesDTO validSurchagesDTOWith3Ranges(SurchargeTypeDTO type) {
        List<RangeDTO> ranges = new ArrayList<>();

        ranges.add(RangeDTOBuilder.aRange(0).withFix(1D).build());
        ranges.add(RangeDTOBuilder.aRange(10).withPercentage(1D).withMax(5D).withMin(4D).build());
        ranges.add(RangeDTOBuilder.aRange(5).withFix(2D).withMax(3D).build());

        SurchargesDTO surchargesDTO = new SurchargesDTO(type, null, ranges);

        return surchargesDTO;
    }

    public static SurchargesDTO validSurchagesDTOWith1Range(SurchargeTypeDTO type) {
        List<RangeDTO> ranges = new ArrayList<>();

        ranges.add(RangeDTOBuilder.aRange(0).withFix(1D).withPercentage(1D).withMax(10D).build());

        SurchargesDTO surchargesDTO = new SurchargesDTO(type, null, ranges);

        return surchargesDTO;
    }

    public static SurchargesDTO withEmptyRanges(SurchargeTypeDTO type) {
        List<RangeDTO> ranges = new ArrayList<>();

        SurchargesDTO surchargesDTO = new SurchargesDTO(type, null, ranges);

        return surchargesDTO;
    }

    public static SurchargesDTO withNullFromRange(SurchargeTypeDTO type) {
        List<RangeDTO> ranges = new ArrayList<>();
        ranges.add(RangeDTOBuilder.aRange(0D).withFix(1D).build());

        ranges.get(0).setFrom(null);

        SurchargesDTO surchargesDTO = new SurchargesDTO(type, null, ranges);

        return surchargesDTO;
    }

    public static SurchargesDTO withDuplicatedInitialRange(SurchargeTypeDTO type) {
        List<RangeDTO> ranges = new ArrayList<>();
        ranges.add(RangeDTOBuilder.aRange(0D).withFix(1D).build());
        ranges.add(RangeDTOBuilder.aRange(7D).withFix(3D).build());
        ranges.add(RangeDTOBuilder.aRange(1D).withFix(5D).build());
        ranges.add(RangeDTOBuilder.aRange(1D).withFix(3D).build());

        SurchargesDTO surchargesDTO = new SurchargesDTO(type, null, ranges);

        return surchargesDTO;
    }

    public static SurchargesDTO withoutInitialRangeValueZero(SurchargeTypeDTO type) {
        List<RangeDTO> ranges = new ArrayList<>();
        ranges.add(RangeDTOBuilder.aRange(7D).withFix(3D).build());
        ranges.add(RangeDTOBuilder.aRange(1D).withFix(3D).build());

        SurchargesDTO surchargesDTO = new SurchargesDTO(type, null, ranges);

        return surchargesDTO;
    }

}

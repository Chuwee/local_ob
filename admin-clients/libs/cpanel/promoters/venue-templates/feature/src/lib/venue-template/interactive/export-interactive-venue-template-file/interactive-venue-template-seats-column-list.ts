import { ExportField } from '@admin-clients/shared/data-access/models';
import { InteractiveVenueTemplateSeatsFields } from '@admin-clients/shared/venues/data-access/venue-tpls';

export const interactiveVenueTemplateSeatsColumnList: ExportField[] = [
    {
        field: InteractiveVenueTemplateSeatsFields.containerId,
        name: 'VENUE_TPLS.CSV_SEATS_FIELDS.CONTAINER_ID'
    },
    {
        field: InteractiveVenueTemplateSeatsFields.containerName,
        name: 'VENUE_TPLS.CSV_SEATS_FIELDS.CONTAINER_NAME'
    },
    {
        field: InteractiveVenueTemplateSeatsFields.sectorName,
        name: 'VENUE_TPLS.CSV_SEATS_FIELDS.SECTOR_NAME'
    },
    {
        field: InteractiveVenueTemplateSeatsFields.row,
        name: 'VENUE_TPLS.CSV_SEATS_FIELDS.ROW'
    },
    {
        field: InteractiveVenueTemplateSeatsFields.seat,
        name: 'VENUE_TPLS.CSV_SEATS_FIELDS.SEAT'
    },
    {
        field: InteractiveVenueTemplateSeatsFields.seatId,
        name: 'VENUE_TPLS.CSV_SEATS_FIELDS.SEAT_ID'
    }
];

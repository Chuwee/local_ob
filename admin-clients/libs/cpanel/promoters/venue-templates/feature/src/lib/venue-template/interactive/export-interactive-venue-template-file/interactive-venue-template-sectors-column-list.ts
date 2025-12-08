import { ExportField } from '@admin-clients/shared/data-access/models';
import { InteractiveVenueTemplateSectorsFields } from '@admin-clients/shared/venues/data-access/venue-tpls';

export const interactiveVenueTemplateSectorsColumnList: ExportField[] = [
    {
        field: InteractiveVenueTemplateSectorsFields.code,
        name: 'VENUE_TPLS.CSV_SECTORS_FIELDS.SECTOR_CODE'
    },
    {
        field: InteractiveVenueTemplateSectorsFields.name,
        name: 'VENUE_TPLS.CSV_SECTORS_FIELDS.SECTOR_NAME'
    }
];

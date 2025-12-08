import { CsvSeasonTicketRenewalGeneration } from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { CsvHeaderMappingField } from '@admin-clients/shared/common/feature/csv';

export type CsvSeasonTicketRenewalGenerationValueTypes = CsvSeasonTicketRenewalGeneration[keyof CsvSeasonTicketRenewalGeneration];

export function createCsvRenewalGenerationMappingFields(): CsvHeaderMappingField<CsvSeasonTicketRenewalGeneration>[] {
    return [
        { key: 'renewal_id', header: 'SEASON_TICKETS.RENEWAL_ID', columnIndex: null, required: true, example: '1234' },
        { key: 'reference', header: 'SEASON_TICKETS.RENEWAL_REFERENCE', columnIndex: null, required: true, example: '5678' }
    ];
}


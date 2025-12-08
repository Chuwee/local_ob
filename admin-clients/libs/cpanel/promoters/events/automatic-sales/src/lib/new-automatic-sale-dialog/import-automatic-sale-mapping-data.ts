
import { AutomaticSale, AutomaticSaleConfigCsv } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { CsvHeaderMappingField } from '@admin-clients/shared/common/feature/csv';

export type CsvAutomaticSalesToImport = AutomaticSale;
export type CsvAutomaticSalesToImportValueTypes = CsvAutomaticSalesToImport[keyof CsvAutomaticSalesToImport];

export function createCsvAutomaticSalesToImportMappingFields(config: AutomaticSaleConfigCsv, mandatoryFields?: string[]):
    CsvHeaderMappingField<CsvAutomaticSalesToImport>[] {
    const exampleHeadersCsv: CsvHeaderMappingField<AutomaticSale>[] = [
        { key: 'group', header: 'group', columnIndex: null, required: true },
        { key: 'num', header: 'num', columnIndex: null, required: true },
        mandatoryFields?.includes('firstName') || mandatoryFields?.includes('ATTENDANT_NAME') ?
            { key: 'name', header: 'name', columnIndex: null } : null,
        mandatoryFields?.includes('lastName') || mandatoryFields?.includes('ATTENDANT_SURNAME') ?
            { key: 'first_surname', header: 'first_surname', columnIndex: null } : null,
        mandatoryFields?.includes('lastName') || mandatoryFields?.includes('ATTENDANT_SURNAME') ?
            { key: 'second_surname', header: 'second_surname', columnIndex: null } : null,
        mandatoryFields?.includes('identification') || mandatoryFields?.includes('ATTENDANT_ID_NUMBER') ?
            { key: 'dni', header: 'dni', columnIndex: null } : null,
        mandatoryFields?.includes('telephone') || mandatoryFields?.includes('ATTENDANT_CELLPHONE') ?
            { key: 'phone', header: 'phone', columnIndex: null } : null,
        mandatoryFields?.includes('email') || mandatoryFields?.includes('ATTENDANT_MAIL') ?
            { key: 'email', header: 'email', columnIndex: null, required: true } : null,
        !config?.use_seat_mappings && config?.automatic_type === 'SECTOR' ?
            { key: 'sector', header: 'sector', columnIndex: null, required: config?.automatic_type === 'SECTOR' } : null,
        !config?.use_seat_mappings ?
            { key: 'price_zone', header: 'price_zone', columnIndex: null, required: !config?.use_seat_mappings } : null,
        { key: 'owner', header: 'owner', columnIndex: null, required: true },
        config?.use_seat_mappings ? { key: 'seat_id', header: 'seat_id', columnIndex: null, required: config?.use_seat_mappings } : null,
        config?.use_locators ? { key: 'original_locator', header: 'original_locator', columnIndex: null, required: true } : null,
        config?.default_purchase_language ? null :
            { key: 'language', header: 'language', columnIndex: null, required: !config?.default_purchase_language },
        { key: 'processed', header: 'processed', columnIndex: null },
        { key: 'error_code', header: 'error_code', columnIndex: null },
        { key: 'error_description', header: 'error_description', columnIndex: null },
        { key: 'order_id', header: 'order_id', columnIndex: null },
        config?.add_extra_attendee_information ? { key: 'extra_field', header: 'extra_field', columnIndex: null } : null
    ];
    const reducedHeadersCsv = exampleHeadersCsv.reduce<CsvHeaderMappingField<AutomaticSale>[]>((acc, value) => {
        if (value) acc.push(value);
        return acc;
    }, []);
    return reducedHeadersCsv;
}


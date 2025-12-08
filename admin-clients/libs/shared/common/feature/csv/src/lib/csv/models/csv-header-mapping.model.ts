import { CsvHeader } from './csv-header.model';

export interface CsvHeaderMapping<T> {
    mappingFields: CsvHeaderMappingField<T>[];
    parsedHeaders: string[];
}

export interface CsvHeaderMappingField<T> extends CsvHeader {
    key: keyof T;
    columnIndex: number;
    required?: boolean;
    example?: string;
}

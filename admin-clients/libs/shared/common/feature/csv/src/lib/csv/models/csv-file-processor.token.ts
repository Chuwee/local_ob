import { InjectionToken } from '@angular/core';

export interface CsvFileProcessor {
    processFile(
        file: File,
        valueCB: (processedFile: unknown) => void,
        errorsCB?: () => void
    ): void;
}

export const CSV_FILE_PROCESSOR = new InjectionToken<CsvFileProcessor>('CSV_FILE_PROCESSOR');


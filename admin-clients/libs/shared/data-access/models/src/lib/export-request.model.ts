import { FieldDataGroup } from './field-data-group.model';

export interface ExportRequest {
    fields?: ExportField[];
    format: ExportFormat;
    delivery: ExportDelivery;
}

export interface ExportJsonRequest {
    format: ExportFormat;
    delivery: ExportDelivery;
}

export interface ExportField {
    field: string;
    name?: string;
}

export enum ExportFormat {
    csv = 'CSV',
    json = 'JSON'
}

export interface ExportDelivery {
    type: ExportDeliveryType;
    properties: {
        address: string;
    };
}

export enum ExportDeliveryType {
    email = 'EMAIL'
}

export interface ExportDialogData {
    exportData: FieldDataGroup[];
    exportFormat: ExportFormat;
    selectedFields?: string[];
}

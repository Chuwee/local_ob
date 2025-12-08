import { ExportRequest } from '@admin-clients/shared/data-access/models';

export interface AutomaticSalesExportRequest extends ExportRequest {
    filename: string;
}

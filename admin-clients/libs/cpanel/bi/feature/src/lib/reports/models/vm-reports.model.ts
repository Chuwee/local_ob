import { BiReport, BiReportPrompt } from '@admin-clients/cpanel/bi/data-access';

export interface VmBiReportCategorySearch {
    name: string;
    reports: BiReport[];
}

export interface VmBiReportPrompt extends BiReportPrompt {
    canRender: true;
}

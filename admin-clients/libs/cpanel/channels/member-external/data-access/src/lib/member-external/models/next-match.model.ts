import { IdName } from '@admin-clients/shared/data-access/models';

export interface NextMatch extends IdName {
    season: string;
    competition: string;
}

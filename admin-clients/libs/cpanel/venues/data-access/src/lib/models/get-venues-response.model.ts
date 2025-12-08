import { ListResponse } from '@OneboxTM/utils-state';
import { Venue } from '@admin-clients/shared/data-access/models';

export interface GetVenuesResponse extends ListResponse<Venue> {
}

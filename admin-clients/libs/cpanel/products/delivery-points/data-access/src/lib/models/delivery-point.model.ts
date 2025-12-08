import { Country, Region } from '@admin-clients/shared/common/data-access';

export interface DeliveryPoint {
    id: number;
    entity: {
      id: number;
      name: string;
    };
    name: string;
    location: {
      country: Country;
      country_subdivision: Region;
      city: string;
      zip_code: string;
      address: string;
      notes: string;
    };
    status: DeliveryPointStatus;
    venue: {
      id: number;
      name: string;
    };
}

export enum DeliveryPointStatus {
    active = 'ACTIVE',
    inactive = 'INACTIVE'
}

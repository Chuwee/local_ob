import { DonationProviders } from './donations-providers.enum';

export interface DonationProvider {
    id: DonationProviders;
    name: string;
}

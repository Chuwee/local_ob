import { Country, Region } from '@admin-clients/shared/common/data-access';
import { Producer } from './producer.model';

interface Contact {
    address: string;
    city: string;
    postal_code: string;
    country: Country;
    country_subdivision: Region;
    email: string;
    phone: string;
}

export interface ProducerDetails extends Producer {
    social_reason: string;
    default: boolean;
    contact: Contact;
    use_simplified_invoice?: boolean;
}

export interface PutProducerDetails extends Partial<ProducerDetails> {
    id: number;
    contact?: Contact;
}

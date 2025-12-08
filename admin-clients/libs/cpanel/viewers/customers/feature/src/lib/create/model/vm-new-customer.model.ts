import { PostCustomer } from '@admin-clients/cpanel-viewers-customers-data-access';
import { Moment } from 'moment-timezone';

export interface VmNewCustomer extends Omit<PostCustomer, 'birthday'> {
    birthday?: Moment;
}

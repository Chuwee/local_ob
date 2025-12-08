import { Id, IdName } from '@admin-clients/shared/data-access/models';

export enum ChannelSurchargeTaxesOrigin {
    event = 'EVENT',
    channel = 'CHANNEL'
}

export interface ChannelSurchargeTaxes {
    origin: ChannelSurchargeTaxesOrigin;
    taxes?: IdName[];
}

export interface PutChannelSurchargeTaxes {
    origin: ChannelSurchargeTaxesOrigin;
    taxes?: Id[];
}

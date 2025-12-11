import { Id, IdName } from './common-types';

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

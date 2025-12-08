import { VenueTemplateBlockingReasonCode } from './venue-template-blocking-reason-code.enum';

export interface VenueTemplateBlockingReason {
    id: number;
    name: string;
    color: string;
    default: boolean;
    code: VenueTemplateBlockingReasonCode;
}

export type VenueTemplateBlockingReasonPut = Omit<VenueTemplateBlockingReason, 'code' | 'default'>

export type VenueTemplateBlockingReasonPost = Omit<VenueTemplateBlockingReason, 'id' | 'default' | 'code'>

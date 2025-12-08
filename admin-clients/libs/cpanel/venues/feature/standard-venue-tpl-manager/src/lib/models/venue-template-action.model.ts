import { Seat } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';

export enum VenueTemplateActionType {
    viewTicketDetail = 'viewTicketDetail',
    exportCapacity = 'exportCapacity',
    selectRelocationOrigin = 'selectRelocationOrigin',
    startRelocation = 'startRelocation'
}

export interface VenueTemplateAction {
    type: VenueTemplateActionType;
}

export interface VenueTemplateSeatClickAction extends VenueTemplateAction {
    data: Seat;
}

export interface VenueTemplateGetSeatInfo extends VenueTemplateAction {
    data: number[];
}

export interface VenueTemplateStartRelocation extends VenueTemplateAction {
    data: { source_id: number; destination_id: number }[];
}
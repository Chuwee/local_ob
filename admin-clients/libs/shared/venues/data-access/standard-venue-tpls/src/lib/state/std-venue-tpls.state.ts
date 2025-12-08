import { StateProperty, ListResponse } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { SectorElement } from '../models/api-sector.model';
import { VenueTemplateAvetCompetition } from '../models/avet/venue-template-avet-competition.model';
import { VenueTemplateView } from '../models/venue-template-view.model';
import { NotNumberedZone, Sector, VenueMap } from '../models/vm-item.model';

@Injectable()
export class StdVenueTplsState {

    readonly venueMap = new StateProperty<VenueMap>();
    readonly venueMapUpdate = new StateProperty();

    readonly views = new StateProperty<ListResponse<VenueTemplateView>>();
    readonly view = new StateProperty<VenueTemplateView>();
    readonly svg = new StateProperty<string>();

    readonly sectors = new StateProperty<SectorElement[]>();
    readonly sector = new StateProperty<Sector>();
    readonly sectorUpdate = new StateProperty();

    readonly nnz = new StateProperty<NotNumberedZone>();
    readonly nnzUpdate = new StateProperty();
    readonly nnzIncrease = new StateProperty();

    readonly avetCompetitions = new StateProperty<VenueTemplateAvetCompetition[]>();
}

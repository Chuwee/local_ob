import { EdNotNumberedZone, EdSeat } from './venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorViewLink } from './venue-tpl-editor-view-link.model';

export interface VenueTplEditorConsistencyCheckResult {
    result: 'ok' | 'ko';
    linksWithoutShape?: VenueTplEditorViewLink[];
    zonesWithoutShape?: EdNotNumberedZone[];
    seatsWithoutShape?: EdSeat[];
    linksWithoutModel?: SVGElement[];
    zonesWithoutModel?: SVGElement[];
    seatsWithoutModel?: SVGElement[];
}

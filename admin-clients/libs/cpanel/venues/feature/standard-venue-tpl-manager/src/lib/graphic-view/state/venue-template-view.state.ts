import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { SvgElementWrapper } from '../../models/svg/svg-element.wrapper.model';

@Injectable()
export class VenueTemplateViewState {
    // SEATS ELEMENTS
    private readonly _svgSeats = new BaseStateProp<Map<string, SvgElementWrapper>>();
    readonly setSvgSeats = this._svgSeats.setValueFunction();
    readonly getSvgSeats$ = this._svgSeats.getValueFunction();
    // NOT NUMBERED ZONE ELEMENTS
    private readonly _svgNnz = new BaseStateProp<Map<string, SvgElementWrapper>>();
    readonly setSvgNnz = this._svgNnz.setValueFunction();
    readonly getSvgNnz$ = this._svgNnz.getValueFunction();
    // VIEW UPDATE TRIGGER
    private readonly _viewUpdated = new BaseStateProp<void>();
    readonly setViewUpdated = this._viewUpdated.setValueFunction();
    readonly getViewUpdated$ = this._viewUpdated.getValueFunction();
}

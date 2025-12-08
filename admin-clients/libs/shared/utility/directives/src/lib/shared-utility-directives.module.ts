import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { NgModule } from '@angular/core';
import { EllipsifyDirective } from './ellipsify/ellipsify.directive';
import { ResizeObserverDirective } from './resize-observer.directive';
import { NgForTrackByPropertyDirective } from './track-by-property/track-by-property.directive';

@NgModule({
    imports: [
        EllipsifyDirective,
        FormControlErrorsComponent,
        NgForTrackByPropertyDirective,
        ResizeObserverDirective
    ],
    exports: [
        EllipsifyDirective,
        FormControlErrorsComponent,
        NgForTrackByPropertyDirective,
        ResizeObserverDirective
    ]
})
export class SharedUtilityDirectivesModule { }

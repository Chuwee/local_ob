import {
    ActivityVenueTemplateGatesComponent, ActivityVenueTemplatePriceTypesGatesComponent
} from '@admin-clients/cpanel-common-venue-templates-feature';
import { MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ACTIVITY_PRICE_TYPES_GATES_SERVICE, ActVenueTplService } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { VenueTemplate, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-promoter-venue-template-gates',
    templateUrl: './promoter-venue-template-gates.component.html',
    styleUrls: ['./promoter-venue-template-gates.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{ provide: ACTIVITY_PRICE_TYPES_GATES_SERVICE, useExisting: ActVenueTplService }],
    imports: [
        FormContainerComponent, FlexLayoutModule, ReactiveFormsModule, MatExpansionPanel, AsyncPipe,
        MatExpansionPanelHeader, MatExpansionPanelTitle, TranslatePipe, ReactiveFormsModule, MatProgressSpinner,
        ActivityVenueTemplatePriceTypesGatesComponent, ActivityVenueTemplateGatesComponent
    ]
})
export class PromoterVenueTemplateGatesComponent implements OnInit, AfterViewInit, WritingComponent {
    @ViewChild(ActivityVenueTemplatePriceTypesGatesComponent) private _priceGatesComponent: ActivityVenueTemplatePriceTypesGatesComponent;
    @ViewChild(ActivityVenueTemplateGatesComponent) private _gatesComponent: ActivityVenueTemplateGatesComponent;

    form: UntypedFormGroup;
    venueTemplate$: Observable<VenueTemplate>;
    loading$: Observable<boolean>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _msgDialogService: MessageDialogService,
        private _venueTemplatesSrv: VenueTemplatesService
    ) { }

    ngOnInit(): void {

        this.venueTemplate$ = this._venueTemplatesSrv.venueTpl.get$();
        this.form = this._fb.group({});
    }

    ngAfterViewInit(): void {
        this.loading$ = booleanOrMerge([
            this._priceGatesComponent.loading$,
            this._gatesComponent.loading$
        ]);
    }

    gatesChanged(): void {
        this._priceGatesComponent.reset();
    }

    save(): void {
        this._priceGatesComponent.save().subscribe(() => this._priceGatesComponent.reset());
    }

    cancel(): void {
        this._priceGatesComponent.reset();
    }
}

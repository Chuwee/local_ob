import { ImageUploaderComponent } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { StdVenueTplService, VENUE_MAP_SERVICE, VenueMapService } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, Input, OnDestroy, OnInit, Optional } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl } from '@angular/forms';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { Observable, Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { VenueTemplateEditorType } from '../models/venue-template-editor-type.model';
import { venueTemplateImageRestrictions } from '../models/venue-template-image-restrictions.model';
import { StandardVenueTemplateBaseService } from '../services/standard-venue-template-base.service';
import { StandardVenueTemplateChangesService } from '../services/standard-venue-template-changes.service';

@Component({
    imports: [
        AsyncPipe,
        MatProgressSpinner,
        FlexLayoutModule,
        ReactiveFormsModule,
        ImageUploaderComponent
    ],
    selector: 'app-template-image',
    templateUrl: './template-image.component.html',
    styleUrls: ['./template-image.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TemplateImageComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _disabledEditorTypes = [
        VenueTemplateEditorType.sessionTemplate,
        VenueTemplateEditorType.multiSessionTemplate,
        VenueTemplateEditorType.sessionPackTemplate,
        VenueTemplateEditorType.seasonTicketTemplate
    ];

    @Input() editorType: VenueTemplateEditorType;
    disabled = false;
    hide = false;
    loading$: Observable<boolean>;
    imageControl: UntypedFormControl;
    imageRestrictions = venueTemplateImageRestrictions;

    constructor(
        private _fb: UntypedFormBuilder,
        private _venueTemplatesSrv: VenueTemplatesService,
        private _stdVenueTplSrv: StdVenueTplService,
        private _standardVenueTemplateBaseSrv: StandardVenueTemplateBaseService,
        private _standardVenueTemplateChangesSrv: StandardVenueTemplateChangesService,
        @Inject(VENUE_MAP_SERVICE) @Optional() private _venueMapSrv: VenueMapService
    ) {
        this._venueMapSrv ??= this._stdVenueTplSrv;
    }

    ngOnInit(): void {
        this.loading$ = booleanOrMerge([
            this._venueTemplatesSrv.venueTpl.inProgress$(),
            this._venueTemplatesSrv.isVenueTemplateSaving$(),
            this._venueMapSrv.isVenueMapLoading$(),
            this._stdVenueTplSrv.isSectorLoading$(),
            this._stdVenueTplSrv.isZoneLoading$(),
            this._venueMapSrv.isVenueMapSaving$()
        ]);
        this.disabled = this._disabledEditorTypes.includes(this.editorType);
        this.imageControl = this._fb.control({ value: null, disabled: this.disabled });
        this.imageControl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(image => this._standardVenueTemplateChangesSrv.setTemplateImage(image));
        this._venueTemplatesSrv.venueTpl.get$().pipe(filter(vt => vt !== null))
            .pipe(takeUntil(this._onDestroy))
            .subscribe(venueTemplate => {
                this.imageControl.setValue(venueTemplate.image_url);
                this.hide = this.imageControl.disabled && !this.imageControl.value;
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}

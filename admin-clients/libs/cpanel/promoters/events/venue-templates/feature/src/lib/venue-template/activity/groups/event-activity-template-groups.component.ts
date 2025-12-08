import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { ActivityVenueTemplateGroupsComponent } from '@admin-clients/cpanel-common-venue-templates-feature';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ACTIVITY_GROUPS_SERVICE, ActVenueTplService } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { VenueTemplate, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, throwError } from 'rxjs';
import { switchMap, take, tap } from 'rxjs/operators';

@Component({
    selector: 'app-event-activity-template-groups',
    templateUrl: './event-activity-template-groups.component.html',
    styleUrls: ['./event-activity-template-groups.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{ provide: ACTIVITY_GROUPS_SERVICE, useExisting: ActVenueTplService }],
    imports: [
        FormContainerComponent, AsyncPipe, ReactiveFormsModule, FlexLayoutModule, TranslatePipe,
        MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, MatProgressSpinner,
        ArchivedEventMgrComponent, ActivityVenueTemplateGroupsComponent
    ]
})
export class EventActivityTemplateGroupsComponent implements OnInit, WritingComponent, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    @ViewChild(ActivityVenueTemplateGroupsComponent)
    private _activityVenueTemplateGroupsComponent: ActivityVenueTemplateGroupsComponent;

    form: UntypedFormGroup;
    venueTemplate$: Observable<VenueTemplate>;
    reqInProgress$: Observable<boolean>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _ephemeralSrv: EphemeralMessageService,
        private _venueTemplatesSrv: VenueTemplatesService,
        @Inject(ACTIVITY_GROUPS_SERVICE) private _activityGroupsService: ActVenueTplService
    ) {
    }

    ngOnInit(): void {
        this.reqInProgress$ = this._activityGroupsService.isActivityGroupsConfigInProgress$();
        this.venueTemplate$ = this._venueTemplatesSrv.venueTpl.get$();
        this.form = this._fb.group({});
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cancelChanges(): void {
        this._activityVenueTemplateGroupsComponent.reset();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<VenueTemplate> {
        if (this.form.valid) {
            return this._activityVenueTemplateGroupsComponent.save()
                .pipe(
                    tap(() => this._ephemeralSrv.showSaveSuccess()),
                    switchMap(() => this.venueTemplate$
                        .pipe(
                            take(1),
                            tap(venueTemplate => this._venueTemplatesSrv.venueTpl.load(venueTemplate.id))
                        ))
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }
}


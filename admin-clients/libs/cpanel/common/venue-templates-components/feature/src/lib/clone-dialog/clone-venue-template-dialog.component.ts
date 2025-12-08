import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { GetVenuesRequest, venuesProviders, VenuesService } from '@admin-clients/cpanel/venues/data-access';
import { EntitiesBaseService, EntitiesBaseState, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent, SelectServerSearchComponent } from '@admin-clients/shared/common/ui/components';
import { IdName } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    VenueTemplate, VenueTemplateFieldsRestrictions, VenueTemplateScope, VenueTemplatesService, VenueTemplatesState
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { first, Subject, take } from 'rxjs';
import { filter, map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { CloneVenueTplDialogResult } from './models/clone-venue-tpl-dialog-result.model';

@Component({
    selector: 'app-clone-venue-template-dialog',
    templateUrl: './clone-venue-template-dialog.component.html',
    styleUrls: ['./clone-venue-template-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        VenueTemplatesService, VenueTemplatesState,
        EntitiesBaseService, EntitiesBaseState,
        venuesProviders
    ],
    imports: [
        MatDialogModule, MatFormFieldModule, MatIcon, ReactiveFormsModule,
        MatSelectModule, MatSpinner, MatInputModule, MatButtonModule, AsyncPipe,
        TranslatePipe, SelectSearchComponent, SelectServerSearchComponent, FormControlErrorsComponent
    ]
})
export class CloneVenueTemplateDialogComponent implements OnInit, OnDestroy {
    private readonly _onDestroy: Subject<void> = new Subject();

    private readonly _dialogRef = inject(MatDialogRef<CloneVenueTemplateDialogComponent, CloneVenueTplDialogResult>);
    private readonly _data: { fromVenueTemplate: VenueTemplate } = inject(MAT_DIALOG_DATA);

    private readonly _auth = inject(AuthenticationService);
    private readonly _venueTemplatesService = inject(VenueTemplatesService);
    private readonly _entitiesService = inject(EntitiesBaseService);
    private readonly _venuesService = inject(VenuesService);
    private readonly _fb = inject(FormBuilder);

    readonly restrictions = VenueTemplateFieldsRestrictions;

    readonly form = this._fb.group({
        name: [null as string, [Validators.required, Validators.maxLength(VenueTemplateFieldsRestrictions.nameLength)]],
        entity: [null as number, [Validators.required]],
        venue: [null as IdName, [Validators.required]],
        space: [null as number, [Validators.required]]
    });

    readonly operatorMode$ = this._auth.getLoggedUser$().pipe(
        first(user => user !== null),
        map(user => AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR])));

    readonly entities$ = this._entitiesService.entityList.getData$();

    readonly venues$ = this._venuesService.venuesList.getData$().pipe(
        map(venues => venues?.map(venue => ({
            id: venue.id,
            name: venue.name + ' - ' + venue.entity.name
        })))
    );

    readonly moreVenuesAvailable$ = this._venuesService.venuesList.getMetadata$()
        .pipe(map(meta => meta ? meta.offset + meta.limit < meta.total : true));

    readonly spaces$ = this._venuesService.getVenue$().pipe(map(venue => venue?.spaces));

    readonly loading$ = booleanOrMerge([
        this._entitiesService.entityList.inProgress$(),
        this._venueTemplatesService.venueTpl.inProgress$(),
        this._venuesService.isVenueLoading$()
    ]);

    constructor() {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this._entitiesService.entityList.clear();
        this._venueTemplatesService.venueTpl.clear();
        this._venueTemplatesService.venueTpl.load(this._data.fromVenueTemplate.id);
        this._venueTemplatesService.venueTpl.get$()
            .pipe(first(Boolean))
            .subscribe(venueTemplate => {
                this.form.patchValue({
                    entity: venueTemplate.entity.id,
                    venue: venueTemplate.venue,
                    space: venueTemplate.space.id
                });
            });
        this.operatorMode$.pipe()
            .pipe(
                take(1),
                filter(Boolean),
                tap(() => {
                    this._entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name],
                        type: this._data.fromVenueTemplate.scope === VenueTemplateScope.archetype ?
                            'VENUE_ENTITY' : 'EVENT_ENTITY'
                    });
                }),
                switchMap(() => this._entitiesService.entityList.getData$()),
                filter(Boolean),
                take(1)
            )
            .subscribe(entities => {
                this.form.controls.entity.valueChanges.pipe(takeUntil(this._onDestroy)).subscribe(() => {
                    this.form.controls.venue.setValue(null);
                    this._venuesService.venuesList.clear();
                });
                // venue
                this.form.controls.venue.valueChanges.pipe(takeUntil(this._onDestroy)).subscribe(venue => {
                    this.form.controls.space.setValue(null);
                    this._venuesService.clearVenue();
                    if (venue) {
                        this._venuesService.loadVenue(venue.id);
                    }
                });
                this._venuesService.getVenue$().pipe(takeUntil(this._onDestroy)).subscribe(venue => {
                    if (venue && this.form.value?.venue?.id === venue.id) {
                        if (this.form.value.space === null
                            || !venue.spaces.map(space => space.id).includes(this.form.value.space)) {
                            this.form.controls.space.setValue(venue.spaces[0].id);
                        }
                    }
                });
                if (!entities.map(entity => entity.id).includes(this.form.value.entity)) {
                    this.form.setValue({
                        name: null,
                        entity: null,
                        venue: null,
                        space: null
                    });
                } else {
                    this._venuesService.loadVenue(this.form.value.venue.id);
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cloneVenueTemplate(): void {
        if (this.form.valid) {
            this._venueTemplatesService.cloneVenueTemplate(
                this._data.fromVenueTemplate.id,
                {
                    name: this.form.value.name,
                    entity_id: this.form.value.entity,
                    venue_id: this.form.value.venue.id,
                    space_id: this.form.value.space
                })
                .subscribe(id => this.close(this.form.value.entity, id));
        } else {
            this.form.markAllAsTouched();
        }
    }

    close(entityId: number = null, templateId: number = null): void {
        if (entityId || templateId) {
            this._dialogRef.close({ entityId, templateId });
        } else {
            this._dialogRef.close();
        }
    }

    loadVenues(q: string = undefined, next = false): void {
        if (this.form.value.entity) {
            const request: GetVenuesRequest = {
                offset: 0,
                limit: 50,
                // uncomment when api accepts entity field
                // fields: [VenuesFilterFields.name, VenuesFilterFields.entity],
                entityId: this.form.value.entity,
                includeOwnTemplateVenues: this._data.fromVenueTemplate.scope === VenueTemplateScope.standard,
                includeThirdPartyVenues: this._data.fromVenueTemplate.scope === VenueTemplateScope.standard,
                q
            };
            if (!next) {
                this._venuesService.venuesList.load(request);
            } else {
                this._venuesService.venuesList.loadMore(request);
            }
        } else {
            this._venuesService.venuesList.setEmpty();
        }

    }
}

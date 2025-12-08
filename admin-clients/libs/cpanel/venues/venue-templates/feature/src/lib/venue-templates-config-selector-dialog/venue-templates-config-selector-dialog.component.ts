import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { eventsProviders, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EntitiesBaseService, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { DialogSize, MessageDialogService, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { compareWithIdOrCode } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    PostVenueTemplateElementInfoRequest, VenueTemplatesService, VenueTemplatesState, VenueTemplateStatus
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject, OnDestroy } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelect, MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, map, Subject, takeUntil, tap } from 'rxjs';

@Component({
    imports: [
        NgIf, NgFor, MatIconModule, TranslatePipe, AsyncPipe, MatProgressSpinnerModule, ReactiveFormsModule,
        MatTooltipModule, MatDialogModule, MatButtonModule, FlexLayoutModule, ObFormFieldLabelDirective, SelectSearchComponent,
        FormControlErrorsComponent, MatFormFieldModule, MatSelectModule, MatRadioModule
    ],
    selector: 'app-venue-tpls-config-selector-dialog',
    templateUrl: './venue-templates-config-selector-dialog.component.html',
    styleUrls: ['./venue-templates-config-selector-dialog.component.scss'],
    //TODO: Check this
    providers: [VenueTemplatesService, VenueTemplatesState, eventsProviders, MatSelect],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTemplatesConfigSelectorDialogComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<VenueTemplatesConfigSelectorDialogComponent>);
    readonly #destroyRef: DestroyRef = inject(DestroyRef);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #eventsService = inject(EventsService);
    readonly #auth = inject(AuthenticationService);
    readonly #onDestroy: Subject<void> = new Subject();
    readonly #msgDialogService = inject(MessageDialogService);

    readonly #data: {
        entityId: number;
        templateId: number;
    } = inject(MAT_DIALOG_DATA);

    readonly #templateId = this.#data.templateId;

    readonly operatorMode$ = this.#auth.getLoggedUser$().pipe(
        first(user => user !== null),
        map(user => AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR])),
        tap(isOperator => {
            if (isOperator) {
                this.#entitiesService.entityList.load({
                    limit: 999,
                    sort: 'name:asc',
                    fields: [EntitiesFilterFields.name]
                });
            }
        })
    );

    readonly entities$ = this.#entitiesService.entityList.getData$();
    readonly events$ = this.#eventsService.eventsList.getData$();

    readonly form = this.#fb.group({
        match: ['BY_CODE' as 'BY_NAME' | 'BY_CODE'],
        entity: [null as number, [Validators.required]],
        event: [null as number],
        venueTpl: [null as number, [Validators.required]]
    });

    readonly compareWith = compareWithIdOrCode;

    readonly venueTemplates$ = this.#venueTemplatesSrv.getVenueTemplatesList$()
        .pipe(
            filter(Boolean),
            map(value => value.data)
        );

    readonly isLoading$ = booleanOrMerge([
        this.#venueTemplatesSrv.isVenueTemplatesListLoading$(),
        this.#venueTemplatesSrv.venueTplElementInfo.inProgress$()
    ]);

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;

        this.form.controls.entity.valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe(entityId => {
                this.#eventsService.eventsList.clear();
                this.#eventsService.eventsList.load({
                    limit: 999, offset: 0, entityId, sort: 'name:asc'
                });

                this.#venueTemplatesSrv.clearVenueTemplateData();
                this.#venueTemplatesSrv.loadVenueTemplatesList({
                    limit: 999, offset: 0, entityId, sort: 'name:asc', status: [VenueTemplateStatus.active]
                });
            });

        this.form.controls.event.valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe(eventId => {
                this.#venueTemplatesSrv.clearVenueTemplateData();
                this.#venueTemplatesSrv.loadVenueTemplatesList({
                    limit: 999, offset: 0, entityId: this.form.controls.entity.value, eventId, sort: 'name:asc', status: [VenueTemplateStatus.active]
                });
            });

        this.form.controls.entity.setValue(this.#data.entityId);

    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    importConfig(): void {
        const req: PostVenueTemplateElementInfoRequest = {
            copy_info: {
                source: this.form.controls.venueTpl.value,
                match_type: this.form.controls.match.value
            }
        };
        this.#venueTemplatesSrv.venueTplElementInfo.create(this.#templateId, req).pipe(
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe({
            next: () => {
                this.close(true);
            },
            error: (error: HttpErrorResponse) => {
                if (error.error.code === 'VENUE_TEMPLATE_INFO_SOURCE_AND_TARGET_TEMPLATES_CANNOT_HAVE_DIFFERENT_TYPES') {
                    this.#msgDialogService.showAlert({
                        size: DialogSize.SMALL,
                        title: 'VENUE_TEMPLATES.IMPORT_CONTENTS.IMPORT_ERROR_TITLE',
                        message: 'VENUE_TEMPLATES.IMPORT_CONTENTS.IMPORT_ERROR_MESSAGE'
                    });
                }
            }
        });
    }

    close(isDone = false): void {
        this.#dialogRef.close(isDone);
    }
}

/* eslint-disable @typescript-eslint/dot-notation */
import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { EventExternalBarcodes, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable, shareReplay, Subject, switchMap, takeUntil, tap, throwError } from 'rxjs';

@Component({
    imports: [
        NgIf, NgFor, AsyncPipe,
        FlexLayoutModule,
        ReactiveFormsModule,
        MaterialModule,
        TranslatePipe,
        FormControlErrorsComponent,
        FormContainerComponent,
        SelectSearchComponent,
        ArchivedEventMgrComponent
    ],
    selector: 'app-event-code-configuration',
    templateUrl: './event-code-configuration.component.html',
    styleUrls: ['./event-code-configuration.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventCodeConfigurationComponent implements OnInit, OnDestroy, WritingComponent {
    private _eventId: number;
    private _onDestroy = new Subject<void>();
    form: UntypedFormGroup;
    isLoadingOrSaving$: Observable<boolean>;
    componentBlocked$: Observable<boolean>;
    fairCodeOptions$: Observable<string[]>;
    fairEditionOptions$: Observable<string[]>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _eventsService: EventsService,
        private _entitiesSrv: EntitiesBaseService,
        private _ephemeralMessage: EphemeralMessageService
    ) { }

    ngOnInit(): void {
        this.initForm();

        this._eventsService.event.get$()
            .pipe(
                filter(event => !!event),
                switchMap(event => {
                    this._eventId = event.id;
                    this._eventsService.eventExternalBarcodes.load(this._eventId);
                    this._entitiesSrv.loadExternalBarcodesEntityOptions(event.entity.id);
                    return this._eventsService.eventExternalBarcodes.get$();
                }),
                filter(externalBarcodes => !!externalBarcodes),
                takeUntil(this._onDestroy)
            ).subscribe(externalBarcodes => {
                this.updateFormValues(externalBarcodes);
            });

        this.componentBlocked$ = this._eventsService.event.get$()
            .pipe(
                filter(event => !!event),
                tap(event => this.disableIfHasSales(event.has_sales)),
                map(event => event.has_sales),
                shareReplay(1)
            );

        this.fairCodeOptions$ = this._entitiesSrv.getExternalBarcodesEntityOptions$()
            .pipe(
                filter(response => !!response),
                map(resp => resp.properties['fairCodes'])
            );

        this.fairEditionOptions$ = this._entitiesSrv.getExternalBarcodesEntityOptions$()
            .pipe(
                filter(response => !!response),
                map(resp => resp.properties['fairEditions'])
            );

        this.isLoadingOrSaving$ = booleanOrMerge([
            this._eventsService.event.inProgress$(),
            this._eventsService.eventExternalBarcodes.inProgress$()
        ]);

        this.form.get('toggleControl').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => {
                if (value && !this.form.get('toggleControl').disabled) {
                    this.form.get('fairCode').enable();
                    this.form.get('fairEdition').enable();
                } else {
                    this.form.get('fairCode').disable();
                    this.form.get('fairEdition').disable();
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._eventsService.eventExternalBarcodes.clear();
    }

    refresh(): void {
        this._eventsService.eventExternalBarcodes.load(this._eventId);
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const formValues = this.form.value;
            const externalBarcodesData: Partial<EventExternalBarcodes> = {
                allowed: formValues.toggleControl
            };

            if (externalBarcodesData.allowed) {
                externalBarcodesData.fair_code = formValues.fairCode.length < 2
                    ? ('0' + formValues.fairCode)
                    : formValues.fairCode.toString();
                externalBarcodesData.fair_edition = formValues.fairEdition.length < 2
                    ? ('0' + formValues.fairEdition)
                    : formValues.fairEdition.toString();
            }

            return this._eventsService.eventExternalBarcodes.update(this._eventId, externalBarcodesData).pipe(
                tap(() => {
                    this._ephemeralMessage.showSaveSuccess();
                    this._eventsService.eventExternalBarcodes.load(this._eventId);
                })
            );
        } else {
            this.form.markAsDirty();
            this.form.markAllAsTouched();
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    private initForm(): void {
        this.form = this._fb.group({
            toggleControl: [false, Validators.required],
            fairCode: ['', [Validators.required, Validators.min(0), Validators.max(99)]],
            fairEdition: ['', [Validators.required, Validators.min(0), Validators.max(99)]]
        });

    }

    private updateFormValues(externalBarcodes: EventExternalBarcodes): void {
        this.form.patchValue({
            toggleControl: externalBarcodes.allowed,
            fairCode: externalBarcodes.fair_code,
            fairEdition: externalBarcodes.fair_edition
        });
        this.form.markAsPristine();
    }

    private disableIfHasSales(eventHasSales: boolean): void {
        if (eventHasSales) {
            this.form.get('toggleControl').disable();
            this.form.get('fairCode').disable();
            this.form.get('fairEdition').disable();
        }
    }
}

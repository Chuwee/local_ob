import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, SessionExternalBarcodes, SessionExternalBarcodesPassType
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { filter, map, Observable, Subject, switchMap, takeUntil, tap, throwError, withLatestFrom } from 'rxjs';

@Component({
    selector: 'app-session-code-configuration',
    templateUrl: './session-code-configuration.component.html',
    styleUrls: ['./session-code-configuration.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionCodeConfigurationComponent implements OnInit, OnDestroy, WritingComponent {
    private _sessionId: number;
    private _eventId: number;
    private _onDestroy = new Subject<void>();
    form: UntypedFormGroup;
    isLoadingOrSaving$: Observable<boolean>;
    isCodesAllowed$: Observable<boolean>;
    personTypeOptions$: Observable<string[]>;
    variableCodeOptions$: Observable<string[]>;
    barcodesPassTypes: SessionExternalBarcodesPassType[] = [
        SessionExternalBarcodesPassType.days,
        SessionExternalBarcodesPassType.uses,
        SessionExternalBarcodesPassType.allFair,
        SessionExternalBarcodesPassType.period
    ];

    constructor(
        private _fb: UntypedFormBuilder,
        private _eventSessionsService: EventSessionsService,
        private _eventsService: EventsService,
        private _entitiesSrv: EntitiesBaseService,
        private _ephemeralMessage: EphemeralMessageService
    ) { }

    ngOnInit(): void {
        this.initForm();
        this.model();
        this.initFormHandler();

    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._eventSessionsService.clearSessionExternalBarcodes();
    }

    refresh(): void {
        this._eventSessionsService.loadSessionExternalBarcodes(this._eventId, this._sessionId);
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const { passType, days, uses, personType, variableCode } = this.form.value;
            const sessionExternalBarcodesData: Partial<SessionExternalBarcodes> = {
                pass_type: passType,
                uses,
                days,
                person_type: personType,
                variable_code: variableCode
            };

            return this._eventSessionsService.saveSessionExternalBarcodeConfig(this._eventId, this._sessionId, sessionExternalBarcodesData)
                .pipe(
                    tap(() => {
                        this._ephemeralMessage.showSaveSuccess();
                        this._eventSessionsService.loadSessionExternalBarcodes(this._eventId, this._sessionId);
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
            personType: [null, [Validators.required]],
            variableCode: [null, [Validators.required]],
            uses: [null, [Validators.required, Validators.min(1), Validators.max(10)]],
            days: [null, [Validators.required, Validators.min(1), Validators.max(10)]],
            passType: [null, Validators.required]
        });
    }

    private model(): void {
        this.personTypeOptions$ = this._entitiesSrv.getExternalBarcodesEntityOptions$()
            .pipe(
                filter(options => !!options),
                map(resp => resp.properties['personTypes'])
            );

        this.variableCodeOptions$ = this._entitiesSrv.getExternalBarcodesEntityOptions$()
            .pipe(
                filter(options => !!options),
                map(resp => resp.properties['variableCodes'])
            );

        //Controla si desde confCodes de evento se permite códigos propios
        this.isCodesAllowed$ = this._eventsService.event.get$()
            .pipe(
                filter(event => !!event),
                switchMap(event => {
                    this._eventsService.eventExternalBarcodes.load(event.id);
                    return this._eventsService.eventExternalBarcodes.get$();
                }),
                filter(externalBarcodes => !!externalBarcodes),
                map(externalBarcodes => externalBarcodes.allowed),
                takeUntil(this._onDestroy)
            );

        this.isLoadingOrSaving$ = booleanOrMerge([
            this._eventSessionsService.session.loading$(),
            this._eventSessionsService.isSessionExternalBarcodesLoading$(),
            this._eventSessionsService.isSessionExternalBarcodesSaving$()
        ]);
    }

    private initFormHandler(): void {
        this._eventSessionsService.session.get$()
            .pipe(
                filter(session => !!session),
                switchMap(session => {
                    this._sessionId = session.id;
                    this._eventId = session.event.id;
                    this._eventSessionsService.loadSessionExternalBarcodes(this._eventId, this._sessionId);
                    this._entitiesSrv.loadExternalBarcodesEntityOptions(session.entity.id);
                    return this._eventSessionsService.getSessionExternalBarcodes$();
                }),
                filter(sessionExternalBarcodes => !!sessionExternalBarcodes),
                takeUntil(this._onDestroy)
            ).subscribe(sessionExternalBarcodes => {
                this.updateFormValues(sessionExternalBarcodes);
            });

        this.isCodesAllowed$.subscribe(allowed => {
            if (!allowed) {
                this.form.disable();
            }
        });

        //Controla enable de days y uses en función de passType
        this.form.get('passType').valueChanges
            .pipe(
                takeUntil(this._onDestroy),
                withLatestFrom(this.isCodesAllowed$)
            )
            .subscribe(([passType, codesAllowed]) => {
                if (passType === SessionExternalBarcodesPassType.uses && codesAllowed) {
                    this.form.get('uses').enable();
                    this.form.get('days').disable();
                } else if (passType === SessionExternalBarcodesPassType.days && codesAllowed) {
                    this.form.get('days').enable();
                    this.form.get('uses').disable();
                } else {
                    this.form.get('uses').disable();
                    this.form.get('days').disable();
                }
            });

        //Controla disable de personType y variableCode en función de si sesión tiene ventas
        this._eventSessionsService.session.get$()
            .pipe(
                filter(session => !!session),
                takeUntil(this._onDestroy),
                map(session => session.has_sales)
            )
            .subscribe(hasSales => {
                this.disableIfHasSales(hasSales);
            });
    }

    private updateFormValues({
        person_type: personType, variable_code: variableCode, uses, days, pass_type: passType
    }: SessionExternalBarcodes): void {
        this.form.patchValue({ personType, variableCode, uses, days, passType });
        this.form.markAsPristine();
    }

    private disableIfHasSales(sessionHasSales: boolean): void {
        if (sessionHasSales) {
            this.form.get('personType').disable();
            this.form.get('variableCode').disable();
        } else {
            this.form.get('personType').enable();
            this.form.get('variableCode').enable();
        }
    }
}

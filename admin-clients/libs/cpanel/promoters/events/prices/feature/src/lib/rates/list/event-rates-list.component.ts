import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { EventsService, EventStatus, PostEventRate } from '@admin-clients/cpanel/promoters/events/data-access';
import { Rate } from '@admin-clients/cpanel/promoters/shared/data-access';
import { VmEventRate } from '@admin-clients/cpanel-promoters-events-prices-data-access';
import { IsItalianComplianceEventPipe } from '@admin-clients/cpanel-promoters-events-utils';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { noDuplicateValuesValidatorForm } from '@admin-clients/shared/utility/utils';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { ChangeDetectionStrategy, Component, computed, effect, inject, signal, viewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormArray, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatButtonToggle } from '@angular/material/button-toggle';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTable, MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { filter, finalize, map, take, tap } from 'rxjs/operators';
import { TranslateRatesDialogComponent } from './translate-rates-dialog/translate-rates-dialog.component';

enum EventRateTableHeaders {
    rateActions = 'RATE_ACTIONS',
    rateName = 'RATE_NAME',
    rateType = 'RATE_TYPE',
    deleteRate = 'DELETE_RATE'
};

@Component({
    selector: 'app-event-rates-list',
    templateUrl: './event-rates-list.component.html',
    styleUrls: ['./event-rates-list.component.scss'],
    imports: [
        ReactiveFormsModule, MatFormField, MatLabel, MatIcon, FormControlErrorsComponent, TranslatePipe, MatButton, MatIconButton,
        MatButtonToggle, MatTooltip, MatInput, DragDropModule, MatSelect, MatOption, MatTableModule, IsItalianComplianceEventPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventRatesListComponent {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #eventsSrv = inject(EventsService);
    readonly #translate = inject(TranslateService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #matDialog = inject(MatDialog);
    readonly #isItalianCompliancePipe = inject(IsItalianComplianceEventPipe);

    private readonly _$eventRatesTable = viewChild<MatTable<VmEventRate>>('eventRatesTable');

    readonly ratesForm = this.#fb.group({
        rateNamesList: this.#fb.array([])
    });

    readonly $event = toSignal(
        this.#eventsSrv.event.get$().pipe(
            filter(Boolean),
            take(1),
            tap(event => this.#eventsSrv.eventRatesExternalTypes.load(event.id.toString()))
        )
    );

    readonly newRateNameCtrl = this.#fb.control('', [noDuplicateValuesValidatorForm(this.rateNamesList)]);
    readonly newRateTypeCtrl = this.#fb.control(null);
    readonly $vmEventRates = toSignal(this.#eventsSrv.eventRates.get$()
        .pipe(
            filter(Boolean),
            map(eventRates => {
                this.rateNamesList.clear({ emitEvent: false });
                return eventRates?.map(elem => {
                    const nameCtrl = this.#fb.control(elem.name, {
                        updateOn: 'blur',
                        validators: [Validators.required, noDuplicateValuesValidatorForm(this.rateNamesList)]
                    });
                    this.rateNamesList.push(nameCtrl, { emitEvent: false });
                    return {
                        ...elem,
                        nameCtrl
                    };
                }).sort((a, b) => a.position - b.position);
            })
        )
    );

    // Más adelante, se eliminará este flag específico de Italia y vendrá flageado de manera que el front
    // no necesite saber de qué provider externo se trata, sino que se base en la necesidad o no de un external_rate_type_id
    readonly $isRateTypeRequired = computed(() => this.#isItalianCompliancePipe.transform(this.$event()));
    readonly $eventRatesExternalTypes = toSignal(this.#eventsSrv.eventRatesExternalTypes.get$());
    readonly $isInProgrammingStatus = computed(() => this.$event()?.status === EventStatus.inProgramming);

    readonly $disableDeleteOrCreate = computed(() => this.$vmEventRates() && this.$isRateTypeRequired() && !this.$isInProgrammingStatus());
    readonly $hoveredRateId = signal<number | null>(null);

    get rateNamesList(): UntypedFormArray {
        return this.ratesForm.get('rateNamesList') as UntypedFormArray;
    }

    readonly rateTableHeaders = EventRateTableHeaders;

    readonly eventRateTableHeaders = computed(() =>
        this.$isRateTypeRequired()
            ? [
                this.rateTableHeaders.rateActions,
                this.rateTableHeaders.rateName,
                this.rateTableHeaders.rateType,
                this.rateTableHeaders.deleteRate
            ]
            : [
                this.rateTableHeaders.rateActions,
                this.rateTableHeaders.rateName,
                this.rateTableHeaders.deleteRate
            ]
    );

    constructor() {
        effect(() => {
            if (this.$disableDeleteOrCreate()) {
                this.newRateNameCtrl.disable();
                this.newRateTypeCtrl.disable();
            }
        });
    }

    switchHoveredRow(rateId: number | null): void {
        this.$hoveredRateId.set(rateId);
    }

    addNewRate(event: Event): void {
        event.preventDefault();
        if (!this.newRateNameCtrl.invalid && this.newRateNameCtrl.value) {
            const newRate: PostEventRate = {
                name: this.newRateNameCtrl.value,
                default: false,
                restrictive_access: false,
                external_rate_type_id: this.newRateTypeCtrl.value || null
            };
            this.#eventsSrv.eventRates.create(this.$event()?.id.toString(), newRate)
                .pipe(finalize(() => this.#eventsSrv.eventRates.load(this.$event()?.id.toString())))
                .subscribe(() => {
                    this.newRateNameCtrl.reset('', { emitEvent: false });
                    this.#showSuccess('EVENTS.FEEDBACK.NEW_RATE_SUCCESS', { rateName: newRate.name });
                });
        }
    }

    setDefaultRate(rate: VmEventRate): void {
        const modifRate: Partial<Rate> = {
            id: rate.id,
            default: true
        };
        this.#eventsSrv.eventRates.update(this.$event()?.id.toString(), [modifRate])
            .pipe(finalize(() => this.#eventsSrv.eventRates.load(this.$event()?.id.toString())))
            .subscribe(() => {
                this.#showSuccess('EVENTS.FEEDBACK.DEFAULT_RATE_SUCCESS', { rateName: rate.name });
            });
    }

    setRestrictiveAccess(rate: VmEventRate): void {
        const modifRate: Partial<Rate> = {
            id: rate.id,
            restrictive_access: !rate.restrictive_access
        };
        this.#eventsSrv.eventRates.update(this.$event()?.id.toString(), [modifRate])
            .pipe(finalize(() => this.#eventsSrv.eventRates.load(this.$event().id.toString())))
            .subscribe(() =>
                this.#showSuccess('EVENTS.FEEDBACK.RESTRICTIVE_ACCESS_RATE_SUCCESS', { rateName: rate.name })
            );
    }

    deleteRate(rate: VmEventRate): void {
        this.#msgDialogSrv
            .showWarn({
                size: DialogSize.SMALL,
                title: 'TITLES.WARNING',
                message: 'EVENTS.DELETE_RATE_WARNING',
                messageParams: { rateName: rate.name },
                actionLabel: 'FORMS.ACTIONS.DELETE',
                showCancelButton: true
            })
            .subscribe(success => {
                if (success) {
                    this.#eventsSrv.eventRates.delete(this.$event()?.id.toString(), rate.id)
                        .pipe(finalize(() => {
                            this.#eventsSrv.eventRates.load(this.$event()?.id.toString());
                            this.#eventsSrv.ratesRestrictions.load(this.$event()?.id);
                        }))
                        .subscribe(() => this.#showSuccess('EVENTS.FEEDBACK.DELETE_RATE_SUCCESS', { rateName: rate.name }));
                }
            });
    }

    saveName(rateInput: HTMLInputElement, rate: VmEventRate): void {
        const ctrl = rate.nameCtrl;
        if (ctrl.invalid) {
            const title = this.#translate.instant('TITLES.ERROR_DIALOG');
            const message = this.#translate.instant('FORMS.ERRORS.INVALID_FIELD');
            this.#msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message })
                .subscribe(() => rateInput.focus());
        } else if (ctrl.touched && ctrl.dirty) {
            this.#setNewName(rate, ctrl.value);
        }
    }

    openTranslateRatesDialog(): void {
        const data = {
            eventId: this.$event().id,
            languages: this.$event().settings.languages.selected,
            rates: this.$vmEventRates(),
            isSga: false,
            isProducts: false
        };
        this.#matDialog.open(TranslateRatesDialogComponent, new ObMatDialogConfig(data))
            .beforeClosed()
            .subscribe(isSaved => {
                if (isSaved) {
                    this.#eventsSrv.eventRates.load(this.$event()?.id.toString());
                }
            });
    }

    onListDrop(event: CdkDragDrop<VmEventRate[]>): void {
        const positionHasChanged = event.currentIndex !== event.previousIndex;
        if (positionHasChanged) {
            moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
            this._$eventRatesTable()?.renderRows();
            const orderedRates = event.container.data.map((rate, index) => {
                const { nameCtrl, ...rateData } = rate;
                void nameCtrl; // Acknowledge the variable exists but we don't use it
                return { ...rateData, position: index };
            });
            this.#eventsSrv.eventRates.update(this.$event()?.id.toString(), orderedRates)
                .pipe(finalize(() => this.#eventsSrv.eventRates.load(this.$event().id.toString())))
                .subscribe(() =>
                    this.#showSuccess('EVENTS.FEEDBACK.RATE_POSITION_SUCCESS')
                );
        }
    }

    #setNewName(rate: VmEventRate, currentName: string): void {
        const modifRate: Partial<Rate> = {
            id: rate.id,
            name: currentName
        };
        this.#eventsSrv.eventRates.update(this.$event().id.toString(), [modifRate])
            .pipe(finalize(() => {
                this.#eventsSrv.eventRates.load(this.$event().id.toString());
                this.#eventsSrv.ratesRestrictions.load(this.$event().id);
            }))
            .subscribe(() => this.#showSuccess('EVENTS.FEEDBACK.CHANGE_NAME_RATE_SUCCESS', { rateName: rate.name }));
    }

    #showSuccess(msgKey: string, msgParams?: { [key: string]: string }): void {
        this.#ephemeralMsgSrv.showSuccess({
            msgKey,
            msgParams
        });
    }
}

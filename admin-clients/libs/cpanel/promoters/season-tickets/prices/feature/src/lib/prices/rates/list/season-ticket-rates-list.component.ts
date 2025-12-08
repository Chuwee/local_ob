import {
    PostSeasonTicketRate, PutSeasonTicketRate, SeasonTicketsService, VmSeasonTicketRate
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { noDuplicateValuesValidatorForm } from '@admin-clients/shared/utility/utils';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormArray, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatButtonToggle } from '@angular/material/button-toggle';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatList, MatListItem } from '@angular/material/list';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { filter, finalize, map } from 'rxjs/operators';
import { SeasonTicketTranslateRatesDialogComponent } from './translate-rates-dialog/season-ticket-translate-rates-dialog.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, MatFormField, MatLabel, MatIcon, TranslatePipe, MatButton, MatIconButton, MatButtonToggle, MatList,
        MatListItem, MatTooltip, MatInput, DragDropModule, UpperCasePipe
    ],
    selector: 'app-season-ticket-rates-list',
    templateUrl: './season-ticket-rates-list.component.html',
    styleUrls: ['./season-ticket-rates-list.component.scss']
})
export class SeasonTicketRatesListComponent {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #seasonTicketsSrv = inject(SeasonTicketsService);
    readonly #translate = inject(TranslateService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #matDialog = inject(MatDialog);

    readonly ratesForm = this.#fb.group({
        rateNamesList: this.#fb.array([])
    });

    readonly #$seasonTicket = toSignal(this.#seasonTicketsSrv.seasonTicket.get$().pipe(filter(Boolean)));
    readonly newRateNameCtrl = this.#fb.control('', [noDuplicateValuesValidatorForm(this.rateNamesList)]);
    readonly $vmSeasonTicketRates = toSignal(this.#seasonTicketsSrv.getSeasonTicketRates$()
        .pipe(
            filter(Boolean),
            map(seasonTicketRates => {
                this.rateNamesList.clear({ emitEvent: false });
                return seasonTicketRates.map(elem => {
                    const nameCtrl = this.#fb.control(elem.name, {
                        updateOn: 'blur',
                        validators: [Validators.required, noDuplicateValuesValidatorForm(this.rateNamesList)]
                    });
                    this.rateNamesList.push(nameCtrl, { emitEvent: false });
                    return {
                        ...elem,
                        nameCtrl
                    };
                }).sort((a, b) => a.position - b.position);;
            })
        ));

    get rateNamesList(): UntypedFormArray {
        return this.ratesForm.get('rateNamesList') as UntypedFormArray;
    }

    addNewRate(event: Event): void {
        event.preventDefault();
        if (!this.newRateNameCtrl.invalid && this.newRateNameCtrl.value) {
            const newRate: PostSeasonTicketRate = {
                name: this.newRateNameCtrl.value,
                default: false,
                restrictive_access: false,
                enabled: false
            };

            this.#seasonTicketsSrv.createSeasonTicketRate(this.#$seasonTicket()?.id.toString(), newRate)
                .pipe(finalize(() => this.#seasonTicketsSrv.loadSeasonTicketRates(this.#$seasonTicket()?.id.toString()))
                ).subscribe(() => {
                    this.newRateNameCtrl.reset('', { emitEvent: false });
                    this.#showSuccess('SEASON_TICKET.FEEDBACK.NEW_RATE_SUCCESS', { rateName: newRate.name });
                });
        }
    }

    setDefaultRate(rate: VmSeasonTicketRate): void {
        const modifRate: PutSeasonTicketRate = {
            id: rate.id,
            default: true
        };
        this.#seasonTicketsSrv.saveSeasonTicketRates(this.#$seasonTicket()?.id.toString(), [modifRate])
            .subscribe({
                next: () => this.#showSuccess(
                    'SEASON_TICKET.FEEDBACK.DEFAULT_RATE_SUCCESS', { rateName: rate.name }),
                complete: () => this.#seasonTicketsSrv.loadSeasonTicketRates(this.#$seasonTicket()?.id.toString())
            });
    }

    setRestrictiveAccess(rate: VmSeasonTicketRate): void {
        const modifRate: PutSeasonTicketRate = {
            id: rate.id,
            restrictive_access: !rate.restrictive_access
        };
        this.#seasonTicketsSrv.saveSeasonTicketRates(this.#$seasonTicket()?.id.toString(), [modifRate])
            .pipe(finalize(() => this.#seasonTicketsSrv.loadSeasonTicketRates(this.#$seasonTicket()?.id.toString())))
            .subscribe(() => this.#showSuccess('SEASON_TICKET.FEEDBACK.RESTRICTIVE_ACCESS_RATE.'
                + (!rate.restrictive_access ? 'ENABLED' : 'DISABLED') + '_SUCCESS'));
    }

    setEnable(rate: VmSeasonTicketRate): void {
        const modifRate: PutSeasonTicketRate = {
            id: rate.id,
            enabled: !rate.enabled
        };
        this.#seasonTicketsSrv.saveSeasonTicketRates(this.#$seasonTicket()?.id.toString(), [modifRate])
            .subscribe({
                next: () => this.#showSuccess('SEASON_TICKET.FEEDBACK.VISIBLE_RATE.'
                    + (!rate.enabled ? 'ENABLED' : 'DISABLED') + '_SUCCESS'),
                complete: () => this.#seasonTicketsSrv.loadSeasonTicketRates(this.#$seasonTicket()?.id.toString())
            });
    }

    deleteRate(rate: VmSeasonTicketRate): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_SEASON_TICKET_RATE',
            message: 'SEASON_TICKET.DELETE_SEASON_TICKET_RATE_WARNING',
            messageParams: { rateName: rate.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this.#seasonTicketsSrv.deleteSeasonTicketRate(this.#$seasonTicket()?.id.toString(), rate.id)
                        .pipe(finalize(() => {
                            this.#seasonTicketsSrv.loadSeasonTicketRates(this.#$seasonTicket()?.id.toString());
                            this.#seasonTicketsSrv.ratesRestrictions.load(this.#$seasonTicket()?.id);
                        }))
                        .subscribe(() => this.#showSuccess('SEASON_TICKET.FEEDBACK.DELETE_RATE_SUCCESS', { rateName: rate.name }));
                }
            });
    }

    saveName(rateInput: HTMLInputElement, rate: VmSeasonTicketRate): void {
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
            eventId: this.#$seasonTicket()?.id.toString(),
            languages: this.#$seasonTicket()?.settings.languages.selected,
            rates: this.$vmSeasonTicketRates()
        };
        this.#matDialog.open(SeasonTicketTranslateRatesDialogComponent, new ObMatDialogConfig(data))
            .beforeClosed()
            .subscribe(isSaved => {
                if (isSaved) {
                    this.#seasonTicketsSrv.loadSeasonTicketRates(this.#$seasonTicket()?.id.toString());
                }
            });
    }

    onListDrop(event: CdkDragDrop<VmSeasonTicketRate[]>): void {
        const positionHasChanged = event.currentIndex !== event.previousIndex;
        if (positionHasChanged) {
            moveItemInArray(this.$vmSeasonTicketRates(), event.previousIndex, event.currentIndex);
            const orderedRates = event.container.data.map((rate, index) => {
                const { nameCtrl, ...rateData } = rate;
                return { ...rateData, position: index };
            });
            this.#seasonTicketsSrv.saveSeasonTicketRates(this.#$seasonTicket()?.id.toString(), orderedRates)
                .pipe(finalize(() => this.#seasonTicketsSrv.loadSeasonTicketRates(this.#$seasonTicket().id.toString())))
                .subscribe(() =>
                    this.#showSuccess('SEASON_TICKET.FEEDBACK.RATE_POSITION_SUCCESS')
                );
        }

    }

    #setNewName(rate: VmSeasonTicketRate, currentName: string): void {
        const modifRate: PutSeasonTicketRate = {
            id: rate.id,
            name: currentName
        };

        const seasonTicketId = this.#$seasonTicket().id.toString();
        this.#seasonTicketsSrv.saveSeasonTicketRates(seasonTicketId, [modifRate])
            .pipe(finalize(() => {
                this.#seasonTicketsSrv.loadSeasonTicketRates(seasonTicketId);
                this.#seasonTicketsSrv.ratesRestrictions.load(Number(seasonTicketId));
            }))
            .subscribe(() => this.#showSuccess('SEASON_TICKET.FEEDBACK.CHANGE_NAME_RATE_SUCCESS', { rateName: rate.name }));

    }

    #showSuccess(msgKey: string, msgParams?: { [key: string]: string }): void {
        this.#ephemeralMsgSrv.showSuccess({
            msgKey,
            msgParams
        });
    }
}

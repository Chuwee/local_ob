import { PutSeasonTicketRate, SeasonTicketRate, SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    DialogSize, EphemeralMessageService, TabDirective, TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';

import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { finalize } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule,
        TabsMenuComponent,
        TabDirective,
        ReactiveFormsModule,
        TranslatePipe,
        FlexLayoutModule
    ],
    selector: 'app-translate-rates-dialog',
    templateUrl: './season-ticket-translate-rates-dialog.component.html',
    styleUrls: ['./season-ticket-translate-rates-dialog.component.scss']
})
export class SeasonTicketTranslateRatesDialogComponent implements OnInit {
    private _seasonTicketId: string;

    languages: string[];
    rates: SeasonTicketRate[];
    displayedColumns = ['rates', 'translation'];

    form: UntypedFormGroup;

    constructor(
        private _dialogRef: MatDialogRef<SeasonTicketTranslateRatesDialogComponent>,
        private _seasonTicketService: SeasonTicketsService,
        private _ephemeralMessageService: EphemeralMessageService,
        private _fb: UntypedFormBuilder,
        @Inject(MAT_DIALOG_DATA) private _data: {
            eventId: string;
            languages: string[];
            rates: SeasonTicketRate[];
        }
    ) {
        this._dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this._seasonTicketId = this._data.eventId;
        this.rates = this._data.rates;
        this.languages = this._data.languages;
    }

    ngOnInit(): void {
        this.form = this._fb.group({});
        this.languages.forEach(lang => {
            // First create an array of controls for each language
            this.form.addControl(lang, this._fb.array([]));
            // Then add a control for each rate in current array
            this.rates.forEach(rate => {
                (this.form.get(lang) as UntypedFormArray).push(
                    this._fb.control(rate.texts?.name[lang] || '')
                );
            });
        });
    }

    close(isSaved = false): void {
        this._dialogRef.close(isSaved);
    }

    save(): void {
        const modifRates: PutSeasonTicketRate[] = [];
        this.rates.forEach((rate, i) => {
            const modifRate: PutSeasonTicketRate = {
                id: rate.id,
                texts: {
                    name: {}
                }
            };
            this.languages.forEach(lang => {
                const langControls = this.form.get(lang) as UntypedFormArray;
                const rateTranslation = langControls.at(i).value;
                modifRate.texts.name[lang] = rateTranslation || '';
            });
            modifRates.push(modifRate);
        });

        this._seasonTicketService.saveSeasonTicketRates(this._seasonTicketId, modifRates)
            .pipe(finalize(() => this.close(true)))
            .subscribe(() => {
                this._ephemeralMessageService.showSuccess({
                    msgKey: 'SEASON_TICKET.FEEDBACK.TRANSLATE_RATES_SUCCESS'
                });
            });
    }
}

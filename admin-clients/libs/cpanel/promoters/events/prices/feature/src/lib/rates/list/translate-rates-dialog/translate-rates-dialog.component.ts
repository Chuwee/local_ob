import { EventsService, PutRateGroup } from '@admin-clients/cpanel/promoters/events/data-access';
import { Rate } from '@admin-clients/cpanel/promoters/shared/data-access';
import { DialogSize, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

@Component({
    selector: 'app-translate-rates-dialog',
    templateUrl: './translate-rates-dialog.component.html',
    styleUrls: ['./translate-rates-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TranslateRatesDialogComponent implements OnInit {
    private _eventId: string;

    languages: string[];
    rates: Rate[];
    displayedColumns = ['rates', 'translation'];

    form: UntypedFormGroup;

    constructor(
        private _dialogRef: MatDialogRef<TranslateRatesDialogComponent>,
        private _eventsSrv: EventsService,
        private _ephemeralMessageService: EphemeralMessageService,
        private _fb: UntypedFormBuilder,
        @Inject(MAT_DIALOG_DATA) private _data: {
            eventId: string;
            isSga: boolean;
            isProducts: boolean;
            languages: string[];
            rates: Rate[];
        }
    ) {
        this._dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this._eventId = this._data.eventId;
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
        const modifRates: Partial<Rate>[] = [];
        this.rates.forEach((rate, i) => {
            const modifRate: Partial<Rate> = {
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

        let request: Observable<void>;
        if (this._data.isSga) {
            if (this._data.isProducts) {
                request = this._eventsSrv.sgaProducts.update(Number(this._eventId), modifRates as PutRateGroup[]);
            } else {
                request = this._eventsSrv.ratesGroup.updateMany(Number(this._eventId), modifRates as PutRateGroup[]);
            }
        } else {
            request = this._eventsSrv.eventRates.update(this._eventId, modifRates);
        }

        request.pipe(finalize(() => this.close(true)))
            .subscribe(() => {
                this._ephemeralMessageService.showSuccess({
                    msgKey: 'EVENTS.FEEDBACK.TRANSLATE_RATES_SUCCESS'
                });
            });
    }
}

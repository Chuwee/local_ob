import { PutSeasonTicket, SeasonTicket } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { LanguageSelector, LanguageSelectorComponent } from '@admin-clients/shared/common/ui/components';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, Input, ViewChild
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { first, map } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        LanguageSelectorComponent,
        TranslatePipe
    ],
    selector: 'app-season-ticket-principal-info-language',
    templateUrl: './season-ticket-principal-info-language.component.html'
})
export class SeasonTicketPrincipalInfoLanguageComponent implements AfterViewInit {
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _destroyRef = inject(DestroyRef);

    @ViewChild(LanguageSelectorComponent) private readonly _languageSelector: LanguageSelectorComponent;

    languageSelectorData: LanguageSelector;

    @Input() putSeasonTicketCtrl: FormControl<PutSeasonTicket>;
    @Input() form: FormGroup;
    @Input() set seasonTicket(value: SeasonTicket) {
        this._entitiesSrv.getEntity$()
            .pipe(
                first(entity => !!(entity?.settings?.languages?.available?.length)),
                map(entity => entity.settings?.languages.available)
            )
            .subscribe(availableLanguages => {
                this.languageSelectorData = {
                    default: value.settings.languages.default,
                    selected: value.settings.languages.selected,
                    languages: availableLanguages
                };
            });
    }

    ngAfterViewInit(): void {
        this.form.addControl('language', this._languageSelector.form, { emitEvent: false });

        this.putSeasonTicketCtrl.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(putST => {
                if (this.form.invalid) return;

                const { defaultLanguage, languagesGroup } = this._languageSelector.form.controls;
                if (languagesGroup.dirty || defaultLanguage.dirty) {
                    const languages = {
                        default: defaultLanguage.value,
                        selected: this._languageSelector.getSelectedLanguages()
                    };
                    putST.settings = putST.settings ?? { languages };
                    putST.settings.languages = putST.settings.languages ?? languages;
                    this.putSeasonTicketCtrl.setValue(putST, { emitEvent: false });
                }
            });
    }
}

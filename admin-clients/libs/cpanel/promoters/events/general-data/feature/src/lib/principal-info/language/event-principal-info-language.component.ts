import { Event, PutEvent } from '@admin-clients/cpanel/promoters/events/data-access';
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
    selector: 'app-event-principal-info-language',
    templateUrl: './event-principal-info-language.component.html'
})
export class EventPrincipalInfoLanguageComponent implements AfterViewInit {
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _destroyRef = inject(DestroyRef);

    @ViewChild(LanguageSelectorComponent) private readonly _languageSelector: LanguageSelectorComponent;

    languageSelectorData: LanguageSelector;

    @Input() putEventCtrl: FormControl<PutEvent>;
    @Input() form: FormGroup;
    @Input() set event(value: Event) {
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

        this.putEventCtrl.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(putEvent => {
                if (this.form.invalid) return;

                const { defaultLanguage, languagesGroup } = this._languageSelector.form.controls;
                if (languagesGroup.dirty || defaultLanguage.dirty) {
                    const languages = {
                        default: defaultLanguage.value,
                        selected: this._languageSelector.getSelectedLanguages()
                    };
                    putEvent.settings = putEvent.settings ?? { languages };
                    putEvent.settings.languages = putEvent.settings.languages ?? languages;
                    this.putEventCtrl.setValue(putEvent, { emitEvent: false });
                }
            });
    }
}

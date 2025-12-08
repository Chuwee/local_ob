import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject, takeUntil } from 'rxjs';
import { ChannelsPipesModule, PutChannel } from '@admin-clients/cpanel/channels/data-access';
import { LanguageSelector, LanguageSelectorComponent } from '@admin-clients/shared/common/ui/components';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-channel-language-selector',
    templateUrl: './channel-language-selector.component.html',
    imports: [
        LanguageSelectorComponent,
        TranslatePipe,
        ChannelsPipesModule
    ]
})
export class ChannelLanguageSelectorComponent implements AfterViewInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    @ViewChild(LanguageSelectorComponent) private readonly _languageSelector: LanguageSelectorComponent;

    @Input() languageSelectorData: LanguageSelector;
    @Input() enableDefaultSelection: boolean;
    @Input() putChannelCtrl: FormControl<PutChannel>;
    @Input() form: FormGroup;

    ngAfterViewInit(): void {
        this.form.setControl('language', this._languageSelector.form, { emitEvent: false });

        this.putChannelCtrl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(putChannel => {
                if (!this.form.valid) return;

                const { defaultLanguage, languagesGroup } = this._languageSelector.form.controls;

                if (languagesGroup.dirty || defaultLanguage?.dirty) {
                    putChannel.languages = putChannel.languages ?? { default: defaultLanguage.value };
                    putChannel.languages.selected = this._languageSelector.getSelectedLanguages();
                    this.putChannelCtrl.setValue(putChannel, { emitEvent: false });
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}

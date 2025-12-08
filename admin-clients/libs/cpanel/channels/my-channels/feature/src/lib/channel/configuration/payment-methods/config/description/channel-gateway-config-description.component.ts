import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelGatewayConfig, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { RichTextAreaComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    inject,
    Input,
    OnDestroy,
    OnInit,
    ViewChild
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, first, map, takeUntil } from 'rxjs/operators';
import { ChannelGatewayCtrlType } from '../channel-gateway-config-dialog.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        FormControlErrorsComponent,
        ReactiveFormsModule,
        TabsMenuComponent,
        TabDirective,
        RichTextAreaComponent
    ],
    selector: 'app-channel-gateway-config-description',
    templateUrl: './channel-gateway-config-description.component.html'
})
export class ChannelGatewayConfigDescriptionComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(FormBuilder);
    private readonly _channelsSrv = inject(ChannelsService);

    private readonly _onDestroy = new Subject<void>();

    @ViewChild('translationTabs') private readonly _translationTabs: TabsMenuComponent;

    readonly translationsGroup = this._fb.nonNullable.group({
        namesCtrl: this._fb.nonNullable.record<string>({}),
        subtitlesCtrl: this._fb.nonNullable.record<string>({})
    });

    readonly extraForm = this._fb.nonNullable.group({
        nameCtrl: ['', Validators.required],
        descriptionCtrl: '',
        translationsGroup: this.translationsGroup
    });

    readonly languages$ = this._channelsSrv.getChannel$()
        .pipe(
            filter(channel => !!channel.languages),
            map(channel => channel.languages.selected)
        );

    readonly defaultLanguageIndex$ = this._channelsSrv.getChannel$()
        .pipe(
            filter(channel => !!channel.languages),
            map(channel =>
                channel.languages.selected.findIndex(language => language === channel.languages.default)
            )
        );

    @Input() channelGatewayConfig: ChannelGatewayConfig;
    @Input() gatewayRequestCtrl: FormControl<ChannelGatewayCtrlType>;
    @Input() form: FormGroup;

    ngOnInit(): void {
        this.form.addControl('extra', this.extraForm, { emitEvent: false });
        this.initialConfig();
        this.mapToGatewayRequest();
    }

    ngOnDestroy(): void {
        this.form.removeControl('extra', { emitEvent: false });
    }

    private initialConfig(): void {
        const { request } = this.gatewayRequestCtrl.value;
        const {
            name,
            description,
            translations
        } = this.channelGatewayConfig ?? {};

        this.translationsGroup.disable({ emitEvent: false });

        this.initCtrl(this.extraForm.controls.nameCtrl, request.name, name);
        this.initCtrl(this.extraForm.controls.descriptionCtrl, request.description, description);

        this._channelsSrv.getChannel$()
            .pipe(first())
            .subscribe(channel => {
                if (!channel.languages) return;

                const newTranslationNamesRecord = this._fb.nonNullable.record<string>(
                    channel.languages.selected.reduce((acc, language) => {
                        acc[language] = ['', Validators.required];
                        return acc;
                    }, {}));
                this.translationsGroup.setControl('namesCtrl', newTranslationNamesRecord);

                const newTranslationSubtitlesRecord = this._fb.nonNullable.record<string>(
                    channel.languages.selected.reduce((acc, language) => {
                        acc[language] = '';
                        return acc;
                    }, {}));
                this.translationsGroup.setControl('subtitlesCtrl', newTranslationSubtitlesRecord);

                this.initCtrl(
                    this.translationsGroup.controls.namesCtrl,
                    request.translations?.name,
                    translations?.name
                );
                this.initCtrl(
                    this.translationsGroup.controls.subtitlesCtrl,
                    request.translations?.subtitle,
                    translations?.subtitle
                );
            });
    }

    private initCtrl(ctrl: AbstractControl, previousValue: unknown, serverValue: unknown): void {
        ctrl.reset(previousValue ?? serverValue, { emitEvent: false });
        if (previousValue) {
            ctrl.markAsDirty();
        }
    }

    private mapToGatewayRequest(): void {
        this.gatewayRequestCtrl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(({ request }) => {
                if (this.form.invalid) {
                    this._translationTabs.goToInvalidCtrlTab();
                    return;
                }

                const {
                    nameCtrl,
                    descriptionCtrl
                } = this.extraForm.controls;

                const {
                    namesCtrl,
                    subtitlesCtrl
                } = this.translationsGroup.controls;

                this.setRequestValue(nameCtrl, request, 'name');
                this.setRequestValue(descriptionCtrl, request, 'description');
                if (namesCtrl.dirty || subtitlesCtrl.dirty) {
                    request.translations = request.translations ?? {};
                    if (namesCtrl.dirty) {
                        request.translations.name = request.translations.name ?? {};
                        request.translations.name = namesCtrl.value;
                    }
                    if (subtitlesCtrl.dirty) {
                        request.translations.subtitle = request.translations.subtitle ?? {};
                        request.translations.subtitle = subtitlesCtrl.value;
                    }
                }

                this.gatewayRequestCtrl.setValue({ request }, { emitEvent: false });
            });
    }

    private setRequestValue<T, K extends keyof T>(ctrl: AbstractControl<T[K]>, request: T, key: K): void {
        if (ctrl.dirty) {
            request[key] = ctrl.value;
        }
    }
}

import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelsService, IsBoxOfficePipe, IsMembersChannelPipe, IsWebV4$Pipe, IsWebChannelPipe, type PutChannel, channelWebTypes
} from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles, isMultiCurrency$ } from '@admin-clients/cpanel/core/data-access';
import { CurrenciesService, EntitiesBaseService, LanguagesService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, type LanguageSelector, LanguageSelectorComponent } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, QueryList, ViewChildren, computed, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, skip, switchMap, throwError } from 'rxjs';
import { first, shareReplay } from 'rxjs/operators';
import {
    ChannelExchangeSelectorComponent
} from './channel-currency-selector/channel-exchange-selector/channel-exchange-selector.component';
import {
    ChannelLanguageSelectorComponent
} from './channel-currency-selector/channel-language-selector/channel-language-selector.component';
import {
    ChannelMultipleCurrencySelectorComponent
} from './channel-currency-selector/channel-multiple-currency-selector/channel-multiple-currency-selector.component';
import { ChannelSingleCurrencySelectorComponent } from './channel-single-currency-selector/channel-single-currency-selector.component';
import { FormDestinationChannelComponent } from './destination-channel/destination-channel.component';
import { FormChannelDataComponent } from './form-channel-data/form-channel-data.component';
import { FormContactDataComponent } from './form-contact-data/form-contact-data.component';

const DEFAULT_LANGUAGE_SELECTOR: LanguageSelector = { default: '', selected: [], languages: [] };

const V4_ALLOWED_LANGUAGES = [
    'es-ES', 'ca-ES', 'ca-ES-valencia', 'cz-CZ', 'de-DE', 'en-US', 'en-GB',
    'es-CR', 'eu-ES', 'fr-FR', 'gl-ES', 'it-IT', 'ko-KR', 'pt-PT', 'ru-RU', 'ar-QA',
    'es-MX'
];

@Component({
    selector: 'app-channel-general-data',
    templateUrl: './channel-general-data.component.html',
    styleUrls: ['./channel-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, MatCheckbox, MatAccordion, MatExpansionPanel, FormChannelDataComponent, MatProgressSpinner, AsyncPipe,
        MatExpansionPanelTitle, ChannelSingleCurrencySelectorComponent, ChannelMultipleCurrencySelectorComponent, MatExpansionPanelHeader,
        ChannelLanguageSelectorComponent, IsBoxOfficePipe, IsWebV4$Pipe, IsWebChannelPipe, IsMembersChannelPipe, FormContactDataComponent,
        ChannelExchangeSelectorComponent, ReactiveFormsModule, TranslatePipe, FormDestinationChannelComponent
    ]
})
export class ChannelGeneralDataComponent implements OnDestroy, WritingComponent {
    readonly #channelService = inject(ChannelsService);
    readonly #languagesService = inject(LanguagesService);
    readonly #entityService = inject(EntitiesBaseService);
    readonly #currenciesService = inject(CurrenciesService);
    readonly #auth = inject(AuthenticationService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    @ViewChildren(MatExpansionPanel) private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly form = this.#fb.nonNullable.group({
        isPublic: false,
        multiEvent: [{ value: false as boolean, disabled: true }],
        language: null as LanguageSelectorComponent['form']
    });

    readonly putChannelCtrl = this.#fb.nonNullable.control(null as PutChannel);

    readonly isInProgress$ = booleanOrMerge([
        this.#channelService.isChannelLoading$(),
        this.#channelService.isChannelSaving$(),
        this.#languagesService.isLanguagesInProgress$(),
        this.#entityService.isEntityLoading$(),
        this.#currenciesService.currencies.loading$()
    ]);

    readonly $channel = toSignal(this.#channelService.getChannel$());
    readonly $user = toSignal(this.#auth.getLoggedUser$());
    readonly $languages = toSignal(this.#languagesService.getLanguages$());
    readonly $entity = toSignal(this.#entityService.getEntity$());
    readonly $availableCurrencies = toSignal(this.#currenciesService.operatorCurrencies.get$());
    readonly $isSuperOperator = toSignal(this.#auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR]));
    readonly $isOperator = toSignal(this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS]));

    //TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly isMultiCurrency$ = isMultiCurrency$().pipe(shareReplay({ bufferSize: 1, refCount: true }));

    readonly $currencySelectorData = computed(() => {
        if (this.$availableCurrencies()) {
            return {
                available: this.$availableCurrencies(),
                selected: this.$channel()?.currencies
            };
        }
        return null;
    });

    readonly $languageSelectorData = computed(() => {
        if (this.$languages() && this.$channel()) {
            return {
                default: this.$channel().languages.default,
                selected: this.$channel().languages.selected,
                languages: this.$languages().map(lang => lang.code)
                    .filter(code => this.$channel().settings.v4_enabled ? V4_ALLOWED_LANGUAGES.includes(code) : true)
            };
        }
        return DEFAULT_LANGUAGE_SELECTOR;
    });

    constructor() {
        effect(() => {
            const user = this.$user();
            const channel = this.$channel();

            if (user && channel) {
                this.form.controls.isPublic.setValue(channel.public, { emitEvent: false });
                this.form.controls.multiEvent.setValue(channel.settings.use_multi_event, { emitEvent: false });
                if (
                    user.entity.settings.types.some(type => type === 'OPERATOR' || type === 'SUPER_OPERATOR') &&
                    channelWebTypes.includes(channel.type)
                ) {
                    this.#entityService.loadEntity(channel.entity.id);
                }
            }
        });

        effect(() => {
            const entity = this.$entity();
            const user = this.$user();

            if (this.$isSuperOperator()) {
                if (entity?.operator?.id) {
                    this.#currenciesService.operatorCurrencies.load(entity.operator.id);
                }
            } else if (user?.operator) {
                const currencies = user.operator?.currencies?.selected || [user.operator?.currency] || [];
                this.#currenciesService.operatorCurrencies.setValue(currencies);
            }
        });

        effect(() => {
            if ((this.$isSuperOperator() || this.$isOperator()) &&
                this.$entity()?.id === this.$channel()?.entity?.id &&
                this.$entity()?.settings?.enable_multievent_cart) {
                this.form.controls.multiEvent.enable();
            }
        });

        this.#languagesService.loadLanguages();
    }

    ngOnDestroy(): void {
        this.#languagesService.clearLanguages();
        this.#currenciesService.operatorCurrencies.clear();
        this.#entityService.clearEntity();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<unknown> {
        this.putChannelCtrl.setValue({});
        if (this.form.valid) {
            const { multiEvent, isPublic } = this.form.controls;
            if (multiEvent.dirty) {
                this.putChannelCtrl.value.settings = this.putChannelCtrl.value.settings ?? {};
                this.putChannelCtrl.value.settings.use_multi_event = multiEvent.value;
            }
            if (isPublic.dirty) {
                this.putChannelCtrl.value.public = isPublic.value;
            }

            return this.#channelService.saveChannel(this.$channel().id, this.putChannelCtrl.value)
                .pipe(
                    switchMap(() => {
                        this.#ephemeralMessageSrv.showSaveSuccess();
                        this.reset();
                        return this.#channelService.getChannel$()
                            .pipe(skip(1), first(Boolean));
                    }))
                ;
        } else {
            this.form.markAllAsTouched();
            //Calling setValue to force rerender in child components with form fields to show input errors.
            this.form.setValue(this.form.getRawValue());
            this.putChannelCtrl.reset(null, { emitEvent: false });
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    reset(): void {
        this.form.markAsPristine();
        this.form.markAsUntouched();
        this.putChannelCtrl.reset(null, { emitEvent: false });
        this.form.controls.isPublic.reset(null, { emitEvent: false });
        this.form.controls.multiEvent.reset(null, { emitEvent: false });
        this.#channelService.loadChannel(this.$channel().id.toString());
    }

}

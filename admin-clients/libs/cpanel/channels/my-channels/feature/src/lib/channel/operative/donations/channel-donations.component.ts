/* eslint-disable @typescript-eslint/naming-convention */
import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService, DonationType } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Chip, ChipsComponent, CollectionInputComponent, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPipe, LocalCurrencySymbolPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, Subject, combineLatest, throwError } from 'rxjs';
import { distinct, filter, first, map, shareReplay, startWith, takeUntil, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-channel-donations',
    templateUrl: './channel-donations.component.html',
    styleUrls: ['./channel-donations.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, ChipsComponent, CollectionInputComponent,
        MaterialModule, CommonModule, FlexLayoutModule, FormsModule, LocalCurrencySymbolPipe,
        FormContainerComponent, ReactiveFormsModule, FormControlErrorsComponent
    ]
})
export class ChannelDonationsComponent implements OnInit, OnDestroy, AfterViewInit, WritingComponent {
    private readonly _onDestroy = new Subject<void>();
    private readonly _fb = inject(FormBuilder);
    private readonly _entitiessService = inject(EntitiesService);
    private readonly _authSrv = inject(AuthenticationService);
    private readonly _channelsService = inject(ChannelsService);
    private readonly _ephemeralMessageService = inject(EphemeralMessageService);
    private readonly _currencyPipe = new LocalCurrencyPipe();
    private _channelId: number;
    private _channelName = '';
    private _currentAmounts = new BehaviorSubject<number[]>([]);
    @ViewChild(CollectionInputComponent) private _collectionInputComponent: CollectionInputComponent;
    readonly currentAmounts$ = this._currentAmounts.asObservable();
    readonly providerFormGroup = this._fb.group({
        id: [{ value: null as number, disabled: true }, Validators.required],
        target_id: [{ value: null as string, disabled: true }, Validators.required]
    });

    readonly settingsFormGroup = this._fb.group({
        type: [{ value: 'ROUND_UP' as DonationType, disabled: false }, Validators.required],
        options: [{ value: [] as number[], disabled: true }, [Validators.required, Validators.maxLength(3)]]
    });

    readonly form = this._fb.group({
        enabled: false, provider: this.providerFormGroup, settings: this.settingsFormGroup
    });

    readonly selectedProvider$ = this.form.controls.provider.controls.id.valueChanges
        .pipe(
            filter(Boolean),
            takeUntil(this._onDestroy)
        );

    readonly showAmounts$ = this.settingsFormGroup.controls.type.valueChanges
        .pipe(
            startWith(this.settingsFormGroup.controls.type.value),
            map(type => type === 'CUSTOM')
        );

    readonly campaigns$ = combineLatest([
        this._entitiessService.donationsCampaigns.get$(),
        this._channelsService.getChannel$(),
        this._authSrv.getLoggedUser$()
            .pipe(
                first(user => user !== null),
                map(user => user.currency),
                startWith(null),
                shareReplay(1))

    ]).pipe(
        map(([campaigns, channel, userCurrency]) =>
            campaigns?.filter(campaign =>
                channel?.currencies?.some(currency => currency.code === campaign.currency_code) ??
                (campaign.currency_code === userCurrency || !campaign.currency_code))),
        filter(Boolean),
        shareReplay(1));

    readonly campaignValueChanges$ = this.providerFormGroup.controls.target_id.valueChanges
        .pipe(
            startWith(this.providerFormGroup.controls.target_id.value));

    readonly selectedCampaign$ = combineLatest([
        this.campaignValueChanges$,
        this.campaigns$])
        .pipe(
            filter(values => values.every(Boolean)),
            map(([id, campaigns]) => campaigns?.filter(c => c.id === id)?.at(0)),
            shareReplay(1));

    readonly currency$ = combineLatest([
        this._authSrv.getLoggedUser$()
            .pipe(
                first(user => user !== null),
                map(user => user.currency),
                startWith(null),
                shareReplay(1)),
        this.selectedCampaign$
            .pipe(
                startWith(null),
                map(campaign => campaign?.currency_code))])
        .pipe(map(([userCurrency, campaignCurrency]) => campaignCurrency ?? userCurrency), filter(Boolean), shareReplay(1));

    readonly currentAmountsChips$ = this.currentAmounts$.pipe(
        filter(Boolean),
        withLatestFrom(this.currency$),
        map(([amounts, currency]) =>
            amounts.map((amount, index) => ({ label: this._currencyPipe.transform(amount, currency, 'wide'), value: index })))
    );

    readonly inProgress$ = booleanOrMerge([
        this._channelsService.isChannelLoading$(),
        this._channelsService.isChannelSaving$(),
        this._entitiessService.isEntityLoading$(),
        this._entitiessService.donationsCampaigns.inProgress$(),
        this._entitiessService.donationsProviders.inProgress$()
    ]);

    readonly donationsProviders$ = this._entitiessService.donationsProviders.get$();

    ngOnInit(): void {
        combineLatest([
            this.selectedProvider$.pipe(distinct()),
            this._channelsService.getChannel$().pipe(distinct(), shareReplay(1))])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([provider, channel]) => {
                if (provider && channel?.entity?.id) {
                    this._entitiessService.donationsCampaigns.load(channel?.entity?.id, provider);
                }
            });

        this.form.controls.enabled.valueChanges
            .pipe(
                startWith(this.form.controls.enabled.value),
                takeUntil(this._onDestroy))
            .subscribe(enabled => {
                if (enabled) {
                    this.form.controls.provider.enable();
                    this.form.controls.settings.enable();
                } else {
                    this.form.controls.provider.disable({ emitEvent: false });
                    this.form.controls.settings.disable({ emitEvent: false });
                }
            }

            );

        this.showAmounts$.pipe(takeUntil(this._onDestroy)).subscribe(enabled => {
            if (enabled) {
                this.settingsFormGroup.controls.options.enable();
                this.settingsFormGroup.controls.options.markAsUntouched();
                this._collectionInputComponent?.createElement.markAsUntouched();
                this._collectionInputComponent?.createElement.enable({ emitEvent: false });
            } else {
                this.settingsFormGroup.controls.options.disable();
                this._collectionInputComponent?.createElement.markAsUntouched();
                this._collectionInputComponent?.createElement.disable({ emitEvent: false });
            }
        });

        this.settingsFormGroup.controls.options.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((amounts: number[]) =>
                this._currentAmounts.next(amounts));

        this.campaignValueChanges$.pipe(takeUntil(this._onDestroy)).subscribe(enabled => {
            if (enabled) {
                this.settingsFormGroup.controls.type.enable();
            } else {
                this.settingsFormGroup.controls.type.disable();
            }
        });

        this.donationsProviders$.pipe(takeUntil(this._onDestroy)).subscribe(providers => {
            if (providers?.length === 1) {
                this.form.controls.provider.controls.id.patchValue(providers[0].id);
            }
        });
    }

    ngAfterViewInit(): void {
        this._channelsService.getChannel$()
            .pipe(
                takeUntil(this._onDestroy),
                filter(Boolean)
            ).subscribe(channel => {
                this._channelName = channel.name;
                this._channelId = channel.id;
                if (channel.entity?.id) {
                    this._entitiessService.donationsProviders.load(channel.entity.id);
                }
                this.form.patchValue({ ...channel.settings?.donations });
                this.form.markAsPristine();
            });

    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    save$(): Observable<number> {
        if (this.form.valid && this.form.dirty) {
            return this._channelsService.saveChannel(this._channelId,
                {
                    settings: { donations: this.form.value }
                })
                .pipe(
                    map(() => {
                        this._ephemeralMessageService.showSuccess({
                            msgKey: 'CHANNELS.UPDATE_SUCCESS',
                            msgParams: { channelName: this._channelName }
                        });
                        return this._channelId;
                    }),
                    tap(channelId => this._channelsService.loadChannel(channelId.toString()))
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    cancel(): void {
        this._channelsService.loadChannel(this._channelId.toString());
    }

    removeChip(chip: Chip): void {
        if (this.settingsFormGroup.controls.options.enabled) {
            const index = chip.value as number;
            const currentAmounts = this.settingsFormGroup.controls.options.value;

            currentAmounts.splice(index, 1);

            this.settingsFormGroup.controls.options.patchValue(currentAmounts);
            this.settingsFormGroup.controls.options.markAsDirty();
            this._collectionInputComponent.createElement.markAsTouched();
            this._currentAmounts.next(currentAmounts);
        }
    }
}

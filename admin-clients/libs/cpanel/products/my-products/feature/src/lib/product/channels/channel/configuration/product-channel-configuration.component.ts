/* eslint-disable @typescript-eslint/dot-notation */
import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { ProductChannel, ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { atLeastOneRequiredInFormGroup, booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, filter, first, map, Observable, switchMap, tap, throwError } from 'rxjs';
import { getSaleStatusIndicator } from '../../product-channel-status-mapping-function';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';

@Component({
    selector: 'app-product-channel-configuration',
    imports: [
        TranslatePipe, ReactiveFormsModule, AsyncPipe, NgClass,
        MatCheckboxModule, MatFormFieldModule, MatFormFieldModule, MatProgressSpinner, MatIcon, MatButton, MatTooltip,
        FormControlErrorsComponent, FormContainerComponent, MatRadioButton, MatRadioGroup
    ],
    templateUrl: './product-channel-configuration.component.html',
    styleUrls: ['./product-channel-configuration.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductChannelConfigurationComponent implements WritingComponent {
    readonly #fb = inject(FormBuilder);
    readonly #productsSrv = inject(ProductsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #translate = inject(TranslateService);

    readonly #$productChannel = toSignal(this.#productsSrv.product.channel.get$().pipe(filter(Boolean)));

    readonly form = this.#fb.group({
        checkout_suggestion_enabled: null as boolean,
        standalone_enabled: null as boolean,
        pack_only_enabled: false,
    }, { validators: [atLeastOneRequiredInFormGroup('required')] }
    );

    readonly productChannel$ = this.#productsSrv.product.channel.get$().pipe(
        filter(Boolean),
        tap(channel => {
            this.form.setValue({
                checkout_suggestion_enabled: channel.checkout_suggestion_enabled,
                standalone_enabled: channel.standalone_enabled,
                pack_only_enabled: !(channel.standalone_enabled || channel.checkout_suggestion_enabled),
            });
            this.form.markAsPristine();
        })
    );

    readonly isNotBoxOfficeChannel$ = this.#productsSrv.product.channel.get$()
        .pipe(map(channel => !channel || channel.channel.type !== ChannelType.boxOffice));

    readonly isLoading$ = booleanOrMerge([
        this.#productsSrv.product.channel.inProgress$(),
        this.#productsSrv.product.productChannelRelation.isInProgress$()
    ]);

    readonly $productStatus = toSignal(this.#productsSrv.product.get$().pipe(filter(Boolean), map(product => product.product_state)));

    /* In the future, if products continue to grow, we will need a status object (productChannel.status) as
    in event-channel and we will have to rethink literal keys */
    readonly $productChannelSaleRequestStatus = toSignal(this.#productsSrv.product.channel.get$()
        .pipe(filter(Boolean), map(productChannel => productChannel.sale_request_status)));

    readonly $saleStatusIndicator = computed(() => {
        const productStatus = this.$productStatus();
        const saleRequestStatus = this.$productChannelSaleRequestStatus();
        return getSaleStatusIndicator(saleRequestStatus, productStatus);
    });

    cancel(): void {
        this.#reloadProductChannel();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#reloadProductChannel();
            this.#ephemeralSrv.showSaveSuccess();
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return this.#productsSrv.product.channel.update(
                this.#$productChannel()?.product.id, this.#$productChannel()?.channel.id, {
                    checkout_suggestion_enabled: this.form.value.pack_only_enabled ? false : this.form.value.checkout_suggestion_enabled,
                    standalone_enabled: this.form.value.pack_only_enabled ? false : this.form.value.standalone_enabled }
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    requestChannel(productChannel: Partial<ProductChannel>): void {
        const productId = productChannel.product.id;
        const channelId = productChannel.channel.id;

        this.#messageDialogService.showWarn({
            size: DialogSize.SMALL,
            showCancelButton: true,
            title: this.#translate.instant('PRODUCT.CHANNELS.FORMS.INFOS.REQUEST_CHANNEL_WARN_TITLE'),
            message: this.#translate.instant('PRODUCT.CHANNELS.FORMS.INFOS.REQUEST_CHANNEL_WARN_MESSAGE',
                { channelName: productChannel.channel.name })
        }).pipe(
            first(Boolean),
            switchMap(() => this.#productsSrv.product.productChannelRelation.request(productId, channelId))
        ).subscribe(() => this.#reloadProductChannel(true));
    }

    #reloadProductChannel(updateStatusOnList = false): void {
        this.#productsSrv.product.channel.load(this.#$productChannel()?.product.id, this.#$productChannel()?.channel.id);
        if (updateStatusOnList) {
            combineLatest([
                this.#productsSrv.product.channel.get$(),
                this.#productsSrv.product.channel.inProgress$()
            ]).pipe(first(([productChannel, isInProgress]) => !!productChannel && !isInProgress))
                .subscribe(([productChannel, _]) =>
                    this.#productsSrv.product.channelsList.updateProductChannelStatusOnList(productChannel));
        }
    }
}

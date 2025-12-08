import { PackItem, PacksService, PutPackItem } from '@admin-clients/cpanel/channels/packs/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { productsDeliveryPointsProviders } from '@admin-clients/cpanel/products/delivery-points/data-access';
import { productsProviders } from '@admin-clients/cpanel/products/my-products/data-access';
import { CurrencyInputComponent, EphemeralMessageService, HelpButtonComponent, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { tap, throwError } from 'rxjs';
import { first, map } from 'rxjs/operators';

@Component({
    selector: 'app-session-detail',
    templateUrl: './session-detail.component.html',
    styleUrls: ['./session-detail.component.scss'],
    imports: [
        FlexLayoutModule, AsyncPipe, TranslatePipe, MatProgressSpinnerModule, MatIconModule, MatButton, DateTimePipe,
        MatExpansionModule, MatButtonModule, MatDividerModule, MatFormFieldModule, MatSelectModule, MatRadioModule, ReactiveFormsModule,
        MatCheckboxModule, CurrencyInputComponent, LocalCurrencyPipe, HelpButtonComponent
    ],
    providers: [productsProviders, productsDeliveryPointsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SessionDetailComponent {
    readonly #packsSrv = inject(PacksService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #authSrv = inject(AuthenticationService);

    dateTimeFormats = DateTimeFormats;
    item!: Partial<PackItem>;

    readonly priceTypes$ = this.#venueTemplatesSrv.getVenueTemplatePriceTypes$();
    readonly $pack = toSignal(this.#packsSrv.pack.get$());
    readonly currency$ = this.#authSrv.getLoggedUser$()
        .pipe(
            first(Boolean),
            map(user => user.currency)
        );

    readonly form = this.#fb.group({
        price_type: [null as number, Validators.required],
        display_item_in_channels: null as boolean,
        informative_price_check: null as boolean,
        informative_price: null as number
    });

    @Input() channelId: number;
    @Input() packId: number;

    @Input() set session(value: Partial<PackItem>) {
        value.editing = false;
        this.form.disable();

        this.item = value;
        this.#initValues();
    }

    edit(): void {
        if (this.item.editing) {
            this.item.editing = !this.item.editing;
            this.form.disable();
        } else {
            if (this.$pack().has_sales) {
                this.form.controls.display_item_in_channels.enable();
                this.form.controls.informative_price_check.enable();
                this.form.controls.informative_price.enable();
                this.item.editing = !this.item.editing;
            } else {
                this.form.enable();
                this.item.editing = !this.item.editing;
                this.#loadVenueTemplatePriceTypes(this.item.session_data.venue_template.id);
            }
        }
    }

    cancel(): void {
        this.#initValues();
        this.item.editing = false;
        this.form.disable();
    }

    save(): void {
        if (this.form.valid) {
            this.item.editing = false;
            this.form.disable();

            if (this.item.session_data.price_type) {
                const {
                    price_type: priceType,
                    display_item_in_channels: displayItemInChannels,
                    informative_price_check: informativePriceCheck,
                    informative_price: informativePrice
                } = this.form.value;
                const data: PutPackItem = {
                    price_type_id: priceType,
                    display_item_in_channels: displayItemInChannels,
                    informative_price: informativePriceCheck ? informativePrice : 0
                };
                this.#packsSrv.packItems.update(this.channelId, this.packId, this.item.id, data)
                    .subscribe({
                        next: () => {
                            this.#ephemeralSrv.showSaveSuccess();
                            this.item.session_data.price_type.id = this.form.get('price_type').value;
                        },
                        error: () => this.form.get('price_type')?.setValue(this.item.session_data.price_type.id)
                    });
            }
        } else {
            throwError(() => new Error('Invalid form')).pipe(tap(() => this.cancel()));
        }
    }

    #loadVenueTemplatePriceTypes(venueTemplateId: number): void {
        this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(venueTemplateId);
    }

    #initValues(): void {
        this.form.patchValue({
            price_type: this.item.session_data.price_type?.id,
            display_item_in_channels: !!this.item.display_item_in_channels,
            informative_price_check: this.item.informative_price ? true : false,
            informative_price: this.item.informative_price || 0
        });
    }
}

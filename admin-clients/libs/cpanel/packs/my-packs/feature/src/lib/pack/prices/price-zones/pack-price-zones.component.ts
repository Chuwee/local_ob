import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { PackPriceTypesScope, PacksService, PutPackPriceTypes } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import {
    VenueTemplatesService, venueTemplatesProviders
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, throwError } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';

@Component({
    selector: 'app-pack-price-zones',
    templateUrl: './pack-price-zones.component.html',
    styleUrls: ['./pack-price-zones.component.scss'],
    imports: [
        AsyncPipe, TranslatePipe, ReactiveFormsModule, MatRadioModule, MatTooltipModule, MatListModule,
        FormContainerComponent, MatProgressSpinner
    ],
    providers: [
        venueTemplatesProviders
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackPriceZonesComponent implements OnInit {
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #packsSrv = inject(PacksService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);

    readonly packPriceTypesScope = PackPriceTypesScope;
    readonly priceTypes$ = this.#venueTemplatesSrv.getVenueTemplatePriceTypes$();
    readonly loading$ = booleanOrMerge([
        this.#venueTemplatesSrv.isVenueTemplatePriceTypesLoading$(),
        this.#packsSrv.priceTypes.loading$()
    ]);

    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(
        filter(Boolean)));

    mainItemId = null;

    readonly $venueTpl = toSignal(this.#packsSrv.packItems.get$().pipe(
        filter(Boolean),
        map(packItems => {
            const mainItem = packItems.find(item => item.main);
            this.mainItemId = mainItem.id;
            this.#packsSrv.priceTypes.load(this.$pack().id, this.mainItemId);
            return mainItem.type === 'SESSION' ? mainItem.session_data.venue_template : mainItem.event_data.venue_template;
        }),
        tap(venueTpl => {
            this.#venueTemplatesSrv.clearVenueTemplatePriceTypes();
            if (venueTpl) {
                this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(venueTpl.id);
            }
        })
    ));

    readonly form = this.#fb.group({
        selection_type: [null as PackPriceTypesScope, Validators.required],
        price_type_ids: [{ value: null as number[], disabled: true }, Validators.required]
    });

    ngOnInit(): void {
        this.form.controls.selection_type.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((priceTypesScope: PackPriceTypesScope) =>
                priceTypesScope === PackPriceTypesScope.restricted ?
                    this.form.controls.price_type_ids.enable() : this.form.controls.price_type_ids.disable()
            );

        combineLatest([
            this.#packsSrv.priceTypes.get$().pipe(filter(Boolean)),
            this.form.valueChanges // only used as a trigger
        ]).pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(([packPriceTypes]) => {
            FormControlHandler.checkAndRefreshDirtyState(
                this.form.controls.selection_type,
                packPriceTypes.selection_type
            );
            FormControlHandler.checkAndRefreshDirtyState(
                this.form.controls.price_type_ids,
                packPriceTypes.price_types?.map(({ id }) => id)
            );
        });

        combineLatest([
            this.#packsSrv.priceTypes.get$(),
            this.priceTypes$
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([packPriceTypes, priceTypes]) => {
            this.form.patchValue({
                selection_type: packPriceTypes.selection_type,
                price_type_ids: packPriceTypes.price_types?.map(({ id }) => id) || priceTypes?.map(({ id }) => id)
            });
            this.form.markAsPristine();
            this.form.markAsUntouched();
        });
    }

    cancel(): void {
        this.load();
    }

    save$(): Observable<void | void[]> {
        if (this.form.valid && this.form.dirty) {
            const req: PutPackPriceTypes = {
                selection_type: this.form.value.selection_type,
                price_type_ids: this.form.value.selection_type === PackPriceTypesScope.restricted ? this.form.value.price_type_ids : []
            };
            return this.#packsSrv.priceTypes.update(this.$pack().id, this.mainItemId, req);
        } else {
            this.form.markAllAsTouched();
            //Refresh error state in currency-input child component
            this.form.setValue(this.form.getRawValue(), { emitEvent: false });
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => new Error('Invalid form'));
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#ephemeralMessageSrv.showSaveSuccess();
            this.load();
        });
    }

    private load(): void {
        this.#packsSrv.pack.load(this.$pack().id);
        this.#packsSrv.packItems.load(this.$pack().id);
        this.form.markAsPristine();
    }
}

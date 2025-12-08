import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, filter, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-product-design',
    imports: [
        AsyncPipe, FormContainerComponent, TranslatePipe, MatProgressSpinnerModule, MatFormFieldModule, ReactiveFormsModule,
        MatCheckboxModule, MatIconModule
    ],
    styleUrl: './product-design.component.scss',
    templateUrl: './product-design.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductDesignComponent implements OnInit {
    readonly #productsSrv = inject(ProductsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    readonly inProgress$ = this.#productsSrv.product.inProgress$();
    readonly form = this.#fb.group({
        hide_delivery_date_time: false as boolean,
        hide_delivery_point: false as boolean
    });

    readonly $product = toSignal(this.#productsSrv.product.get$().pipe(
        filter(Boolean),
        tap(product => {
            this.form.controls.hide_delivery_date_time.patchValue(product.ui_settings?.hide_delivery_date_time);
            this.form.controls.hide_delivery_point.patchValue(product.ui_settings?.hide_delivery_point);
        })
    ));

    ngOnInit(): void {
        this.reloadModels();
    }

    cancel(): void {
        this.reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const updatedProduct = { ui_settings: this.form.value };
            return this.#productsSrv.product.update(this.$product().product_id, updatedProduct)
                .pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
            return throwError(() => 'invalid form');
        }
    }

    private reloadModels(): void {
        this.#productsSrv.product.load(this.$product().product_id);
        this.form.markAsPristine();
    }
}

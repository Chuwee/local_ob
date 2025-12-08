/* eslint-disable @typescript-eslint/dot-notation */
import {
    CustomersService, CustomerContentImageField, PutCustomerContentImage, customerImageRestrictions, Customer
} from '@admin-clients/cpanel-viewers-customers-data-access';
import { ImageUploaderComponent } from '@admin-clients/shared/common/ui/components';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { UpperCasePipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, effect, input } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, combineLatest, filter, tap } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [TranslatePipe, ImageUploaderComponent, ReactiveFormsModule, UpperCasePipe],
    selector: 'app-customer-general-data-sidebar',
    styleUrls: ['./customer-general-data-sidebar.component.scss'],
    templateUrl: './customer-general-data-sidebar.component.html'
})
export class CustomerGeneralDataSidebarComponent implements AfterViewInit {
    readonly #fb = inject(FormBuilder);
    readonly #customerSrv = inject(CustomersService);
    readonly #onDestroy = inject(DestroyRef);

    readonly #imageField = {
        formField: 'customerImage',
        type: 'AVATAR',
        maxSize: customerImageRestrictions['profilePicture'].size
    } as CustomerContentImageField;

    readonly #image$ = this.#customerSrv.customer.image.get$()
        .pipe(
            filter(Boolean),
            tap(customerImage => {
                if (customerImage.type === this.#imageField.type) {
                    this.customerSidebarContentForm.patchValue({
                        [this.#imageField.formField]: customerImage.image_url
                    });
                }
            }),
            takeUntilDestroyed(this.#onDestroy)
        );

    readonly imageRestrictions = customerImageRestrictions;
    readonly customerSidebarContentForm = this.#fb.group({
        [this.#imageField.formField]: null
    });

    readonly $customer = input.required<Customer>({ alias: 'customer' });
    readonly $form = input.required<FormGroup>({ alias: 'form' });

    constructor() {
        this.#customerSrv.customer.image.clear();
        effect(() => {
            this.$form().addControl('customerSidebarContent', this.customerSidebarContentForm);
            this.#customerSrv.customer.image.load(this.$customer().id, this.$customer().entity.id);
        });
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    cancel(): void {
        this.customerSidebarContentForm.markAsPristine();
        this.#customerSrv.customer.image.load(this.$customer().id, this.$customer().entity.id);
    }

    save(getImageField: (contentForm: FormGroup, imageField: CustomerContentImageField) => PutCustomerContentImage):
        Observable<void>[] {
        const obsToSave$: Observable<void>[] = [];
        const image = getImageField(this.customerSidebarContentForm, this.#imageField);

        if (Object.keys(image).length === 0) {
            obsToSave$.push(
                this.#customerSrv.customer.image.delete(this.$customer().id, this.$customer().entity.id)
            );
        } else {
            obsToSave$.push(
                this.#customerSrv.customer.image.update(
                    this.$customer().id, image, this.$customer().entity.id).pipe(tap(() => this.cancel()))
            );
        }
        return obsToSave$;
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.#image$,
            this.$form().valueChanges
        ]).pipe(
            filter(data => data.every(Boolean)),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(image => {
            const field = this.customerSidebarContentForm.controls[this.#imageField.formField];
            const originalValue = image.filter(img => img.type === this.#imageField.type)[0]?.image_url || null;
            FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
        });
    }
}

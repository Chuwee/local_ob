import { Metadata } from '@OneboxTM/utils-state';
import {
    DialogSize, ObDialog, pageSize, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { IdString } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDialogActions, MatDialogContent, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { first, map, shareReplay } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatButton, MatDialogActions, MatDialogContent, MatDialogTitle, MatIcon, MatIconButton,
        TranslatePipe, SearchablePaginatedSelectionModule, AsyncPipe, MatTooltip, MatCheckbox, MatTableModule, EllipsifyDirective
    ],
    selector: 'app-sale-request-payment-methods-benefits-bins-more',
    templateUrl: './sale-request-payment-methods-benefits-bins-more.component.html',
    styleUrl: './sale-request-payment-methods-benefits-bins-more.component.scss'
})
export class SaleRequestPaymentMethodsBenefitsBinsMoreComponent extends ObDialog<SaleRequestPaymentMethodsBenefitsBinsMoreComponent,
    { bins: IdString[]; isDisabled: boolean }, IdString[]> implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #nextPaged = new BehaviorSubject<IdString[]>(null);
    readonly #nextMetadataPaged = new BehaviorSubject<Metadata>(null);

    readonly columns = ['active', 'name'];
    readonly allSelectedClick = new EventEmitter<boolean>();
    readonly nextPaged$ = this.#nextPaged.asObservable();
    readonly nextMetadata$ = this.#nextMetadataPaged.asObservable();
    readonly total$ = this.#nextMetadataPaged
        .pipe(first(Boolean), map(metadata => metadata?.total || 0));

    readonly allSelected$ = this.allSelectedClick.pipe(
        takeUntilDestroyed(),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly selectedCtrl = this.#fb.control([] as IdString[]);

    selectedOnly = false;

    get selected(): number {
        return this.selectedCtrl?.value?.length || 0;
    }

    constructor() {
        super(DialogSize.MEDIUM, true);
    }

    ngOnInit(): void {
        this.allSelected$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(allSelected => {
                this.selectedCtrl.setValue(allSelected ? [...this.data.bins] : []);
                this.selectedCtrl.markAsDirty();
            });
    }

    delete(): void {
        this.dialogRef.close(this.selectedCtrl.value);
    }

    load({ offset, q }: SearchablePaginatedSelectionLoadEvent): void {
        let bins = [...this.data.bins];
        if (this.selectedOnly) {
            bins = bins.filter(bin => !!this.selectedCtrl.value?.find(selectedBin => selectedBin.id === bin.id));
        }
        if (q) {
            bins = bins.filter(bin => bin.id.toString().includes(q));
        }

        this.#nextPaged.next(bins.slice(offset, offset + pageSize));
        this.#nextMetadataPaged.next(new Metadata({ total: bins.length, offset, limit: pageSize }));
    }
}

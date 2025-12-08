import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { VenueTemplatePriceType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { Component, ChangeDetectionStrategy, Output, EventEmitter, ViewChild, inject, Input } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatSelect } from '@angular/material/select';
import { SatPopoverComponent, SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, map, of, startWith } from 'rxjs';
import { PriceTypesFilter } from '../models/price-types-filter.model';

@Component({
    selector: 'app-price-types-filter',
    templateUrl: './price-types-filter.component.html',
    styleUrls: ['./price-types-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, CommonModule, FlexLayoutModule, SelectSearchComponent, ReactiveFormsModule,
        SatPopoverModule, CommonModule, TranslatePipe
    ]
})
export class PriceTypesFilterComponent {

    private readonly _fb = inject(FormBuilder);

    @ViewChild('filterPopover') private _filterPopover: SatPopoverComponent;

    form = this._fb.group({
        price_types_origin: [],
        price_types_target: []
    });

    readonly activeFilters$ = this.form.valueChanges
        .pipe(
            map(value => Object.values(value).filter(value => value !== null && (!Array.isArray(value) || value.length > 0)).length),
            startWith(false)
        );

    @Input() priceTypes$: Observable<VenueTemplatePriceType[]>;
    @Output() filterChange = new EventEmitter<PriceTypesFilter>();

    apply(): void {
        this.filterChange.emit(this.form.value);
        this.close();
    }

    selectAll(select: MatSelect): void {
        this.allSelected(select)
            .subscribe(selected => {
                if (selected) {
                    select.options.forEach(opt => opt.value && opt.select());
                } else {
                    select.options.forEach(opt => opt.value && opt.deselect());
                }
            });
    }

    allSelected(select: MatSelect): Observable<boolean> {
        return of(select?.options?.filter(opt => !!opt.value).some(opt => !opt.selected));
    }

    clear(): void {
        this.form.reset();
        this.apply();
    }

    close(): void {
        this._filterPopover.close();
    }
}

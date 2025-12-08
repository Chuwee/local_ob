import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ElementsInfoFilterRequest, VenueTemplateElementInfoType } from '@admin-clients/shared/venues/data-access/venue-tpls';

import { ChangeDetectionStrategy, Component, inject, input, output, ViewChild } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { SatPopoverComponent, SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-elements-info-filter',
    templateUrl: './elements-info-filter.component.html',
    styleUrls: ['./elements-info-filter.component.scss'],
    imports: [MaterialModule, TranslatePipe, ReactiveFormsModule, SatPopoverModule, FlexModule],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ElementsInfoFilterComponent {
    readonly #fb = inject(FormBuilder);

    @ViewChild('filterPopover') private _filterPopover: SatPopoverComponent;
    #appliedFilters: ElementsInfoFilterRequest = {};

    readonly elementInfoTypes = Object.values(VenueTemplateElementInfoType);

    readonly form = this.#fb.group({
        type: [null as string, [Validators.required]],
        status: [null as string, [Validators.required]],
        hasContents: [false as boolean, [Validators.required]]
    });

    $status = input<boolean>(false, { alias: 'status' });
    $disabled = input<boolean>(false, { alias: 'disabled' });

    filterChange = output<ElementsInfoFilterRequest>();

    apply(): void {
        this.#appliedFilters = {
            type: this.form.value.type,
            status: this.form.value.status,
            hasContents: this.form.value.hasContents
        };
        this.filterChange.emit(this.#appliedFilters);
        this.close();
    }

    clear(): void {
        this.form.reset();
        this.apply();
    }

    close(): void {
        this._filterPopover.close();
    }

    open(): void {
        this.form.reset({
            ...this.#appliedFilters
        });
        this._filterPopover.open();
    }

    get activeFilters(): number {
        return Object.values(this.#appliedFilters).filter(Boolean)?.length || 0;
    }

}

import { ChangeDetectionStrategy, Component, EventEmitter, inject, Input, Output, ViewChild } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { SatPopoverComponent, SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-channel-faqs-filter',
    templateUrl: './channel-faqs-filter.component.html',
    styleUrls: ['./channel-faqs-filter.component.scss'],
    imports: [
        TranslatePipe, ReactiveFormsModule, SatPopoverModule, FlexModule, MatCardModule, MatIconModule,
        MatButtonModule, MatFormFieldModule, MatSelectModule, MatBadgeModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelFaqsFilterComponent {
    readonly #fb = inject(FormBuilder);

    @ViewChild('filterPopover') private _filterPopover: SatPopoverComponent;
    #appliedFilters: { tag?: string } = {};

    readonly form = this.#fb.group({
        tag: [null as string, [Validators.required]]
    });

    @Input() categories: string[];
    @Output() filterChange = new EventEmitter<{ tag?: string }>();

    apply(): void {
        this.#appliedFilters = {
            tag: this.form.value.tag
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

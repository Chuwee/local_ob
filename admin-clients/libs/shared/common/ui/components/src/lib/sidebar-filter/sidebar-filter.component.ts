import { AsyncPipe } from '@angular/common';
import { AfterContentInit, ChangeDetectionStrategy, Component, ContentChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButton } from '@angular/material/button';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { FilterWrapped } from '../list-filters/filter-wrapped.component';
import { FilterWrapperComponent } from '../list-filters/filter-wrapper.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe,
        FlexLayoutModule,
        MatProgressSpinner,
        MatButton,
        TranslatePipe
    ],
    selector: 'app-sidebar-filter',
    templateUrl: './sidebar-filter.component.html',
    styleUrls: ['./sidebar-filter.component.scss']
})
export class SidebarFilterComponent extends FilterWrapperComponent implements AfterContentInit {
    @ContentChild('filterContent')
    private _filterComponent: FilterWrapped;

    override isLoading$: Observable<boolean>;

    constructor() {
        super();
    }

    ngAfterContentInit(): void {
        if (this._filterComponent === undefined) {
            throw Error('SidebarFilterComponent @ContentChild with #filterContent selector should be provided');
        }
        super.setInnerFilterComponent(this._filterComponent);
        this.isLoading$ = this._filterComponent.isLoading$;
    }

    emmitFilter(): void {
        this._filterComponent.applyFilters();
    }
}

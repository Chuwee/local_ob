import { BiSupersetService } from '@admin-clients/cpanel/bi/data-access';
import { ListFilteredComponent, ListFiltersService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { isHandset$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs';
import { shareReplay } from 'rxjs/operators';
import { BiSupersetReportsListRecentsComponent } from './recent/bi-superset-reports-list-recents.component';
import { BiSupersetReportsListSearchInputComponent } from './search-input/bi-superset-reports-list-search-input.component';
import { BiSupersetReportsListSearchResultsComponent } from './search-results/bi-superset-reports-list-search-results.component';
import { BiSupersetReportsListTabsComponent } from './tabs/bi-superset-reports-list-tabs.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-bi-superset-reports-list',
    imports: [
        AsyncPipe, TranslatePipe, FormContainerComponent, BiSupersetReportsListTabsComponent,
        BiSupersetReportsListRecentsComponent, BiSupersetReportsListSearchResultsComponent,
        ReactiveFormsModule, BiSupersetReportsListSearchInputComponent, MatProgressSpinner
    ],
    providers: [ListFiltersService],
    templateUrl: './bi-superset-reports-list.component.html'
})
export class BiSupersetReportsListComponent extends ListFilteredComponent implements OnInit, OnDestroy {
    readonly #biService = inject(BiSupersetService);

    readonly isHandset$ = isHandset$().pipe(shareReplay({ bufferSize: 1, refCount: true }));

    readonly isLoading$ = this.#biService.reportsList.loading$();

    readonly recentReports$ = this.#biService.reportsHistoryList.get$().pipe(filter(Boolean));
    readonly reports$ = this.#biService.reportsList.get$().pipe(filter(Boolean));

    readonly searchResults$ = this.#biService.reportsSearch.get$().pipe(map(biReports => !!biReports));

    ngOnInit(): void {
        this.#biService.reportsList.load();
        this.#biService.reportsHistoryList.load();
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#biService.reportsHistoryList.clear();
        this.#biService.reportsList.clear();
        this.#biService.reportsSearch.clear();
    }

    loadData(): void {
        this.#biService.reportsList.load();
        this.#biService.reportsHistoryList.load();
        this.#biService.reportsSearch.clear();
    }
}

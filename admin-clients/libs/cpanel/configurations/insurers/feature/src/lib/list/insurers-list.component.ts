import { Insurer, InsurersService } from '@admin-clients/cpanel-configurations-insurers-data-access';
import { EmptyStateComponent, ListFiltersService, ObMatDialogConfig, PaginatorComponent } from '@admin-clients/shared/common/ui/components';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit, signal, viewChild } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import {
    MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef,
    MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable } from 'rxjs';
import { NewInsurerDialogComponent } from '../new-insurer-dialog/new-insurer-dialog.component';

@Component({
    selector: 'app-insurers-list',
    imports: [
        TranslatePipe, RouterModule, AsyncPipe, NgClass,
        EmptyStateComponent,
        MatDivider, MatProgressSpinner, MatTable, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
        MatHeaderCell, MatHeaderCellDef, MatCell, MatCellDef, MatColumnDef, MatIcon, MatTooltip, MatButton,
        MatDialogModule
    ],
    providers: [
        ListFiltersService
    ],
    templateUrl: './insurers-list.component.html',
    styleUrls: ['./insurers-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class InsurersListComponent /* extends ListFilteredComponent */ implements OnInit { // TO DO: Add filters when backend ready
    readonly #insurersService = inject(InsurersService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #matDialog = inject(MatDialog);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    readonly $paginatorComponent = viewChild(PaginatorComponent);
    readonly insurers$ = this.#insurersService.insurersList.getData$();
    readonly insurersMetadata$ = this.#insurersService.insurersList.getMetadata$();
    readonly isLoading$ = this.#insurersService.insurersList.loading$();

    readonly displayedColumns = [
        'name', 'tax_name', 'operator', 'contact_email', 'phone'
    ];

    readonly isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    $hasAppliedFilters = signal(false);

    trackByFn = (_, insurer: Insurer): number => insurer.id;

    ngOnInit(): void {
        this.#insurersService.insurersList.load();
    }

    openNewInsurerDialog(): void {
        this.#matDialog.open<NewInsurerDialogComponent, null, { id: number; password: string }>(
            NewInsurerDialogComponent, new ObMatDialogConfig()
        )
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(insurerId => {
                this.#router.navigate([insurerId, 'general-data'], { relativeTo: this.#route });
            });
        ;
    }

    /* ngAfterViewInit(): void { // TO DO: Add filters when backend ready
        this.initListFilteredComponent([
            this.$paginatorComponent()
        ]);
    } */

    /* loadData(filters: FilterItem[]): void { // TO DO: Add filters when backend ready
        this.hasAppliedFilters = false;
        this.#request = new GetInsurersReq();
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'PAGINATION':
                        this.#request.limit = values[0].value.limit;
                        this.#request.offset = values[0].value.offset;
                        break;
                }
            }
        });
        this.loadInsurers();
    } */
}

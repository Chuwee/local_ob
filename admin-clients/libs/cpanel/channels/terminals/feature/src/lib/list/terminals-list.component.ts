import { getListData, getMetadata } from '@OneboxTM/utils-state';
import { GetTerminalsRequest, Terminal, TerminalsService } from '@admin-clients/cpanel-channels-terminals-data-access';
import {
    PaginatorComponent, ListFiltersService, ListFilteredComponent, SearchInputComponent,
    FilterItem, MessageDialogService, DialogSize, EphemeralMessageService, openDialog, PopoverComponent, ContextNotificationComponent,
    PopoverFilterDirective, ChipsComponent, ChipsFilterDirective
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, inject, OnDestroy, ViewChild, ViewContainerRef
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, Observable, switchMap } from 'rxjs';
import { map } from 'rxjs/operators';
import { NewTerminalDialogComponent } from '../create/new-terminal-dialog.component';
import { TerminalsListFilterComponent } from './filter/terminals-list-filter.component';

@Component({
    imports: [
        CommonModule,
        FlexLayoutModule,
        TranslatePipe,
        MaterialModule,
        PopoverComponent,
        PaginatorComponent,
        ContextNotificationComponent,
        RouterLink,
        PopoverFilterDirective,
        TerminalsListFilterComponent,
        SearchInputComponent,
        ChipsComponent,
        ChipsFilterDirective
    ],
    selector: 'app-terminals-list',
    templateUrl: './terminals-list.component.html',
    styleUrls: ['./terminals-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TerminalsListComponent extends ListFilteredComponent implements AfterViewInit, OnDestroy {

    private readonly _terminalsSrv = inject(TerminalsService);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _ephemeralMessageSrv = inject(EphemeralMessageService);
    private readonly _breakpointObserver = inject(BreakpointObserver);
    private readonly _listFilterSrv = inject(ListFiltersService);
    private readonly _dialogSrv = inject(MatDialog);
    private readonly _viewRef = inject(ViewContainerRef);

    @ViewChild(PaginatorComponent) private readonly _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(TerminalsListFilterComponent) private readonly _filterComponent: TerminalsListFilterComponent;

    readonly pageSize = 20;
    readonly cols = { name: 'name', entity: 'entity', enabled: 'enabled', actions: 'actions' };
    readonly colNames = Object.values(this.cols);

    readonly terminals$ = this._terminalsSrv.terminals.get$().pipe(getListData());
    readonly metadata$ = this._terminalsSrv.terminals.get$().pipe(getMetadata());
    readonly loading$ = booleanOrMerge([
        this._terminalsSrv.terminals.inProgress$(),
        this._terminalsSrv.terminal.inProgress$()
    ]);

    readonly isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    trackByFn = (_: number, item: Terminal): number => item.id;

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this._terminalsSrv.terminals.clear();
    }

    ngAfterViewInit(): void {
        this.initListFilteredComponent([
            this._paginatorComponent,
            this._searchInputComponent,
            this._filterComponent
        ]);
    }

    loadData(filters: FilterItem[] = null): void {
        filters ??= this._listFilterSrv.getFilters();
        const request: GetTerminalsRequest = { limit: this.pageSize, offset: 0 };
        filters.forEach(filterItem => {
            const value = filterItem.values?.[0]?.value;
            switch (filterItem.key) {
                case 'PAGINATION':
                    request.offset = value?.offset;
                    break;
                case 'SEARCH_INPUT':
                    request.q = value;
                    break;
                case 'ENTITY':
                    request.entity_id = Number(value);
                    break;
                case 'ENABLED':
                    request.license_enabled = String(true) === value;
                    break;
                case 'TYPE': // secret filter, only by url param
                    request.type = value;
                    break;
            }
        });
        this._terminalsSrv.terminals.load({
            type: 'BOX_OFFICE',
            ...request
        });
    }

    createTerminal(): void {
        openDialog(this._dialogSrv, NewTerminalDialogComponent, null, this._viewRef)
            .beforeClosed()
            .subscribe(id => {
                if (id) {
                    this._ephemeralMessageSrv.showSuccess({ msgKey: 'TERMINALS.FORMS.FEEDBACK.CREATE_TERMINAL_SUCCESS' });
                    this.router.navigate([id], { relativeTo: this.activatedRoute });
                }
            });
    }

    deleteTerminal(terminal: Terminal): void {
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TERMINALS.FORMS.INFOS.DELETE_TERMINAL_WARNING',
            message: 'TERMINALS.FORMS.INFOS.DELETE_TERMINAL',
            actionLabel: 'FORMS.ACTIONS.DELETE'
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this._terminalsSrv.terminal.delete(terminal.id))
            )
            .subscribe(() => {
                this._ephemeralMessageSrv.showSuccess({ msgKey: 'TERMINALS.FORMS.FEEDBACK.DELETE_TERMINAL_SUCCESS' });
                this.loadData();
            });
    }
}

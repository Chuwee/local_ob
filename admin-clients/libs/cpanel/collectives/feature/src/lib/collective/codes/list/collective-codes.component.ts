import { Metadata } from '@OneboxTM/utils-state';
import { Collective, CollectiveCode, CollectivesService, CollectiveValidationMethod } from '@admin-clients/cpanel/collectives/data-access';
import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService,
    ObMatDialogConfig, ExportDialogComponent, SearchablePaginatedSelectionLoadEvent
} from '@admin-clients/shared/common/ui/components';
import { PageableFilter, DateTimeFormats, ExportFormat } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ChangeDetectionStrategy, Component, EventEmitter, OnDestroy, OnInit, inject } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, shareReplay, startWith, switchMap, take, takeUntil, tap, withLatestFrom } from 'rxjs/operators';
import { NewCollectiveCodeDialogComponent } from '../create/new-collective-code-dialog.component';
import { EditCollectiveCodeDialogComponent } from '../edit/edit-collective-code-dialog.component';
import { ImportCollectiveCodesDialogComponent } from '../import/import-dialog/import-collective-codes-dialog.component';
import {
    exportDataCollectiveCodes, exportDataCollectiveCodesUser, exportDataCollectiveCodesUserPass
} from './collective-codes-export-data';

const PAGE_SIZE = 20;

@Component({
    selector: 'app-collective-codes',
    templateUrl: './collective-codes.component.html',
    styleUrls: ['./collective-codes.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CollectiveCodesComponent implements OnInit, OnDestroy {
    private readonly _tableSrv = inject(TableColConfigService);

    private _onDestroy = new Subject<void>();
    private _filters: PageableFilter = { limit: PAGE_SIZE };
    private _collectiveId: number;

    readonly dateTimeFormats = DateTimeFormats;
    readonly pageSize = PAGE_SIZE;
    readonly showSelectedOnlyClick = new EventEmitter<boolean>();
    readonly allSelectedClick = new EventEmitter<boolean>();

    collective$: Observable<Collective>;
    collectiveCodesList$: Observable<CollectiveCode[]>;
    metadata$: Observable<Metadata>;

    selectedCodes$: Observable<CollectiveCode[]>;
    selectedOnly$: Observable<boolean>;
    allSelected$: Observable<boolean>;
    totalCodes$: Observable<number>;

    selected = new UntypedFormControl([]);
    columns: string[];
    csvTpl: { name: string; data: string };

    reqInProgress$: Observable<boolean>;
    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches)
        );

    constructor(
        private _collectiveSrv: CollectivesService,
        private _matDialog: MatDialog,
        private _msgDialogSrv: MessageDialogService,
        private _ephemeralSrv: EphemeralMessageService,
        private _breakpointObserver: BreakpointObserver
    ) { }

    ngOnInit(): void {
        this.collective$ = this._collectiveSrv.getCollective$()
            .pipe(
                filter(collective => !!collective),
                tap(collective => {
                    this._collectiveId = collective.id;
                    if (collective.validation_method === CollectiveValidationMethod.userPassword) {
                        this.columns = ['active', 'code', 'key', 'usage_limit', 'usage_current', 'validity_from', 'validity_to', 'actions'];
                    } else {
                        this.columns = ['active', 'code', 'usage_limit', 'usage_current', 'validity_from', 'validity_to', 'actions'];
                    }
                }),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );

        this.reqInProgress$ = booleanOrMerge([
            this._collectiveSrv.isCollectiveLoading$(),
            this._collectiveSrv.isCollectiveCodesLoading$(),
            this._collectiveSrv.isCollectiveCodeDeleting$(),
            this._collectiveSrv.isCollectiveCodeSaving$(),
            this._collectiveSrv.isCollectiveCodesSaving$(),
            this._collectiveSrv.isCollectiveCodeExporting$()
        ]);

        this.selectedOnly$ = this.showSelectedOnlyClick.pipe(
            startWith(false),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );

        this.allSelected$ = this.allSelectedClick.pipe(
            startWith(false),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );

        this.selectedCodes$ = this.selected.valueChanges
            .pipe(
                map(selected => {
                    if (!selected || selected.length === 0) {
                        this.showSelectedOnlyClick.next(false);
                        return [];
                    }
                    return selected?.sort((a, b) => a.code.localeCompare(b.code));
                }),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );

        this.selectedCodes$.subscribe();

        // all selectable sessions
        const allCodes$ = this._collectiveSrv.getCollectiveCodesData$()
            .pipe(
                filter(collectiveCodes => !!collectiveCodes),
                tap(collectiveCodes => {
                    // Update selected values
                    this.selected.value?.forEach((selectedCollective, index) => {
                        const updatedCollective = collectiveCodes.find(collectiveCode => selectedCollective.code === collectiveCode.code);
                        if (updatedCollective) {
                            this.selected.value[index] = updatedCollective;
                        }
                    });
                }),
                shareReplay(1)
            );

        this.collectiveCodesList$ = this.selectedOnly$.pipe(
            switchMap(isActive => isActive ? this.selectedCodes$ : allCodes$),
            shareReplay(1)
        );

        this.totalCodes$ = this._collectiveSrv.getCollectiveCodesMetadata$()
            .pipe(map(metadata => metadata?.total || 0));

        this.metadata$ = this.selectedOnly$.pipe(
            switchMap(isActive => isActive ?
                this.selectedCodes$.pipe(map(list => new Metadata({ total: list?.length, limit: 999, offset: 0 }))) :
                this._collectiveSrv.getCollectiveCodesMetadata$()
            ),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._collectiveSrv.clearCollectiveCodes();
    }

    get selectedCodes(): number {
        return this.selected?.value?.length || 0;
    }

    clickShowSelected(): void {
        this.selectedOnly$.pipe(take(1)).subscribe((isSelected => this.showSelectedOnlyClick.emit(!isSelected)));
    }

    loadCollectiveCodes({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this._filters = { ...this._filters, limit, offset, q: q?.length ? q : null };

        // cancel prev requests so it keeps consistency
        this._collectiveSrv.cancelCollectiveCodes();
        this._collectiveSrv.loadCollectiveCodes(this._collectiveId, this._filters);
        // change to non selected only view if a search is made
        this._collectiveSrv.getCollectiveCodesData$().pipe(
            withLatestFrom(this.selectedOnly$),
            take(1)
        ).subscribe(([, isSelectedOnlyMode]) => this.showSelectedOnlyClick.emit(isSelectedOnlyMode));
    }

    newCollectiveCode(validationMethod: CollectiveValidationMethod): void {
        this._matDialog.open(NewCollectiveCodeDialogComponent, new ObMatDialogConfig(
            { collectiveId: this._collectiveId, validationMethod }
        )).beforeClosed()
            .pipe(filter(created => !!created))
            .subscribe(() => {
                this.loadCollectiveCodes({ limit: this.pageSize });
            });
    }

    importCollectives(validationMethod: CollectiveValidationMethod): void {
        this._matDialog.open(
            ImportCollectiveCodesDialogComponent,
            new ObMatDialogConfig({ validationMethod })
        )
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(collectiveCodesData => {
                this._collectiveSrv.createCollectiveCodes(this._collectiveId, collectiveCodesData)
                    .subscribe(() => {
                        this._ephemeralSrv.showSaveSuccess();
                        this.loadCollectiveCodes({ limit: this.pageSize });
                    });
            });
    }

    exportCollectives(validationMethod: CollectiveValidationMethod): void {
        const exportDataArray = (validationMethod === CollectiveValidationMethod.user ? exportDataCollectiveCodesUser :
            (validationMethod === CollectiveValidationMethod.userPassword ? exportDataCollectiveCodesUserPass : exportDataCollectiveCodes));
        const selectedFieldsKey = (validationMethod === CollectiveValidationMethod.user ? 'EXP_COLLECTIVES_USER' :
            (validationMethod === CollectiveValidationMethod.userPassword ? 'EXP_COLLECTIVES_USER_PASS' : 'EXP_COLLECTIVES'));
        const selectedFieldsArray = this._tableSrv.getColumns(selectedFieldsKey);
        this._matDialog.open(
            ExportDialogComponent,
            new ObMatDialogConfig({
                exportData: exportDataArray,
                exportFormat: ExportFormat.csv,
                selectedFields: selectedFieldsArray
            })
        )
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(exportList => {
                this._tableSrv.setColumns(selectedFieldsKey, exportList.fields.map(resultData => resultData.field));
                this._collectiveSrv.exportCollectiveCodes(this._collectiveId, { q: this._filters?.q?.length ? this._filters.q : null },
                    exportList)
                    .pipe(filter(result => !!result.export_id))
                    .subscribe(() => {
                        this._ephemeralSrv.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' });
                    });
            });
    }

    editCollectiveCode(collectiveCode: CollectiveCode): void {
        this.openEditCollectiveDialog([collectiveCode]);
    }

    editMultipleCollectiveCodes(): void {
        combineLatest([
            this.selectedCodes$.pipe(startWith([])),
            this.totalCodes$,
            this.allSelected$
        ]).pipe(
            take(1)
        ).subscribe(([selected, totalCodes, allSelected]) => {
            if (allSelected) {
                this.openEditCollectiveDialog([], totalCodes, this._filters?.q?.length ? this._filters.q : null);
            } else {
                this.openEditCollectiveDialog(selected, selected.length);
            }
        });
    }

    deleteCollectiveCode(collectiveCode: CollectiveCode): void {
        this.openDeleteCollectiveDialog([collectiveCode.code]);
    }

    deleteMultipleCollectiveCodes(): void {
        combineLatest([
            this.selectedCodes$.pipe(startWith([])),
            this.totalCodes$,
            this.allSelected$
        ]).pipe(
            take(1)
        ).subscribe(([selected, totalCodes, allSelected]) => {
            if (allSelected) {
                this.openDeleteCollectiveDialog([], totalCodes, this._filters?.q?.length ? this._filters.q : null);
            } else {
                this.openDeleteCollectiveDialog(selected?.map(collectiveCode => collectiveCode.code), selected.length);
            }
        });
    }

    private openDeleteCollectiveDialog(collectiveCodes: string[], codesSelected = 1, q: string = null): void {
        const messageMultiple = `${codesSelected > 1 ? '_MULTIPLE' : ''}${collectiveCodes.length === 0 ? '_ALL' : ''}`;
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_COLLECTIVE_CODES',
            message: `COLLECTIVE.CODES.DELETE_WARNING${messageMultiple}`,
            messageParams: { number: codesSelected },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this._collectiveSrv.deleteCollectiveCodes(this._collectiveId, collectiveCodes, q))
            )
            .subscribe(() => {
                this._ephemeralSrv.showSuccess({
                    msgKey: `COLLECTIVE.CODES.DELETE_COLLECTIVE_CODE_SUCCESS${messageMultiple}`,
                    msgParams: { number: codesSelected }
                });

                this.selected.patchValue([]);
                this.loadCollectiveCodes({ limit: this.pageSize });
            });
    }

    private openEditCollectiveDialog(collectiveCodes: CollectiveCode[], codesSelected = 1, q: string = null): void {
        this._matDialog.open(EditCollectiveCodeDialogComponent, new ObMatDialogConfig(
            { collectiveId: this._collectiveId, collectiveCodes, codesSelected, q }
        ))
            .beforeClosed()
            .pipe(filter(edited => !!edited))
            .subscribe(() => {
                this.loadCollectiveCodes({ limit: this.pageSize, q: this._filters.q });
            });
    }
}

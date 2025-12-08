import { BuyersService, BuyersQueryDef } from '@admin-clients/cpanel-viewers-buyers-data-access';
import { DialogSize, MessageDialogService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { mergeObjects } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSelectionListChange } from '@angular/material/list';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, map, switchMap, take, tap } from 'rxjs/operators';
import { BuyersQueryDialogComponent } from '../query-dialog/buyers-query-dialog.component';
import { BuyersQueryDialogData } from '../query-dialog/model/buyers-query-dialog-data.model';

@Component({
    selector: 'app-buyers-query-list',
    templateUrl: './buyers-query-list.component.html',
    styleUrls: ['./buyers-query-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class BuyersQueryListComponent implements OnInit {

    queries$: Observable<BuyersQueryDef[]>;
    selectedQuery$: Observable<BuyersQueryDef>;

    constructor(
        private _matDialog: MatDialog,
        private _messageDialogSrv: MessageDialogService,
        private _translateSrv: TranslateService,
        private _buyersSrv: BuyersService
    ) { }

    ngOnInit(): void {
        this.queries$ = this._buyersSrv.getQueries$();
        this.selectedQuery$ = this._buyersSrv.getQuery$();
    }

    saveQuery(): void {
        this._buyersSrv.getQuery$()
            .pipe(take(1))
            .subscribe(selectedQuery => {
                if (selectedQuery.id) {
                    this._buyersSrv.updateQuery(selectedQuery.id, selectedQuery)
                        .subscribe(() => this._buyersSrv.loadQueries());
                } else {
                    this.openQueryDialog(selectedQuery, this._translateSrv.instant('BUYERS.QUERIES.SAVE'))
                        .pipe(
                            filter(resultQueryDef => !!resultQueryDef),
                            map(resultQueryDef => mergeObjects(selectedQuery, resultQueryDef)),
                            switchMap(query => this._buyersSrv.createQuery(query))
                        )
                        .subscribe(id => {
                            selectedQuery.id = id.id;
                            this._buyersSrv.loadQueries();
                        });

                }
            });
    }

    loadQuery(change: MatSelectionListChange): void {
        this._buyersSrv.loadQuery(change.options.pop().value);
    }

    editQuery(event: MouseEvent, query: BuyersQueryDef): void {
        event.preventDefault();
        event.stopImmediatePropagation();
        this.openQueryDialog(query, this._translateSrv.instant('BUYERS.QUERIES.EDIT'))
            .subscribe(editedQueryValue => {
                query.query_name = editedQueryValue.query_name;
                query.query_description = editedQueryValue.query_description;
                this._buyersSrv.updateQuery(query.id, query)
                    .subscribe(() => this._buyersSrv.loadQueries());
            });
    }

    cloneQuery(event: MouseEvent, query: BuyersQueryDef): void {
        event.preventDefault();
        event.stopImmediatePropagation();
        this.openQueryDialog(query, this._translateSrv.instant('BUYERS.QUERIES.CLONE'))
            .subscribe(resultQuery =>
                this._buyersSrv.cloneQuery(query.id, resultQuery)
                    .subscribe(() => this._buyersSrv.loadQueries()));
    }

    deleteQuery(event: MouseEvent, query: BuyersQueryDef): void {
        event.preventDefault();
        event.stopImmediatePropagation();
        this._messageDialogSrv.showWarn({
            message: this._translateSrv.instant('BUYERS.QUERIES.DELETE_WARNING', { name: query.query_name }),
            title: 'BUYERS.QUERIES.DELETE',
            size: DialogSize.SMALL
        })
            .pipe(
                filter(success => !!success),
                switchMap(() => this._buyersSrv.deleteQuery(query.id)),
                switchMap(() => {
                    this._buyersSrv.loadQueries();
                    return this._buyersSrv.getQuery$();
                }),
                take(1),
                tap(currentQuery => {
                    if (currentQuery?.id === query.id) {
                        this._buyersSrv.setBlankQuery();
                    }
                })
            )
            .subscribe();
    }

    private openQueryDialog(query: BuyersQueryDef, title: string): Observable<BuyersQueryDef> {
        return this._matDialog.open(
            BuyersQueryDialogComponent,
            new ObMatDialogConfig(
                {
                    queryDef: query,
                    title
                } as BuyersQueryDialogData)
        )
            .beforeClosed()
            .pipe(
                filter(result => !!result),
                map(query => query as BuyersQueryDef)
            );
    }
}

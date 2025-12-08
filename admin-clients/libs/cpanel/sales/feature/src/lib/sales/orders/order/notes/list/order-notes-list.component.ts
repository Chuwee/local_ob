import { Metadata } from '@OneboxTM/utils-state';
import { NewNoteDialogComponent, NoteDialogData, Note } from '@admin-clients/cpanel/common/feature/notes';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    OrderDetail, OrderNotesLoadCase, OrderNotesService, OrderNotesStateMachine, OrdersService
} from '@admin-clients/cpanel-sales-data-access';
import { EphemeralMessageService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, Observable, Subject } from 'rxjs';
import { distinctUntilChanged, filter, first, map, shareReplay, switchMap, take, takeUntil, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-order-notes-list',
    templateUrl: './order-notes-list.component.html',
    styleUrls: ['./order-notes-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class OrderNotesListComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _order$: Observable<OrderDetail>;
    canLoggedUserWrite$: Observable<boolean>;
    notesListMetadata$: Observable<Metadata>;
    isLoadingList$: Observable<boolean>;
    notesList$: Observable<Note[]>;
    selectedOrderNote: string;
    note$: Observable<Note>;

    private get _idPath(): string | undefined {
        return this._route.snapshot.children[0]?.params?.['orderCode'];
    }

    constructor(
        private _orderNotesSrv: OrderNotesService,
        private _ordersSrv: OrdersService,
        private _route: ActivatedRoute,
        private _ephemeralMessageService: EphemeralMessageService,
        private _auth: AuthenticationService,
        private _matDialog: MatDialog,
        private _orderNotesSM: OrderNotesStateMachine
    ) {
    }

    ngOnInit(): void {
        this.model();
        this.loadDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._orderNotesSrv.clearOrderNotesList();
    }

    openNewNoteDialog(): void {
        this._matDialog.open(NewNoteDialogComponent, new ObMatDialogConfig()).beforeClosed().pipe(
            filter((noteDialogData: NoteDialogData) => !!noteDialogData),
            switchMap((noteDialogData: NoteDialogData) => this._order$.pipe(
                switchMap((order: OrderDetail) => this._orderNotesSrv.createOrderNote(
                    order.code,
                    noteDialogData
                )),
                take(1)
            ))
        ).subscribe((noteId: number) => {
            if (noteId) {
                this._ephemeralMessageService.showSuccess({
                    msgKey: 'NOTES.CREATE_SUCCESS'
                });
                this._orderNotesSM.setCurrentState({
                    state: OrderNotesLoadCase.loadNote,
                    idPath: noteId.toString()
                });
            }
        });
    }

    deleteNote(): void {
        combineLatest([this._order$, this.note$])
            .pipe(
                take(1),
                switchMap(([order, note]) => this._orderNotesSrv.deleteOrderNote(
                    order.code,
                    note.id
                ).pipe(
                    tap(() => {
                        this._ephemeralMessageService.showSuccess({
                            msgKey: 'NOTES.DELETE_SUCCESS',
                            msgParams: note
                        });
                        this._orderNotesSM.setCurrentState({
                            state: OrderNotesLoadCase.loadNote
                        });
                    })
                )
                ),
                switchMap(() => this.notesList$)
            ).subscribe((noteList: Note[]) => {
                if (!noteList?.length) {
                    this._orderNotesSrv.clearOrderNote();
                }
            });
    }

    selectionChangeHandler(noteId: string): void {
        if (!!noteId && this.selectedOrderNote !== noteId) {
            this._orderNotesSM.setCurrentState({
                state: OrderNotesLoadCase.selectedNote,
                idPath: noteId
            });
        }
    }

    private loadDataHandler(): void {
        this._orderNotesSM.getListDetailState$()
            .pipe(
                tap(state => {
                    if (state === OrderNotesLoadCase.none) {
                        this._orderNotesSM.setCurrentState({
                            state: OrderNotesLoadCase.loadNote,
                            idPath: this._idPath
                        });
                    }
                }),
                takeUntil(this._onDestroy)
            ).subscribe();

        this.note$
            .pipe(
                withLatestFrom(this._orderNotesSM.getListDetailState$()),
                tap(([note, state]) => {
                    this.selectedOrderNote = note.id;
                    if (state === OrderNotesLoadCase.loadNote) {
                        this.scrollToSelectedOrderNote(note.id);
                    }
                }),
                takeUntil(this._onDestroy)
            )
            .subscribe();
    }

    private model(): void {
        this.isLoadingList$ = combineLatest([
            this._orderNotesSrv.isOrderNoteInProgress$(),
            this._orderNotesSrv.isOrderNoteSaveOrDeleteInProgress$(),
            this._ordersSrv.isOrderDetailLoading$(),
            this._orderNotesSrv.isOrderNotesListInProgress$()
        ]).pipe(
            map(loadings => loadings.some(loading => loading)),
            distinctUntilChanged(),
            shareReplay(1)
        );
        this.notesList$ = this._orderNotesSrv.getOrderNotesListData$()
            .pipe(
                filter(notesList => !!notesList)
            );
        this.notesListMetadata$ = this._orderNotesSrv.getOrderNotesListMetadata$();
        this.note$ = this._orderNotesSrv.getOrderNote$()
            .pipe(
                filter(note => !!note),
                shareReplay(1)
            );
        this._order$ = this._ordersSrv.getOrderDetail$()
            .pipe(
                filter(order => !!order),
                shareReplay(1)
            );
        // Logged user
        const loggedUser$ = this._auth.getLoggedUser$().pipe(first(user => user !== null));

        const writingRoles = [
            UserRoles.CRM_MGR, UserRoles.OPR_MGR, UserRoles.OPR_ANS, UserRoles.OPR_CALL,
            UserRoles.CNL_MGR, UserRoles.CNL_INT,
            UserRoles.ENT_MGR, UserRoles.ENT_ANS
        ];

        // check if logged user has write permissions
        this.canLoggedUserWrite$ = loggedUser$
            .pipe(
                map(user => AuthenticationService.isSomeRoleInUserRoles(user, writingRoles)),
                shareReplay(1)
            );
    }

    private scrollToSelectedOrderNote(noteId: string): void {
        setTimeout(() => {
            const element = document.getElementById('note-list-option-' + noteId);
            if (element) {
                element.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }, 500);
    }
}

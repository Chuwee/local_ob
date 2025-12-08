import { Metadata } from '@OneboxTM/utils-state';
import { NewNoteDialogComponent, NoteDialogData, Note } from '@admin-clients/cpanel/common/feature/notes';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { OrderNotesStateMachine, OrdersService, OrderNotesLoadCase, OrderNotesService } from '@admin-clients/cpanel-sales-data-access';
import { EphemeralMessageService, ObMatDialogConfig, BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, shareReplay, switchMap, take, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-order-note-details',
    templateUrl: './order-note-details.component.html',
    styleUrls: ['./order-note-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class OrderNoteDetailsComponent implements OnInit, OnDestroy, WritingComponent {
    private _onDestroy = new Subject<void>();

    private get _breadcrumb(): string | undefined {
        return this._route.snapshot.data['breadcrumb'];
    }

    form: UntypedFormGroup;
    note$: Observable<Note>;
    isLoading$: Observable<boolean>;
    orderNotesListMetadata$: Observable<Metadata>;
    canLoggedUserWrite$: Observable<boolean>;

    constructor(
        private _route: ActivatedRoute,
        private _breadcrumbsService: BreadcrumbsService,
        private _orderNotesSrv: OrderNotesService,
        private _ordersSrv: OrdersService,
        private _auth: AuthenticationService,
        private _matDialog: MatDialog,
        private _ephemeralSrv: EphemeralMessageService,
        private _orderNotesSM: OrderNotesStateMachine
    ) { }

    ngOnInit(): void {
        this.form = new UntypedFormGroup({});
        this.loadDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    openNewNoteDialog(): void {
        this._matDialog.open<NewNoteDialogComponent, null, NoteDialogData>(NewNoteDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .pipe(
                filter(noteDialogData => !!noteDialogData),
                withLatestFrom(this._ordersSrv.getOrderDetail$()),
                switchMap(([noteDialogData, order]) =>
                    this._orderNotesSrv.createOrderNote(order.code, noteDialogData)
                )
            )
            .subscribe(noteId => {
                if (noteId) {
                    this._ephemeralSrv.showSuccess({ msgKey: 'NOTES.CREATE_SUCCESS' });
                    this._orderNotesSM.setCurrentState({
                        state: OrderNotesLoadCase.loadNote,
                        idPath: noteId.toString()
                    });
                }
            });
    }

    save$(): Observable<string> {
        return combineLatest([this._ordersSrv.getOrderDetail$(), this.note$])
            .pipe(
                take(1),
                switchMap(([order, note]) => {
                    const putOrderNote = Object.assign({}, this.form.value);
                    return this._orderNotesSrv.saveOrderNote(
                        order.code,
                        note.id,
                        putOrderNote
                    ).pipe(
                        tap(() => {
                            this._ephemeralSrv.showSuccess({ msgKey: 'NOTES.NOTE_UPDATE_SUCCESS' });
                        }),
                        map(() => note.id)
                    );
                })
            );
    }

    save(): void {
        this.save$().subscribe(noteId => {
            this._orderNotesSM.setCurrentState({
                state: OrderNotesLoadCase.loadNote,
                idPath: noteId
            });
        });
    }

    cancel(noteId: string): void {
        this._orderNotesSM.setCurrentState({
            state: OrderNotesLoadCase.loadNote,
            idPath: noteId
        });
    }

    private loadDataHandler(): void {
        this.note$ = this._orderNotesSrv.getOrderNote$()
            .pipe(
                filter(note => !!note),
                tap(note => this._breadcrumbsService.addDynamicSegment(this._breadcrumb, note.title)),
                shareReplay(1)
            );

        // Loading
        this.isLoading$ = booleanOrMerge([
            this._orderNotesSrv.isOrderNoteInProgress$(),
            this._orderNotesSrv.isOrderNoteSaveOrDeleteInProgress$(),
            this._ordersSrv.isOrderDetailLoading$(),
            this._orderNotesSrv.isOrderNotesListInProgress$()
        ]);

        this.orderNotesListMetadata$ = this._orderNotesSrv.getOrderNotesListMetadata$();
        // Logged user
        const writingRoles = [UserRoles.CRM_MGR, UserRoles.OPR_MGR, UserRoles.ENT_MGR];
        // check if logged user has write permissions
        this.canLoggedUserWrite$ = this._auth.hasLoggedUserSomeRoles$(writingRoles);
    }
}

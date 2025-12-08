/* eslint-disable @typescript-eslint/dot-notation */
import { Metadata } from '@OneboxTM/utils-state';
import { NewNoteDialogComponent, NoteDialogData, Note, NotesModule } from '@admin-clients/cpanel/common/feature/notes';
import { UserRoles, AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    CustomersService, Customer, CustomerNotesLoadCase,
    CustomerNotesService
} from '@admin-clients/cpanel-viewers-customers-data-access';
import { EphemeralMessageService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, Observable, Subject } from 'rxjs';
import { distinctUntilChanged, filter, first, map, shareReplay, switchMap, take, takeUntil, tap, withLatestFrom } from 'rxjs/operators';
import { CustomerNotesStateMachine } from '../customer-notes-state-machine';

@Component({
    selector: 'app-customer-notes-list',
    imports: [NotesModule, AsyncPipe],
    templateUrl: './customer-notes-list.component.html',
    styleUrls: ['./customer-notes-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerNotesListComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _customer$: Observable<Customer>;
    canLoggedUserWrite$: Observable<boolean>;
    notesListMetadata$: Observable<Metadata>;
    isLoadingList$: Observable<boolean>;
    notesList$: Observable<Note[]>;
    selectedCustomerNote: string;
    note$: Observable<Note>;

    private get _idPath(): string | undefined {
        return this._route.snapshot.children[0]?.params?.['noteId'];
    }

    constructor(
        private _customerNotesSrv: CustomerNotesService,
        private _customersSrv: CustomersService,
        private _route: ActivatedRoute,
        private _ephemeralMessageService: EphemeralMessageService,
        private _auth: AuthenticationService,
        private _matDialog: MatDialog,
        private _customerNotesSM: CustomerNotesStateMachine
    ) {
    }

    ngOnInit(): void {
        this.model();
        this.loadDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._customerNotesSrv.clearCustomerNotesList();
    }

    openNewNoteDialog(): void {
        this._matDialog.open(NewNoteDialogComponent, new ObMatDialogConfig()).beforeClosed().pipe(
            filter((noteDialogData: NoteDialogData) => !!noteDialogData),
            switchMap((noteDialogData: NoteDialogData) => this._customer$.pipe(
                switchMap((customer: Customer) => this._customerNotesSrv.createCustomerNote(
                    customer.id,
                    noteDialogData,
                    customer.entity?.id?.toString()
                )),
                take(1)
            ))
        ).subscribe(noteId => {
            if (noteId) {
                this._ephemeralMessageService.showSuccess({ msgKey: 'NOTES.CREATE_SUCCESS' });
                this._customerNotesSM.setCurrentState({
                    state: CustomerNotesLoadCase.loadNote,
                    idPath: noteId.toString()
                });
            }
        });
    }

    deleteNote(): void {
        combineLatest([this._customer$, this.note$])
            .pipe(
                take(1),
                switchMap(([customer, note]) => this._customerNotesSrv.deleteCustomerNote(
                    customer.id,
                    note.id,
                    customer.entity?.id?.toString()
                ).pipe(
                    tap(() => {
                        this._ephemeralMessageService.showSuccess({
                            msgKey: 'DELETE_SUCCESS',
                            msgParams: note
                        });
                        this._customerNotesSM.setCurrentState({
                            state: CustomerNotesLoadCase.loadNote
                        });
                    })
                )
                ),
                switchMap(() => this.notesList$)
            ).subscribe((noteList: Note[]) => {
                if (!noteList?.length) {
                    this._customerNotesSrv.clearCustomerNote();
                }
            });
    }

    selectionChangeHandler(noteId: string): void {
        if (!!noteId && this.selectedCustomerNote !== noteId) {
            this._customerNotesSM.setCurrentState({
                state: CustomerNotesLoadCase.selectedNote,
                idPath: noteId
            });
        }
    }

    private loadDataHandler(): void {
        this._customerNotesSM.getListDetailState$()
            .pipe(
                tap(state => {
                    if (state === CustomerNotesLoadCase.none) {
                        this._customerNotesSM.setCurrentState({
                            state: CustomerNotesLoadCase.loadNote,
                            idPath: this._idPath
                        });
                    }
                }),
                takeUntil(this._onDestroy)
            ).subscribe();

        this.note$
            .pipe(
                withLatestFrom(this._customerNotesSM.getListDetailState$()),
                tap(([note, state]) => {
                    this.selectedCustomerNote = note.id;
                    if (state === CustomerNotesLoadCase.loadNote) {
                        this.scrollToSelectedCustomerNote(note.id);
                    }
                }),
                takeUntil(this._onDestroy)
            )
            .subscribe();
    }

    private model(): void {
        this.isLoadingList$ = combineLatest([
            this._customerNotesSrv.isCustomerNoteInProgress$(),
            this._customerNotesSrv.isCustomerNoteSaveOrDeleteInProgress$(),
            this._customersSrv.customer.loading$(),
            this._customerNotesSrv.isCustomerNotesListInProgress$()
        ]).pipe(
            map(loadings => loadings.some(loading => loading)),
            distinctUntilChanged(),
            shareReplay(1)
        );
        this.notesList$ = this._customerNotesSrv.getCustomerNotesListData$()
            .pipe(
                filter(notesList => !!notesList)
            );
        this.notesListMetadata$ = this._customerNotesSrv.getCustomerNotesLisMetadata$();
        this.note$ = this._customerNotesSrv.getCustomerNote$()
            .pipe(
                filter(note => !!note),
                shareReplay(1)
            );
        this._customer$ = this._customersSrv.customer.get$()
            .pipe(
                filter(customer => !!customer),
                shareReplay(1)
            );
        // Logged user
        const loggedUser$ = this._auth.getLoggedUser$().pipe(first(user => user !== null));

        const writingRoles = [UserRoles.CRM_MGR, UserRoles.OPR_MGR, UserRoles.ENT_MGR];

        // check if logged user has write permissions
        this.canLoggedUserWrite$ = loggedUser$
            .pipe(
                map(user => AuthenticationService.isSomeRoleInUserRoles(user, writingRoles)),
                shareReplay(1)
            );
    }

    private scrollToSelectedCustomerNote(noteId: string): void {
        setTimeout(() => {
            const element = document.getElementById('note-list-option-' + noteId);
            if (element) {
                element.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }, 500);
    }
}

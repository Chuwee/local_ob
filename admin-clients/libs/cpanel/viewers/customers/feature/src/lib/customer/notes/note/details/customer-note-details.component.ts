/* eslint-disable @typescript-eslint/dot-notation */
import { Metadata } from '@OneboxTM/utils-state';
import { Note, NoteDialogData, NewNoteDialogComponent, NotesModule } from '@admin-clients/cpanel/common/feature/notes';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { CustomersService, CustomerNotesLoadCase, CustomerNotesService } from '@admin-clients/cpanel-viewers-customers-data-access';
import { EphemeralMessageService, ObMatDialogConfig, BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, mapTo, shareReplay, switchMap, take, tap, withLatestFrom } from 'rxjs/operators';
import { CustomerNotesStateMachine } from '../../customer-notes-state-machine';

@Component({
    selector: 'app-customer-note-details',
    imports: [NotesModule, AsyncPipe],
    templateUrl: './customer-note-details.component.html',
    styleUrls: ['./customer-note-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerNoteDetailsComponent implements OnInit, OnDestroy, WritingComponent {
    private _onDestroy = new Subject<void>();

    private get _breadcrumb(): string | undefined {
        return this._route.snapshot.data['breadcrumb'];
    }

    form: UntypedFormGroup;
    note$: Observable<Note>;
    isLoading$: Observable<boolean>;
    customerNotesListMetadata$: Observable<Metadata>;
    canLoggedUserWrite$: Observable<boolean>;

    constructor(
        private _route: ActivatedRoute,
        private _breadcrumbsService: BreadcrumbsService,
        private _customerNotesSrv: CustomerNotesService,
        private _customersSrv: CustomersService,
        private _auth: AuthenticationService,
        private _matDialog: MatDialog,
        private _ephemeralSrv: EphemeralMessageService,
        private _customerNotesSM: CustomerNotesStateMachine
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
                withLatestFrom(this._customersSrv.customer.get$()),
                switchMap(([noteDialogData, customer]) =>
                    this._customerNotesSrv.createCustomerNote(
                        customer.id,
                        noteDialogData,
                        customer.entity?.id?.toString()
                    )
                )
            )
            .subscribe(noteId => {
                if (noteId) {
                    this._ephemeralSrv.showSuccess({ msgKey: 'NOTES.CREATE_SUCCESS' });
                    this._customerNotesSM.setCurrentState({
                        state: CustomerNotesLoadCase.loadNote,
                        idPath: noteId.toString()
                    });
                }
            });
    }

    save(): void {
        this.save$(true).subscribe();
    }

    save$(...args: unknown[]): Observable<string> {
        const [loadWithNavigation]
            = args as boolean[]; // loadWithNavigation will be true if the save method is executed by the component instead of by the guard.

        return combineLatest([this._customersSrv.customer.get$(), this.note$])
            .pipe(
                take(1),
                switchMap(([customer, note]) => {
                    const putCustomerNote = Object.assign({}, this.form.value);
                    return this._customerNotesSrv.saveCustomerNote(
                        customer.id,
                        note.id,
                        putCustomerNote,
                        customer.entity?.id?.toString()
                    ).pipe(
                        mapTo(note.id)
                    );

                }),
                tap(noteId => {
                    this._ephemeralSrv.showSaveSuccess();
                    this._customerNotesSM.setCurrentState({
                        state: loadWithNavigation ? CustomerNotesLoadCase.loadNote :
                            CustomerNotesLoadCase.loadNoteWithoutNavigating,
                        idPath: noteId
                    });
                })
            );
    }

    cancel(noteId: string): void {
        this._customerNotesSM.setCurrentState({
            state: CustomerNotesLoadCase.loadNote,
            idPath: noteId
        });
    }

    private loadDataHandler(): void {
        this.note$ = this._customerNotesSrv.getCustomerNote$()
            .pipe(
                filter(note => !!note),
                tap(note => this._breadcrumbsService.addDynamicSegment(this._breadcrumb, note.title)),
                shareReplay(1)
            );

        // Loading
        this.isLoading$ = booleanOrMerge([
            this._customerNotesSrv.isCustomerNoteInProgress$(),
            this._customerNotesSrv.isCustomerNoteSaveOrDeleteInProgress$(),
            this._customersSrv.customer.loading$(),
            this._customerNotesSrv.isCustomerNotesListInProgress$()
        ]);

        this.customerNotesListMetadata$ = this._customerNotesSrv.getCustomerNotesLisMetadata$();
        // Logged user
        const writingRoles = [UserRoles.CRM_MGR, UserRoles.OPR_MGR, UserRoles.ENT_MGR];
        // check if logged user has write permissions
        this.canLoggedUserWrite$ = this._auth.hasLoggedUserSomeRoles$(writingRoles);
    }
}

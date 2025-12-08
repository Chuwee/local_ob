import { Note } from '@admin-clients/cpanel/common/feature/notes';
import {
    CustomersService,
    CustomerNotesService,
    CustomerNotesLoadCase
} from '@admin-clients/cpanel-viewers-customers-data-access';
import { Injectable, OnDestroy } from '@angular/core';
import { GuardsCheckEnd, Router } from '@angular/router';
import { Observable, of, Subject } from 'rxjs';
import { filter, first, mapTo, switchMap, take, takeUntil, tap } from 'rxjs/operators';

export type CustomerNotesStateParams = {
    state: CustomerNotesLoadCase;
    idPath?: string;
};

@Injectable()
export class CustomerNotesStateMachine implements OnDestroy {
    private _onDestroy = new Subject<void>();
    private _idPath: string;
    private _noteId: string;

    constructor(
        private _customerNotesSrv: CustomerNotesService,
        private _customersSrv: CustomersService,
        private _router: Router
    ) {
        this.getListDetailState$()
            .pipe(
                filter(state => state !== null),
                tap(state => {
                    switch (state) {
                        case CustomerNotesLoadCase.loadNote:
                            this.loadNote();
                            break;
                        case CustomerNotesLoadCase.loadNoteWithoutNavigating:
                            this.loadNoteWithoutNavigate();
                            break;
                        case CustomerNotesLoadCase.selectedNote:
                            this.selectedNote();
                            break;
                        case CustomerNotesLoadCase.none:
                        default:
                            break;
                    }
                }),
                takeUntil(this._onDestroy)
            ).subscribe();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    setCurrentState({ state, idPath }: CustomerNotesStateParams): void {
        this._idPath = idPath;
        this._customerNotesSrv.setListDetailState(state);
    }

    getListDetailState$(): Observable<CustomerNotesLoadCase> {
        return this._customerNotesSrv.getListDetailState$();
    }

    private loadNote(): void {
        this.loadCustomerNotesList();
        this.getCustomerNotesList()
            .pipe(
                tap(notesList => {
                    if (notesList.length) {
                        this.setNoteIdOfNotesList(notesList);
                        this.loadCustomerNote();
                        this.navigateToNote();
                    }
                })
            ).subscribe();
    }

    private loadNoteWithoutNavigate(): void {
        this.loadCustomerNotesList();
        this.getCustomerNotesList()
            .pipe(
                tap(notesList => {
                    if (notesList.length) {
                        this.loadCustomerNote();
                    }
                })
            ).subscribe();
    }

    private selectedNote(): void {
        this.getCustomerNotesList()
            .pipe(
                tap(notesList => {
                    if (notesList.length) {
                        this.setNoteId(this._idPath);
                        this.navigateToNote();
                    }
                })
            ).subscribe();

        this._router.events.pipe(
            first(event => event instanceof GuardsCheckEnd),
            tap(event => {
                if ((event).shouldActivate) {
                    this.loadCustomerNote();
                }
            })
        ).subscribe();
    }

    private setNoteId(noteId: string): void {
        this._noteId = noteId;
    }

    private setNoteIdOfNotesList(notesList: Note[]): void {
        this.setNoteId(notesList[0].id);
    }

    private navigateToNote(): void {
        this._customersSrv.customer.get$()
            .pipe(
                take(1),
                tap(customer => {
                    this._router.navigate(['/customers', customer.id, 'notes', this._noteId]);
                })
            ).subscribe();
    }

    private loadCustomerNote(): void {
        this._customerNotesSrv.clearCustomerNote();
        this._customerNotesSrv.loadCustomerNote(this._noteId);
    }

    private loadCustomerNotesList(): void {
        this._customersSrv.customer.get$()
            .pipe(
                first(customer => !!customer),
                tap(customer => {
                    this._customerNotesSrv.clearCustomerNotesList();
                    this._customerNotesSrv.loadCustomerNotesList(customer.id, customer.entity?.id?.toString());
                }
                )
            ).subscribe();
    }

    private getCustomerNotesList(): Observable<Note[]> {
        return this._customerNotesSrv.getCustomerNotesListData$()
            .pipe(
                first(notesList => !!notesList),
                switchMap(notesList => {
                    if (!notesList.length) {
                        return this._customersSrv.customer.get$()
                            .pipe(
                                tap(customer => this._router.navigate(['/customers', customer.id, 'notes'])),
                                mapTo(notesList)
                            );
                    } else {
                        return of(notesList);
                    }
                }),
                take(1)
            );
    }
}


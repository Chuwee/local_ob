import { Note } from '@admin-clients/cpanel/common/feature/notes';
import { Injectable, OnDestroy } from '@angular/core';
import { Router, GuardsCheckEnd } from '@angular/router';
import { Subject, Observable, of } from 'rxjs';
import { filter, tap, takeUntil, first, switchMap, mapTo, take } from 'rxjs/operators';
import { OrdersService } from '../orders.service';
import { OrderNotesLoadCase } from './models/order-notes-load.case';
import { OrderNotesService } from './order-notes.service';
import { OrderNotesState } from './state/order-notes.state';

export type OrderNotesStateParams = {
    state: OrderNotesLoadCase;
    idPath?: string;
};

@Injectable()
export class OrderNotesStateMachine implements OnDestroy {
    private _onDestroy = new Subject<void>();
    private _idPath: string;
    private _noteId: string;

    constructor(
        private _orderNotesState: OrderNotesState,
        private _orderNotesSrv: OrderNotesService,
        private _ordersSrv: OrdersService,
        private _router: Router
    ) {
        this.getListDetailState$()
            .pipe(
                filter(state => state !== null),
                tap(state => {
                    switch (state) {
                        case OrderNotesLoadCase.loadNote:
                            this.loadNote();
                            break;
                        case OrderNotesLoadCase.selectedNote:
                            this.selectedNote();
                            break;
                        case OrderNotesLoadCase.none:
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

    setCurrentState({ state, idPath }: OrderNotesStateParams): void {
        this._idPath = idPath;
        this._orderNotesState.setListDetailState(state);
    }

    getListDetailState$(): Observable<OrderNotesLoadCase> {
        return this._orderNotesState.getListDetailState$();
    }

    private loadNote(): void {
        this.loadOrderNotesList();
        this.getOrderNotesList()
            .pipe(
                tap(notesList => {
                    if (notesList.length) {
                        this.setNoteIdOfNotesList(notesList);
                        this.loadOrderNote();
                        this.navigateToNote();
                    }
                })
            ).subscribe();
    }

    private selectedNote(): void {
        this.getOrderNotesList()
            .pipe(
                tap(notesList => {
                    if (notesList.length) {
                        this.setNoteId(this._idPath);
                        this.navigateToNote();
                    }
                })
            ).subscribe();

        this._router.events.pipe(
            first((event): event is GuardsCheckEnd => event instanceof GuardsCheckEnd),
            tap((event: GuardsCheckEnd) => {
                if (event.shouldActivate) {
                    this.loadOrderNote();
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

    private navigateToNote(): void { // fix
        this._ordersSrv.getOrderDetail$()
            .pipe(
                take(1),
                tap(order => {
                    this._router.navigate(['/transactions', order.code, 'notes', this._noteId]);
                })
            ).subscribe();
    }

    private loadOrderNote(): void {
        this._orderNotesSrv.clearOrderNote();
        this._orderNotesSrv.loadOrderNote(this._noteId);
    }

    private loadOrderNotesList(): void {
        this._ordersSrv.getOrderDetail$()
            .pipe(
                first(order => !!order),
                tap(order => {
                    this._orderNotesSrv.clearOrderNotesList();
                    this._orderNotesSrv.loadOrderNotesList(order.code);
                }
                )
            ).subscribe();
    }

    private getOrderNotesList(): Observable<Note[]> {
        return this._orderNotesSrv.getOrderNotesListData$()
            .pipe(
                first(notesList => !!notesList),
                switchMap(notesList => {
                    if (!notesList.length) {
                        return this._ordersSrv.getOrderDetail$()
                            .pipe(
                                tap(order => {
                                    this._router.navigate(['/transactions', order.code, 'notes']);
                                }),
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

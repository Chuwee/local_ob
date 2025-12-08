import { Metadata } from '@OneboxTM/utils-state';
import { Note } from '@admin-clients/cpanel/common/feature/notes';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { finalize, map, take } from 'rxjs/operators';
import { OrderNotesApi } from './api/order-notes.api';
import { PostOrderNote } from './models/post-order-note.model';
import { PutOrderNote } from './models/put-order-note.model';
import { OrderNotesState } from './state/order-notes.state';

@Injectable({
    providedIn: 'root'
})
export class OrderNotesService {

    constructor(private _orderNotesApi: OrderNotesApi, private _orderNotesState: OrderNotesState) { }

    loadOrderNotesList(orderCode: string): void {
        this._orderNotesState.setOrderNotesListInProgress(true);
        this._orderNotesApi.getOrderNotes$(orderCode)
            .pipe(
                finalize(() => this._orderNotesState.setOrderNotesListInProgress(false))
            ).subscribe(notes => this._orderNotesState.setOrderNotesList(notes));
    }

    clearOrderNotesList(): void {
        this._orderNotesState.setOrderNotesList(null);
    }

    getOrderNotesListData$(): Observable<Note[]> {
        return this._orderNotesState.getOrderNotesList$()
            .pipe(map(notes => notes?.data));
    }

    getOrderNotesListMetadata$(): Observable<Metadata> {
        return this._orderNotesState.getOrderNotesList$()
            .pipe(
                map(notes =>
                    notes?.metadata && Object.assign(new Metadata(), notes.metadata)
                )
            );
    }

    isOrderNotesListInProgress$(): Observable<boolean> {
        return this._orderNotesState.isOrderNotesListInProgress$();
    }

    loadOrderNote(noteId: string): void {
        this._orderNotesState.setOrderNoteInProgress(true);
        this.getOrderNotesListData$()
            .pipe(
                take(1),
                finalize(() => this._orderNotesState.setOrderNoteInProgress(false))
            )
            .subscribe(notes => this._orderNotesState.setOrderNote(notes?.find(note => note.id === noteId)));
    }

    getOrderNote$(): Observable<Note> {
        return this._orderNotesState.getOrderNote$();
    }

    isOrderNoteInProgress$(): Observable<boolean> {
        return this._orderNotesState.isOrderNoteInProgress$();
    }

    saveOrderNote(customerId: string, noteId: string, putOrderNote: PutOrderNote): Observable<void> {
        this._orderNotesState.setOrderNoteInProgress(true);
        return this._orderNotesApi.putOrderNote(customerId, noteId, putOrderNote)
            .pipe(
                finalize(() => this._orderNotesState.setOrderNoteInProgress(false))
            );
    }

    clearOrderNote(): void {
        this._orderNotesState.setOrderNote(null);
    }

    createOrderNote(customerId: string, postOrderNote: PostOrderNote): Observable<number> {
        this._orderNotesState.setOrderNoteSaveOrDeleteInProgress(true);
        return this._orderNotesApi.postOrderNote(customerId, postOrderNote)
            .pipe(
                map(result => result.id),
                finalize(() => this._orderNotesState.setOrderNoteSaveOrDeleteInProgress(false))
            );
    }

    deleteOrderNote(customerId: string, noteId: string): Observable<void> {
        this._orderNotesState.setOrderNoteSaveOrDeleteInProgress(true);
        return this._orderNotesApi.deleteOrderNote(customerId, noteId)
            .pipe(
                finalize(() => this._orderNotesState.setOrderNoteSaveOrDeleteInProgress(false))
            );
    }

    isOrderNoteSaveOrDeleteInProgress$(): Observable<boolean> {
        return this._orderNotesState.isOrderNoteSaveOrDeleteInProgress$();
    }
}

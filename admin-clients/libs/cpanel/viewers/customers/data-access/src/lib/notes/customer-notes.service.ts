import { Metadata } from '@OneboxTM/utils-state';
import { Note } from '@admin-clients/cpanel/common/feature/notes';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { finalize, map, take } from 'rxjs/operators';
import { CustomerNotesApi } from './api/customer-notes.api';
import { CustomerNotesLoadCase } from './models';
import { PostCustomerNote, PutCustomerNote } from './models/customer-note.model';
import { CustomerNotesState } from './state/customer-notes.state';

@Injectable()
export class CustomerNotesService {

    #api = new CustomerNotesApi();
    #state = new CustomerNotesState();

    loadCustomerNotesList(customerId: string, entityId: string): void {
        this.#state.setCustomerNotesListInProgress(true);
        this.#api.getCustomerNotes$(customerId, entityId)
            .pipe(
                finalize(() => this.#state.setCustomerNotesListInProgress(false))
            ).subscribe(notes => this.#state.setCustomerNotesList(notes));
    }

    clearCustomerNotesList(): void {
        this.#state.setCustomerNotesList(null);
    }

    getCustomerNotesListData$(): Observable<Note[]> {
        return this.#state.getCustomerNotesList$()
            .pipe(map(notes => notes?.data));
    }

    getCustomerNotesLisMetadata$(): Observable<Metadata> {
        return this.#state.getCustomerNotesList$()
            .pipe(
                map(notes =>
                    notes?.metadata && Object.assign(new Metadata(), notes.metadata)
                )
            );
    }

    isCustomerNotesListInProgress$(): Observable<boolean> {
        return this.#state.isCustomerNotesListInProgress$();
    }

    loadCustomerNote(noteId: string): void {
        this.#state.setCustomerNoteInProgress(true);
        this.getCustomerNotesListData$()
            .pipe(
                take(1),
                finalize(() => this.#state.setCustomerNoteInProgress(false))
            )
            .subscribe(notes => this.#state.setCustomerNote(notes?.find(note => note.id === noteId)));
    }

    getCustomerNote$(): Observable<Note> {
        return this.#state.getCustomerNote$();
    }

    isCustomerNoteInProgress$(): Observable<boolean> {
        return this.#state.isCustomerNoteInProgress$();
    }

    saveCustomerNote(customerId: string, noteId: string, putCustomerNote: PutCustomerNote, entityId: string): Observable<void> {
        this.#state.setCustomerNoteInProgress(true);
        return this.#api.putCustomerNote(customerId, noteId, putCustomerNote, entityId)
            .pipe(
                finalize(() => this.#state.setCustomerNoteInProgress(false))
            );
    }

    clearCustomerNote(): void {
        this.#state.setCustomerNote(null);
    }

    createCustomerNote(customerId: string, postCustomerNote: PostCustomerNote, entityId: string): Observable<number> {
        this.#state.setCustomerNoteSaveOrDeleteInProgress(true);
        return this.#api.postCustomerNote(customerId, postCustomerNote, entityId)
            .pipe(
                map(result => result.id),
                finalize(() => this.#state.setCustomerNoteSaveOrDeleteInProgress(false))
            );
    }

    deleteCustomerNote(customerId: string, noteId: string, entityId: string): Observable<void> {
        this.#state.setCustomerNoteSaveOrDeleteInProgress(true);
        return this.#api.deleteCustomerNote(customerId, noteId, entityId)
            .pipe(
                finalize(() => this.#state.setCustomerNoteSaveOrDeleteInProgress(false))
            );
    }

    isCustomerNoteSaveOrDeleteInProgress$(): Observable<boolean> {
        return this.#state.isCustomerNoteSaveOrDeleteInProgress$();
    }

    setListDetailState(state: CustomerNotesLoadCase): void {
        this.#state.setListDetailState(state);
    }

    clearListDetailState(): void {
        this.#state.setListDetailState(null);
    }

    getListDetailState$(): Observable<CustomerNotesLoadCase> {
        return this.#state.getListDetailState$();
    }

}

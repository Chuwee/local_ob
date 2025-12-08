import { Note } from '@admin-clients/cpanel/common/feature/notes';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { GetCustomerNotesResponse } from '../models/customer-note.model';
import { CustomerNotesLoadCase } from '../models/customer-notes-load.case';

export class CustomerNotesState {
    // Notes List
    private readonly _customerNotesList = new BaseStateProp<GetCustomerNotesResponse>();
    readonly getCustomerNotesList$ = this._customerNotesList.getValueFunction();
    readonly setCustomerNotesList = this._customerNotesList.setValueFunction();
    readonly isCustomerNotesListInProgress$ = this._customerNotesList.getInProgressFunction();
    readonly setCustomerNotesListInProgress = this._customerNotesList.setInProgressFunction();
    // Note
    private readonly _customerNote = new BaseStateProp<Note>();
    readonly getCustomerNote$ = this._customerNote.getValueFunction();
    readonly setCustomerNote = this._customerNote.setValueFunction();
    readonly isCustomerNoteInProgress$ = this._customerNote.getInProgressFunction();
    readonly setCustomerNoteInProgress = this._customerNote.setInProgressFunction();
    // Note saving or deleting
    private readonly _customerNoteSaveOrDelete = new BaseStateProp<GetCustomerNotesResponse>();
    readonly isCustomerNoteSaveOrDeleteInProgress$ = this._customerNoteSaveOrDelete.getInProgressFunction();
    readonly setCustomerNoteSaveOrDeleteInProgress = this._customerNoteSaveOrDelete.setInProgressFunction();
    // ListDetailState
    private readonly _listDetailState = new BaseStateProp<CustomerNotesLoadCase>(CustomerNotesLoadCase.none);
    readonly getListDetailState$ = this._listDetailState.getValueFunction();
    readonly setListDetailState = this._listDetailState.setValueFunction();
}

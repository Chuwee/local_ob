import { Note } from '@admin-clients/cpanel/common/feature/notes';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { GetOrderNotesResponse } from '../models/get-order-notes-response.model';
import { OrderNotesLoadCase } from '../models/order-notes-load.case';

@Injectable()
export class OrderNotesState {
    private readonly _orderNotesList = new BaseStateProp<GetOrderNotesResponse>();
    private readonly _orderNote = new BaseStateProp<Note>();
    private readonly _orderNoteSaveOrDelete = new BaseStateProp<GetOrderNotesResponse>();
    private readonly _listDetailState = new BaseStateProp<OrderNotesLoadCase>(OrderNotesLoadCase.none);

    // Notes List
    readonly getOrderNotesList$ = this._orderNotesList.getValueFunction();
    readonly setOrderNotesList = this._orderNotesList.setValueFunction();
    readonly isOrderNotesListInProgress$ = this._orderNotesList.getInProgressFunction();
    readonly setOrderNotesListInProgress = this._orderNotesList.setInProgressFunction();

    // Note
    readonly getOrderNote$ = this._orderNote.getValueFunction();
    readonly setOrderNote = this._orderNote.setValueFunction();
    readonly isOrderNoteInProgress$ = this._orderNote.getInProgressFunction();
    readonly setOrderNoteInProgress = this._orderNote.setInProgressFunction();

    // Note saving or deleting
    readonly isOrderNoteSaveOrDeleteInProgress$ = this._orderNoteSaveOrDelete.getInProgressFunction();
    readonly setOrderNoteSaveOrDeleteInProgress = this._orderNoteSaveOrDelete.setInProgressFunction();

    // ListDetailState
    readonly getListDetailState$ = this._listDetailState.getValueFunction();
    readonly setListDetailState = this._listDetailState.setValueFunction();
}

import { MessageDialogService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { Observable } from 'rxjs';
import { Note } from '../models/note.model';

@Component({
    selector: 'app-notes-list',
    templateUrl: './notes-list.component.html',
    styleUrls: ['./notes-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class NotesListComponent {

    readonly dateTimeFormats = DateTimeFormats;

    @Input() selectedNoteId: string;
    @Input() totalNotes: string | number = '-';
    @Input() isLoading$: Observable<boolean>;
    @Input() notesList$: Observable<Note[]>;
    @Input() currentNote$: Observable<Note>;
    @Input() userCanWrite = true;
    @Input() showDateTime = false;
    @Input() showNoteUser = false;
    @Output() selectionChange: EventEmitter<string> = new EventEmitter();
    @Output() deleteNote: EventEmitter<void> = new EventEmitter();
    @Output() addNote: EventEmitter<void> = new EventEmitter();

    constructor(private _msgDialog: MessageDialogService) { }

    onNoteSelected(noteId: string): void {
        this.selectedNoteId = noteId;
        this.selectionChange.emit(noteId);
    }

    onDeleteNote(note: Note): void {
        this._msgDialog.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_NOTE',
            message: 'NOTES.DELETE_NOTE_WARNING',
            messageParams: { name: note.title },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        }).subscribe(success => {
            if (success) {
                this.deleteNote.emit();
            }
        });
    }

}

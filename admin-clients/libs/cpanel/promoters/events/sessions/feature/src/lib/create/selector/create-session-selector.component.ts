import { ChangeDetectionStrategy, Component, EventEmitter, Output } from '@angular/core';
import { SessionCreationType } from '../models/session-creation-type.enum';

@Component({
    selector: 'app-create-session-selector',
    templateUrl: './create-session-selector.component.html',
    styleUrls: ['./create-session-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CreateSessionSelectorComponent {
    @Output() closeDialog = new EventEmitter<void>();
    @Output() typeSelection = new EventEmitter<SessionCreationType>();
    sessionCreationType = SessionCreationType;

    constructor() { }

    close(): void {
        this.closeDialog.emit();
    }

    setCreationTypeSelection(type: SessionCreationType): void {
        this.typeSelection.emit(type);
    }
}

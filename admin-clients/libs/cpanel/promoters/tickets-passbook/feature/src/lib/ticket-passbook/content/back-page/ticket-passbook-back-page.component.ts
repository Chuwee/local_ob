import { ChangeDetectionStrategy, Component, ViewChild } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { TicketPassbookContentsEditorComponent } from '../editor/ticket-passbook-contents-editor.component';
import { TicketPassbookFieldsGroup } from '../models/ticket-passbook-fields-group.enum';

@Component({
    selector: 'app-ticket-passbook-back-page',
    templateUrl: './ticket-passbook-back-page.component.html',
    styleUrls: ['./ticket-passbook-back-page.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TicketPassbookBackPageComponent {
    @ViewChild(TicketPassbookContentsEditorComponent)
    private _editor: TicketPassbookContentsEditorComponent;

    readonly passbookFields = [TicketPassbookFieldsGroup.backFields];

    get form(): UntypedFormGroup {
        return this._editor.form;
    }

    save$(): Observable<unknown> {
        return this._editor.save$();
    }
}

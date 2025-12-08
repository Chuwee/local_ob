import { ChangeDetectionStrategy, Component, ViewChild } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { TicketPassbookContentsEditorComponent } from '../editor/ticket-passbook-contents-editor.component';
import { TicketPassbookFieldsGroup } from '../models/ticket-passbook-fields-group.enum';

@Component({
    selector: 'app-ticket-passbook-cover-page',
    templateUrl: './ticket-passbook-cover-page.component.html',
    styleUrls: ['./ticket-passbook-cover-page.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TicketPassbookCoverPageComponent implements WritingComponent {
    @ViewChild(TicketPassbookContentsEditorComponent)
    private _editor: TicketPassbookContentsEditorComponent;

    readonly passbookFields = [
        TicketPassbookFieldsGroup.header,
        TicketPassbookFieldsGroup.primaryField,
        TicketPassbookFieldsGroup.secondaryFields,
        TicketPassbookFieldsGroup.auxiliaryFields
    ];

    get form(): UntypedFormGroup {
        return this._editor.form;
    }

    save$(): Observable<unknown> {
        return this._editor.save$();
    }
}

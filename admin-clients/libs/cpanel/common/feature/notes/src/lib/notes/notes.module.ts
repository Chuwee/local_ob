import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { NewNoteDialogComponent } from './create/new-note-dialog.component';
import { NoteDetailsComponent } from './details/note-details.component';
import { NotesListComponent } from './list/notes-list.component';

const DECLARATIONS = [
    NewNoteDialogComponent,
    NoteDetailsComponent,
    NotesListComponent
];

@NgModule({
    declarations: DECLARATIONS,
    exports: DECLARATIONS,
    imports: [
        CommonModule,
        MaterialModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        FormControlErrorsComponent,
        FormContainerComponent,
        TranslatePipe,
        DateTimePipe,
        LastPathGuardListenerDirective,
        EmptyStateComponent,
        EllipsifyDirective
    ]
})
export class NotesModule { }

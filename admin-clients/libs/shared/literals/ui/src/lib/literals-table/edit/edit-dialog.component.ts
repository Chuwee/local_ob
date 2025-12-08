import { DialogSize, RichTextAreaComponent } from '@admin-clients/shared/common/ui/components';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
    MatDialogRef,
    MAT_DIALOG_DATA,
    MatDialogModule
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSlideToggleModule, MatSlideToggleChange } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { TextContent } from '../models/content.model';

export type EditLiteralDialogData = { content: TextContent; allowHtmlEditor?: boolean };
export type EditLiteralDialogResult = { content: TextContent };

enum Editor {
    html = 'html',
    string = 'string'
}

@Component({
    imports: [
        TranslatePipe,
        ReactiveFormsModule,
        RichTextAreaComponent,
        CommonModule,
        FlexLayoutModule,
        MatButtonModule,
        MatDialogModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatSlideToggleModule,
        MatTooltipModule
    ],
    selector: 'app-edit-literal-dialog',
    templateUrl: './edit-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditLiteralDialogComponent implements OnInit {

    private readonly _dialogRef = inject<MatDialogRef<EditLiteralDialogComponent, EditLiteralDialogResult>>(MatDialogRef);
    private readonly _data = inject<EditLiteralDialogData>(MAT_DIALOG_DATA);

    readonly content = this._data.content;
    readonly showHtmlEditor = this._data.allowHtmlEditor || false;
    readonly control = new FormControl<string>(this.content.value);
    readonly editors = Editor;

    editor = Editor.string;

    ngOnInit(): void {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    close(content?: TextContent): void {
        this._dialogRef.close({ content });
    }

    accept(): void {
        if (this.control.valid) {
            this.close({ key: this.content.key, value: this.control.value });
        } else {
            this.control.markAllAsTouched();
        }
    }

    toggleHtml(change: MatSlideToggleChange): void {
        this.editor = change.checked ? Editor.html : Editor.string;
    }

}

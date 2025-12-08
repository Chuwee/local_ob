import {
    LanguageBarComponent, MessageDialogService, ObMatDialogConfig, SearchTableComponent
} from '@admin-clients/shared/common/ui/components';
import { isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    EventEmitter, inject,
    Input,
    OnInit,
    Output
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, FormGroup, FormGroupDirective, ReactiveFormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, map, of } from 'rxjs';
import { EditLiteralDialogComponent, EditLiteralDialogData, EditLiteralDialogResult } from './edit/edit-dialog.component';
import { TextContent } from './models/content.model';

@Component({
    imports: [
        LanguageBarComponent,
        MatTableModule,
        MatIconButton,
        MatFormField,
        MatIcon,
        MatInput,
        MatTooltip,
        SearchTableComponent,
        CommonModule,
        TranslatePipe,
        FlexLayoutModule,
        ReactiveFormsModule
    ],
    selector: 'app-literals-table',
    templateUrl: './literals-table.component.html',
    styleUrls: ['./literals-table.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class LiteralsTableComponent implements OnInit {

    private readonly _messageDialogService = inject(MessageDialogService);
    private readonly _form = inject(FormGroupDirective);
    private readonly _dialog = inject(MatDialog);

    @Input() name!: string;
    @Input() allowHtmlEditor: boolean = false;
    @Input() languages$: Observable<string[]>;
    @Input() selectedLanguage: string;
    @Input() literals$: Observable<TextContent[]>;
    @Output() readonly changedLanguage = new EventEmitter<string>();

    readonly columns = ['key', 'value', 'actions'];
    readonly showActionsOnHover$ = isHandsetOrTablet$().pipe(map(value => !value));

    form: FormGroup<Record<string, FormControl<string>>>;

    trackByFn = (_, item: TextContent): string => item.key;

    ngOnInit(): void {
        this.form = this._form.control;
    }

    filter = (q: string, { key, value }: TextContent): boolean =>
        key.toLowerCase().includes(q.toLowerCase()) ||
        value?.toLowerCase().includes(q.toLowerCase());

    canChangeLanguage: (() => Observable<boolean>) = () => {
        if (this.form.dirty) {
            return this._messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    };

    edit({ key }: TextContent): void {
        const control = this.form.get([this.name, key]);
        const data = new ObMatDialogConfig<EditLiteralDialogData>({
            content: { key, value: control.value },
            allowHtmlEditor: this.allowHtmlEditor
        });
        this._dialog.open<EditLiteralDialogComponent, EditLiteralDialogData, EditLiteralDialogResult>(EditLiteralDialogComponent, data)
            .beforeClosed().subscribe(({ content }) => {
                if (content) {
                    control.setValue(content.value);
                    control.markAsDirty();
                }
            });
    }

}

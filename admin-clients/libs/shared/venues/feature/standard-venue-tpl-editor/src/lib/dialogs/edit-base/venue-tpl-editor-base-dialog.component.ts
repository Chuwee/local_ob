import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { noDuplicateValuesValidatorStatic, urlFriendlyValidator } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { VenueTplEditorBaseDialogData } from './venue-tpl-editor-base-dialog.data';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        FormControlErrorsComponent
    ],
    selector: 'app-venue-tpl-editor-base-dialog',
    templateUrl: './venue-tpl-editor-base-dialog.component.html',
    styleUrls: ['./venue-tpl-editor-base-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorBaseDialogComponent implements OnInit {

    @Output()
    commit = new EventEmitter<void>();

    @Output()
    cancel = new EventEmitter<void>();

    @Input()
    title: string;

    @Input()
    nameMaxLength: number;

    @Input()
    codeMaxLength: number;

    @Input()
    codeDisabled: boolean;

    @Input()
    data: VenueTplEditorBaseDialogData;

    @Input()
    create: boolean;

    @Input()
    existentValues: { name: string; code?: string }[];

    form = new FormGroup({
        name: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
        code: new FormControl('', { nonNullable: true, validators: [Validators.required] })
    });

    ngOnInit(): void {
        this.create = !this.data?.id;
        this.form.controls.name.addValidators(Validators.maxLength(this.nameMaxLength));
        this.form.controls.code.addValidators([Validators.maxLength(this.codeMaxLength), urlFriendlyValidator]);
        if (this.existentValues) {
            this.form.controls.name.addValidators(noDuplicateValuesValidatorStatic(this.existentValues.map(v => v.name)));
            this.form.controls.code.addValidators(noDuplicateValuesValidatorStatic(this.existentValues.map(v => v.code)));
        }
        if (this.codeDisabled) {
            this.form.controls.code.clearValidators();
        }
        if (this.data) {
            this.form.patchValue({ name: this.data.name, code: this.data.code });
        }
    }

    commitForm(): void {
        if (this.form.valid) {
            this.commit.emit();
        }
    }
}

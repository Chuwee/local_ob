import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import { MatDialogActions, MatDialogClose, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';

export type VersionManagementDialogResult = Partial<{
    branch: Partial<{
        type: string;
        id: string;
    }>;
    sp: Partial<{
        type: string;
        id: string;
        fm: string;
    }>;
}>;

@Component({
    selector: 'ob-version-management-dialog',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, MatRadioButton, MatRadioGroup, MatButton, MatInput, MatSelect, MatOption,
        MatDialogContent, MatDialogTitle, MatDialogActions, MatDialogClose,
        MatFormField, MatLabel, MatOption, MatDivider,
        UpperCasePipe, ReactiveFormsModule
    ],
    template: `
        <div class="options-container dialog-msg-container" [formGroup]="form"> 
            <h2 mat-dialog-title>
                <span class="secondary title">{{'TITLES.WARNING_LOW_ENV' | translate | uppercase}}</span>
            </h2>
            <mat-dialog-content>
                <div class="option-container branch" formGroupName="branch">
                    <mat-form-field name="branch-type" appearance="outline" subscriptSizing="dynamic"  
                        class="ob-form-field field-with-label no-margin no-grid-spacing branch-type">
                        <mat-label>Client</mat-label>
                        <mat-select formControlName="type">
                            <mat-option value="feature">feature</mat-option>
                            <mat-option value="bugfix">bugfix</mat-option>
                        </mat-select>
                    </mat-form-field>
                    <mat-form-field name="branch-id" appearance="outline" subscriptSizing="dynamic" 
                        class="ob-form-field field-with-label no-margin no-grid-spacing branch-id">
                        <input formControlName="id" matInput placeholder="OB-00000">
                    </mat-form-field>
                </div>  
                <mat-divider></mat-divider>
                <div class="option-container sp" formGroupName="sp">
                    <mat-form-field name="sp-type" appearance="outline" subscriptSizing="dynamic" 
                        class="ob-form-field field-with-label no-margin no-grid-spacing sp-type">
                        <mat-label>Service Preview</mat-label>
                        <mat-select formControlName="type">
                            <mat-option value="feature">feature</mat-option>
                            <mat-option value="bugfix">bugfix</mat-option>
                        </mat-select>
                    </mat-form-field>
                    <mat-form-field name="sp-id" appearance="outline" subscriptSizing="dynamic" 
                        class="ob-form-field field-with-label no-margin no-grid-spacing sp-id">
                        <input matInput formControlName="id" placeholder="OB-00000">
                    </mat-form-field>
                </div>
                <div class="option-container fm" formGroupName="sp">
                    <mat-radio-group name="sp-fm" formControlName="fm">
                        <mat-radio-button value="LAZY">LAZY</mat-radio-button>
                        <mat-radio-button value="NORMAL">NORMAL</mat-radio-button>
                        <mat-radio-button value="STRICT">STRICT</mat-radio-button>
                    </mat-radio-group>
                </div>
            </mat-dialog-content>
            <mat-dialog-actions align="end">
                <button mat-button class="ob-button" mat-dialog-close>
                    {{'FORMS.ACTIONS.CANCEL' | translate}}
                </button>
                <button mat-flat-button color="primary" class="ob-button" 
                    type="submit" [disabled]="form.invalid" (click)="submit()">
                    {{'FORMS.ACTIONS.SAVE' | translate}}
                </button>
            </mat-dialog-actions>
        </div>
    `,
    styles: [`
        .option-container {
            display: flex;
            gap: 8px;
        }
        mat-radio-group {
            display: flex;
            gap: 8px;
            margin-top: 16px;
        }
        mat-divider {
            margin: 16px 0 21px;
        }
    `]
})
export class VersionManagementDialogComponent {

    readonly #env = inject(ENVIRONMENT_TOKEN);
    readonly #branch = this.#env?.branch !== 'default' ? this.#env.branch : undefined;
    readonly #ref = inject(MatDialogRef);
    readonly #fb = inject(FormBuilder);

    branch = splitBranch(this.#branch);
    sp = splitBranch(sessionStorage.getItem('sp'));
    fm = sessionStorage.getItem('fm');

    readonly form = this.#fb.group({
        branch: this.#fb.group({
            type: this.#fb.control<string>(this.branch.type || 'feature'),
            id: this.#fb.control<string>(this.branch.id)
        }),
        sp: this.#fb.group({
            type: this.#fb.control<string>(this.sp.type || 'feature'),
            id: this.#fb.control<string>(this.sp.id),
            fm: this.#fb.control<string>(this.fm || 'NORMAL')
        })
    });

    constructor() {
        this.#ref.disableClose = false;
    }

    submit(): void {
        this.#ref.close(this.form.value);
    }
}

const splitBranch = (value: string): { type: string; id: string } => {
    const [type, ...id] = value?.split('-') || [];
    return { type, id: id?.join('-') };
};
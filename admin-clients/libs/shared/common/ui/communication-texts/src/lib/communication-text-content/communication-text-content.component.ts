/* eslint-disable @typescript-eslint/dot-notation */
import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { CommunicationContentTextType, CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { shareReplay, takeUntil, tap } from 'rxjs/operators';
import { FormInputType } from '../models/FormInputType';

@Component({
    selector: 'app-communication-text-content',
    templateUrl: './communication-text-content.component.html',
    styleUrls: ['./communication-text-content.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TabsMenuComponent,
        TabDirective,
        ReactiveFormsModule,
        TranslatePipe,
        NgFor,
        NgIf,
        MatError,
        MatFormField,
        MatInput,
        MatLabel,
        AsyncPipe,
        FormControlErrorsComponent,
        FlexLayoutModule
    ]
})
export class CommunicationTextContentComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _fb = inject(FormBuilder);

    @ViewChild(TabsMenuComponent) private _languageTabs: TabsMenuComponent;

    @Input() form: FormInputType;
    @Input() restrictions: { [x: string]: number };
    @Input() languages$: Observable<string[]>;
    @Input() description = true; // Pasado como 'false' hace que solo se muestre el campo 'name'

    ngOnInit(): void {
        this.languages$ = this.languages$
            .pipe(
                tap(languages => {
                    const languagesControls = this._fb.group({});
                    languages.forEach(language => {
                        languagesControls.addControl(language, this._fb.group({
                            name: [null, [
                                Validators.required,
                                Validators.minLength(this.restrictions['minComElemNameLength']),
                                Validators.maxLength(this.restrictions['maxComElemNameLength'])
                            ]],
                            description: [null, [
                                Validators.minLength(this.restrictions['minComElemDescriptionLength']),
                                Validators.maxLength(this.restrictions['maxComElemDescriptionLength'])
                            ]]
                        }));
                    });
                    this.form.setControl('contents', languagesControls);
                    this.form.controls.contents.markAsPristine();
                }),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    showValidationErrors(): void {
        // change langugage tab if invalid fields found
        const form = this.form.controls.contents;
        if (form?.invalid) {
            this._languageTabs.goToInvalidCtrlTab();
        }
    }

    getContents(): CommunicationTextContent[] {
        // if names are disabled gets them anyway
        const data = this.form.getRawValue().contents;
        const contents: CommunicationTextContent[] = [];
        Object.keys(data).forEach(lang => {
            if (data[lang].name) {
                contents.push({
                    type: CommunicationContentTextType.name,
                    value: data[lang].name,
                    language: lang
                });
            }
            if (this.description && this.form.get(['contents', lang, 'description']).dirty) {
                contents.push({
                    type: CommunicationContentTextType.description,
                    value: data[lang].description,
                    language: lang
                });
            }
        });
        return contents;
    }
}

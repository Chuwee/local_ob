import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { b2bClientFieldsRestrictions } from '../../models/b2b-client-fields-restrictions';

@Component({
    selector: 'app-b2b-client-user-creation',
    templateUrl: './b2b-client-user-creation.component.html',
    styleUrls: ['./b2b-client-user-creation.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bClientUserCreationComponent implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();

    @Input() mainForm: FormGroup;
    @Input() refreshChanges$: Observable<void>;
    userCreationform: FormGroup;

    constructor(
        private _fb: FormBuilder,
        private _ref: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        if (!this.mainForm.get('userCreation')) {
            this.userCreationform = this._fb.group({
                userCreationName: [
                    null,
                    [Validators.required, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientUserCreationNameMaxLength)]
                ],
                accessEmail: [
                    null,
                    [Validators.required, Validators.email, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientAccessEmailMaxLength)]
                ],
                credentialReceptionEmail: [
                    null,
                    [
                        Validators.required, Validators.email,
                        Validators.maxLength(b2bClientFieldsRestrictions.b2bClientCredentialEmailMaxLength)
                    ]
                ]
            });
            this.mainForm.addControl('userCreation', this.userCreationform);
        } else {
            this.userCreationform = this.mainForm.get('userCreation') as FormGroup;
        }
        this.updateFormValues();
    }

    ngAfterViewInit(): void {
        this.refreshChanges$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                this._ref.detectChanges();
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private updateFormValues(): void {
        if (this.mainForm.get('basicData')) {
            this.userCreationform.get('userCreationName').patchValue(this.mainForm.get('basicData').get('name')?.value);
        }
        if (this.mainForm.get('contactData')) {
            this.userCreationform.get('accessEmail').patchValue(this.mainForm.get('contactData').get('email')?.value);
            this.userCreationform.get('credentialReceptionEmail').patchValue(this.mainForm.get('contactData').get('email')?.value);
        }
    }

}

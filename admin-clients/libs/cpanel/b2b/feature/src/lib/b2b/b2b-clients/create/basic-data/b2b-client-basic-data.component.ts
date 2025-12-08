import { B2bClientCategoryType, B2bService } from '@admin-clients/cpanel/b2b/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { B2bClientTaxIdValidator } from '../../../validators/b2b-client-tax-id-validator';
import { b2bClientFieldsRestrictions } from '../../models/b2b-client-fields-restrictions';

@Component({
    selector: 'app-b2b-client-basic-data',
    templateUrl: './b2b-client-basic-data.component.html',
    styleUrls: ['./b2b-client-basic-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bClientBasicDataComponent implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();

    @Input() mainForm: FormGroup;
    @Input() refreshChanges$: Observable<void>;
    @Input() newB2bClientDialogData: { entityId: number };
    basicDataform: FormGroup;

    readonly categoryTypes = B2bClientCategoryType;

    constructor(
        private _fb: FormBuilder,
        private _authSrv: AuthenticationService,
        private _ref: ChangeDetectorRef,
        private _b2bSrv: B2bService
    ) { }

    ngOnInit(): void {
        this._authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(isOperator => {
                const taxIdAsyncValidator = isOperator
                    ? [B2bClientTaxIdValidator.createValidator(this._b2bSrv, this.newB2bClientDialogData.entityId)]
                    : [B2bClientTaxIdValidator.createValidator(this._b2bSrv)];

                if (!this.mainForm.get('basicData')) {
                    this.basicDataform = this._fb.group({
                        taxId: [
                            null,
                            [Validators.required, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientTaxIdMaxLength)],
                            taxIdAsyncValidator
                        ],
                        categoryType: [null, Validators.required],
                        name: [null, [Validators.required, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientNameMaxLength)]],
                        businessName: [
                            null,
                            [Validators.required, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientBusinessNameMaxLength)]
                        ],
                        iataCode: [null, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientIataCodeMaxLength)],
                        description: [null, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientDescriptionMaxLength)],
                        keywords: [null, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientKeywordsMaxLength)]
                    });
                    this.mainForm.addControl('basicData', this.basicDataform);
                } else {
                    this.basicDataform = this.mainForm.get('basicData') as FormGroup;
                }
            });
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

}

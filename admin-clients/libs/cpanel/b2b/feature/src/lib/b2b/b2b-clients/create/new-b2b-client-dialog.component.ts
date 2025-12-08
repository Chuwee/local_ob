import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { B2bService, PostB2bClient } from '@admin-clients/cpanel/b2b/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { CountriesService, RegionsService } from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, ElementRef, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-new-b2b-client-dialog',
    templateUrl: './new-b2b-client-dialog.component.html',
    styleUrls: ['./new-b2b-client-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class NewB2bClientDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    @ViewChild(WizardBarComponent, { static: true })
    private _wizardBar: WizardBarComponent;

    private _refreshChanges = new Subject<void>();
    private _isOperator: boolean;

    newB2bClientForm: FormGroup;
    currentStep = 0;
    refreshChanges$ = this._refreshChanges.asObservable();
    isInProgress$: Observable<boolean>;

    steps = ['B2B_CLIENTS.NEW_B2B_CLIENT.BASIC_DATA', 'B2B_CLIENTS.NEW_B2B_CLIENT.CONTACT_DATA',
        'B2B_CLIENTS.NEW_B2B_CLIENT.USER_CREATION'];

    constructor(
        private _dialogRef: MatDialogRef<NewB2bClientDialogComponent, number>,
        private _fb: FormBuilder,
        private _elemRef: ElementRef,
        private _countriesService: CountriesService,
        private _regionsService: RegionsService,
        private _b2bSrv: B2bService,
        private _authSrv: AuthenticationService,
        @Inject(MAT_DIALOG_DATA) public data: { entityId: number }
    ) {
        this._dialogRef.addPanelClass(DialogSize.LARGE);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.newB2bClientForm = this._fb.group({});
        this._countriesService.loadCountries({ sort: true });
        this._regionsService.loadRegions();
        this._authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(isOperator => this._isOperator = isOperator);
        this.isInProgress$ = booleanOrMerge([
            this._countriesService.isCountriesLoading$(),
            this._regionsService.isRegionsLoading$(),
            this._b2bSrv.isB2bClientInProgress$()
        ]);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._countriesService.clearCountries();
        this._regionsService.clearRegions();
    }

    close(b2bClientId: number = null): void {
        this._dialogRef.close(b2bClientId);
    }

    goToStep(step: number): void {
        this.currentStep = step;
        this._wizardBar.setActiveStep(step);
    }

    nextStep(): void {
        if (this.currentStep < this.steps.length - 1) {
            if ((this.newB2bClientForm.get('basicData')?.valid && this.currentStep === 0)
                || this.newB2bClientForm.get('contactData')?.valid && this.currentStep === 1) {
                this.goToStep(this.currentStep + 1);
            } else {
                this.newB2bClientForm.markAllAsTouched();
                this._refreshChanges.next();
                scrollIntoFirstInvalidFieldOrErrorMsg(this._elemRef.nativeElement);
            }
        } else {
            if (this.newB2bClientForm.valid) {
                this.createNewB2bClient();
            } else {
                this.newB2bClientForm.markAllAsTouched();
                this._refreshChanges.next();
                scrollIntoFirstInvalidFieldOrErrorMsg(this._elemRef.nativeElement);
            }
        }
    }

    private createNewB2bClient(): void {
        const basicData = this.newB2bClientForm.get('basicData').value;
        const contactData = this.newB2bClientForm.get('contactData').value;
        const userCreation = this.newB2bClientForm.get('userCreation').value;

        const request: PostB2bClient = {
            name: basicData.name,
            category_type: basicData.categoryType,
            tax_id: basicData.taxId,
            iata_code: basicData.iataCode ? basicData.iataCode : undefined,
            business_name: basicData.businessName,
            description: basicData.description ? basicData.description : undefined,
            keywords: basicData.keywords ? this.buildKeywordsArray(basicData.keywords) : undefined,
            country: { code: contactData.country },
            country_subdivision: contactData.countrySubdivision
                ? { code: contactData.countrySubdivision }
                : { code: 'NOT_DEF' },
            contact_data: {
                contact_person: contactData.contactPerson,
                address: contactData.address ? contactData.address : undefined,
                email: contactData.email,
                phone: contactData.phone
            },
            user: {
                username: userCreation.accessEmail,
                name: userCreation.userCreationName,
                email: userCreation.credentialReceptionEmail
            }
        };
        if (this._isOperator) {
            request.entity_id = this.data.entityId;
        }

        this._b2bSrv.createB2bClient(request)
            .subscribe(b2bClientId => this.close(b2bClientId));
    }

    private buildKeywordsArray(keywords: string): string[] {
        const keywordsArr = keywords.split(',').map(keyword => keyword.trim());
        return keywordsArr;
    }

}

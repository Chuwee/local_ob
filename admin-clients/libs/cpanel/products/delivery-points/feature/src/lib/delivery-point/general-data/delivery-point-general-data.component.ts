import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    DeliveryPoint, ProductsDeliveryPointsService, PutProductDeliveryPoint
} from '@admin-clients/cpanel/products/delivery-points/data-access';
import { CountriesService, RegionsService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChildren, inject } from '@angular/core';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, map, shareReplay, takeUntil, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-delivery-point-general-data',
    imports: [CommonModule, EllipsifyDirective, FlexLayoutModule, FlexModule, TranslatePipe, SelectSearchComponent,
        FormContainerComponent, MaterialModule, FormControlErrorsComponent, ReactiveFormsModule],
    templateUrl: './delivery-point-general-data.component.html',
    styleUrls: ['./delivery-point-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DeliveryPointGeneralDataComponent implements OnInit, OnDestroy {
    @ViewChildren(MatExpansionPanel) private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    private readonly _deliveryPointSrv = inject(ProductsDeliveryPointsService);
    private readonly _countriesService = inject(CountriesService);
    private readonly _regionsService = inject(RegionsService);
    private readonly _ephemeralMessageSrv = inject(EphemeralMessageService);
    private readonly _fb = inject(FormBuilder);
    private readonly _auth = inject(AuthenticationService);

    private readonly _onDestroy = new Subject<void>();
    private _deliveryPointId: number;

    readonly form = this._fb.group({
        name: ['', [Validators.required]],
        country: ['', [Validators.required]],
        country_subdivision: ['', [Validators.required]],
        city: ['', [Validators.required]],
        address: ['', [Validators.required]],
        zip_code: ['', Validators.pattern('[0-9]*')],
        notes: ['']
    });

    readonly deliveryPoint$ = this._deliveryPointSrv.deliveryPoint.get$().pipe(tap(deliveryPoint => this.patchFormValues(deliveryPoint)));
    readonly countries$ = this._countriesService.getCountries$();
    readonly regions$ = this._regionsService.getRegions$();
    readonly isLoadingOrSaving$ = this._deliveryPointSrv.deliveryPoint.inProgress$();
    readonly canWrite$ = this._auth.getLoggedUser$().pipe(
        map(user => AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR, UserRoles.EVN_MGR])),
        tap(canWrite => {
            if (!canWrite) {
                this.form.disable({ emitEvent: false });
            }
        }),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    ngOnInit(): void {
        this._countriesService.loadCountries();
        this._regionsService.loadRegions();
        this.form.get('country').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(val => val && this._regionsService.loadSystemRegions(val));
    }

    ngOnDestroy(): void {
        this._countriesService.clearCountries();
        this._regionsService.clearRegions();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cancel(): void {
        this.reloadModels();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const putBody = this.getPutBody();
            return this._deliveryPointSrv.deliveryPoint.upload(this._deliveryPointId, putBody)
                .pipe(tap(() => this._ephemeralMessageSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            //SetValue in order to rerender child components with form fields in order to show input errors.
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    private reloadModels(): void {
        this._deliveryPointSrv.deliveryPoint.load(this._deliveryPointId);
        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

    private patchFormValues(deliveryPoint: DeliveryPoint): void {
        this._deliveryPointId = deliveryPoint.id;
        this.form.patchValue({
            name: deliveryPoint.name,
            country: deliveryPoint.location.country.code,
            country_subdivision: deliveryPoint.location.country_subdivision.code,
            city: deliveryPoint.location.city,
            address: deliveryPoint.location.address,
            zip_code: deliveryPoint.location.zip_code,
            notes: deliveryPoint.location.notes
        });
    }

    private getPutBody(): PutProductDeliveryPoint {
        return {
            name: this.form.value.name,
            location: {
                address: this.form.value.address,
                city: this.form.value.city,
                country: this.form.value.country,
                country_subdivision: this.form.value.country_subdivision,
                notes: this.form.value.notes,
                zip_code: this.form.value.zip_code
            }
        };
    }
}

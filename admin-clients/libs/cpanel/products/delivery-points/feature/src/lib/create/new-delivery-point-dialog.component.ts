import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { PostDeliveryPoint, ProductsDeliveryPointsService } from '@admin-clients/cpanel/products/delivery-points/data-access';
import {
    CountriesService, EntitiesBaseService, EntitiesFilterFields, RegionsService
} from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { LayoutModule } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject, combineLatest, filter, map, shareReplay, switchMap, takeUntil, tap } from 'rxjs';

@Component({
    selector: 'app-new-delivery-point-dialog',
    imports: [
        SelectSearchComponent,
        TranslatePipe,
        ReactiveFormsModule,
        CommonModule,
        LayoutModule,
        FlexLayoutModule,
        MaterialModule,
        FormControlErrorsComponent
    ],
    templateUrl: './new-delivery-point-dialog.component.html',
    styleUrls: [],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewDeliveryPointDialogComponent implements OnInit, OnDestroy {

    private readonly _auth = inject(AuthenticationService);
    private readonly _fb = inject(FormBuilder);
    private readonly _dialogRef = inject(MatDialogRef<NewDeliveryPointDialogComponent>);
    private readonly _entitiesService = inject(EntitiesBaseService);
    private readonly _elemRef = inject(ElementRef);
    private readonly _ephemeralMsgService = inject(EphemeralMessageService);
    private readonly _countriesService = inject(CountriesService);
    private readonly _regionsService = inject(RegionsService);
    private readonly _deliveryPointSrv = inject(ProductsDeliveryPointsService);

    private _onDestroy = new Subject<void>();
    readonly canSelectEntity$ = this._auth.canReadMultipleEntities$();

    countries$ = this._countriesService.getCountries$();
    regions$ = this._regionsService.getRegions$();

    readonly entities$ = combineLatest([
        this._auth.getLoggedUser$().pipe(filter(Boolean)),
        this.canSelectEntity$
    ]).pipe(
        switchMap(([user, canSelectEntity]) => {
            if (canSelectEntity) {
                this._entitiesService.entityList.load({
                    limit: 999,
                    sort: 'name:asc',
                    fields: [
                        EntitiesFilterFields.name,
                        EntitiesFilterFields.allowActivityEvents,
                        EntitiesFilterFields.allowAvetIntegration
                    ],
                    type: 'EVENT_ENTITY'
                });
                return this._entitiesService.entityList.getData$();
            } else {
                this._entitiesService.loadEntity(user.entity.id);
                return this._entitiesService.getEntity$().pipe(
                    filter(Boolean),
                    tap(entity => this.form.controls.entity.patchValue(entity.id)),
                    map(entity => [entity])
                );
            }
        }),
        takeUntil(this._onDestroy),
        shareReplay(1)
    );

    readonly isInProgress$ = booleanOrMerge([
        this._entitiesService.entityList.inProgress$(),
        this._entitiesService.isEntityLoading$(),
        this._countriesService.isCountriesLoading$(),
        this._regionsService.isRegionsLoading$()
    ]);

    readonly form = this._fb.group({
        entity: [null as number, Validators.required],
        name: ['', [Validators.required]],
        country: ['', [Validators.required]],
        country_subdivision: ['', [Validators.required]],
        city: ['', [Validators.required]],
        address: ['', [Validators.required]]
    });

    ngOnInit(): void {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this.entities$.subscribe();
        this.form.get('country_subdivision').disable();
        this.initFormHandlers();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(productId: number = null): void {
        this._dialogRef.close(productId);
    }

    createDeliveryPoint(): void {
        if (this.form.valid) {
            const reqBody = this.mapFormValuesToPost();
            this._deliveryPointSrv.deliveryPoint.create(reqBody).subscribe(res => {
                this._ephemeralMsgService.showCreateSuccess();
                this.close(res.id);
            });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this._elemRef.nativeElement);
        }
    }

    private initFormHandlers(): void {
        this.form.get('country').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(country => {
                if (country) {
                    this.form.get('country_subdivision').enable();
                    this._regionsService.loadSystemRegions(country);
                    //Delete country subdivision value if we change country value
                    this.form.get('country_subdivision').setValue(null);
                } else {
                    this.form.get('country_subdivision').disable();
                }
            });
    }

    private mapFormValuesToPost(): PostDeliveryPoint {
        const { address, city, country, entity, name } = this.form.value;
        return {
            entity_id: entity,
            name,
            location: {
                address,
                city,
                country,
                country_subdivision: this.form.value.country_subdivision
            }
        };
    }

}

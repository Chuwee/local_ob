import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Metadata } from '@OneboxTM/utils-state';
import { CreatePackItemRequest, Pack, PackItem, PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import { DeliveryPoint, GetProductsDeliveryPointsRequest, productsDeliveryPointsProviders, ProductsDeliveryPointsService } from '@admin-clients/cpanel/products/delivery-points/data-access';
import { Product, productsProviders, ProductsService, ProductStatus, ProductType, ProductVariant } from '@admin-clients/cpanel/products/my-products/data-access';
import { Event, eventsProviders } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    eventSessionsProviders, EventSessionsService, GetSessionsRequest, Session, SessionsFilterFields, SessionStatus
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import {
    SalesRequestsEventStatus, SalesRequestsService, SalesRequestsStatus
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import {
    DialogSize, EphemeralMessageService, SelectSearchComponent, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplatePriceType, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, viewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Subject, take, takeUntil, tap, throwError } from 'rxjs';

type ProductForm = FormGroup<{
    product: FormControl<Product>;
    variant?: FormControl<ProductVariant>;
    deliveryPoint?: FormControl<DeliveryPoint>;
    sharedBarcode?: FormControl<boolean>;
}>;

type SessionForm = FormGroup<{
    event: FormControl<Event>;
    session: FormControl<Session>;
    priceType?: FormControl<VenueTemplatePriceType>;
}>;

type CreationTypes = 'SESSION' | 'PRODUCT';

type AddPackElementForm = FormGroup<{
    type: FormControl<CreationTypes>;
    displayItemInChannels: FormControl<boolean>;
    sessionForm?: SessionForm;
    productForm?: ProductForm;
}>;

@Component({
    selector: 'app-create-pack-elements-dialog',
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule, FormControlErrorsComponent, FlexLayoutModule,
        SelectSearchComponent, WizardBarComponent, DateTimePipe, NgTemplateOutlet, AsyncPipe, SelectServerSearchComponent,
        EllipsifyDirective
    ],
    templateUrl: './create-pack-elements-dialog.component.html',
    styleUrl: './create-pack-elements-dialog.component.scss',
    providers: [eventsProviders, eventSessionsProviders, productsProviders, productsDeliveryPointsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreatePackElemetsDialogComponent {

    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<CreatePackElemetsDialogComponent>);
    readonly #salesRequestSrv = inject(SalesRequestsService);
    readonly #eventSessionsService = inject(EventSessionsService);
    readonly #packSrv = inject(PacksService);
    readonly #venueTemplateSrv = inject(VenueTemplatesService);
    readonly #productsSrv = inject(ProductsService);
    readonly #productDeliveryPointsSrv = inject(ProductsDeliveryPointsService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #data = inject<{
        channelId: number;
        packType: Pack['type'];
        packId: number;
        mainSession: PackItem;
        entityId: number;
        currentItemsIds: number[];
    }>(MAT_DIALOG_DATA);

    readonly #PAGE_LIMIT = 20;

    readonly #formClean$ = new Subject();
    private readonly _wizardBar = viewChild(WizardBarComponent);
    readonly packType = this.#data.packType;
    readonly mainSession = this.#data.mainSession;
    readonly productDeliveryPointsReq = new GetProductsDeliveryPointsRequest();
    readonly productDeliveryPoints$ = this.#productDeliveryPointsSrv.productsDeliveryPointsList.getData$();
    readonly products$ = this.#productsSrv.productsList.getData$().pipe(
        filter(Boolean),
        map(products => products.filter(product => !this.#data.currentItemsIds.includes(product.product_id)))
    );

    readonly productVariants$ = this.#productsSrv.product.variants.getData$().pipe(
        filter(Boolean),
        tap(variants => (variants.length === 1) && this.form.get('productForm.variant').setValue(variants[0]))
    );

    readonly events$ = this.#salesRequestSrv.getSalesRequestsListData$().pipe(
        filter(Boolean),
        tap(() => this.form.get('sessionForm.session').disable()),
        map(salesRequests => salesRequests.map(salesRequest => salesRequest.event))
    );

    readonly sessions$ = this.#eventSessionsService.sessionList.get$().pipe(
        filter(Boolean),
        map(sessions => sessions.data.filter(session => !this.#data.currentItemsIds.includes(session.id))),
        tap(sessions => {
            if (sessions.length === 1) {
                this.form.get('sessionForm.session').setValue(sessions[0]);
            }
        })
    );

    readonly sessionPriceTypes$ = this.#venueTemplateSrv.getVenueTemplatePriceTypes$()
        .pipe(
            filter(Boolean),
            tap(() => {
                if (!this.hasAutomaticSessionSameVenuTmplAsMainSession) {
                    this.form.controls.sessionForm.controls.priceType.reset({ value: null as VenueTemplatePriceType, disabled: false });
                    this.form.controls.sessionForm.controls.priceType.addValidators([Validators.required]);
                }
            })
        );

    readonly moreEventsAvailable$ = this.#salesRequestSrv.getSalesRequestsListMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    readonly moreSessionsAvailable$ = this.#eventSessionsService.sessionList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    readonly $loading = toSignal(booleanOrMerge([
        this.#salesRequestSrv.isSalesRequestsListLoading$(),
        this.#eventSessionsService.sessionList.inProgress$(),
        this.#productsSrv.productsList.loading$(),
        this.#productsSrv.product.variants.loading$(),
        this.#productDeliveryPointsSrv.productsDeliveryPointsList.loading$(),
        this.#packSrv.packItems.loading$()
    ]));

    readonly creationTypes = {
        session: 'SESSION',
        product: 'PRODUCT'
    };

    readonly form: AddPackElementForm = this.#fb.group({
        type: [this.creationTypes.session as CreationTypes, [Validators.required]],
        displayItemInChannels: [true]
    });

    readonly dateTimeFormats = DateTimeFormats;

    currentStep = 1;
    subFormName = null as string;
    hasAutomaticSessionSameVenuTmplAsMainSession = true;

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
    }

    setStep1(): void {
        this._wizardBar()?.previousStep();
        this.currentStep = 1;
    }

    setStep2(): void {
        this.#cleanStep2FormGroups();
        if (this.form.value.type === this.creationTypes.session) this.#generateSessionFormGroup();
        else this.#generateProductFormGroup();
        this._wizardBar()?.nextStep();
        this.currentStep = 2;
    }

    close(packItemId: number = null): void {
        this.#dialogRef.close(packItemId);
    }

    save(): void {
        if (this.form.valid) {
            const reqBody = this.#generateRequestBody();
            this.#packSrv.packItems.create(this.#data.channelId, this.#data.packId, [reqBody])
                .subscribe(() => {
                    this.#ephemeralMessageService.showSuccess({ msgKey: 'CHANNELS.PACKS.ELEMENTS.CREATE.CREATE_SUCCESS' });
                    this.close(reqBody.item_id);
                });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            throwError(() => 'invalid form');
        }
    }

    #loadEventsList(): void {
        this.#salesRequestSrv.loadSalesRequestsList({
            limit: 20, offset: 0, status: [SalesRequestsStatus.accepted],
            event_status: [SalesRequestsEventStatus.ready, SalesRequestsEventStatus.planned, SalesRequestsEventStatus.inProgramming],
            channel: this.#data.channelId, sort: 'name:asc'
        });
    }

    #clearEventsList(): void {
        this.#salesRequestSrv.clearSalesRequestsList();
    }

    #loadSessions(eventId): void {
        this.#eventSessionsService.sessionList.load(eventId,
            {
                sort: 'name:asc',
                fields: [SessionsFilterFields.name, SessionsFilterFields.venueTemplateId, SessionsFilterFields.venueTemplateName,
                SessionsFilterFields.startDate, SessionsFilterFields.settingsSmartbookingRelatedSession, SessionsFilterFields.venueTplType],
                status: [SessionStatus.preview, SessionStatus.ready, SessionStatus.scheduled]
            }
        );
    }

    #clearSessions(): void {
        this.#eventSessionsService.sessionList.clear();
    }

    #loadProducts(): void {
        this.#productsSrv.productsList.load({
            limit: 20, offset: 0, status: ProductStatus.active,
            entityId: this.#data.entityId, sort: 'name:asc'
        });
    }

    #clearProducts(): void {
        this.#productsSrv.productsList.clear();
    }

    #loadProductVariants(productId: number): void {
        this.#productsSrv.product.variants.load(productId);
    }

    #clearProductVariants(): void {
        this.#productsSrv.product.variants.clear();
    }

    #loadProductDeliveryPoints(): void {
        this.#productDeliveryPointsSrv.productsDeliveryPointsList.load({
            ...this.productDeliveryPointsReq,
            entityId: this.#data.entityId
        });
    }

    #clearProductDeliveryPoints(): void {
        this.#productDeliveryPointsSrv.productsDeliveryPointsList.clear();
    }

    #loadSessionPriceTypes(venueTemplateId: number): void {
        this.#venueTemplateSrv.loadVenueTemplatePriceTypes(venueTemplateId);
    }

    #generateProductFormGroup(): void {
        this.subFormName = 'productForm';
        this.#loadProducts();
        this.#loadProductDeliveryPoints();
        const productForm: ProductForm = this.#fb.group({
            product: [null as Product, Validators.required]
        });

        if (this.packType === 'AUTOMATIC') {
            productForm.addControl('deliveryPoint', this.#fb.control(null, Validators.required));
            productForm.addControl('sharedBarcode', this.#fb.control(null as boolean, Validators.required));
        }

        this.form.addControl('productForm', productForm);
        if (this.packType === 'AUTOMATIC') this.#addProductChangeListener();
    }

    #generateSessionFormGroup(): void {
        this.subFormName = 'sessionForm';
        this.#loadEventsList();
        const sessionForm: SessionForm = this.#fb.group({
            event: [null as Event, Validators.required],
            session: [null as Session, Validators.required]
        });
        if (this.packType === 'AUTOMATIC') {
            sessionForm.addControl('priceType', this.#fb.control(
                { value: null as VenueTemplatePriceType, disabled: true }, []));
        }
        this.form.addControl('sessionForm', sessionForm);
        this.#addEventChangeListener();
    }

    #cleanStep2FormGroups(): void {
        this.#formClean$.next(null);
        this.#formClean$.complete();
        if (this.form.controls.sessionForm) {
            this.#clearEventsList();
            this.#clearSessions();
            this.form.removeControl('sessionForm');
        }
        if (this.form.controls.productForm) {
            this.#clearProducts();
            this.#clearProductVariants();
            this.#clearProductDeliveryPoints();
            this.form.removeControl('productForm');
        }
        this.subFormName = null;
        this.form.controls.displayItemInChannels.reset(true);
    }

    #addEventChangeListener(): void {
        this.form.get('sessionForm.event').valueChanges
            .pipe(
                takeUntil(this.#formClean$),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(event => {
                if (!event) return;
                this.form.get('sessionForm.session').setValue(null);
                this.form.get('sessionForm.session').enable();
                this.#loadSessions(event.id);
                this.#addSessionChangeListener();
            });
    }

    #addSessionChangeListener(): void {
        this.form.get('sessionForm.session').valueChanges.pipe(
            filter(Boolean),
            takeUntil(this.form.get('sessionForm.event').valueChanges),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(session => {
            this.hasAutomaticSessionSameVenuTmplAsMainSession =
                session?.venue_template?.id === this.mainSession?.session_data?.venue_template?.id;
            if (this.packType === 'AUTOMATIC') {
                this.#loadSessionPriceTypes(session.venue_template.id);
                if (this.hasAutomaticSessionSameVenuTmplAsMainSession) this.sessionPriceTypes$.pipe(take(1)).subscribe();
            }
        });
    }

    #addProductChangeListener(): void {
        this.form.get('productForm.product').valueChanges
            .pipe(
                takeUntil(this.#formClean$),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(product => {
                if (product.product_type === ProductType.variant) {
                    (this.form.get('productForm') as FormGroup).addControl('variant', this.#fb.control(null, Validators.required));
                    this.#loadProductVariants(product.product_id);
                } else if (this.form.get('productForm.variant')) {
                    (this.form.get('productForm') as FormGroup).removeControl('variant');
                }
            });
    }

    #generateRequestBody(): CreatePackItemRequest {
        const formRaw = this.form.getRawValue();
        const reqBody: CreatePackItemRequest = {
            item_id: formRaw.type === this.creationTypes.session ? formRaw.sessionForm.session.id : formRaw.productForm.product.product_id,
            type: formRaw.type,
            display_item_in_channels: formRaw.displayItemInChannels
        };

        if (formRaw.type === this.creationTypes.session && this.packType === 'AUTOMATIC'
            && !this.hasAutomaticSessionSameVenuTmplAsMainSession) {
            reqBody.price_type_id = formRaw.sessionForm.priceType.id || this.mainSession.session_data.price_type.id;
        }

        if (formRaw.type === this.creationTypes.product) {
            if (this.packType === 'AUTOMATIC') {
                reqBody.shared_barcode = formRaw.productForm.sharedBarcode;
                reqBody.delivery_point_id = formRaw.productForm.deliveryPoint.id;
                if (formRaw.productForm.product.product_type === ProductType.variant) {
                    reqBody.variant_id = formRaw.productForm.variant.id;
                }
            }
        }

        return reqBody;
    }

    loadEvents(q: string, next = false): void {
        this.#salesRequestSrv.loadServerSearchSalesRequestList({
            limit: this.#PAGE_LIMIT,
            sort: 'name:asc',
            status: [SalesRequestsStatus.accepted],
            channel: this.#data.channelId,
            event_status: [SalesRequestsEventStatus.ready, SalesRequestsEventStatus.planned, SalesRequestsEventStatus.inProgramming],
            q
        }, next);
    }

    loadSessions(q: string, next = false): void {
        const request: GetSessionsRequest = {
            limit: this.#PAGE_LIMIT,
            sort: 'name:asc',
            fields: [SessionsFilterFields.name, SessionsFilterFields.venueTemplateId,
            SessionsFilterFields.startDate, SessionsFilterFields.settingsSmartbookingRelatedSession, SessionsFilterFields.venueTplType],
            status: [SessionStatus.preview, SessionStatus.ready, SessionStatus.scheduled],
            q
        };
        if (!next) {
            this.#eventSessionsService.sessionList.load(this.form.controls.sessionForm.controls.event.value.id, request);
        } else {
            this.#eventSessionsService.sessionList.loadMore(this.form.controls.sessionForm.controls.event.value.id, request);
        }
    }
}

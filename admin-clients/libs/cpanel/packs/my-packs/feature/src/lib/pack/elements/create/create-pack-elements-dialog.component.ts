import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Metadata } from '@OneboxTM/utils-state';
import { CreatePackItemRequest, Pack, PackItem, PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { DeliveryPoint, GetProductsDeliveryPointsRequest, productsDeliveryPointsProviders, ProductsDeliveryPointsService } from '@admin-clients/cpanel/products/delivery-points/data-access';
import { GetProductsRequest, Product, productsProviders, ProductsService, ProductStatus, ProductType, ProductVariant } from '@admin-clients/cpanel/products/my-products/data-access';
import { Event, eventsProviders, EventsService, EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { eventSessionsProviders, EventSessionsService, GetSessionsRequest, Session, SessionsFilterFields, SessionStatus } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { DialogSize, EphemeralMessageService, SelectSearchComponent, SelectServerSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplatePriceType, VenueTemplatesApi, VenueTemplatesService, VenueTemplatesState } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe, NgTemplateOutlet, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, InjectionToken, viewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, FormRecord, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, skip, Subject, take, takeUntil, tap, throwError, withLatestFrom } from 'rxjs';

type ProductForm = FormGroup<{
    product: FormControl<Product>;
    variant?: FormControl<ProductVariant>;
    deliveryPoint?: FormControl<DeliveryPoint>;
    sharedBarcode?: FormControl<boolean>;
}>;

type SessionForm = FormGroup<{
    event: FormControl<Event>;
    session: FormControl<Session>;
    priceTypeMapping?: FormRecord<FormControl<number>>;
}>;

type CreationTypes = 'SESSION' | 'PRODUCT';

type AddPackElementForm = FormGroup<{
    type: FormControl<CreationTypes>;
    displayItemInChannels: FormControl<boolean>;
    sessionForm?: SessionForm;
    productForm?: ProductForm;
}>;

const VENUE_TEMPLATE_SOURCE_PACK = new InjectionToken<VenueTemplatesService>('VENUE_TEMPLATE_SOURCE_PACK');

@Component({
    selector: 'app-create-pack-elements-dialog',
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule, FormControlErrorsComponent,
        SelectSearchComponent, WizardBarComponent, DateTimePipe, NgTemplateOutlet,
        AsyncPipe, SelectServerSearchComponent, EllipsifyDirective, UpperCasePipe,
        FlexLayoutModule
    ],
    templateUrl: './create-pack-elements-dialog.component.html',
    styleUrl: './create-pack-elements-dialog.component.scss',
    providers: [eventsProviders, eventSessionsProviders, productsProviders, productsDeliveryPointsProviders, VenueTemplatesState,
        VenueTemplatesApi, {
            provide: VENUE_TEMPLATE_SOURCE_PACK,
            useClass: VenueTemplatesService
        }],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreatePackElemetsDialogComponent {
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<CreatePackElemetsDialogComponent>);
    readonly #eventsSrv = inject(EventsService);
    readonly #eventSessionsService = inject(EventSessionsService);
    readonly #packsSrv = inject(PacksService);
    readonly #venueTemplateTargetSrv = inject(VenueTemplatesService);
    readonly #venueTemplateSourceSrv = inject(VENUE_TEMPLATE_SOURCE_PACK);
    readonly #productsSrv = inject(ProductsService);
    readonly #productDeliveryPointsSrv = inject(ProductsDeliveryPointsService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #data = inject<{
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
        map(products => products.filter(product => !this.#data.currentItemsIds.includes(product.product_id))),
        map(products => products?.map(product => ({ ...product, id: product.product_id })))
    );

    readonly productVariants$ = this.#productsSrv.product.variants.getData$().pipe(
        filter(Boolean),
        tap(variants => (variants.length === 1) && this.form.get('productForm.variant').setValue(variants[0]))
    );

    readonly events$ = this.#eventsSrv.eventsList.getData$().pipe(
        filter(Boolean),
        map(events => this.mainSession.type === 'EVENT' ? events.filter(event => this.mainSession.item_id !== event.id) : events),
        tap(() => this.form.get('sessionForm.session').disable())
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

    readonly sessionTargetPriceTypes$ = this.#venueTemplateTargetSrv.getVenueTemplatePriceTypes$()
        .pipe(filter(Boolean));

    readonly sessionSourcePriceTypes$ = this.#venueTemplateSourceSrv.getVenueTemplatePriceTypes$()
        .pipe(filter(Boolean));

    readonly moreEventsAvailable$ = this.#eventsSrv.eventsList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    readonly moreSessionsAvailable$ = this.#eventSessionsService.sessionList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    readonly moreDeliveryPointsAvailable$ = this.#productDeliveryPointsSrv.productsDeliveryPointsList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    readonly moreProductsAvailable$ = this.#productsSrv.productsList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    readonly $loading = toSignal(booleanOrMerge([
        this.#eventsSrv.eventsList.loading$(),
        this.#eventSessionsService.sessionList.inProgress$(),
        this.#productsSrv.product.variants.loading$(),
        this.#packsSrv.packItems.loading$()
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
    readonly #cdr = inject(ChangeDetectorRef);

    currentStep = 1;
    subFormName = null as string;
    hasAutomaticSessionSameVenuTmplAsMainSession = true;
    priceTypeMappingForm = new FormRecord<FormControl<number>>({});

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.LARGE);
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
        this.loadSourcePriceTypes();
        this._wizardBar()?.nextStep();
        this.currentStep = 2;
    }

    close(packItemId: number = null): void {
        this.#dialogRef.close(packItemId);
    }

    save(): void {
        if (this.form.valid) {
            const reqBody = this.#generateRequestBody();
            this.#packsSrv.packItems.create(this.#data.packId, [reqBody])
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
        this.#eventsSrv.eventsList.load({
            limit: 20, offset: 0,
            status: [EventStatus.ready, EventStatus.planned, EventStatus.inProgramming],
            entityId: this.#data.entityId, sort: 'name:asc'
        });
    }

    #clearEventsList(): void {
        this.#eventsSrv.eventsList.clear();
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

    #initPriceTypeMappingForm(sourcePriceTypes: VenueTemplatePriceType[]): void {
        this.#removePriceTypeMappingForm();
        const controls: Record<number, FormControl<number>> = {};
        for (const source of sourcePriceTypes) {
            controls[source.id] = new FormControl<number>(null, Validators.required);
        }
        this.priceTypeMappingForm = new FormRecord<FormControl<number>>(controls);
        this.form.controls.sessionForm.addControl('priceTypeMapping', this.priceTypeMappingForm);
        this.#cdr.markForCheck();
    }

    #removePriceTypeMappingForm(): void {
        const sessionForm = this.form.controls.sessionForm;
        if (sessionForm?.contains?.('priceTypeMapping')) {
            sessionForm.removeControl('priceTypeMapping');
        }
        this.priceTypeMappingForm = new FormRecord<FormControl<number>>({});
    }

    #applyDefaultTargetPriceType(targetPriceTypes: VenueTemplatePriceType[]): void {
        const defaultId = targetPriceTypes?.[0]?.id;
        if (!defaultId || !this.priceTypeMappingForm) return;
        Object.values(this.priceTypeMappingForm.controls).forEach(ctrl => {
            if (ctrl.value == null) {
                ctrl.setValue(defaultId, { emitEvent: false });
            }
        });
        this.#cdr.markForCheck();
    }

    #syncPriceTypeMappingForSession(venueTemplateId: number): void {
        this.#loadSessionPriceTypes(venueTemplateId);
        this.sessionTargetPriceTypes$.pipe(
            skip(1),
            take(1),
            withLatestFrom(this.sessionSourcePriceTypes$),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([target, source]) => {
            if (!source?.length || !target?.length) return;
            this.#initPriceTypeMappingForm(source);
            this.#applyDefaultTargetPriceType(target);
            this.#cdr.markForCheck();
        });
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

    #clearProductDeliveryPoints(): void {
        this.#productDeliveryPointsSrv.productsDeliveryPointsList.clear();
    }

    #loadSessionPriceTypes(venueTemplateId: number): void {
        this.#venueTemplateTargetSrv.loadVenueTemplatePriceTypes(venueTemplateId);
    }

    #generateProductFormGroup(): void {
        this.subFormName = 'productForm';
        this.#loadProducts();
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
                session?.venue_template?.id === (
                    this.mainSession.type === 'SESSION'
                        ? this.mainSession?.session_data?.venue_template?.id
                        : this.mainSession?.event_data?.venue_template?.id
                );
            if (this.packType === 'AUTOMATIC') {
                this.#loadSessionPriceTypes(session.venue_template.id);
                if (!this.hasAutomaticSessionSameVenuTmplAsMainSession) {
                    this.#syncPriceTypeMappingForSession(session.venue_template.id);
                } else {
                    this.#removePriceTypeMappingForm();
                    this.#cdr.markForCheck();
                }
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
        const isSession = formRaw.type === this.creationTypes.session;
        const isAutomatic = this.packType === 'AUTOMATIC';
        const reqBody: CreatePackItemRequest = {
            item_id: formRaw.type === this.creationTypes.session ? formRaw.sessionForm.session.id : formRaw.productForm.product.product_id,
            type: formRaw.type,
            display_item_in_channels: formRaw.displayItemInChannels
        };
        if (isSession && isAutomatic && !this.hasAutomaticSessionSameVenuTmplAsMainSession) {
            const mapping = this.#mapPriceTypeMappingFromForm();
            reqBody.price_type_mapping = mapping;
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

    #mapPriceTypeMappingFromForm(): { source_price_type_id: number; target_price_type_id: number[] }[] {
        const priceTypeMapping = this.form.controls.sessionForm.controls.priceTypeMapping;

        return Object.entries(priceTypeMapping.controls)
            .filter(([_, control]) => control.value != null)
            .map(([sourceIdStr, control]) => ({
                source_price_type_id: Number(sourceIdStr),
                target_price_type_id: [control.value]
            }));
    }

    loadEvents(q: string): void {
        this.#eventsSrv.eventsList.load({
            limit: this.#PAGE_LIMIT,
            sort: 'name:asc',
            entityId: this.#data.entityId,
            status: [EventStatus.ready, EventStatus.planned, EventStatus.inProgramming],
            q,
            offset: 0
        });
    }

    loadSourcePriceTypes(): void {
        const venueTemplateId = this.mainSession.type === 'SESSION'
            ? this.mainSession?.session_data?.venue_template?.id
            : this.mainSession?.event_data?.venue_template?.id;
        if (!venueTemplateId) return;
        this.#venueTemplateSourceSrv.loadVenueTemplatePriceTypes(venueTemplateId);
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

    loadProductDeliveryPoints(q: string, next = false): void {
        const request: GetProductsDeliveryPointsRequest = {
            offset: 0,
            limit: this.#PAGE_LIMIT,
            sort: 'name:asc',
            entityId: this.#data.entityId,
            status: null,
            q
        };
        if (!next) {
            this.#productDeliveryPointsSrv.productsDeliveryPointsList.load(request);
        } else {
            this.#productDeliveryPointsSrv.productsDeliveryPointsList.loadMore(request);
        }
    }

    loadProducts(q: string, next = false): void {
        const request: GetProductsRequest = {
            limit: this.#PAGE_LIMIT,
            status: ProductStatus.active,
            entityId: this.#data.entityId, sort: 'name:asc',
            q
        };
        this.#productsSrv.productsList.loadMoreAndCache(request, next);
    }
}

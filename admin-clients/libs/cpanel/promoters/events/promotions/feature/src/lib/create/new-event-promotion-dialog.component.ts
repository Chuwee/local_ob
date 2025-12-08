import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { PromotionFieldRestrictions } from '@admin-clients/cpanel/promoters/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventPromotionListElement,
    EventPromotionsService, PostEventPromotion
} from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import {
    GetPromotionTplsRequest,
    PromotionTplListElement,
    PromotionTplsService
} from '@admin-clients/cpanel/promoters/promotion-templates/data-access';
import { PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import {
    ContextNotificationComponent,
    DialogSize, MessageDialogService, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, UntypedFormControl, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatInput } from '@angular/material/input';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { first, map, shareReplay, startWith } from 'rxjs/operators';

const PAGE_SIZE = 10;

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule,
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        ContextNotificationComponent,
        SearchablePaginatedSelectionModule,
        EllipsifyDirective,
        CommonModule,
        FormControlErrorsComponent
    ],
    selector: 'app-new-event-promotion-dialog',
    templateUrl: './new-event-promotion-dialog.component.html',
    styleUrls: ['./new-event-promotion-dialog.component.scss']
})
export class NewEventPromotionDialogComponent implements OnInit, AfterViewInit {
    readonly #datasource = new MatTableDataSource<EventPromotionListElement>();
    readonly #dialogRef = inject(MatDialogRef<NewEventPromotionDialogComponent>);
    readonly #promotionTplsSrv = inject(PromotionTplsService);
    readonly #eventPromotionsSrv = inject(EventPromotionsService);
    readonly #fb = inject(FormBuilder);
    readonly #elemRef = inject(ElementRef);
    readonly #eventSrv = inject(EventsService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #data = inject(MAT_DIALOG_DATA) as { eventId: number; entityId: number };
    #filters: GetPromotionTplsRequest = { limit: PAGE_SIZE, offset: 0, sort: 'name:asc' };

    @ViewChild(MatSort) private readonly _matSort: MatSort;
    @ViewChild(MatInput) private readonly _input: MatInput;

    readonly nameRestrictions = PromotionFieldRestrictions;
    readonly creationTypes = {
        fromTemplate: 'FROM_TEMPLATE',
        automatic: PromotionType.automatic,
        manual: 'MANUAL'
    };

    readonly creationSubtypes = {
        basic: PromotionType.basic,
        plus: PromotionType.plus
    };

    readonly tableColumns = ['name', 'type'];
    readonly pageSize = PAGE_SIZE;
    readonly form = this.#fb.group({
        name: [null as string, [
            Validators.required,
            Validators.minLength(this.nameRestrictions.minNameLength),
            Validators.maxLength(this.nameRestrictions.maxNameLength)
        ]],
        type: [null as PromotionType, [Validators.required]],
        manualType: [{ value: null as PromotionType, disabled: true }, [Validators.required]],
        fromTemplate: [{ value: null as PromotionTplListElement[], disabled: true }, Validators.required]
    });

    readonly fromTemplate = this.form.get('fromTemplate') as UntypedFormControl;
    readonly saving$ = this.#eventPromotionsSrv.promotion.loading$();
    readonly promotionTpls$ = this.#promotionTplsSrv.getPromotionTemplatesData$();
    readonly promotionTplsMetadata$ = this.#promotionTplsSrv.getPromotionTemplatesMetadata$();
    readonly promotionTplLoading$ = this.#promotionTplsSrv.isPromotionTemplatesLoading$();
    readonly promotions$ = combineLatest([
        this.#eventPromotionsSrv.promotionsList.getData$(),
        this.form.get('manualType').valueChanges.pipe(startWith(null as string))
    ])
        .pipe(
            map(([promotions, type]) => {
                promotions = promotions.filter(promo => promo.type === type || !type);
                this.#datasource.data = promotions;
                return this.#datasource;
            }),
            shareReplay(1)
        );

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this.#dialogRef.disableClose = false;
        this.#filters = {
            ...this.#filters,
            entityId: this.#data.entityId
        };

        this.#promotionTplsSrv.loadPromotionTemplates(this.#filters);
        this.form.get('type').valueChanges
            .subscribe(value => {
                if (value === this.creationTypes.manual) {
                    this.form.get('manualType').reset();
                    this.form.get('manualType').enable();
                    this.form.get('fromTemplate').disable();
                } else if (value === this.creationTypes.fromTemplate) {
                    this.form.get('manualType').disable();
                    this.form.get('fromTemplate').enable();
                } else {
                    this.form.get('manualType').disable();
                    this.form.get('fromTemplate').disable();
                }
            });
    }

    ngAfterViewInit(): void {
        // sort is made case-insensitive this way
        this.#datasource.sortingDataAccessor = (data: any, sortHeaderId: string): string => {
            if (typeof data[sortHeaderId] === 'string') {
                return data[sortHeaderId].toLocaleLowerCase();
            }
            return data[sortHeaderId];
        };
        this.#datasource.sort = this._matSort;

        // focus first input improves UX
        setTimeout(() => this._input.focus(), 500);
    }

    loadPromotionTemplates({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this.#filters = { ...this.#filters, limit, offset, q: q?.length ? q : null };
        !!this.#filters && this.#promotionTplsSrv.loadPromotionTemplates(this.#filters);
    }

    create(): void {
        if (this.form.valid) {
            this.#eventSrv.event.get$()
                .pipe(first())
                .subscribe(event => {
                    const data = this.form.value;
                    const promo: PostEventPromotion = {
                        name: data.name,
                        type: data.type === this.creationTypes.manual ? data.manualType : data.type
                    };
                    const template = data.fromTemplate?.[0];
                    if (data.type === this.creationTypes.fromTemplate) {
                        promo.type = template.type;
                        promo.from_entity_template_id = template.id;

                    } if (data.type === this.creationTypes.fromTemplate && event.currency_code && template.currency_code
                        && event.currency_code !== template.currency_code) {
                        this.#msgDialogSrv.showWarn({
                            size: DialogSize.MEDIUM,
                            title: 'EVENTS.FORMS.INFOS.PROMOTION_TEMPLATE_IMPORT_CURRENCY_WARN',
                            message: 'EVENTS.FORMS.INFOS.PROMOTION_TEMPLATE_IMPORT_CURRENCY_WARN_DETAILS',
                            actionLabel: 'FORMS.ACTIONS.USE',
                            showCancelButton: true
                        })
                            .subscribe(isConfirmed => {
                                if (isConfirmed) {
                                    this.#eventPromotionsSrv.promotion.create(this.#data.eventId, promo)
                                        .subscribe(id => this.close(id));
                                }
                            });
                    } else {
                        this.#eventPromotionsSrv.promotion.create(this.#data.eventId, promo)
                            .subscribe(id => this.close(id));
                    }
                });

        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
        }
    }

    close(promotionId: number = null): void {
        this.#dialogRef.close(promotionId);
    }
}

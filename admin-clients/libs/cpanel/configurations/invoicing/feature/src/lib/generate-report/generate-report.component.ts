import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    InvoicingService, GetInvoicingEntityEventsRequest, InvoicingEntityOperatorTypes, PostInvoicingReport
} from '@admin-clients/cpanel-configurations-invoicing-data-access';
import { OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import type { Operator } from '@admin-clients/cpanel-configurations-operators-data-access';
import { GetEntitiesRequest } from '@admin-clients/shared/common/data-access';
import {
    MessageDialogService, EphemeralMessageService,
    DialogSize, SelectSearchComponent, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge, dateIsAfter, dateIsBefore, dateIsSameOrBefore, dateTimeValidator } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDateFormats, MAT_DATE_FORMATS } from '@angular/material/core';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment';
import { filter, first, map, Observable, startWith, switchMap, tap } from 'rxjs';

type InvoicingEntity = { id: number | 'ALL' | 'FULL'; name: string; code?: 'ALL' | 'FULL'; entity_ids?: number[] };

const STATIC_ENTITY_OPTIONS: InvoicingEntity[] = [
    { name: 'Otras', code: 'ALL', entity_ids: [103, 55, 835, 663, 53, 477, 28, 254, 466, 494, 901, 997, 784, 125], id: 'ALL' },
    { name: 'Todas', code: 'FULL', entity_ids: [], id: 'FULL' }
];

@Component({
    selector: 'app-generate-report',
    templateUrl: './generate-report.component.html',
    styleUrls: ['./generate-report.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, TranslatePipe, AsyncPipe, FormControlErrorsComponent,
        SelectSearchComponent, SelectServerSearchComponent, FlexLayoutModule, ReactiveFormsModule
    ]
})
export class GenerateInvoicingReportComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #invoicingSrv = inject(InvoicingService);
    readonly #operatorsSrv = inject(OperatorsService);
    readonly #auth = inject(AuthenticationService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #msgDialog = inject(MessageDialogService);
    readonly #fb = inject(FormBuilder);
    readonly #formats: MatDateFormats = inject(MAT_DATE_FORMATS);

    #defaultOperator: Operator;

    readonly dateFormat = moment.localeData().longDateFormat(this.#formats.display.dateInput).toLowerCase();
    readonly dateTimeFormats = DateTimeFormats;
    readonly orderPerspectiveEnum = InvoicingEntityOperatorTypes;

    readonly form = this.#fb.group({
        operator: [null as Operator, Validators.required],
        entity: [null as InvoicingEntity, Validators.required],
        events: null as IdName[],
        type: [this.orderPerspectiveEnum.event, Validators.required],
        email: [null as string, [Validators.required, Validators.email]],
        dateFrom: [null as Date, [
            Validators.required,
            control => dateTimeValidator(dateIsBefore, 'endAfterStart', this.form?.value?.dateTo)(control),
            dateTimeValidator(dateIsSameOrBefore, 'dateIsFuture', new Date())
        ]],
        dateTo: [null as Date, [
            Validators.required,
            control => dateTimeValidator(dateIsAfter, 'endDateBeforeStartDate', this.form?.value?.dateFrom)(control),
            dateTimeValidator(dateIsSameOrBefore, 'dateIsFuture', new Date())
        ]]
    });

    readonly entities$ = this.#entitiesSrv.entityList.getData$().pipe(
        map(entities => entities ?
            [...STATIC_ENTITY_OPTIONS, ...entities] : null)
    );

    readonly moreAvailableEntities$ = this.#entitiesSrv.entityList.getMetadata$()
        .pipe(map(metadata => !!metadata && metadata.offset + metadata.limit < metadata.total));

    readonly events$ = this.#invoicingSrv.invoicingEntityEvents.getList$();

    readonly isPromotorSelected$ = this.form.controls.type.valueChanges.pipe(
        startWith(this.form.value.type),
        map(value => value === this.orderPerspectiveEnum.event),
        tap(isPromotorSelected => {
            this.form.controls.entity.reset();
            this.form.controls.events.reset();
            if (isPromotorSelected && this.form.value.operator) {
                this.form.controls.entity.enable();
            } else {
                this.form.controls.entity.disable();
            }
            this.form.controls.entity.updateValueAndValidity();
        })
    );

    readonly reqInProgress$: Observable<boolean> = booleanOrMerge([
        this.#invoicingSrv.isInvoicingEntitiesFilterLoading$(),
        this.#invoicingSrv.isInvoicingReportGenerating$(),
        this.#operatorsSrv.operators.loading$()
    ]);

    readonly operators$: Observable<Operator[]> = this.#operatorsSrv.operators.getData$()
        .pipe(
            filter(list => !!list),
            tap(operators => {
                this.#defaultOperator = operators.find(({ id }) => id === 1);
                if (this.#defaultOperator) {
                    this.form.controls.operator.setValue(this.#defaultOperator);
                }
            })
        );

    ngOnInit(): void {
        this.loadOptions();
        this.initForm();
    }

    clearForm(): void {
        const { startDate, endDate } = this.getInitialDates();
        this.form.reset({ type: this.orderPerspectiveEnum.event, operator: this.#defaultOperator });
        this.form.patchValue({
            dateFrom: startDate,
            dateTo: endDate
        });
    }

    generateReport(): void {
        this.form.markAllAsTouched();
        if (this.form.valid) {
            this.#auth.getLoggedUser$()
                .pipe(
                    first(user => !!user),
                    switchMap(user => {
                        const { operator: { id }, entity, dateFrom, email, type, dateTo: dateToValue, events }
                            = this.form.value;
                        const dateTo = new Date(new Date(dateToValue).setHours(23, 59, 59, 999)).toISOString();

                        const report: PostInvoicingReport = {
                            user_id: user.id,
                            operator_id: id,
                            email,
                            from: dateFrom.toISOString(),
                            to: dateTo,
                            order_perspective: type
                        };

                        if (entity) {
                            const { code, entity_ids: entityIds, id: entityId } = entity;
                            report.entities_id = code ? entityIds : [entityId as number];
                            report.entity_code = code;
                            report.event_ids = events?.map(({ id }) => id);
                        }

                        return this.#msgDialog.showInfo({
                            message: 'INVOICING.GENERATE_REPORT.GENERATION_INFO',
                            title: 'INVOICING.GENERATE_REPORT.GENERATION_INFO_TITLE',
                            size: DialogSize.SMALL
                        })
                            .pipe(switchMap(() => this.#invoicingSrv.generateInvoicingReport(report)));
                    })
                )
                .subscribe(() => {
                    this.#ephemeralSrv.showSuccess({ msgKey: 'INVOICING.GENERATE_REPORT.GENERATION_SUCCESS' });
                });
        }
    }

    loadEntities({ q, nextPage }: { q?: string; nextPage?: boolean }): void {
        const request: GetEntitiesRequest = {
            q,
            operator_id: this.form.value.operator?.id,
            limit: 100
        };
        if (!nextPage) {
            this.#entitiesSrv.entityList.load(request);
        } else {
            this.#entitiesSrv.entityList.loadMore(request);
        }
    }

    private initForm(): void {
        const { startDate, endDate } = this.getInitialDates();
        this.form.controls.dateFrom.setValue(startDate);
        this.form.controls.dateTo.setValue(endDate);
    }

    private loadOptions(): void {
        this.#operatorsSrv.operators.load({ limit: 999, sort: 'name:asc', offset: 0 });

        // Load entities based on the selected operator id
        this.form.controls.operator.valueChanges
            .pipe(
                startWith(this.form.value.operator),
                filter(() => this.form.value.type === this.orderPerspectiveEnum.event),
                tap(() => {
                    this.form.controls.entity.reset();
                    this.form.controls.entity.disable({ emitEvent: false });
                    this.#entitiesSrv.entityList.clear();
                }),
                filter(Boolean),
                tap(() => this.form.controls.entity.enable()),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe();

        // Load events based on the selected entity id
        this.form.controls.entity.valueChanges
            .pipe(
                filter(() => this.form.value.type === this.orderPerspectiveEnum.event),
                tap((val: InvoicingEntity) => {
                    this.form.controls.events.reset();
                    this.form.controls.events.disable();
                    if (val?.id && !val.code) {
                        const request: GetInvoicingEntityEventsRequest = {
                            limit: 999,
                            offset: 0,
                            status: [EventStatus.inProgramming, EventStatus.planned, EventStatus.ready],
                            sort: 'name:asc'
                        };
                        this.#invoicingSrv.invoicingEntityEvents.load(val.id as number, request);
                    } else {
                        this.#invoicingSrv.invoicingEntityEvents.clear();
                    }
                }),
                filter((val: InvoicingEntity) => !val?.code),
                takeUntilDestroyed(this.#destroyRef),
                switchMap(() => this.#invoicingSrv.invoicingEntityEvents.loading$()),
                filter(inProgress => this.form.value.entity && !inProgress),
                tap(() => this.form.controls.events.enable()),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe();
    }

    private getInitialDates(): { startDate: Date; endDate: Date } {
        const today = new Date();
        const currentDay = today.getDate();
        const currentMonth = today.getMonth();
        const currentYear = today.getFullYear();
        const endDate = new Date(currentYear, currentMonth, currentDay > 20 ? 20 : (currentDay > 1 ? currentDay - 1 : currentDay));
        const startDate = new Date(currentMonth === 0 ? currentYear - 1 : currentYear, currentMonth === 0 ? 11 : currentMonth - 1, 21);
        return { startDate, endDate };
    }
}

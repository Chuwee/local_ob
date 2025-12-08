import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { PromotionFieldRestrictions } from '@admin-clients/cpanel/promoters/data-access';
import { PromotionTplListElement, PromotionTplsService } from '@admin-clients/cpanel/promoters/promotion-templates/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    PostSeasonTicketPromotion, SeasonTicketPromotionListElement, SeasonTicketPromotionsService
} from '@admin-clients/cpanel/promoters/season-tickets/promotions/data-access';
import { PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { ContextNotificationComponent, DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatInput } from '@angular/material/input';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { first, map, shareReplay, startWith } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ContextNotificationComponent,
        EllipsifyDirective,
        CommonModule,
        FormControlErrorsComponent
    ],
    selector: 'app-new-promotion-dialog',
    templateUrl: './new-promotion-dialog.component.html',
    styleUrls: ['./new-promotion-dialog.component.scss']
})
export class NewPromotionDialogComponent implements OnInit, AfterViewInit {
    readonly #datasource = new MatTableDataSource<SeasonTicketPromotionListElement>();
    readonly #dialogRef = inject(MatDialogRef<NewPromotionDialogComponent>);
    readonly #promotionTplsSrv = inject(PromotionTplsService);
    readonly #stPromotionsSrv = inject(SeasonTicketPromotionsService);
    readonly #stSrv = inject(SeasonTicketsService);
    readonly #fb = inject(FormBuilder);
    readonly #elemRef = inject(ElementRef);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #data = inject(MAT_DIALOG_DATA) as { entityId: number; seasonTicketId: number };
    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(MatInput) private _input: MatInput;

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
    readonly form = this.#fb.group({
        name: [null as string, [
            Validators.required,
            Validators.minLength(this.nameRestrictions.minNameLength),
            Validators.maxLength(this.nameRestrictions.maxNameLength)
        ]],
        type: [null as PromotionType, [Validators.required]],
        manualType: [{ value: null as PromotionType, disabled: true }, [Validators.required]],
        fromTemplate: [{ value: null as PromotionTplListElement, disabled: true }, [Validators.required]]
    });

    readonly loading$ = this.#stPromotionsSrv.promotion.loading$();
    readonly promotionTpls$ = this.#promotionTplsSrv.getPromotionTemplatesData$();
    readonly promotions$ = combineLatest([
        this.#stPromotionsSrv.promotionsList.getData$(),
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
        this.#promotionTplsSrv.loadPromotionTemplates({ entityId: this.#data.entityId });

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

    create(): void {
        if (this.form.valid) {
            this.#stSrv.seasonTicket.get$()
                .pipe(first())
                .subscribe(seasonTicket => {
                    const data = this.form.value;
                    const promo: PostSeasonTicketPromotion = {
                        name: data.name,
                        type: data.type === this.creationTypes.manual ? data.manualType : data.type
                    };
                    const template = data.fromTemplate?.[0];
                    if (data.type === this.creationTypes.fromTemplate) {
                        promo.type = template.type;
                        promo.from_entity_template_id = template.id;
                    }
                    if (data.type === this.creationTypes.fromTemplate && seasonTicket.currency_code && template.currency_code
                        && seasonTicket.currency_code !== template.currency_code) {
                        this.#msgDialogSrv.showWarn({
                            size: DialogSize.MEDIUM,
                            title: 'EVENTS.FORMS.INFOS.PROMOTION_TEMPLATE_IMPORT_CURRENCY_WARN',
                            message: 'SEASON_TICKETS.FORMS.INFOS.PROMOTION_TEMPLATE_IMPORT_CURRENCY_WARN_DETAILS',
                            actionLabel: 'FORMS.ACTIONS.USE',
                            showCancelButton: true
                        })
                            .subscribe(isConfirmed => {
                                if (isConfirmed) {
                                    this.#stPromotionsSrv.promotion.create(this.#data.seasonTicketId, promo)
                                        .subscribe(id => this.close(id));
                                }
                            });
                    } else {
                        this.#stPromotionsSrv.promotion.create(this.#data.seasonTicketId, promo)
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

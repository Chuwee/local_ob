import { GetPriceTypeRestricion, PostPriceTypeRestriction } from '@admin-clients/cpanel/venues/venue-templates/data-access';
import { MessageDialogService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { VenueTemplatePriceType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ActionsTableComponent } from '@admin-clients/shared-common-ui-actions-table';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { filter, finalize, switchMap, tap } from 'rxjs/operators';
import { PriceTypeRestrictionDialogComponent } from './restrictions-dialog/restrictions-dialog.component';

export type PriceTypeWithRestriction = VenueTemplatePriceType & {
    hasRestrictions?: boolean;
};

enum ACTIONS {
    add = 'add',
    delete = 'delete',
    update = 'update'
}

@Component({
    selector: 'app-price-type-restrictions',
    templateUrl: './price-type-restrictions.component.html',
    styleUrls: ['./price-type-restrictions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatProgressSpinner, AsyncPipe, MatTableModule, FlexLayoutModule, TranslatePipe, ActionsTableComponent
    ]
})
export class PriceTypeRestrictionsComponent implements OnInit {
    private _priceTypes: PriceTypeWithRestriction[];
    private _translate = inject(TranslateService);

    isLoadingOrSaving$ = new BehaviorSubject<boolean>(true);

    @Input() priceTypes$: Observable<PriceTypeWithRestriction[]>;
    @Input() restriction$: (id: number) => Observable<GetPriceTypeRestricion>;
    @Input() save$: (id: number, restriction: PostPriceTypeRestriction) => Observable<void>;
    @Input() delete$: (id: number) => Observable<void>;

    actions = {
        [ACTIONS.add]: {
            icon: 'add',
            enabled: (priceType: PriceTypeWithRestriction): boolean => !priceType.hasRestrictions,
            label: this._translate.instant('FORMS.ACTIONS.ADD')
        },
        [ACTIONS.update]: {
            icon: 'edit',
            enabled: (priceType: PriceTypeWithRestriction): boolean => priceType.hasRestrictions,
            label: this._translate.instant('FORMS.ACTIONS.MODIFY')
        },
        [ACTIONS.delete]: {
            icon: 'delete',
            enabled: (priceType: PriceTypeWithRestriction): boolean => priceType.hasRestrictions,
            label: this._translate.instant('FORMS.ACTIONS.DELETE')
        }
    };

    columns = ['name', 'badge', 'actions'];

    constructor(
        private _matDialog: MatDialog,
        private _msgDialogService: MessageDialogService
    ) { }

    ngOnInit(): void {
        this.priceTypes$ = this.priceTypes$.pipe(tap(priceTypes => {
            this._priceTypes = priceTypes;
            this.isLoadingOrSaving$.next(false);
        }));
    }

    actionClicked({ action, elem }: { action: string; elem: unknown }): void {
        switch (action) {
            case ACTIONS.add:
            case ACTIONS.update:
                this.openModal(elem as PriceTypeWithRestriction);
                break;
            case ACTIONS.delete:
                this.openDeleteConfirmation(elem as PriceTypeWithRestriction);
                break;
        }
    }

    openModal(priceType: PriceTypeWithRestriction): void {
        const data = { priceType, priceTypes: this._priceTypes, restriction$: this.restriction$ };
        this._matDialog.open(PriceTypeRestrictionDialogComponent, new ObMatDialogConfig(data))
            .beforeClosed()
            .pipe(
                filter(result => !!result),
                tap(() => this.isLoadingOrSaving$.next(true)),
                switchMap(restriction => this.save$(priceType.id, restriction)),
                finalize(() => this.isLoadingOrSaving$.next(false))
            ).subscribe();
    }

    openDeleteConfirmation(priceType: PriceTypeWithRestriction): void {
        this._msgDialogService.showDeleteConfirmation({
            confirmation: {
                title: 'VENUE_TPLS.PRICE_TYPES.RESTRICTIONS.DELETE_TITLE',
                message: 'VENUE_TPLS.PRICE_TYPES.RESTRICTIONS.DELETE_MESSAGE',
                messageParams: priceType
            },
            success: {
                msgKey: 'VENUE_TPLS.PRICE_TYPES.RESTRICTIONS.DELETE_SUCCESS',
                msgParams: priceType
            },
            delete$: of(null).pipe(
                tap(() => this.isLoadingOrSaving$.next(true)),
                switchMap(() => this.delete$(priceType.id)),
                finalize(() => this.isLoadingOrSaving$.next(false))
            )
        });
    }

}

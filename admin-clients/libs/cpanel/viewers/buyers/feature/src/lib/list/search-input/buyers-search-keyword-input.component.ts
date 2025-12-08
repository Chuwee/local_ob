import { BuyerKeywordSearchType } from '@admin-clients/cpanel-viewers-buyers-data-access';
import { FilterWrapped, FilterItem, FilterItemValue } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component } from '@angular/core';
import { Params } from '@angular/router';
import { Observable, of } from 'rxjs';
import { buyerFilterElements } from '../buyers-filter-elements';

@Component({
    selector: 'app-buyers-search-keyword-input',
    templateUrl: './buyers-search-keyword-input.component.html',
    styleUrls: ['./buyers-search-keyword-input.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class BuyersSearchKeywordInputComponent extends FilterWrapped {
    readonly keywordSearchType = BuyerKeywordSearchType;
    currentKeyword: string;
    currentKeywordOption: BuyerKeywordSearchType;

    constructor(private _changeDet: ChangeDetectorRef) {
        super();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        if (params[buyerFilterElements.keyword.param]) {
            this.currentKeyword = params[buyerFilterElements.keyword.param];
            this.currentKeywordOption = BuyerKeywordSearchType.allFields;
        } else if (params[buyerFilterElements.orderCode.param]) {
            this.currentKeyword = params[buyerFilterElements.orderCode.param];
            this.currentKeywordOption = BuyerKeywordSearchType.orderCode;
        } else if (params[buyerFilterElements.barcode.param]) {
            this.currentKeyword = params[buyerFilterElements.barcode.param];
            this.currentKeywordOption = BuyerKeywordSearchType.barcode;
        }
        this._changeDet.markForCheck();
        return of(this.getFilters());
    }

    getFilters(): FilterItem[] {
        const result: FilterItem[] = [];
        if (this.currentKeyword?.length) {
            const filterItem: FilterItem = {
                key: null,
                label: null,
                urlQueryParams: {},
                values: [{ value: this.currentKeyword, text: this.currentKeyword } as FilterItemValue]
            };
            if (!this.currentKeywordOption || this.currentKeywordOption === BuyerKeywordSearchType.allFields) {
                filterItem.key = buyerFilterElements.keyword.key;
                filterItem.urlQueryParams[buyerFilterElements.keyword.param] = this.currentKeyword;
            } else if (this.currentKeywordOption === BuyerKeywordSearchType.orderCode) {
                filterItem.key = buyerFilterElements.orderCode.key;
                filterItem.urlQueryParams[buyerFilterElements.orderCode.param] = this.currentKeyword;
            } else if (this.currentKeywordOption === BuyerKeywordSearchType.barcode) {
                filterItem.key = buyerFilterElements.barcode.key;
                filterItem.urlQueryParams[buyerFilterElements.barcode.param] = this.currentKeyword;
            }
            result.push(filterItem);
        }
        return result;
    }

    removeFilter(): void { // not used, useless
    }

    resetFilters(): void {
        this.currentKeyword = '';
        this.currentKeywordOption = null;
    }

    keywordChange(change: { value: string; option: string }): void {
        this.currentKeyword = change.value;
        this.currentKeywordOption = change.option as BuyerKeywordSearchType;
        this.applyFilters();
    }
}

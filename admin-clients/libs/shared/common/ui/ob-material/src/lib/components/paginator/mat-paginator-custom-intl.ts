import { Injectable } from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class MatPaginatorCustomIntl extends MatPaginatorIntl {
    constructor(private _translate: TranslateService) {
        super();
        this.nextPageLabel = this._translate.instant('PAGINATION.NEXT_PAGE');
        this.previousPageLabel = this._translate.instant(
            'PAGINATION.PREVIOUS_PAGE'
        );
        this.itemsPerPageLabel = this._translate.instant(
            'PAGINATION.ITEMS_PER_PAGE'
        );
        this.firstPageLabel = this._translate.instant('PAGINATION.FIRST_PAGE');
        this.lastPageLabel = this._translate.instant('PAGINATION.LAST_PAGE');
        this.getRangeLabel = (...args) => this.#getRangeLabel(...args);
    }

    #getRangeLabel(page: number, pageSize: number, length: number): string {
        if (length === 0 || pageSize === 0) {
            // shows something like '0 of 300'
            return this._translate.instant('PAGINATION.START_RANGE', {
                length
            });
        }
        length = Math.max(length, 0);
        const start = page * pageSize;
        // shows something like '201 - 300 of 500'
        const end =
            start < length
                ? Math.min(start + pageSize, length)
                : start + pageSize;
        return this._translate.instant('PAGINATION.RANGE', {
            start: start + 1,
            end,
            length
        });
    }
}

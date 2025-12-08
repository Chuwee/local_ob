import { TranslateService } from '@ngx-translate/core';
import { FilterItem, FilterItemValue } from './filter-item.model';

export class FilterItemBuilder {

    private _key: string;
    private _labelKey: string;
    private _queryParam: string;
    private _value: { id: string; name: string };
    private _enableTranslateValue = false;

    constructor(private _translateService: TranslateService) {
    }

    key(val: string): FilterItemBuilder {
        this._key = val;
        return this;
    }

    labelKey(val: string): FilterItemBuilder {
        this._labelKey = val;
        return this;
    }

    queryParam(val: string): FilterItemBuilder {
        this._queryParam = val;
        return this;
    }

    value(val: { id: string; name: string }): FilterItemBuilder {
        this._value = val;
        return this;
    }

    translateValue(): FilterItemBuilder {
        this._enableTranslateValue = true;
        return this;
    }

    build(): FilterItem {
        const filterItem = new FilterItem(this._key, this._translateService.instant(this._labelKey));
        if (this._value) {
            const text = this._enableTranslateValue ? this._translateService.instant(this._value.name) : this._value.name;
            filterItem.values = [new FilterItemValue(this._value.id, text)];
            filterItem.urlQueryParams[this._queryParam] = this._value.id;
        }
        return filterItem;
    }
}

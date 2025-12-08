import { Params } from '@angular/router';

export class FilterItem {
    values: FilterItemValue[];
    urlQueryParams: Params = {};

    constructor(public key: string,
        public label: string) {
    }
}

export class FilterItemValue {

    constructor(
        public value: any,
        public text: string
    ) { }
}

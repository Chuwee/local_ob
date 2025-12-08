import { NgForOf } from '@angular/common';
import { Directive, Host, Input, NgIterable } from '@angular/core';

@Directive({
    // eslint-disable-next-line @angular-eslint/directive-selector
    selector: '[ngForTrackByProperty]',
    standalone: true
})
export class NgForTrackByPropertyDirective<T> {

    @Input() ngForOf!: NgIterable<T>;
    @Input() ngForTrackByProperty!: keyof T;

    constructor(@Host() ngForOfDir: NgForOf<T>) {
        ngForOfDir.ngForTrackBy = (_, item: T): T[keyof T] => item[this.ngForTrackByProperty];
    }
}

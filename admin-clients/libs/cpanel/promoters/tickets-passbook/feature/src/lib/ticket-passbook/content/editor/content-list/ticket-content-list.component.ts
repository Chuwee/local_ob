import { TicketPassbookFields, ticketPassbookEmptyField } from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { ChangeDetectionStrategy, Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { UntypedFormArray } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

@Component({
    selector: 'app-ticket-content-list',
    templateUrl: './ticket-content-list.component.html',
    styleUrls: ['./ticket-content-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TicketContentListComponent implements OnInit {

    @Input() form: UntypedFormArray;
    @Output() openAddContentDialog = new EventEmitter<boolean>();
    listElems$: Observable<TicketPassbookFields[]>;

    constructor() { }

    ngOnInit(): void {
        this.listElems$ = this.form.valueChanges.pipe(
            startWith(this.form.value as unknown),
            map(elems => elems.filter(elem => elem.key !== ticketPassbookEmptyField.key))
        );
    }

    isCustomElement(elemGroup: string): boolean {
        return elemGroup === 'custom';
    }

    onListDrop(event: CdkDragDrop<TicketPassbookFields>): void {
        moveItemInArray(this.form.value, event.previousIndex, event.currentIndex);
        this.form.setValue(this.form.value); // Needed to set the new order to the form references
        this.form.markAsDirty();
    }

    removeElem(index: number): void {
        this.form.removeAt(index);
        this.form.markAsDirty();
    }

}

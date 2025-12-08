import { Customer, CustomerTypesHistoricItem } from '@admin-clients/cpanel-viewers-customers-data-access';
import { DialogSize, ObDialog, TimelineElement, TimelineElementStatus, VerticalTimelineComponent } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { TranslateModule } from '@ngx-translate/core';

type CustomerTypesHistoricTimeLineElem = TimelineElement & CustomerTypesHistoricItem & {
    addActionsCts?: string;
    removeActionsCts?: string;
};

@Component({
    standalone: true,
    selector: 'app-customer-types-historic-dialog',
    templateUrl: './customer-types-historic-dialog.component.html',
    imports: [
        TranslateModule, MatDialogModule, MatIconModule, MatButtonModule, VerticalTimelineComponent
    ],
    providers: [DateTimePipe],
    styles: `
        .title,
        .historic-title {
            font: var(--ob-theme-font-subheading-2);
            letter-spacing: normal;
            span { font-size: 18px; font-weight: 700; color:var(--ob-theme-color-black); }
        }
        .field-title {
            font-weight: 700;
        }
    `,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerTypesHistoricDialogComponent extends ObDialog<CustomerTypesHistoricDialogComponent, {
    customer: Customer;
}, unknown> {
    readonly #dialogRef = inject(MatDialogRef<CustomerTypesHistoricDialogComponent>);
    readonly #data = inject<{ customer: Customer; customerTypesHistoric: CustomerTypesHistoricItem[] }>(MAT_DIALOG_DATA);
    readonly #datePipe = inject(DateTimePipe);

    readonly customer = this.#data.customer;
    readonly customerTypesHistoric = this.#data.customerTypesHistoric;
    readonly customerTypes = this.customer.customer_types?.map(ct => ct.name).join(', ') || '';
    readonly elements = this.#mapHistoricItemToTimelineElem(this.customerTypesHistoric);

    constructor() {
        super(DialogSize.LATERAL, true);
        this.#dialogRef.addPanelClass('no-action-bar');
    }

    close(edited = false): void {
        this.#dialogRef.close(edited);
    }

    #mapHistoricItemToTimelineElem(item: CustomerTypesHistoricItem[]): CustomerTypesHistoricTimeLineElem[] {
        return item.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()).map(item => ({
            ...item,
            addActionsCts: item.actions?.filter(action => action.type === 'ADD').map(action => action.customerType.name).join(', '),
            removeActionsCts: item.actions?.filter(action => action.type === 'REMOVE').map(action => action.customerType.name).join(', '),
            title: this.#datePipe.transform(item.date, DateTimeFormats.shortDate),
            date: null,
            status: TimelineElementStatus.ok
        }));
    }
}
import { OrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { CopyTextComponent } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe, LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-order-data',
    templateUrl: './order-data.component.html',
    styleUrls: ['./order-data.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, LocalDateTimePipe, NgIf, CopyTextComponent, DateTimePipe, MatTooltipModule,
        EllipsifyDirective
    ]
})

export class OrderDataComponent {

    @Input()
    order: OrderDetail;

    readonly dateTimeFormats = DateTimeFormats;

}

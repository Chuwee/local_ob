import { TicketDetail } from '@admin-clients/shared/common/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-ticket-validation',
    templateUrl: './ticket-validation.component.html',
    styleUrls: ['./ticket-validation.component.scss'],
    imports: [MatTableModule, TranslatePipe, EllipsifyDirective, DateTimePipe, MatTooltip],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TicketValidationComponent implements OnInit {

    readonly dateTimeFormats = DateTimeFormats;
    validationsColumns!: string[];

    printsColumns: Observable<string[]>;

    @Input() ticketDetail: TicketDetail;

    constructor() { }

    ngOnInit(): void {
        this.validationsColumns = this.ticketDetail?.ticket ? ['date', 'gate', 'user', 'status'] : ['date', 'user', 'status'];
    }
}

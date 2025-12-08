import { TourEvent } from '@admin-clients/cpanel/promoters/tours/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    selector: 'app-tour-events-list',
    templateUrl: './tour-events-list.component.html',
    styleUrls: ['./tour-events-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule,
        TranslatePipe,
        FlexLayoutModule,
        AsyncPipe,
        DateTimePipe
    ]
})
export class TourEventsListComponent implements OnInit, OnDestroy {

    private _onDestroy: Subject<void> = new Subject();

    dateTimeFormats = DateTimeFormats;
    columns = ['name', 'startDate', 'status', 'archived', 'capacity'];
    totalCapacity$: Observable<number>;

    @Input() tourEvents$: Observable<TourEvent[]>;

    ngOnInit(): void {
        this.totalCapacity$ = this.tourEvents$
            .pipe(map(tourEvents => tourEvents.length && tourEvents.map(event => event.capacity).reduce((a, b) => a + b) || 0));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

}

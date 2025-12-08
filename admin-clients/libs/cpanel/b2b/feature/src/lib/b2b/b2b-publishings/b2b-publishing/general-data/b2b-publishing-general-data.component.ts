import { B2bSeat, B2bService } from '@admin-clients/cpanel/b2b/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Observable, filter, map } from 'rxjs';

@Component({
    selector: 'app-b2b-publishing-general-data',
    templateUrl: './b2b-publishing-general-data.component.html',
    styleUrls: ['./b2b-publishing-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bPublishingGeneralDataComponent implements OnDestroy, OnInit {
    private readonly _b2bSrv = inject(B2bService);
    dateTimeFormats = DateTimeFormats;

    // eslint-disable-next-line max-len
    b2bSeat$ = this._b2bSrv.b2bSeat.get$();
    actions$ = this.b2bSeat$.pipe(filter(Boolean), map((seat: B2bSeat) => seat?.historic));
    actionsLength$ = this.b2bSeat$.pipe(filter(Boolean), map((seat: B2bSeat) => seat?.historic?.length));
    loading$: Observable<boolean>;

    ngOnInit(): void {
        this.loading$ = this._b2bSrv.b2bSeat.loading$();
    }

    ngOnDestroy(): void {
        this._b2bSrv.b2bSeat.clear();
    }
}

import { B2bSeat, B2bService } from '@admin-clients/cpanel/b2b/data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import { ObfuscatePattern } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-b2b-publishing-details',
    templateUrl: './b2b-publishing-details.component.html',
    styleUrls: ['./b2b-publishing-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bPublishingDetailsComponent implements OnInit, OnDestroy {
    private readonly _b2bSrv = inject(B2bService);
    readonly eventType = EventType;
    obfuscatePattern = ObfuscatePattern;
    b2bSeat$: Observable<B2bSeat>;

    ngOnInit(): void {
        this.b2bSeat$ = this._b2bSrv.b2bSeat.get$();
    }

    ngOnDestroy(): void {
        this._b2bSrv.b2bSeat.clear();
    }

}

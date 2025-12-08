import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { SessionType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { Space, VenuesService } from '@admin-clients/cpanel/venues/data-access';
import { DateTimePickerComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { map, Observable, Subject } from 'rxjs';

@Component({
    selector: 'app-session-access-control-configuration',
    templateUrl: './session-access-control-configuration.component.html',
    styleUrls: ['./session-access-control-configuration.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, FlexLayoutModule, CommonModule, TranslatePipe,
        DateTimePickerComponent, FormControlErrorsComponent, ReactiveFormsModule,
        EllipsifyDirective
    ]
})
export class SessionAccessControlConfigurationComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    @Input() form: FormGroup;

    sessionType: SessionType;
    sessionTypes = SessionType;

    spaces$: Observable<Space[]>;

    constructor(
        private _venueSrv: VenuesService
    ) { }

    ngOnInit(): void {
        this.spaces$ = this._venueSrv.getVenue$().pipe(map(venue => venue?.spaces));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}

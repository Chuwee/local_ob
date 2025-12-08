import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { ATM_FEATURES_ROLES, ATM_VENDOR_ID } from '@admin-clients/cpanel/external/data-access';
import { AtmMemberPromotionsComponent, AtmMemberPriceZonesComponent } from '@admin-clients/cpanel/external/feature';
import { EventsService, Event } from '@admin-clients/cpanel/promoters/events/data-access';
import { EntitiesBaseService, EventType } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';

@Component({
    imports: [
        NgIf, AsyncPipe,
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        FormContainerComponent,
        AtmMemberPromotionsComponent,
        AtmMemberPriceZonesComponent
    ],
    selector: 'app-vendor-features',
    templateUrl: './vendor-features.component.html',
    styleUrls: ['./vendor-features.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VendorFeaturesComponent implements OnInit {
    showAtmFeatures$: Observable<boolean>;
    event$: Observable<Event>;

    readonly eventTypes = EventType;

    constructor(
        private _entitiesService: EntitiesBaseService,
        private _eventsService: EventsService,
        private _auth: AuthenticationService
    ) { }

    ngOnInit(): void {
        this.event$ = this._eventsService.event.get$();

        this.showAtmFeatures$ = this._auth.hasLoggedUserSomeRoles$(ATM_FEATURES_ROLES)
            .pipe(
                filter(allowed => !!allowed),
                switchMap(() => this._entitiesService.getEntity$()),
                filter(entity => !!entity),
                map(entity => entity.settings?.external_integration?.auth_vendor?.vendor_id?.includes(ATM_VENDOR_ID))
            );
    }

}

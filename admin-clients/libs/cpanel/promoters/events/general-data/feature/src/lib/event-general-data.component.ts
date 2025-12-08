import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { ATM_FEATURES_ROLES, ATM_VENDOR_ID } from '@admin-clients/cpanel/external/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EntityExternalBarcodesFormat, Entity, EntitiesBaseService, AttributeScope
} from '@admin-clients/shared/common/data-access';
import { BadgeComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { filter, first, map, switchMap } from 'rxjs/operators';

@Component({
    imports: [
        AsyncPipe,
        RouterModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        MaterialModule,
        TranslatePipe,
        LastPathGuardListenerDirective,
        BadgeComponent
    ],
    selector: 'app-event-general-data',
    templateUrl: './event-general-data.component.html',
    styleUrls: ['./event-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventGeneralDataComponent implements OnInit, OnDestroy {
    private readonly _route = inject(ActivatedRoute);
    private readonly _router = inject(Router);
    private readonly _eventsService = inject(EventsService);
    private readonly _auth = inject(AuthenticationService);
    private readonly _entitiesService = inject(EntitiesBaseService);

    readonly deepPath$ = getDeepPath$(this._router, this._route);
    readonly hasAttributes$ = this._entitiesService.getAttributes$()
        .pipe(
            filter(Boolean),
            map(attributes => !!attributes.length)
        );

    readonly showVendorFeatures$ = this._auth.hasLoggedUserSomeRoles$(ATM_FEATURES_ROLES)
        .pipe(
            filter(Boolean),
            switchMap(() => combineLatest([this._eventsService.event.get$(), this._entitiesService.getEntity$()])),
            filter(([event, entity]) => !!event && !!entity && entity.id === event.entity.id),
            map(([, entity]) => this.entityHasAtmVendorFeatures(entity))
        );

    readonly enableCodeConfiguration$ = this._entitiesService.getEntity$()
        .pipe(
            first(Boolean),
            map(entity => {
                const isExternalBarcodeEnabled = entity.settings?.external_integration?.barcode?.enabled;
                const externalBarcodeFormat: string | undefined = entity.settings?.external_integration?.barcode?.integration_id;
                return isExternalBarcodeEnabled && externalBarcodeFormat === EntityExternalBarcodesFormat.ifema;
            })
        );

    readonly isAvetWSEvent$ = this._eventsService.event.get$().pipe(first(Boolean), map(event => EventsService.isAvetWS(event)));

    ngOnInit(): void {
        this._eventsService.event.get$()
            .pipe(first(Boolean))
            .subscribe(event => this._entitiesService.loadAttributes(event.entity.id, AttributeScope.event));
    }

    ngOnDestroy(): void {
        this._entitiesService.clearAttributes();
    }

    private entityHasAtmVendorFeatures(entity: Entity): boolean {
        return entity.settings?.external_integration?.auth_vendor?.enabled &&
            entity.settings?.external_integration?.auth_vendor?.vendor_id?.includes(ATM_VENDOR_ID);
    }
}

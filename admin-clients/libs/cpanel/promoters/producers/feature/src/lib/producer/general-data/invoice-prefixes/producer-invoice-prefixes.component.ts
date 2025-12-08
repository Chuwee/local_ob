import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ProducersService, PutProducerDetails } from '@admin-clients/cpanel/promoters/producers/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { combineLatest, Subject } from 'rxjs';
import { first, map, tap } from 'rxjs/operators';
import {
    ProducerInvoicePrefixesSimplifiedComponent
} from './simplified/producer-invoice-prefixes-simplified.component';
import {
    ProducerInvoicePrefixesSimplifiedProvidersComponent
} from './simplified-providers/producer-invoice-prefixes-simplified-providers.component';

@Component({
    selector: 'app-producer-invoice-prefixes',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './producer-invoice-prefixes.component.html',
    imports: [
        AsyncPipe, NgIf,
        ProducerInvoicePrefixesSimplifiedProvidersComponent,
        ProducerInvoicePrefixesSimplifiedComponent
    ]
})
export class ProducerInvoicePrefixesComponent implements OnInit, OnDestroy {
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _auth = inject(AuthenticationService);
    private readonly _producerSrv = inject(ProducersService);

    private readonly _onDestroy = new Subject<void>();

    private _isExternalNotificationAllowed: boolean;

    readonly isExternalNotificationAllowed$ = combineLatest([
        this._entitiesSrv.getEntity$().pipe(map(entity => entity?.invoice_data?.allow_external_notification ?? false)),
        this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR])
    ]).pipe(
        map(([allowExternalNotification, hasSomeUserRoles]) => allowExternalNotification && hasSomeUserRoles),
        tap(value => this._isExternalNotificationAllowed = value)
    );

    readonly invoicePrefixesFormGroup = inject(UntypedFormBuilder)
        .group({
            simplifiedInvoice: [{ value: null, disabled: true }]
        });

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('invoicePrefixes')) {
            return;
        }
        value.addControl('invoicePrefixes', this.invoicePrefixesFormGroup, { emitEvent: false });
    }

    ngOnInit(): void {
        this._producerSrv.getProducer$()
            .pipe(first())
            .subscribe(producer => this._entitiesSrv.loadEntity(producer.entity.id));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._entitiesSrv.clearEntity();
        const parent = this.invoicePrefixesFormGroup.parent as UntypedFormGroup;
        parent.removeControl('invoicePrefixes');
    }

    getResult(result: PutProducerDetails): PutProducerDetails {
        if (this._isExternalNotificationAllowed || !this.invoicePrefixesFormGroup.dirty) {
            return result;
        } else {
            return {
                ...result,
                use_simplified_invoice: this.invoicePrefixesFormGroup.value.simplifiedInvoice
            };
        }
    }
}

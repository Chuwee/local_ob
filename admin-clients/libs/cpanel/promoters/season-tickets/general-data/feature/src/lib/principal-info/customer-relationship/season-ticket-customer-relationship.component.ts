import { PutSeasonTicket, SeasonTicket } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SubscriptionList, SubscriptionListStatus, SubscriptionListsService
} from '@admin-clients/cpanel/viewers/subscriptions/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe, NgFor } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, Input, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, first } from 'rxjs';

@Component({
    selector: 'app-season-ticket-customer-relationship',
    templateUrl: './season-ticket-customer-relationship.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        TranslatePipe,
        AsyncPipe,
        NgFor,
        EllipsifyDirective
    ]
})
export class SeasonTicketCustomerRelationshipComponent implements OnInit {
    private readonly _subscriptionListsService = inject(SubscriptionListsService);
    private readonly _destroyRef = inject(DestroyRef);

    private _seasonTicket: SeasonTicket;
    readonly customerRelationshipDataForm = inject(FormBuilder).nonNullable.group({
        subscription_list_id: { value: null as number, disabled: true }
    });

    @Input() putSeasonTicketCtrl: FormControl<PutSeasonTicket>;
    @Input() form: FormGroup;
    @Input() set seasonTicket(seasonTicket: SeasonTicket) {
        this._seasonTicket = seasonTicket;
        this.customerRelationshipDataForm.reset({
            subscription_list_id: seasonTicket.settings?.subscription_list.id || null
        });
    }

    get seasonTicket(): SeasonTicket {
        return this._seasonTicket;
    }

    entitySubscriptionListsBS = new BehaviorSubject<SubscriptionList[]>([]);

    ngOnInit(): void {
        this._subscriptionListsService.loadSubscriptionListsList({
            entityId: this._seasonTicket.entity.id,
            status: SubscriptionListStatus.active
        });
        this._subscriptionListsService.getSubscriptionListsListData$()
            .pipe(first(Boolean))
            .subscribe(subscriptions => {
                if (subscriptions.length) {
                    this.customerRelationshipDataForm.controls.subscription_list_id.enable();
                    this.entitySubscriptionListsBS.next(subscriptions);
                }
            });

        this.form.addControl('customer-relationship', this.customerRelationshipDataForm, { emitEvent: false });
        this.putSeasonTicketCtrl.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(putValues => {
                if (this.form.invalid) return;

                if (this.customerRelationshipDataForm.dirty) {
                    putValues.settings = putValues.settings ?? {};
                    putValues.settings.subscription_list = {
                        enable: !!this.customerRelationshipDataForm.controls.subscription_list_id,
                        id: this.customerRelationshipDataForm.controls.subscription_list_id.value
                    };
                    this.putSeasonTicketCtrl.setValue(putValues, { emitEvent: false });
                }
            });
    }
}

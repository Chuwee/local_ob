import { Event, PutEvent } from '@admin-clients/cpanel/promoters/events/data-access';
import { SubscriptionListsService, SubscriptionListStatus } from '@admin-clients/cpanel/viewers/subscriptions/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { first } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        EllipsifyDirective,
        FlexLayoutModule,
        MaterialModule,
        ReactiveFormsModule,
        TranslatePipe
    ],
    selector: 'app-event-principal-info-customer-relationship',
    templateUrl: './event-principal-info-customer-relationship.component.html'
})
export class EventPrincipalInfoCustomerRelationshipComponent implements OnInit, OnDestroy {
    private readonly _destroyRef = inject(DestroyRef);
    private readonly _subscriptionListsSrv = inject(SubscriptionListsService);

    private _event: Event;

    readonly customerRelationshipDataForm = inject(FormBuilder).group({
        subscriptionListId: { value: null as number, disabled: true }
    });

    readonly entitySubscriptionLists$ = this._subscriptionListsSrv.getSubscriptionListsListData$()
        .pipe(first(Boolean));

    @Input() putEventCtrl: FormControl<PutEvent>;
    @Input() form: FormGroup;
    @Input() set event(value: Event) {
        this._event = value;
        this.customerRelationshipDataForm.reset({
            subscriptionListId: value.settings.subscription_list.id
        }, { emitEvent: false });
    }

    ngOnInit(): void {
        this.form.addControl('customer-relationship', this.customerRelationshipDataForm, { emitEvent: false });
        this._subscriptionListsSrv.loadSubscriptionListsList({
            entityId: this._event.entity.id,
            status: SubscriptionListStatus.active
        });
        this._subscriptionListsSrv.getSubscriptionListsListData$()
            .pipe(first(Boolean))
            .subscribe(subscriptions => {
                if (subscriptions.length) {
                    this.customerRelationshipDataForm.controls.subscriptionListId.enable({ emitEvent: false });
                }
            });

        this.putEventCtrl.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(putEvent => {
                if (this.form.invalid) return;

                const { subscriptionListId } = this.customerRelationshipDataForm.controls;
                if (subscriptionListId.dirty) {
                    putEvent.settings = putEvent.settings ?? {};
                    putEvent.settings.subscription_list = putEvent.settings.subscription_list ?? { enable: false };
                    if (subscriptionListId.value) {
                        putEvent.settings.subscription_list = { enable: true, id: subscriptionListId.value };
                    }
                    this.putEventCtrl.setValue(putEvent, { emitEvent: false });
                }
            });
    }

    ngOnDestroy(): void {
        this._subscriptionListsSrv.clearSubscriptionListsList();
    }
}

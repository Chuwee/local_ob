import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    PostSubscriptionList, SubscriptionListFieldsRestrictions, SubscriptionListsService
} from '@admin-clients/cpanel/viewers/subscriptions/data-access';
import { EntitiesBaseService, EntitiesFilterFields, Entity } from '@admin-clients/shared/common/data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { take } from 'rxjs/operators';

@Component({
    selector: 'app-new-subscription-lists-dialog',
    templateUrl: './new-subscription-lists-dialog.component.html',
    styleUrls: ['./new-subscription-lists-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class NewSubscriptionListsDialogComponent extends ObDialog<NewSubscriptionListsDialogComponent, null, number> implements OnInit {

    private readonly _auth = inject(AuthenticationService);
    private readonly _entitiesService = inject(EntitiesBaseService);
    private readonly _subscriptionListService = inject(SubscriptionListsService);
    private readonly _fb = inject(FormBuilder);

    readonly entities$ = this._entitiesService.entityList.getData$();
    readonly subscriptionListSaving$ = this._subscriptionListService.isSubscriptionListLoading$();

    readonly newSubscriptionListForm = this._fb.group({
        entity: [null as Entity, Validators.required],
        name: [null as string, [Validators.required, Validators.maxLength(SubscriptionListFieldsRestrictions.subscriptionListNameLength)]],
        description: [null as string, [Validators.maxLength(SubscriptionListFieldsRestrictions.subscriptionListDescriptionLength)]]
    });

    readonly maxSubscriptionListNameLength = SubscriptionListFieldsRestrictions.subscriptionListNameLength;
    readonly maxSubscriptionListDescriptionLength = SubscriptionListFieldsRestrictions.subscriptionListDescriptionLength;

    constructor() {
        super(DialogSize.MEDIUM);
        this.dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this._auth.canReadMultipleEntities$()
            .pipe(take(1))
            .subscribe(canSelectEntity => {
                if (canSelectEntity) {
                    this._entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name]
                    });
                } else {
                    this.newSubscriptionListForm.controls.entity.disable({ emitEvent: false });
                }
            });
    }

    createSubscriptionList(): void {
        if (this.newSubscriptionListForm.valid) {
            const data = this.newSubscriptionListForm.value;
            const newSubscriptionList: PostSubscriptionList = {
                entity_id: data.entity?.id,
                name: data.name,
                description: data.description
            };
            this._subscriptionListService.createSubscriptionList(newSubscriptionList)
                .subscribe(id => this.dialogRef.close(id));
        } else {
            this.newSubscriptionListForm.markAllAsTouched();
        }
    }
}

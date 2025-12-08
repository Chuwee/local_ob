import { SubscriptionList, SubscriptionListsService } from '@admin-clients/cpanel/viewers/subscriptions/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { IdName } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Component({
    selector: 'app-buyer-subscription-list-sel-dialog',
    templateUrl: './buyer-subscription-list-sel-dialog.component.html',
    styleUrls: ['./buyer-subscription-list-sel-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class BuyerSubscriptionListSelDialogComponent implements OnInit {

    subscriptionLists$: Observable<SubscriptionList[]>;
    control: UntypedFormControl;

    constructor(
        private _dialogRef: MatDialogRef<BuyerSubscriptionListSelDialogComponent>,
        @Inject(MAT_DIALOG_DATA) private _data: { entityId: number; availableSubscriptionLists: number[] },
        private _fb: UntypedFormBuilder,
        private _subscriptionListsSrv: SubscriptionListsService
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this._subscriptionListsSrv.loadSubscriptionListsList({
            entityId: this._data.entityId
        });
        this.subscriptionLists$ = this._subscriptionListsSrv.getSubscriptionListsListData$()
            .pipe(
                filter(subscriptionLists => !!subscriptionLists),
                map(subscriptionLists =>
                    subscriptionLists
                        .filter(subscriptionList => !this._data.availableSubscriptionLists?.includes(subscriptionList.id))
                        .sort((a, b) => a.name.localeCompare(b.name))
                )
            );
        this.control = this._fb.control([], Validators.required);
    }

    commit(): void {
        this.close(this.control.value);
    }

    close(result: IdName[] = null): void {
        this._dialogRef.close(result);
    }
}

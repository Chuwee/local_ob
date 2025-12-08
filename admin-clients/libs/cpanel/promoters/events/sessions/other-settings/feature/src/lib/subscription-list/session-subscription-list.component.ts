import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, SessionSubscriptionList, SessionSubscriptionListScope
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { SubscriptionListsService, SubscriptionListStatus } from '@admin-clients/cpanel/viewers/subscriptions/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, first, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-session-subscription-list',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['../session-other-settings.component.scss'],
    templateUrl: './session-subscription-list.component.html',
    imports: [
        ReactiveFormsModule, MaterialModule, FlexLayoutModule, CommonModule,
        TranslatePipe, EllipsifyDirective
    ]
})
export class SessionSubscriptionListComponent implements OnInit, OnDestroy {
    private readonly _eventsSrv = inject(EventsService);
    private readonly _sessionsService = inject(EventSessionsService);
    private readonly _subscriptionListService = inject(SubscriptionListsService);
    private readonly _onDestroy = new Subject<void>();

    readonly entitySubscriptionLists$ = this._subscriptionListService.getSubscriptionListsListData$()
        .pipe(first(subscriptionLists => !!subscriptionLists));

    readonly sessionSubscriptionListScope = SessionSubscriptionListScope;
    readonly subscriptionListFormGroup = inject(UntypedFormBuilder)
        .group({
            subscriptionListScope: [null, [Validators.required]],
            subscriptionListId: [{ value: null, disabled: true }, [Validators.required]]
        });

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('subscriptionList')) {
            return;
        }
        value.addControl('subscriptionList', this.subscriptionListFormGroup, { emitEvent: false });
    }

    readonly markForCheck = ((): () => void => {
        const cdr = inject(ChangeDetectorRef);
        return () => cdr.markForCheck();
    })();

    ngOnInit(): void {
        this._eventsSrv.event.get$()
            .pipe(first(event => !!event))
            .subscribe(event => {
                this._subscriptionListService.loadSubscriptionListsList({
                    entityId: event.entity.id,
                    status: SubscriptionListStatus.active
                });
            });

        this.subscriptionListFormChangeHandler();
        this.updateSubscriptionListForm();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        const form = this.subscriptionListFormGroup.parent as UntypedFormGroup;
        form.removeControl('subscriptionList', { emitEvent: false });
    }

    getValue(): SessionSubscriptionList {
        return {
            scope: this.subscriptionListFormGroup.value.subscriptionListScope,
            id: this.subscriptionListFormGroup.value.subscriptionListScope === SessionSubscriptionListScope.session
                && this.subscriptionListFormGroup.value.subscriptionListId || null
        };
    }

    private subscriptionListFormChangeHandler(): void {
        this.subscriptionListFormGroup.get('subscriptionListScope').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((scope: SessionSubscriptionListScope) => {
                if (scope === SessionSubscriptionListScope.event) {
                    this.subscriptionListFormGroup.get('subscriptionListId').disable({ emitEvent: false });
                } else {
                    this.subscriptionListFormGroup.get('subscriptionListId').enable({ emitEvent: false });
                }
            });
    }

    private updateSubscriptionListForm(): void {
        this._sessionsService.session.get$()
            .pipe(filter(session => !!session))
            .subscribe(session => {
                this.subscriptionListFormGroup.patchValue({
                    subscriptionListScope: session.settings?.subscription_list?.scope,
                    subscriptionListId: session.settings?.subscription_list?.id
                }, { onlySelf: true });
                this.subscriptionListFormGroup.markAsPristine();
                this.subscriptionListFormGroup.markAsUntouched();
            });
    }
}

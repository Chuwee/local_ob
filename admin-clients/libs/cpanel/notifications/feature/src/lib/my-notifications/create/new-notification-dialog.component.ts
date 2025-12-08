import { scrollIntoFirstInvalidFieldOrErrorMsg, FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { NotificationFieldsRestriction, NotificationsService, PostNotification } from '@admin-clients/cpanel/notifications/data-access';
import { EventFieldsRestriction } from '@admin-clients/cpanel/promoters/events/data-access';
import { EntitiesBaseService, Entity, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { NgIf, NgFor, AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, OnDestroy, OnInit } from '@angular/core';
import { FlexModule } from '@angular/flex-layout/flex';
import { UntypedFormBuilder, UntypedFormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, combineLatest } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-new-notification-dialog',
    templateUrl: './new-notification-dialog.component.html',
    styleUrls: ['./new-notification-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexModule, MaterialModule, FormsModule, ReactiveFormsModule,
        NgIf, SelectSearchComponent, NgFor, FormControlErrorsComponent, TranslatePipe, AsyncPipe
    ]
})
export class NewNotificationDialogComponent implements OnInit, OnDestroy {

    private _onDestroy = new Subject();
    readonly canSelectEntity$ = this._auth.canReadMultipleEntities$();

    form: UntypedFormGroup;
    entities$: Observable<Entity[]>;
    maxEventNameLength = EventFieldsRestriction.eventNameLength;
    isLoading$: Observable<boolean>;

    constructor(
        private _dialogRef: MatDialogRef<NewNotificationDialogComponent>,
        private _auth: AuthenticationService,
        private _entitiesService: EntitiesBaseService,
        private _notificationsService: NotificationsService,
        private _fb: UntypedFormBuilder,
        private _elemRef: ElementRef
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.form = this._fb.group({
            entity: [null, Validators.required],
            name: [null, [Validators.required, Validators.maxLength(NotificationFieldsRestriction.notificationNameLength)]]
        });

        this.entities$ = combineLatest([
            this._auth.getLoggedUser$().pipe(first(Boolean)),
            this.canSelectEntity$
        ]).pipe(
            switchMap(([user, canSelectEntity]) => {
                if (canSelectEntity) {
                    this._entitiesService.entityList.load({
                        limit: 999,
                        offset: 0,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name],
                        allow_massive_email: true
                    });
                    return this._entitiesService.entityList.getData$();
                } else {
                    this._entitiesService.loadEntity(user.entity.id);
                    return this._entitiesService.getEntity$().pipe(
                        filter(Boolean),
                        map(entity => [entity])
                    );
                }
            }),
            tap((entities: Entity[]) => {
                if (entities && entities.length === 1) {
                    this.form.patchValue({ entity: entities[0] });
                }
            }),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );
        this.entities$.subscribe();

        this.isLoading$ = booleanOrMerge([
            this._notificationsService.isNotificationSaving$(),
            this._entitiesService.entityList.inProgress$()
        ]);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    createNotification(): void {
        if (this.form.valid) {
            const notification: PostNotification = {
                entity_id: this.form.value.entity.id,
                name: this.form.value.name
            };
            this._notificationsService.createNotification(notification).subscribe(code => this.close(code));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this._elemRef.nativeElement);
        }
    }

    close(notificationCode: { code: string } = null): void {
        this._dialogRef.close(notificationCode);
    }
}

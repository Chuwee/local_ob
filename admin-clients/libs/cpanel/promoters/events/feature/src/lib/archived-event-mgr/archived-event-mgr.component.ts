import { EventsService, EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { MessageDialogService, DialogSize, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { filter, map, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-archived-event-mgr',
    templateUrl: './archived-event-mgr.component.html',
    styleUrls: ['./archived-event-mgr.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule,
        FlexLayoutModule,
        TranslatePipe,
        MatSlideToggleModule,
        AsyncPipe
    ]
})
export class ArchivedEventMgrComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _archivableStatuses = [EventStatus.finished, EventStatus.cancelled, EventStatus.notAccomplished];
    private _disabled: boolean;
    private _eventId: number;
    visible$: Observable<boolean>;
    form: UntypedFormGroup;

    get disabled(): boolean {
        return this._disabled;
    }

    @Input()
    set disabled(value: BooleanInput) {
        this._disabled = coerceBooleanProperty(value);
        this.updateFormEnabled();
    }

    constructor(
        private _fb: UntypedFormBuilder,
        private _messageDialogSrv: MessageDialogService,
        private _ephemeralMessageService: EphemeralMessageService,
        private _eventSrv: EventsService
    ) {
    }

    ngOnInit(): void {
        // form definition
        this.form = this._fb.group({
            archived: null
        });
        // update enabled status, can be setted externally before form definition
        this.updateFormEnabled();
        // visible updates, is conditioned by event status
        this.visible$ = this._eventSrv.event.get$()
            .pipe(map(event => this._archivableStatuses.includes(event?.status)));
        // event archived value form setting
        this._eventSrv.event.get$()
            .pipe(
                filter(event => !!event),
                takeUntil(this._onDestroy)
            )
            .subscribe(event => {
                this._eventId = event.id;
                this.form.get('archived').setValue(event.archived, { emitEvent: false });
            });
        // archived form changes, this triggers the archived change process
        this.form.get('archived').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(archive => this.archiveEvent(archive));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private updateFormEnabled(): void {
        if (this._disabled) {
            this.form?.disable({ emitEvent: false });
        } else {
            this.form?.enable({ emitEvent: false });
        }
    }

    private archiveEvent(archive: boolean): void {
        this._messageDialogSrv.showWarn({
            size: DialogSize.MEDIUM,
            title: archive ? 'EVENTS.ARCHIVATE_DIALOG_TITLE' : 'EVENTS.DEARCHIVATE_DIALOG_TITLE',
            message: archive ? 'EVENTS.ARCHIVATE_DIALOG_MSG' : 'EVENTS.DEARCHIVATE_DIALOG_MSG',
            actionLabel: archive ? 'EVENTS.ARCHIVATE_BTN' : 'EVENTS.DEARCHIVATE_BTN'
        })
            .subscribe(result => {
                if (!result) {
                    // reverts the form change on cancel
                    this.form.get('archived').setValue(!archive, { emitEvent: false });
                } else {
                    // saves the event with archived change
                    this._eventSrv.event.update(this._eventId, { archived: archive })
                        .subscribe(() => {
                            // notification snackbar
                            this._ephemeralMessageService.showSaveSuccess();
                            // reloads the event
                            this._eventSrv.event.load(String(this._eventId));
                        });
                }
            });
    }
}

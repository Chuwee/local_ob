import { inject, Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable, Subject, Subscription } from 'rxjs';
import { filter, map, switchMap, tap } from 'rxjs/operators';
import { DialogSize } from '../dialog/models/dialog-size.enum';
import { EphemeralMessageService } from '../ephemeral-message/ephemeral-message.service';
import { MessageType } from '../models/message-type.model';
import { MessageDialogComponent } from './message-dialog.component';
import {
    DeleteConfirmationConfig, MessageDialogConfig, MessageDialogConfigBase, MessageDialogConfigWithSecondary, MessageDialogSecondaryValue,
    ObMatDialogConfig
} from './models/message-dialog.model';
import { UnsavedChangesDialogConfig, UnsavedChangesDialogResult } from './models/unsaved-changes-dialog.model';
import { UnsavedChangesDialogComponent } from './unsaved-changes-dialog/unsaved-changes-dialog.component';

@Injectable({ providedIn: 'root' })
export class MessageDialogService {
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    #currentDialog: MatDialogRef<MessageDialogComponent>;
    #pendingDialogs: Subject<void>[] = [];
    #currentSubscription: Subscription;

    constructor() { }

    /**
     * default discard changes warning dialog.
     * Shows a simple small warn dialog with TITLES.DISCARD_CHANGES_WARNING title and FORMS.DISCARD_CHANGES text, yes no buttons
     * Only triggers subscription when user answers "Discard Changes".
     * The observable always returns true (useless but simplier this way)
     */
    defaultDiscardChangesWarn(): Observable<boolean> {
        return this.showWarn({
            size: DialogSize.MEDIUM,
            title: 'TITLES.DISCARD_CHANGES_WARNING',
            message: 'FORMS.DISCARD_CHANGES',
            actionLabel: 'FORMS.ACTIONS.DISCARD_CHANGES',
            cancelLabel: 'FORMS.ACTIONS.BACK'
        })
            .pipe(filter(Boolean));
    }

    openRichUnsavedChangesWarn(data?: UnsavedChangesDialogConfig): Observable<UnsavedChangesDialogResult> {
        data = {
            size: DialogSize.MEDIUM,
            type: MessageType.alert,
            title: 'NAV_ACTIONS.UNSAVED_CHANGES_DIALOG_TITTLE',
            message: 'NAV_ACTIONS.UNSAVED_CHANGES_DIALOG_MSG',
            ...data
        };

        const dialogConfig = new ObMatDialogConfig(data);
        const currentDialog = this.#matDialog
            .open<UnsavedChangesDialogComponent, UnsavedChangesDialogConfig, UnsavedChangesDialogResult>(
                UnsavedChangesDialogComponent, dialogConfig
            );
        this.#currentSubscription = currentDialog.beforeClosed().subscribe(() => this.showPendingDialog());
        return currentDialog.beforeClosed();
    }

    /**
     * default unsaved changes warning dialog.
     * Shows a simple small warn dialog with TITLES.NOTICE title and ACTIONS.UNSAVED_CHANGES text, cancel return buttons
     * subscription result (param) is inverted, true-cancel false-return
     */
    defaultUnsavedChangesWarn(messageDialogConfig?: MessageDialogConfig): Observable<boolean> {
        const config = Object.assign(
            {
                size: DialogSize.MEDIUM,
                title: 'NAV_ACTIONS.UNSAVED_CHANGES_DIALOG_TITTLE',
                message: 'NAV_ACTIONS.UNSAVED_CHANGES_DIALOG_MSG',
                actionLabel: 'FORMS.ACTIONS.GO_BACK',
                showCancelButton: true,
                cancelLabel: 'FORMS.ACTIONS.DONT_SAVE',
                invertSuccess: true
            },
            messageDialogConfig || {});
        return this.showWarn(config).pipe(map(value => !!value));
    }

    /**
     * #### Shows a delete confirmation dialog.
     * if user accepts it then subscribes to delete$,
     * then if delete$ doesn't give an exception/error shows a delete success message
     *
     * - `sucess` is optional, when not included a default message wil be shown
     * - `confirmation` is optional, when not included a default message wil be shown
     */
    showDeleteConfirmation<T>({ confirmation, success, delete$ }: DeleteConfirmationConfig<T>): Observable<T> {
        const obs = this.showWarn({
            title: 'FORMS.ACTIONS.DELETE_ITEM_TITLE', // generic title
            message: 'FORMS.ACTIONS.DELETE_ITEM_MESSAGE', // generic message
            ...confirmation,
            showCancelButton: true,
            size: DialogSize.SMALL,
            actionLabel: 'FORMS.ACTIONS.DELETE'
        })
            .pipe(
                filter(isConfirmed => !!isConfirmed),
                switchMap(() => delete$),
                tap(() => success ? this.#ephemeralMessage.showSuccess(success) : this.#ephemeralMessage.showDeleteSuccess())
            );
        obs.subscribe();
        return obs;
    }

    /**
     * Error messages, with orange cross icon
     * @param messageDialogConfig dialog configuration
     */
    showAlert(messageDialogConfig: MessageDialogConfigBase): Observable<boolean> {
        messageDialogConfig.type = MessageType.warn;
        return this.showDialog(messageDialogConfig);
    }

    /**
     * warning messages, with yellow exclamation icon, used to request an action to the user (cancel and ok by defautl)
     * @param messageDialogConfig dialog configuration
     */
    showWarn<T extends MessageDialogConfig>(
        messageDialogConfig: T
    ): Observable<T extends MessageDialogConfigWithSecondary ? boolean | MessageDialogSecondaryValue : boolean> {
        return this.showDialog({
            ...messageDialogConfig,
            showCancelButton: messageDialogConfig.showCancelButton ?? true,
            type: MessageType.alert
        });
    }

    /**
     * Info messages, with blue i(info) icon
     * @param messageDialogConfig dialog configuration
     */
    showInfo(messageDialogConfig: MessageDialogConfigBase): Observable<boolean> {
        messageDialogConfig.type = MessageType.info;
        return this.showDialog(messageDialogConfig);
    }

    /**
     * Success messages, with green check icon
     * @param messageDialogConfig dialog configuration
     */
    showSuccess(messageDialogConfig: MessageDialogConfigBase): Observable<boolean> {
        messageDialogConfig.type = MessageType.success;
        return this.showDialog(messageDialogConfig);
    }

    listenErrorMessages(messages$: Observable<{ message: string; subMessages?: string[] }>): void {
        messages$.subscribe(messages => {
            this.showAlert({
                size: DialogSize.SMALL,
                title: 'TITLES.ERROR_DIALOG',
                message: messages.message,
                subMessages: messages.subMessages
            });
        });
    }

    showDialog<T extends MessageDialogConfig>(
        messageDialogConfig: T
    ): Observable<T extends MessageDialogConfigWithSecondary ? boolean | MessageDialogSecondaryValue : boolean> {
        if (this.#currentDialog == null) {
            return this.showDialogWithoutCheck<T>(messageDialogConfig);
        } else {
            const dialogSubject = new Subject<void>();
            this.#pendingDialogs.push(dialogSubject);
            return dialogSubject.asObservable()
                .pipe(switchMap(() => this.showDialogWithoutCheck(messageDialogConfig)));
        }
    }

    private showDialogWithoutCheck<
        T extends MessageDialogConfig,
        R = T extends MessageDialogConfigWithSecondary ? boolean | MessageDialogSecondaryValue : boolean
    >(messageDialogConfig: T): Observable<R> {
        const dialogConfig = new ObMatDialogConfig({
            ...messageDialogConfig,
            actionLabel: messageDialogConfig.actionLabel || 'FORMS.ACTIONS.OK',
            type: messageDialogConfig.type || MessageType.warn
        });
        this.#currentDialog = this.#matDialog.open<MessageDialogComponent, T, R>(MessageDialogComponent, dialogConfig);
        this.#currentSubscription = this.#currentDialog.afterClosed().subscribe(() => this.showPendingDialog());
        return this.#currentDialog.afterClosed();
    }

    private showPendingDialog(): void {
        if (this.#currentSubscription !== null) {
            this.#currentSubscription.unsubscribe();
            this.#currentSubscription = null;
        }
        if (this.#currentDialog !== null) {
            this.#currentDialog = null;
        }
        if (this.#pendingDialogs.length) {
            this.#pendingDialogs[0].next(null);
            this.#pendingDialogs[0].complete();
            this.#pendingDialogs = this.#pendingDialogs.slice(1);
        }
    }
}

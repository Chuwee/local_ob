import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService, ChannelBlacklistType, ChannelBlacklistItem } from '@admin-clients/cpanel/channels/data-access';
import { DialogSize, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { Component, OnInit, ChangeDetectionStrategy, Inject, OnDestroy } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UntypedFormBuilder, UntypedFormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule, MatIconButton } from '@angular/material/button';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { first, switchMap } from 'rxjs/operators';
import { ChannelOperativeService } from '../../channel-operative.service';

@Component({
    selector: 'app-create-blacklist-item-dialog',
    templateUrl: './create-blacklist-item-dialog.component.html',
    styleUrls: ['./create-blacklist-item-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe,
        ReactiveFormsModule,
        FlexLayoutModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatProgressSpinner,
        TranslatePipe,
        FormControlErrorsComponent,
        MatIcon, MatIconButton
    ]
})
export class CreateBlacklistItemDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _type: ChannelBlacklistType;

    form: UntypedFormGroup;
    isSaving$: Observable<boolean>;

    constructor(
        private _dialogRef: MatDialogRef<CreateBlacklistItemDialogComponent>,
        private _channelsService: ChannelsService,
        private _channelOperativeService: ChannelOperativeService,
        private _ephemeralMessageService: EphemeralMessageService,
        private _fb: UntypedFormBuilder,
        @Inject(MAT_DIALOG_DATA) private _data?: { type: ChannelBlacklistType }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this._type = this._data.type;
    }

    ngOnInit(): void {
        // init form
        this.form = this._fb.group({
            value: [null, [Validators.required]]
        });

        this.isSaving$ = this._channelOperativeService.isChannelBlacklistItemInProgress$();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(isSaved = false): void {
        this._dialogRef.close(isSaved);
    }

    save(): void {
        if (this.form.valid) {
            this._channelsService.getChannel$()
                .pipe(
                    first(channel => !!channel),
                    switchMap(channel => {
                        const formData = this.form.value;
                        const blacklistItem: ChannelBlacklistItem = {
                            value: formData.value,
                            creation_date: new Date().toISOString() // eq. to moment()
                        };

                        return this._channelOperativeService.createChannelBlacklist(channel.id, this._type, [blacklistItem]);
                    })
                )
                .subscribe(() => {
                    this._ephemeralMessageService.showSaveSuccess();
                    this.close(true);
                });
        } else {
            this.form.markAllAsTouched();
        }
    }

    getPlaceholderValue(): string {
        switch (this._type) {
            case ChannelBlacklistType.email:
                return 'user@domain.com';
            case ChannelBlacklistType.nif:
                return '12345678X';
            default:
                return 'value';
        }
    }
}

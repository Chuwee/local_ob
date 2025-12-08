import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, Inject, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatInput } from '@angular/material/input';
import { Observable } from 'rxjs';
import {
    ChannelPromotionsService,
    ChannelPromotionFieldRestrictions, ChannelPromotionType, PostChannelPromotion
} from '@admin-clients/cpanel-channels-promotions-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';

@Component({
    selector: 'app-new-channel-promotion-dialog',
    templateUrl: './new-promotion-dialog.component.html',
    styleUrls: ['./new-promotion-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class NewPromotionDialogComponent implements OnInit, AfterViewInit {
    @ViewChild(MatInput) private _input: MatInput;

    readonly nameRestrictions = ChannelPromotionFieldRestrictions;
    readonly creationTypes = ChannelPromotionType;

    form: UntypedFormGroup;
    saving$: Observable<boolean>;

    constructor(
        private _dialogRef: MatDialogRef<NewPromotionDialogComponent>,
        private _channelPromotionsService: ChannelPromotionsService,
        private _fb: UntypedFormBuilder,
        private _elemRef: ElementRef,
        @Inject(MAT_DIALOG_DATA) private _data: {
            entityId: number;
            channelId: number;
        }
    ) {
        this._dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.form = this._fb.group({
            name: [null, [
                Validators.required,
                Validators.minLength(this.nameRestrictions.minNameLength),
                Validators.maxLength(this.nameRestrictions.maxNameLength)
            ]],
            type: [null, [Validators.required]]
        });

        this.saving$ = this._channelPromotionsService.isPromotionInProgress$();
    }

    ngAfterViewInit(): void {
        // focus first input improves UX
        setTimeout(() => this._input.focus(), 500);
    }

    create(): void {
        if (this.form.valid) {
            const data = this.form.value;
            const promo: PostChannelPromotion = {
                name: data.name,
                type: data.type
            };
            this._channelPromotionsService.createPromotion(this._data.channelId, promo)
                .subscribe(id => this.close(id));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this._elemRef.nativeElement);
        }
    }

    close(promotionId: number = null): void {
        this._dialogRef.close(promotionId);
    }
}

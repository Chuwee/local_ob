import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelsExtendedService
} from '@admin-clients/cpanel/channels/data-access';
import {
    Chip,
    ChipsComponent, CollectionInputComponent, DateTimeModule,
    DialogSize,
    ObDialog,
    SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    inject,
    OnInit,
    ViewChild
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Component({
    selector: 'app-channel-member-periods-dates-filter-dialog',
    templateUrl: './channel-member-periods-dates-filter-dialog.component.html',
    styleUrls: ['channel-member-periods-dates-filter-dialog.componen.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule,
        CommonModule, MatDividerModule, FlexLayoutModule,
        SearchablePaginatedSelectionModule, ChipsComponent, CollectionInputComponent, FormControlErrorsComponent, DateTimeModule
    ]
})
export class ChannelMemberPeriodsDatesFilterDialogComponent
    extends ObDialog<
        ChannelMemberPeriodsDatesFilterDialogComponent,
        { channelId: number },
        { access: { user: string[]; date: Date } }
    > implements OnInit {

    @ViewChild(CollectionInputComponent) private _collectionInputComponent: CollectionInputComponent;
    readonly #currentAccessUsers = new BehaviorSubject<string[]>([]);

    readonly #channelService = inject(ChannelsExtendedService);
    readonly #fb = inject(FormBuilder);

    readonly currentAccessUsers$ = this.#currentAccessUsers.asObservable();
    readonly form = this.#fb.group({
        users: [[] as string[], Validators.required],
        date: [null as Date, Validators.required]
    });

    readonly isLoading$ = this.#channelService.isChannelEventsLoading$();

    readonly currentAmountsChips$ = this.currentAccessUsers$.pipe(
        filter(Boolean),
        map(amounts =>
            amounts.map(amount => ({ label: amount, value: amount })))
    );

    constructor() {
        super(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.form.controls.users.valueChanges
            .subscribe((users: string[]) => this.#currentAccessUsers.next(users));
    }

    removeChip(chip: Chip): void {
        const index = chip.value;
        const currentAccessUsers = this.form.controls.users.value;
        const newAccessUsers = currentAccessUsers.filter(user => user !== index);

        this.form.controls.users.patchValue(newAccessUsers);
        this.form.controls.users.markAsDirty();
        this._collectionInputComponent.createElement.markAsTouched();
        this.#currentAccessUsers.next(newAccessUsers);
    }

    close(): void {
        this.dialogRef.close();
    }

    save(): void {
        this.dialogRef.close({
            access: { user: this.form.controls.users.value, date: this.form.controls.date.value }
        });
    }

}

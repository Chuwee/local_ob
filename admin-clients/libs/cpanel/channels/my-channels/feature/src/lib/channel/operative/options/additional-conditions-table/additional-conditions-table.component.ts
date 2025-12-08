import { Channel, AdditionalCondition } from '@admin-clients/cpanel/channels/data-access';
import {
    ObMatDialogConfig, DialogSize, MessageDialogService,
    EphemeralMessageService, ContextNotificationComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { DragDropModule, CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, ViewChild, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormArray, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatTable } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, switchMap } from 'rxjs';
import { ChannelOperativeService } from '../../channel-operative.service';
import { AdditionalConditionsDialogComponent } from '../additional-conditions-dialog/additional-conditions-dialog.component';

@Component({
    selector: 'app-additional-conditions-table',
    templateUrl: './additional-conditions-table.component.html',
    styleUrls: ['./additional-conditions-table.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, FlexLayoutModule,
        TranslatePipe, ReactiveFormsModule,
        CommonModule, ContextNotificationComponent, DragDropModule, PrefixPipe
    ]
})
export class AdditionalConditionsTableComponent {

    @ViewChild('conditionsTable')
    private _conditionsTable: MatTable<AdditionalCondition>;

    private readonly _breakpointObserver = inject(BreakpointObserver);
    private readonly _msgDialogService = inject(MessageDialogService);
    private readonly _ephemeralMessageService = inject(EphemeralMessageService);
    private readonly _matDialog = inject(MatDialog);
    private readonly _channelOperativeService = inject(ChannelOperativeService);

    readonly MAX_ADDITIONAL_CONDITIONS = 6;
    readonly columns = ['condition-enabled', 'name', 'condition-required', 'actions'];

    readonly isHandsetOrTablet$ = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    @Input() data: AdditionalCondition[];
    @Input() form: FormArray;
    @Input() channel: Channel;

    onDropCondition(event: CdkDragDrop<string[]>): void {
        moveItemInArray(this.data, event.previousIndex, event.currentIndex);
        moveItemInArray(this.form.controls, event.previousIndex, event.currentIndex);
        this._conditionsTable.renderRows();
        this.form.markAsDirty();
    }

    openAdditionalConditionDialog(condition?: AdditionalCondition): void {
        this._matDialog.open(AdditionalConditionsDialogComponent, new ObMatDialogConfig(condition))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(() => this.loadAdditionalConditions());
    }

    openDeleteConditionDialog(condition: AdditionalCondition): void {
        this._msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'CHANNELS.OPTIONS.DELETE_CONDITION_TITLE',
            message: 'CHANNELS.OPTIONS.DELETE_CONDITION_MSG',
            messageParams: { conditionName: condition.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this._channelOperativeService.deleteAdditionalCondition(this.channel.id, condition.id))
            )
            .subscribe(() => {
                this._ephemeralMessageService.showSuccess({
                    msgKey: 'CHANNELS.OPTIONS.DELETE_CONDITION_SUCCESS',
                    msgParams: { conditionName: condition.name }
                });
                this.loadAdditionalConditions();
            });
    }

    private loadAdditionalConditions(): void {
        this._channelOperativeService.loadAdditionalConditions(this.channel.id);
    }

}

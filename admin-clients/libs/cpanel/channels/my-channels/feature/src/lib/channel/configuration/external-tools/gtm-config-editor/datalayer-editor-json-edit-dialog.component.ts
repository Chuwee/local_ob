import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelsExtendedService, ChannelExternalToolName } from '@admin-clients/cpanel/channels/data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { jsonValidator } from '@admin-clients/shared/utility/utils';
import { CodeEditorComponent } from '@admin-clients/shared-common-ui-code-editor';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import {
    MAT_DIALOG_DATA,
    MatDialogRef
} from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        FormControlErrorsComponent,
        CodeEditorComponent
    ],
    selector: 'app-datalayer-edit-dialog',
    templateUrl: './datalayer-editor-json-edit-dialog.component.html',
    styleUrls: ['./datalayer-editor-json-edit-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DatalayerEditorJsonEditDialogComponent extends ObDialog<
    DatalayerEditorJsonEditDialogComponent, { channelId: number; externalTool: ChannelExternalToolName }, boolean
> implements OnInit {

    private readonly _onDestroy = new Subject<void>();
    private readonly _channelsService = inject(ChannelsExtendedService);
    private readonly _channelId = inject<{ channelId: number; externalTool: ChannelExternalToolName }>(MAT_DIALOG_DATA).channelId;
    readonly externalTool = inject<{ channelId: number; externalTool: ChannelExternalToolName }>(MAT_DIALOG_DATA).externalTool;

    readonly control = new FormControl<string>('{}', [Validators.required, jsonValidator]);
    override dialogRef = inject(MatDialogRef<DatalayerEditorJsonEditDialogComponent>);
    constructor() {
        super(DialogSize.EXTRA_LARGE);
    }

    ngOnInit(): void {
        this._channelsService.externalTools.get$().pipe(
            filter(Boolean),
            takeUntil(this._onDestroy)
        ).subscribe(externalTools => {
            const datalayerConfig = externalTools.find(toolConfig => toolConfig.name === this.externalTool);
            const json = datalayerConfig?.additional_config?.find(item =>
                item.id === (this.externalTool === 'ADOBE_DTM' ? 'adobe_dtm_config' : 'gtm_config'))?.value ?? '{}';
            this.control.patchValue(JSON.stringify(JSON.parse(json), null, 4));
        });
    }

    commit(): void {
        if (this.control.valid) {
            const json = JSON.parse(this.control.value);
            this._channelsService.externalTools.update(this._channelId,
                this.externalTool, {
                additional_config: [{
                    id: this.externalTool === 'ADOBE_DTM' ? 'adobe_dtm_config' : 'gtm_config',
                    value: JSON.stringify(json)
                }]
            }).subscribe(() => {
                this.dialogRef.close(true);
            });
        }
    }
}

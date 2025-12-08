
import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { PostTerminal, TerminalFieldRestrictions, TerminalsService } from '@admin-clients/cpanel-channels-terminals-data-access';
import {
    EntitiesBaseService, EntitiesFilterFields, EntityStatus, GetEntitiesRequest
} from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, ObDialog,
    SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { IdName } from '@admin-clients/shared/data-access/models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        ReactiveFormsModule,
        TranslatePipe,
        FormControlErrorsComponent,
        FlexLayoutModule,
        SelectServerSearchComponent
    ],
    selector: 'app-new-terminal-dialog',
    templateUrl: './new-terminal-dialog.component.html',
    styleUrls: ['./new-terminal-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewTerminalDialogComponent extends ObDialog<NewTerminalDialogComponent, null, number> {

    private readonly _fb = inject(FormBuilder);
    private readonly _ephemeralMsgSrv = inject(EphemeralMessageService);
    private readonly _terminalsSrv = inject(TerminalsService);
    private readonly _entitiesSrv = inject(EntitiesBaseService);

    readonly form = this._fb.group({
        entity: [null as IdName, Validators.required],
        code: [null as string,
        [
            Validators.required,
            Validators.maxLength(TerminalFieldRestrictions.maxCodeLength),
            Validators.pattern(TerminalFieldRestrictions.codePattern)
        ]
        ],
        name: ['', [Validators.required, Validators.maxLength(TerminalFieldRestrictions.maxNameLength)]],
        licenseEnabled: [true, Validators.required]
    });

    readonly isSaving$ = this._terminalsSrv.terminal.inProgress$();
    readonly entities$ = this._entitiesSrv.entityList.getData$();
    readonly moreEntitiesAvailable$ = this._entitiesSrv.entityList.getMetadata$()
        .pipe(map(metadata => !metadata || metadata.total > metadata.offset + metadata.limit));

    constructor() {
        super(DialogSize.MEDIUM);
    }

    loadEntities({ q, nextPage }: { q?: string; nextPage?: boolean }): void {
        const request: GetEntitiesRequest = {
            type: 'CHANNEL_ENTITY',
            fields: [EntitiesFilterFields.name],
            status: [EntityStatus.active]
        };
        if (!nextPage) {
            this._entitiesSrv.entityList.load({ ...request, q });
        } else {
            this._entitiesSrv.entityList.loadMore({ ...request, q });
        }
    }

    createTerminal(): void {
        if (this.form.valid) {
            const value = this.form.value;
            const terminal: PostTerminal = {
                entity_id: value.entity.id,
                name: value.name,
                code: value.code,
                type: 'BOX_OFFICE',
                license_enabled: value.licenseEnabled
            };
            this._terminalsSrv.terminal.create(terminal).subscribe(id => {
                this._ephemeralMsgSrv.showSuccess({ msgKey: 'TERMINALS.FORMS.FEEDBACK.CREATE_TERMINAL_SUCCESS' });
                this.dialogRef.close(id.id);
            });
        } else {
            this.form.markAllAsTouched();
        }
    }
}

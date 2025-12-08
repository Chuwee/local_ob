import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, take } from 'rxjs/operators';
import { VenueTplEditorBlocksSetupService } from '../../venue-tpl-editor-blocks-setup.service';
import { DeletableFormItem } from '../deletable-form-item';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        SharedUtilityDirectivesModule
    ],
    selector: 'app-venue-tpl-editor-blocks-setup-form',
    templateUrl: './venue-tpl-editor-blocks-setup-form.component.html',
    styleUrls: ['../venue-tpl-editor-properties-forms-common-styles.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorBlocksSetupFormComponent implements DeletableFormItem {
    private readonly _messageDialogSrv = inject(MessageDialogService);
    private readonly _blocksSrv = inject(VenueTplEditorBlocksSetupService);

    readonly blockChangesSelected$ = this._blocksSrv.getSelectedBlockChanges$()
        .pipe(map(blockChanges => blockChanges?.length > 0 && blockChanges || null));

    readonly noSelection$ = this._blocksSrv.getSelectedBlockChanges$()
        .pipe(map(blockChanges => !(blockChanges?.length > 0)));

    deleteFormItem(): void {
        this.blockChangesSelected$
            .pipe(
                take(1),
                filter(blockChanges => blockChanges?.length > 0)
            )
            .subscribe(blockChanges => {
                this._messageDialogSrv.showWarn({
                    size: DialogSize.SMALL,
                    title: blockChanges.length === 1 ?
                        'VENUE_TPL_EDITOR.DELETE_INTERSECTION_WARNING_TITLE' : 'VENUE_TPL_EDITOR.DELETE_AISLE_WARNING_TITLE',
                    message: blockChanges.length === 1 ?
                        'VENUE_TPL_EDITOR.DELETE_INTERSECTION_WARNING' : 'VENUE_TPL_EDITOR.DELETE_AISLE_WARNING',
                    actionLabel: 'FORMS.ACTIONS.DELETE'
                })
                    .pipe(filter(Boolean))
                    .subscribe(() => this._blocksSrv.deleteAisle());
            });
    }
}

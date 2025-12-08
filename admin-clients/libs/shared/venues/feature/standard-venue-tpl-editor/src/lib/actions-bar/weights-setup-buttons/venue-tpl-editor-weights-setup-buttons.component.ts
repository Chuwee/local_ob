import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { EditorMode } from '../../models/venue-tpl-editor-modes.enum';
import { VenueTplEditorWeightsSetupService } from '../../venue-tpl-editor-weights-setup.service';
import { VenueTplEditorService } from '../../venue-tpl-editor.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe
    ],
    selector: 'app-venue-tpl-editor-weights-setup-buttons',
    templateUrl: './venue-tpl-editor-weights-setup-buttons.component.html',
    styleUrls: ['./venue-tpl-editor-weights-setup-buttons.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorWeightsSetupButtonsComponent {

    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _weightsSetupSrv = inject(VenueTplEditorWeightsSetupService);

    readonly pendingConfigurationToSet$ = this._weightsSetupSrv.getWeightsConfiguration$().pipe(map(conf => !conf?.type));

    setBaseMode(): void {
        this._editorSrv.modes.setEditorMode(EditorMode.base);
    }

    commitWeightsConfiguration(): void {
        this._weightsSetupSrv.commitConfiguration();
        this.setBaseMode();
    }
}

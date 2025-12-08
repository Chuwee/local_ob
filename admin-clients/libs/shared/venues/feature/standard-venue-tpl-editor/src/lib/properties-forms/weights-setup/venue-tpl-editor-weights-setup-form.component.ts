import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { VenueTplEditorWeightsConfigurationType } from '../../models/venue-tpl-editor-weights-configuration';
import { VenueTplEditorWeightsSetupService } from '../../venue-tpl-editor-weights-setup.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        SharedUtilityDirectivesModule
    ],
    selector: 'app-venue-tpl-editor-weights-setup-form',
    templateUrl: './venue-tpl-editor-weights-setup-form.component.html',
    styleUrls: ['../venue-tpl-editor-properties-forms-common-styles.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorWeightsSetupFormComponent implements OnInit, OnDestroy {

    private readonly _onDestroy = new Subject<void>();

    private readonly _weightsSetupSrv = inject(VenueTplEditorWeightsSetupService);
    private readonly _fb = inject(FormBuilder);

    readonly config$ = this._weightsSetupSrv.getWeightsConfiguration$();
    readonly types = Object.values(VenueTplEditorWeightsConfigurationType);
    readonly formControl = this._fb.control(null as VenueTplEditorWeightsConfigurationType);

    ngOnInit(): void {
        this.formControl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(type => this._weightsSetupSrv.setWeightsConfiguration({ type }));
    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }

    setWeightsSetupMode(): void {
        this._weightsSetupSrv.setWeightsConfiguration({ type: null });
    }
}

import { ChipsComponent, CollectionInputComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import {
    VenueTemplateElementInfoAction,
    VenueTemplateElementInfoContents, VenueTemplateElementInfoDetail
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter,
    Input, OnDestroy, OnInit, Output, inject
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Subject, filter, map, startWith, takeUntil } from 'rxjs';

interface Chip {
    key?: string;
    label: string;
    value?: unknown;
    valueText?: string;
}
@Component({
    selector: 'app-edit-element-info-3d-view',
    imports: [
        TranslatePipe,
        MaterialModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        ChipsComponent,
        CollectionInputComponent
    ],
    templateUrl: './edit-element-info-3d-view.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditElementInfo3dViewComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _current3dViewCodes = new BehaviorSubject<string[]>([]);

    private readonly _ref = inject(ChangeDetectorRef);
    private readonly _fb = inject(FormBuilder);

    readonly elements = this._fb.control({ value: null as string[], disabled: true }, Validators.required);

    readonly info3dViewFormGroup = this._fb.group({
        enabled: false,
        codes: this.elements
    });

    @Input()
    set form(value: FormGroup) {
        value.addControl(VenueTemplateElementInfoContents.view3d, this.info3dViewFormGroup, { emitEvent: false });
    }

    @Input() currentAction: VenueTemplateElementInfoAction;

    @Input() singleCode: string;

    @Output()
    readonly deleteContent = new EventEmitter<VenueTemplateElementInfoContents>();

    @Input() set defaultInfo(elementInfo: VenueTemplateElementInfoDetail) {
        this.info3dViewFormGroup.controls.enabled.patchValue(elementInfo?.default_info?.config_3D?.enabled);
        if (this.allowCodeConfiguration()) {
            const config3dCodes = elementInfo?.default_info?.config_3D?.codes;
            if (config3dCodes) {
                this.info3dViewFormGroup.controls.codes.patchValue(config3dCodes);
                this._current3dViewCodes.next(this.info3dViewFormGroup.controls.codes.value);
            }
        }
    }

    readonly current3dViewCodes$ = this._current3dViewCodes.asObservable();
    readonly current3dViewCodesChips$ = this.current3dViewCodes$.pipe(
        filter(Boolean),
        map(codes => codes.map((code, index) => ({ label: code, value: index })))
    );

    ngOnInit(): void {
        if (this.singleCode && !this.info3dViewFormGroup.controls.codes.value) {
            this.info3dViewFormGroup.controls.codes.patchValue([this.singleCode]);
        }
        if (this.allowCodeConfiguration()) {
            this.info3dViewFormGroup.controls.enabled.valueChanges
                .pipe(
                    startWith(this.info3dViewFormGroup.controls.enabled.value),
                    takeUntil(this._onDestroy)
                )
                .subscribe(enabled => {
                    if (enabled) {
                        this.info3dViewFormGroup.controls.codes.enable();
                    } else {
                        this.info3dViewFormGroup.controls.codes.disable();
                    }
                });
        }

        this.elements.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((currentCodes: string[]) =>
                this._current3dViewCodes.next(currentCodes));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    markForCheck(): void {
        this._ref.markForCheck();
    }

    getValue(): { enabled: boolean; codes?: string[] } {
        const info3dViewValue: { enabled: boolean; codes?: string[] } = { enabled: this.info3dViewFormGroup.controls.enabled.value };

        if (this.allowCodeConfiguration() && this.info3dViewFormGroup.controls.enabled.value) {
            info3dViewValue.codes = this.info3dViewFormGroup.controls.codes.value;
        }
        return info3dViewValue;
    }

    deleteContentComponent(): void {
        this.deleteContent.emit(VenueTemplateElementInfoContents.view3d);
    }

    allowCodeConfiguration(): boolean {
        return this.currentAction === VenueTemplateElementInfoAction.editSingle
            || this.currentAction === VenueTemplateElementInfoAction.createSingle;
    }

    removeChip(chip: Chip): void {
        if (this.elements.enabled) {
            const index = chip.value as number;
            const currentCodes = this.info3dViewFormGroup.controls.codes.value;

            currentCodes.splice(index, 1);

            this.info3dViewFormGroup.controls.codes.patchValue(currentCodes);
            this._current3dViewCodes.next(currentCodes);
        }
    }
}

import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    CopyTextComponent, DialogSize, MessageDialogService, ObDialogService, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { noDuplicateValuesAsyncValidator } from '@admin-clients/shared/utility/utils';
import { VenueTemplateFieldsRestrictions } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild, ViewContainerRef } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AsyncValidatorFn, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, firstValueFrom, of, Subject, switchMap } from 'rxjs';
import { filter, map, shareReplay, take, takeUntil, tap, withLatestFrom } from 'rxjs/operators';
import { DeleteItemsAction } from '../../actions/delete-items-action';
import { EditNnzAction } from '../../actions/edit-nnz-action';
import { VenueTplEditorSvgEditDialogComponent } from '../../dialogs/svg-code/venue-tpl-editor-svg-edit-dialog.component';
import { CapacityEditionCapability } from '../../models/venue-tpl-editor-capacity-edition-capability';
import { EdSector } from '../../models/venue-tpl-editor-venue-map-items.model';
import { IdGenerator } from '../../utils/editor-id-generator.utils';
import { VenueTplEditorDomService } from '../../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../../venue-tpl-editor-selection.service';
import { VenueTplEditorVenueMapService } from '../../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../../venue-tpl-editor.service';
import { DeletableFormItem } from '../deletable-form-item';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        FormControlErrorsComponent,
        SharedUtilityDirectivesModule,
        SelectSearchComponent,
        CopyTextComponent
    ],
    selector: 'app-venue-tpl-editor-nnz-form',
    templateUrl: './venue-tpl-editor-nnz-form.component.html',
    styleUrls: ['../venue-tpl-editor-properties-forms-common-styles.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorNnzFormComponent implements OnInit, OnDestroy, DeletableFormItem {
    private readonly _onDestroy = new Subject<void>();
    private readonly _fb = inject(FormBuilder);
    private readonly _dialogSrv = inject(ObDialogService);
    private readonly _viewContainerRef = inject(ViewContainerRef);
    private readonly _messageDialogSrv = inject(MessageDialogService);
    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _selectionSrv = inject(VenueTplEditorSelectionService);
    private readonly _viewSrv = inject(VenueTplEditorViewsService);
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);

    private readonly _otherNNZsNames$ = combineLatest([
        this._selectionSrv.getSelection$(),
        this._venueMapSrv.getVenueItems$(),
        this._venueMapSrv.getVenueMap$()
    ])
        .pipe(map(([selection, items, venueMap]) => {
            const selectedNNZ = items.nnzs.get(Array.from(selection.nnzs.values())[0]);
            return venueMap.sectors.find(sector => sector.id === selectedNNZ?.sector)
                ?.notNumberedZones.filter(nnz => !nnz.delete && nnz !== selectedNNZ)
                .map(nnz => nnz.name);
        }));

    private readonly _nnz$ = this._selectionSrv.getSelection$()
        .pipe(
            withLatestFrom(this._venueMapSrv.getVenueItems$()),
            map(([selection, venueItems]) => venueItems.nnzs.get(Array.from(selection.nnzs.values())[0])),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    private readonly _nnzIncreaseInitialCapacity$ = this._editorSrv.getCapacityEditionCapability$().pipe(
        switchMap(editionCapability => editionCapability === CapacityEditionCapability.increase ? this._nnz$ : of(null)),
        map(nnz => {
            if (nnz) {
                nnz.initialCapacity ??= nnz.capacity;
                return nnz.initialCapacity;
            } else {
                return undefined;
            }
        })
    );

    readonly operatorMode$ = this._editorSrv.modes.getOperatorMode$();

    @ViewChild('sectorsSelectSearch')
    readonly sectorSelectSearchComponent: SelectSearchComponent<EdSector>;

    readonly nnz$ = this._nnz$;

    readonly sectors$ = this._venueMapSrv.getVenueMap$().pipe(map(venueMap => venueMap.sectors.filter(sector => !sector.delete)));

    readonly form = this._fb.group({
        name: [
            '',
            [Validators.required, Validators.maxLength(VenueTemplateFieldsRestrictions.zoneNameLength)],
            [noDuplicateValuesAsyncValidator(this._otherNNZsNames$)]
        ],
        capacity: [null as number, [Validators.required], [this.noDecrementValidator()]],
        sector: [null as number, Validators.required]
    });

    ngOnInit(): void {
        this.nnz$
            .pipe(
                withLatestFrom(this._editorSrv.getCapacityEditionCapability$()),
                takeUntil(this._onDestroy)
            )
            .subscribe(([nnz, editionCapability]) => {
                this.form.patchValue({
                    name: nnz?.name,
                    capacity: nnz?.capacity,
                    sector: nnz?.sector
                }, { emitEvent: false });
                if (editionCapability === CapacityEditionCapability.denied) {
                    this.form.controls.capacity.disable({ emitEvent: false });
                } else {
                    this.form.controls.capacity.enable({ emitEvent: false });
                }
                if (IdGenerator.isTempId(nnz?.id)) {
                    this.form.controls.sector.enable({ emitEvent: false });
                } else {
                    this.form.controls.sector.disable({ emitEvent: false });
                }
                this.form.markAllAsTouched();
            });
        this.form.valueChanges
            .pipe(
                withLatestFrom(this.nnz$, this._nnzIncreaseInitialCapacity$),
                takeUntil(this._onDestroy)
            )
            .subscribe(([value, nnz, initialCapacity]) => {
                if (initialCapacity !== undefined && value.capacity < initialCapacity) {
                    value.capacity = initialCapacity;
                }
                this._editorSrv.history.enqueue(new EditNnzAction({ ...nnz, ...value }, this._venueMapSrv));
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }

    async fixIncreaseCapacity(): Promise<void> {
        const initialCapacity = await firstValueFrom(this._nnzIncreaseInitialCapacity$);
        if (initialCapacity !== undefined && this.form.controls.capacity.value < initialCapacity) {
            this.form.controls.capacity.setValue(initialCapacity, { emitEvent: false });
        }
    }

    deleteFormItem(): void {
        this.nnz$
            .pipe(
                take(1),
                // delete capability check
                switchMap(nnz =>
                    this._editorSrv.getCapacityEditionCapability$()
                        .pipe(
                            take(1),
                            map(editionCapability =>
                                editionCapability === CapacityEditionCapability.increase ?
                                    IdGenerator.isTempId(nnz.id) : editionCapability === CapacityEditionCapability.total
                            ),
                            tap(canDelete => {
                                if (!canDelete) {
                                    this._messageDialogSrv.showAlert({
                                        size: DialogSize.SMALL,
                                        title: 'VENUE_TPL_EDITOR.CAPACITY_DECREMENT_NOT_AVAILABLE_BY_SESSIONS_ERROR_TITLE',
                                        message: 'VENUE_TPL_EDITOR.CAPACITY_DECREMENT_NOT_AVAILABLE_BY_SESSIONS_ERROR'
                                    });
                                }
                            }),
                            filter(Boolean),
                            map(() => nnz)
                        )
                ),
                // delete warning
                switchMap(nnz =>
                    this._messageDialogSrv.showWarn({
                        size: DialogSize.SMALL,
                        title: 'TITLES.ALERT',
                        message: 'VENUE_TPL_EDITOR.DELETE_NNZ_WARNING',
                        messageParams: { nnzName: nnz.name }
                    })
                        .pipe(
                            filter(Boolean),
                            map(() => nnz)
                        )
                )
            )
            .subscribe(nnz => {
                this._editorSrv.history.enqueue(
                    new DeleteItemsAction({ nnzIds: [nnz.id] }, this._venueMapSrv, this._viewSrv, this._domSrv, this._selectionSrv)
                );
            });
    }

    openEditSvgDialog(): void {
        this.nnz$.pipe(take(1))
            .subscribe(nnz => this._dialogSrv.open(VenueTplEditorSvgEditDialogComponent, { target: nnz }, this._viewContainerRef));
    }

    private noDecrementValidator(): AsyncValidatorFn {
        return control => this._nnzIncreaseInitialCapacity$.pipe(
            take(1),
            map(initialCapacity => control.value < initialCapacity ? { minCapacityError: initialCapacity } : null)
        );
    }
}

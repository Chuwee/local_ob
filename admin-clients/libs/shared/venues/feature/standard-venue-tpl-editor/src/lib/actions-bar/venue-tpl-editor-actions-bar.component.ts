import {
    ImageCropperDialogComponent, MessageDialogService, ObMatDialogConfig, openDialog
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ObFileDimensions } from '@admin-clients/shared/data-access/models';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, DestroyRef, ElementRef, HostListener, inject, OnInit, viewChild, ViewContainerRef
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatMenu } from '@angular/material/menu';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { EditElementsGroupingAction } from '../actions/edit-elements-grouping-action';
import { EditElementsOrderAction } from '../actions/edit-elements-order-action';
import { EditIncreaseModeAction } from '../actions/edit-increase-mode-action';
import { NewImageAction } from '../actions/new-image-action';
import { NewShapeAction } from '../actions/new-shape-action';
import { VenueTplEditorUploadSvgDialogComponent } from '../dialogs/insert-svg/venue-tpl-editor-upload-svg-dialog.component';
import { SVGDefs } from '../models/SVGDefs.enum';
import { CapacityEditionCapability } from '../models/venue-tpl-editor-capacity-edition-capability';
import { EditorMode, InteractionMode, VisualizationMode } from '../models/venue-tpl-editor-modes.enum';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorImagesService } from '../venue-tpl-editor-images.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../venue-tpl-editor.service';
import { VenueTplEditorBlocksSetupButtonsComponent } from './blocks-setup-buttons/venue-tpl-editor-blocks-setup-buttons.component';
import { VenueTplEditorSeatMatrixButtonsComponent } from './seat-matrix-buttons/venue-tpl-editor-seat-matrix-buttons.component';
import { VenueTplEditorWeightsSetupButtonsComponent } from './weights-setup-buttons/venue-tpl-editor-weights-setup-buttons.component';

type CapacityIncreaseButtonState = 'inProgress' | 'inSetup' | 'enabled' | 'disabledByEventState';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        VenueTplEditorSeatMatrixButtonsComponent,
        VenueTplEditorBlocksSetupButtonsComponent,
        VenueTplEditorWeightsSetupButtonsComponent
    ],
    selector: 'app-venue-tpl-editor-actions-bar',
    templateUrl: './venue-tpl-editor-actions-bar.component.html',
    styleUrls: ['./venue-tpl-editor-actions-bar.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorActionsBarComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    // service dependencies
    readonly #dialogSrv = inject(MatDialog);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #venueTplSrv = inject(VenueTemplatesService);
    readonly #editorSrv = inject(VenueTplEditorService);
    readonly #selectionSrv = inject(VenueTplEditorSelectionService);
    readonly #viewsSrv = inject(VenueTplEditorViewsService);
    readonly #imageSrv = inject(VenueTplEditorImagesService);
    readonly #domSrv = inject(VenueTplEditorDomService);
    readonly #viewContainerRef = inject(ViewContainerRef);
    readonly #fb = inject(FormBuilder);
    // list of non-interactive elements, empty when any interactive element is selected
    readonly #selectedElements$ = this.#selectionSrv.getSelection$()
        .pipe(map(si =>
            !(si.nnzs?.size > 0)
            && !(si.seats?.size > 0)
            && !si.elements.some(el => el.classList.contains(SVGDefs.classes.interactive))
            && si.elements || []));

    private readonly _svgInput = viewChild<ElementRef<HTMLInputElement>>('svgInput');

    private readonly _imageInput = viewChild<ElementRef<HTMLInputElement>>('imageInput');

    #openedMenu: MatMenu;

    readonly editorModes = EditorMode;

    readonly form = this.#fb.group({
        lock: this.#fb.group({
            design: false,
            interactive: false
        }),
        show: this.#fb.group({
            blocks: false,
            weights: false,
            names: false
        })
    });

    readonly mode$ = this.#editorSrv.modes.getEditorMode$();

    readonly undoDisabled$ = this.#editorSrv.history.hasUndoSteps$().pipe(map(v => !v));
    readonly redoDisabled$ = this.#editorSrv.history.hasRedoSteps$().pipe(map(v => !v));

    readonly showReorder$ = this.#selectionSrv.getSelection$()
        .pipe(map(selection => selection.elements?.length || selection.nnzs?.size || selection.seats?.size));

    readonly showGroup$ = this.#selectedElements$
        .pipe(map(elements => elements.length > 1));

    readonly showUngroup$ = this.#selectedElements$
        .pipe(map(elements => elements?.length === 1 && elements[0] instanceof SVGGElement));

    readonly isInUse$ = this.#editorSrv.inUse.get$();

    readonly seatMatrixModeAvailable$
        = this.#editorSrv.getCapacityEditionCapability$().pipe(map(capability => capability !== CapacityEditionCapability.denied));

    readonly capacityIncreaseButtonState$: Observable<CapacityIncreaseButtonState> = combineLatest([
        this.#editorSrv.capacityIncrease.isEnabled$(),
        this.#editorSrv.capacityIncrease.isInSetup(),
        this.#editorSrv.capacityIncrease.isInProgress$()
    ])
        .pipe(
            map(([isEnabled, isInSetup, isInProgress]) =>
                isInProgress ? 'inProgress' :
                    isInSetup ? 'inSetup' :
                        isEnabled ? 'enabled' :
                            'disabledByEventState'
            )
        );

    ngOnInit(): void {
        // lock elements logic input
        this.#editorSrv.modes.getInteractionMode$().pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(mode => {
                this.#selectionSrv.unselectAll();
                this.form.controls.lock.patchValue({
                    design: mode === InteractionMode.interactive,
                    interactive: mode === InteractionMode.graphic
                }, { emitEvent: false });
            });
        this.#editorSrv.modes.getVisualizationModes$().pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(modes => {
            modes = modes || [];
            this.form.controls.show.setValue({
                blocks: modes.includes(VisualizationMode.blocks),
                weights: modes.includes(VisualizationMode.weights),
                names: modes.includes(VisualizationMode.names)
            }, { emitEvent: false });
        });
        // output
        this.form.controls.lock.controls.design.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(lockDsg => this.#editorSrv.modes.setInteractionMode(lockDsg ? InteractionMode.interactive : InteractionMode.all));
        this.form.controls.lock.controls.interactive.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(lockInt => this.#editorSrv.modes.setInteractionMode(lockInt ? InteractionMode.graphic : InteractionMode.all));
        this.form.controls.show.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(show => {
                this.#editorSrv.modes.setVisualizationModes([
                    show.blocks ? VisualizationMode.blocks : null,
                    show.weights ? VisualizationMode.weights : null,
                    show.names ? VisualizationMode.names : null
                ].filter(Boolean));
            });
    }

    setOpenedMenu(menu: MatMenu): void {
        this.#openedMenu = menu;
    }

    @HostListener('window:mousedown', ['$event'])
    closeMenus(event: KeyboardEvent): void {
        if (this.#openedMenu && !this.isOverlay(event.target as HTMLElement)) {
            this.#openedMenu.closed.emit('click');
            this.#openedMenu = null;
        }
    }

    isOverlay(element: HTMLElement): boolean {
        return element?.classList.contains('cdk-overlay-pane') || (!!element.parentElement && this.isOverlay(element.parentElement));
    }

    @HostListener('window:keydown.control.y', ['$event'])
    @HostListener('window:keydown.control.z', ['$event'])
    @HostListener('window:keydown.control.shift.z', ['$event'])
    historyKeyboardShortcuts(event: KeyboardEvent): void {
        const target = event.target as HTMLElement;
        if (target.tagName !== 'INPUT' && target.tagName !== 'TEXTAREA' && !target.isContentEditable) {
            if (event.code === 'KeyZ') {
                if (event.shiftKey) {
                    this.redoAction();
                } else {
                    this.undoAction();
                }
            } else if (event.code === 'KeyY') {
                this.redoAction();
            }
        }
    }

    undoAction(): void {
        this.#selectionSrv.unselectAll();
        this.#editorSrv.history.undo().subscribe(result => this.#processHistoryChange(result));
    }

    redoAction(): void {
        this.#selectionSrv.unselectAll();
        this.#editorSrv.history.redo().subscribe();
    }

    changeControlValue(formControl: FormControl<boolean>): void {
        formControl.setValue(!formControl.value);
    }

    openSelectSVGFileDialog(): void {
        this._svgInput().nativeElement.value = null;
        this._svgInput().nativeElement.click();
    }

    openSelectImageFileDialog(): void {
        this._imageInput().nativeElement.value = null;
        this._imageInput().nativeElement.click();
    }

    openInsertSvgDialog(fileInput: EventTarget): void {
        this.#selectionSrv.unselectAll();
        openDialog(this.#dialogSrv, VenueTplEditorUploadSvgDialogComponent, { fileInput }, this.#viewContainerRef);
    }

    openInsertImageDialog(event: EventTarget): void {
        this.#selectionSrv.unselectAll();
        if (event instanceof HTMLInputElement && event.files.length === 1) {
            const file = event.files[0];
            const reader = new FileReader();
            reader.onload = () => this.#addImage(file, reader.result);
            reader.readAsDataURL(file);
        }
    }

    setSeatMatrixMode(): void {
        this.#selectionSrv.unselectAll();
        this.#editorSrv.modes.setEditorMode(EditorMode.seatMatrixCreate);
    }

    setBlocksSetupMode(): void {
        this.#selectionSrv.unselectAll();
        this.#editorSrv.modes.setEditorMode(EditorMode.blocksSetup);
    }

    setWeightsSetupMode(): void {
        this.#selectionSrv.unselectAll();
        this.#editorSrv.modes.setEditorMode(EditorMode.weightsSetup);
    }

    groupSelectedItems(): void {
        this.#editorSrv.history.enqueue(new EditElementsGroupingAction(true, this.#viewsSrv, this.#domSrv, this.#selectionSrv));
    }

    ungroupSelectedItems(): void {
        this.#editorSrv.history.enqueue(new EditElementsGroupingAction(false, this.#viewsSrv, this.#domSrv, this.#selectionSrv));
    }

    moveSelectionToTop(): void {
        this.#editorSrv.history.enqueue(new EditElementsOrderAction('top', this.#viewsSrv, this.#domSrv, this.#selectionSrv));
    }

    moveSelectionUp(): void {
        this.#editorSrv.history.enqueue(new EditElementsOrderAction('up', this.#viewsSrv, this.#domSrv, this.#selectionSrv));
    }

    moveSelectionDown(): void {
        this.#editorSrv.history.enqueue(new EditElementsOrderAction('down', this.#viewsSrv, this.#domSrv, this.#selectionSrv));
    }

    moveSelectionToBottom(): void {
        this.#editorSrv.history.enqueue(new EditElementsOrderAction('bottom', this.#viewsSrv, this.#domSrv, this.#selectionSrv));
    }

    addShape(shape: 'rect' | 'text'): void {
        this.#editorSrv.history.enqueue(new NewShapeAction(shape, this.#viewsSrv, this.#domSrv, this.#selectionSrv));
    }

    setInCapacityIncrease(): void {
        this.#editorSrv.history.enqueue(new EditIncreaseModeAction(this.#editorSrv));
        this.#selectionSrv.refreshSelection();
    }

    #addImage(file: File, data: string | ArrayBuffer): void {
        if (typeof data === 'string') {
            this.#dialogSrv.open(ImageCropperDialogComponent, new ObMatDialogConfig({
                file: {
                    name: file.name,
                    size: file.size,
                    contentType: file.type,
                    data: data.split(',')[1]
                },
                imageRestrictions: { maxWidth: 720, maxHeight: 720, size: 204800 }
            }))
                .afterClosed()
                .pipe(map(result => result as ObFileDimensions))
                .subscribe(obFile => {
                    this.#editorSrv.history.enqueue(
                        new NewImageAction(obFile, this.#venueTplSrv, this.#imageSrv, this.#viewsSrv, this.#domSrv, this.#selectionSrv)
                    );
                });
        }
    }

    #processHistoryChange(result: boolean): void {
        if (!result) {
            this.#msgDialogSrv.showWarn({
                title: 'VENUE_TPL_EDITOR.FORMS.INFOS.UNDO_FIX_CHANGE_WARNING_TITLE',
                message: 'VENUE_TPL_EDITOR.FORMS.INFOS.UNDO_FIX_CHANGE_WARNING'
            })
                .pipe(filter(Boolean))
                .subscribe(() => this.#editorSrv.history.fixUndo().subscribe());
        }
    }
}

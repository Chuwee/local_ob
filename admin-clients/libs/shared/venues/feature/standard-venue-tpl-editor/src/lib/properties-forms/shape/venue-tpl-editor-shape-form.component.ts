import {
    ColorPickerComponent,
    CopyTextComponent, DialogSize, MessageDialogService, ObDialogService
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewContainerRef } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Element, G, Image } from '@svgdotjs/svg.js';
import { Observable, Subject, switchMap } from 'rxjs';
import { debounceTime, filter, map, take, takeUntil } from 'rxjs/operators';
import { DeleteItemsAction } from '../../actions/delete-items-action';
import { EditSvgAction } from '../../actions/edit-svg-action';
import { VenueTplEditorLinkDialogComponent } from '../../dialogs/link/venue-tpl-editor-link-dialog.component';
import { VenueTplEditorNnzDialogComponent } from '../../dialogs/nnz/venue-tpl-editor-nnz-dialog.component';
import { VenueTplEditorSvgEditDialogComponent } from '../../dialogs/svg-code/venue-tpl-editor-svg-edit-dialog.component';
import { CapacityEditionCapability } from '../../models/venue-tpl-editor-capacity-edition-capability';
import { VenueTplEditorDomService } from '../../venue-tpl-editor-dom.service';
import { VenueTplEditorImagesService } from '../../venue-tpl-editor-images.service';
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
        SharedUtilityDirectivesModule,
        CopyTextComponent,
        ColorPickerComponent
    ],
    selector: 'app-venue-tpl-editor-shape-form',
    templateUrl: './venue-tpl-editor-shape-form.component.html',
    styleUrls: ['../venue-tpl-editor-properties-forms-common-styles.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorShapeFormComponent implements OnInit, OnDestroy, DeletableFormItem {
    private readonly _onDestroy = new Subject<void>();
    private readonly _viewContainerRef = inject(ViewContainerRef);
    private readonly _dialogSrv = inject(ObDialogService);
    private readonly _msgSrv = inject(MessageDialogService);
    private readonly _fb = inject(FormBuilder);
    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _selectionSrv = inject(VenueTplEditorSelectionService);
    private readonly _viewSrv = inject(VenueTplEditorViewsService);
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _imagesSrv = inject(VenueTplEditorImagesService);

    private _element: Element;

    readonly operatorMode$ = this._editorSrv.modes.getOperatorMode$();

    readonly newNnzDisabled$ = this._editorSrv.getCapacityEditionCapability$().pipe(map(ec => ec === CapacityEditionCapability.denied));

    readonly form = this._fb.group({
        shape: this._fb.group({
            fillColor: '',
            strokeColor: '',
            strokeWidth: [0, Validators.min(0)]
        }),
        text: ['', Validators.required],
        opacity: [0, [Validators.required, Validators.min(0), Validators.max(100)]]
    });

    readonly items$ = this._selectionSrv.getSelection$().pipe(map(selection => selection.elements));

    readonly imageUrl$ = this.items$
        .pipe(
            map(items => items?.length === 1 && items[0] instanceof SVGImageElement
                && (items[0].getAttribute('href') || items[0].getAttribute('xlink:href')) || null
            )
        );

    readonly selectionType$: Observable<'rect' | 'circle' | 'shape' | 'text' | 'group' | 'image' | 'multiple'> = this.items$.pipe(
        map(items => {
            if (items.length === 1) {
                const item = items[0];
                return item instanceof SVGRectElement ? 'rect' :
                    item instanceof SVGCircleElement ? 'circle' :
                        item instanceof SVGTextElement ? 'text' :
                            item instanceof SVGGElement ? 'group' :
                                item instanceof SVGImageElement ? 'image' : 'shape';
            } else {
                return 'multiple';
            }
        })
    );

    ngOnInit(): void {
        this.items$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(items => {
                if (items?.length === 1) {
                    this.form.enable({ emitEvent: false });
                    this.updateFormValues(items[0]);
                } else {
                    this.form.disable({ emitEvent: false });
                }
            });
        //TODO: remove debounce when undo actions could be merged
        this.form.valueChanges
            .pipe(debounceTime(200), takeUntil(this._onDestroy))
            .subscribe(() => this.updateElementAttributes());
    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }

    deleteFormItem(): void {
        this._msgSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.ALERT',
            message: 'VENUE_TPL_EDITOR.DELETE_SHAPE_WARNING'
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.items$),
                take(1)
            )
            .subscribe(items => {
                this._editorSrv.history.enqueue(
                    new DeleteItemsAction(
                        { elements: items }, this._venueMapSrv, this._viewSrv, this._domSrv, this._selectionSrv, this._imagesSrv
                    )
                );
            });
    }

    openEditSvgDialog(): void {
        this.items$.pipe(take(1))
            .subscribe(items => {
                if (items.length === 1) {
                    this._dialogSrv.open(VenueTplEditorSvgEditDialogComponent, { target: items[0] }, this._viewContainerRef);
                }
            });
    }

    convertTo(type: 'nnz' | 'link'): void {
        if (type === 'nnz') {
            this._dialogSrv.open(VenueTplEditorNnzDialogComponent, null, this._viewContainerRef);
        } else {
            this._dialogSrv.open(VenueTplEditorLinkDialogComponent, null, this._viewContainerRef);
        }
    }

    private updateFormValues(item: SVGElement): void {
        this.form.controls.shape.disable({ emitEvent: false });
        this.form.controls.text.disable({ emitEvent: false });
        if (item instanceof SVGGElement) {
            this._element = new G(item);
        } else if (item instanceof SVGImageElement) {
            this._element = new Image(item);
        } else if (item instanceof SVGCircleElement
            || item instanceof SVGRectElement
            || item instanceof SVGPathElement
            || item instanceof SVGTextElement) {
            this.form.controls.shape.enable({ emitEvent: false });
            const element = this._element = new Element(item as SVGElement);
            this.cleanStyleAttr(item, this._element);
            if (item instanceof SVGTextElement) {
                this.form.controls.text.enable({ emitEvent: false });
            }
            this.form.patchValue({
                shape: {
                    fillColor: element.fill(),
                    strokeColor: element.stroke(),
                    strokeWidth: Number(element.node.getAttribute('stroke-width')) // so ugly I know
                },
                text: item.textContent?.trim()
            }, { emitEvent: false });
        }
        this.form.patchValue({
            opacity: this._element.opacity() * 100
        }, { emitEvent: false });
    }

    // convert style values to attribute values, style attributes overrides attribute, but svgjs works with attributes
    private cleanStyleAttr(svgElement: SVGElement, element: Element): void {
        if (svgElement.style) {
            const fillColor = this.toHexColor(svgElement.style.fill);
            const strokeColor = this.toHexColor(svgElement.style.stroke);// || svgElement.getAttribute('stroke'));
            const strokeWidth = Number(svgElement.style.strokeWidth) || 0;// || svgElement.getAttribute('stroke-width')) || 0;
            const opacity = Number(svgElement.style.opacity);
            svgElement.style.fill = null;
            svgElement.style.stroke = null;
            svgElement.style.strokeWidth = null;
            svgElement.style.opacity = null;
            if (!svgElement.style.length) {
                svgElement.removeAttribute('style');
            }
            if (strokeWidth || strokeColor) {
                element.stroke({ color: strokeColor, width: strokeWidth || 0 });
            }
            if (fillColor) {
                element.fill({ color: fillColor });
            }
            if (opacity) {
                element.opacity(opacity);
            }
        }
        if (svgElement instanceof SVGTextElement) {
            if (!svgElement.hasAttribute('space')) {
                svgElement.setAttribute('space', 'preserve');
            }
            const span = svgElement.children.item(0) as SVGTSpanElement;
            if (svgElement.getAttribute('x')) {
                if (svgElement.getAttribute('x') !== '0') {
                    span.setAttribute('x', String(Number(span.getAttribute('x')) + Number(svgElement.getAttribute('x'))));
                }
                svgElement.removeAttribute('x');
            }
            if (svgElement.getAttribute('y')) {
                if (svgElement.getAttribute('y') !== '0') {
                    span.setAttribute('y', String(Number(span.getAttribute('y')) + Number(svgElement.getAttribute('y'))));
                }
                svgElement.removeAttribute('y');
            }
        }
    }

    private toHexColor(color: string): string {
        if (!color || color === 'none') {
            return null;
        } else if (color.includes('rgb(')) {
            color = color.replace('rgb(', '').replace(')', '');
            const colorValues = color.split(',').map(val => Number(val));
            return '#' + colorValues.map(val => val.toString(16)).join('');
        } else if (Number(color)?.toString() === color) {
            return '#' + Number(color).toString(16);
        } else {
            return color;
        }
    }

    private updateElementAttributes(): void {
        if (this._element) {
            const value = this.form.value;
            if (value.shape) {
                this._element.stroke({ color: value.shape.strokeColor, width: value.shape.strokeWidth || 0 });
                this._element.fill({ color: value.shape.fillColor });
            }
            this._element.opacity(value.opacity / 100);
            if (value.text) {
                this.items$
                    .pipe(take(1))
                    .subscribe(items => {
                        if (items.length === 1) {
                            const text = items[0].children.item(0) as SVGTSpanElement;
                            text.innerHTML = value.text;
                            this._selectionSrv.refreshSelectionAppearance();
                        }
                    });
            }
            this._editorSrv.history.enqueue(
                new EditSvgAction(this._viewSrv, this._domSrv, this._selectionSrv, { elementsToSelect: [this._element.node] })
            );
        }
    }
}

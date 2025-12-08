import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize, MessageDialogService, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { VenueTemplateItemType } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { CodeEditorComponent } from '@admin-clients/shared-common-ui-code-editor';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { filter, map, take, withLatestFrom } from 'rxjs/operators';
import xmlFormat from 'xml-formatter';
import { EditSvgAction } from '../../actions/edit-svg-action';
import { SVGDefs } from '../../models/SVGDefs.enum';
import { defaultSVGCloseTag, defaultSVGOpenTag } from '../../models/venue-tpl-editor-svg-data.model';
import { VenueTplEditorSvgTriggerType } from '../../models/venue-tpl-editor-svg-trigger-type.enum';
import { EdNotNumberedZone, EdSeat } from '../../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorDomService } from '../../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../../venue-tpl-editor-selection.service';
import { VenueTplEditorViewsService } from '../../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../../venue-tpl-editor.service';

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
    selector: 'app-venue-tpl-editor-svg-edit-dialog',
    templateUrl: './venue-tpl-editor-svg-edit-dialog.component.html',
    styleUrls: ['./venue-tpl-editor-svg-edit-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorSvgEditDialogComponent
    extends ObDialog<VenueTplEditorSvgEditDialogComponent, { target: SVGElement | EdSeat | EdNotNumberedZone }, null> implements OnInit {

    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _selectionSrv = inject(VenueTplEditorSelectionService);
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _viewSrv = inject(VenueTplEditorViewsService);
    private readonly _msgDialogSrv = inject(MessageDialogService);

    private _svgElement: SVGElement;
    private _mainSVGElement: SVGSVGElement;

    readonly control = new FormControl<string>('', [Validators.required]);

    constructor() { super(DialogSize.FULL_SCREEN); }

    ngOnInit(): void {
        this._domSrv.getSvgSvgElement$()
            .pipe(take(1))
            .subscribe(mainSvgElement => {
                this._mainSVGElement = mainSvgElement;
                if (this.data.target instanceof SVGElement) {
                    this._svgElement = this.data.target;
                } else if (this.data.target.itemType === VenueTemplateItemType.seat) {
                    const seatId = String(this.data.target.id);
                    this._svgElement = Array.from(this._mainSVGElement.children).find(child =>
                        child.id === seatId && child.classList.contains(SVGDefs.classes.interactive) && child instanceof
                        SVGCircleElement
                    ) as SVGElement;
                } else if (this.data.target.itemType === VenueTemplateItemType.notNumberedZone) {
                    const nnzId = String(this.data.target.id);
                    this._svgElement = Array.from(this._mainSVGElement.children).find(child =>
                        child.id === nnzId && child.classList.contains(SVGDefs.classes.interactive)
                        && child instanceof SVGGElement
                    ) as SVGElement;
                } else {
                    this._msgDialogSrv.showAlert({ message: 'VENUE_TPL_EDITOR.NOT_EDITABLE_ELEMENT' });
                }
                this.control.setValue(xmlFormat(this._svgElement.outerHTML));
            });
    }

    commit(): void {
        if (this.control.valid) {
            if (this._svgElement === this._mainSVGElement) {
                this.commitMainSVG();
            } else {
                this.commitSVG();
            }
        }
    }

    private commitMainSVG(): void {
        of(this.parseSvg(this.control.value))
            .pipe(
                filter(Boolean),
                withLatestFrom(this._domSrv.getSvgSvgElement$())
            )
            .subscribe(([parsedSvg, mainSvg]) => {
                const prevIdElements = new Set(Array.from(mainSvg.children)
                    .filter(child => child.classList.contains(SVGDefs.classes.interactive))
                    .map(child => child.id));
                const newIdElements = new Set(Array.from(parsedSvg.children)
                    .filter(child => child.classList.contains(SVGDefs.classes.interactive))
                    .map(child => child.id));
                if (newIdElements.size !== prevIdElements.size
                    || Array.from(prevIdElements).some(oldId => !newIdElements.has(oldId))
                    || Array.from(newIdElements).some(newId => !prevIdElements.has(newId))
                ) {
                    this.showIdsWarning().subscribe(() => {
                        this.runEditSvgAction(parsedSvg);
                        this.dialogRef.close();
                    });
                } else {
                    this.runEditSvgAction(parsedSvg);
                    this.dialogRef.close();
                }
            });
    }

    private commitSVG(): void {
        of(this.parseSvg([defaultSVGOpenTag, this.control.value, defaultSVGCloseTag].join('')))
            .pipe(filter(Boolean))
            .subscribe(parsedSvg => {
                const newElements = Array.from(parsedSvg.children).map(child => child as SVGElement);
                if (newElements.length > 0) {
                    let newElement: Element;
                    if (this._svgElement.id && this._svgElement.classList.contains(SVGDefs.classes.interactive)) {
                        newElement = newElements.find(el =>
                            el.id === this._svgElement.id && el.classList.contains(SVGDefs.classes.interactive)
                        );
                        if (!newElement) {
                            this.showIdsWarning().subscribe(() => this.addElements(newElements));
                        } else {
                            this.addElements(newElements);
                        }
                    } else {
                        this.addElements(newElements);
                    }
                }
            });
    }

    private parseSvg(svgString: string): SVGSVGElement {
        const parsedSvg = new DOMParser().parseFromString(svgString, 'image/svg+xml');
        if (parsedSvg?.querySelector('parsererror')) {
            this.showParseError();
            console.error('Parse error', parsedSvg);
            return null;
        } else {
            return parsedSvg.children.item(0) as SVGSVGElement;
        }
    }

    private addElements(newElements: SVGElement[]): void {
        this._selectionSrv.unselectAll();
        const elementIndex = Array.from(this._mainSVGElement.children).indexOf(this._svgElement);
        newElements.forEach(el => this._mainSVGElement.insertBefore(el, this._svgElement));
        this._svgElement.remove();
        this.runEditSvgAction(this._mainSVGElement);
        this._domSrv.getSvgSvgElement$()
            .pipe(
                filter(Boolean),
                map(mainSVGElement => mainSVGElement.children.item(elementIndex) as SVGElement),
                take(1),
                filter(Boolean)
            )
            .subscribe(newNewElement => this._selectionSrv.selectElements([newNewElement]));
        this.dialogRef.close();
    }

    private showParseError(): void {
        this._msgDialogSrv.showAlert({
            size: DialogSize.SMALL,
            title: 'TITLES.ERROR_DIALOG',
            message: 'VENUE_TPL_EDITOR.SVG_PARSE_ERROR'
        });
    }

    private showIdsWarning(): Observable<void> {
        return this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'VENUE_TPL_EDITOR.SVG_IDS_MISMATCH_WARNING_TITLE',
            message: 'VENUE_TPL_EDITOR.SVG_IDS_MISMATCH_WARNING',
            actionLabel: 'FORMS.ACTIONS.CONTINUE'
        })
            .pipe(
                filter(Boolean),
                map(() => null)
            );
    }

    private runEditSvgAction(svg: SVGSVGElement): void {
        this._editorSrv.history.enqueue(
            new EditSvgAction(this._viewSrv, this._domSrv, this._selectionSrv, { svg, changer: VenueTplEditorSvgTriggerType.textChange })
        );
    }
}

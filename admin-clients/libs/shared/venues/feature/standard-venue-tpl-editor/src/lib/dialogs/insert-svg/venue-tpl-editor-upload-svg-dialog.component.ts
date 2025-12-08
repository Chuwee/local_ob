import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, inject, OnDestroy, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Subject } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { EditSvgAction } from '../../actions/edit-svg-action';
import { SVGDefs } from '../../models/SVGDefs.enum';
import { VenueTplEditorSvgTriggerType } from '../../models/venue-tpl-editor-svg-trigger-type.enum';
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
        ReactiveFormsModule
    ],
    selector: 'app-venue-tpl-editor-upload-svg-dialog',
    templateUrl: './venue-tpl-editor-upload-svg-dialog.component.html',
    styleUrls: ['./venue-tpl-editor-upload-svg-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorUploadSvgDialogComponent
    extends ObDialog<VenueTplEditorUploadSvgDialogComponent, { fileInput: EventTarget }, void>
    implements OnDestroy {

    private readonly _safeSVG = new Subject<SafeHtml>();
    private readonly _svgElement = new BehaviorSubject(null as SVGSVGElement);

    private readonly _domSanitizer = inject(DomSanitizer);
    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _viewSrv = inject(VenueTplEditorViewsService);
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _selectionSrv = inject(VenueTplEditorSelectionService);

    @ViewChild('svgContainer')
    private readonly _svgContainer: ElementRef<HTMLDivElement>;

    readonly safeSVG$ = this._safeSVG.asObservable();

    readonly isInvalid$ = this._svgElement.pipe(map(el => !el));

    constructor() {
        super(DialogSize.LARGE);
        if (this.data.fileInput instanceof HTMLInputElement
            && this.data.fileInput.files.length === 1
            && this.data.fileInput.files[0].name.includes('.svg', this.data.fileInput.files[0].name.length - 4)
        ) {
            const reader = new FileReader();
            reader.onload = () => {
                const mutationObserver = new MutationObserver(() => {
                    this._svgElement.next(Array.from(this._svgContainer?.nativeElement?.childNodes)
                        ?.find(node => node instanceof SVGSVGElement));
                    mutationObserver.disconnect();
                });
                mutationObserver.observe(this._svgContainer.nativeElement, { subtree: true, childList: true, attributes: true });
                this._safeSVG.next(this._domSanitizer.bypassSecurityTrustHtml(reader.result.toString()));
            };
            reader.readAsText(this.data.fileInput.files[0]);
        } else {
            this.dialogRef.close();
        }
    }

    ngOnDestroy(): void {
        this._svgElement.complete();
        this._safeSVG.complete();
    }

    commit(): void {
        combineLatest([
            this._domSrv.getSvgSvgElement$(),
            this._svgElement.asObservable()
        ])
            .pipe(take(1))
            .subscribe(([mainSVG, svgToAdd]) => {
                if (svgToAdd?.children.length) {
                    const svgChildren = Array.from(svgToAdd.children).map(child => child as SVGElement);
                    svgChildren.forEach(child => this.cleanNode(child));
                    const groupedSvg = this._domSrv.groupElements(svgChildren);
                    mainSVG.append(groupedSvg);
                    this._editorSrv.history.enqueue(new EditSvgAction(
                        this._viewSrv, this._domSrv, this._selectionSrv,
                        { changer: VenueTplEditorSvgTriggerType.DOMChange, elementsToSelect: [groupedSvg] })
                    );
                    this.dialogRef.close();
                }
            });
    }

    private cleanNode(element: Element): void {
        if (element.classList.contains(SVGDefs.classes.interactive)) {
            element.removeAttribute(SVGDefs.attributes.id);
            element.classList.remove(SVGDefs.classes.interactive);
        }
        if (element.children?.length) {
            Array.from(element.children).forEach(child => this.cleanNode(child));
        }
    }
}

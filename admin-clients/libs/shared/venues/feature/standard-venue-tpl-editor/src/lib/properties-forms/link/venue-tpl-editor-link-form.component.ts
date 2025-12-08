import { DialogSize, MessageDialogService, ObDialogService, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { VenueTemplateView } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild, ViewContainerRef } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Subject, switchMap } from 'rxjs';
import { filter, map, take, takeUntil } from 'rxjs/operators';
import { DeleteItemsAction } from '../../actions/delete-items-action';
import { EditCurrentViewAction } from '../../actions/edit-current-view-action';
import { EditLinkAction } from '../../actions/edit-link-action';
import { VenueTplEditorSvgEditDialogComponent } from '../../dialogs/svg-code/venue-tpl-editor-svg-edit-dialog.component';
import { VenueTplEditorViewLink } from '../../models/venue-tpl-editor-view-link.model';
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
        SharedUtilityDirectivesModule,
        SelectSearchComponent
    ],
    selector: 'app-venue-tpl-editor-link-form',
    templateUrl: './venue-tpl-editor-link-form.component.html',
    styleUrls: ['../venue-tpl-editor-properties-forms-common-styles.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorLinkFormComponent implements OnInit, OnDestroy, DeletableFormItem {
    private readonly _onDestroy = new Subject<void>();
    private readonly _fb = inject(FormBuilder);
    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _selectionSrv = inject(VenueTplEditorSelectionService);
    private readonly _viewSrv = inject(VenueTplEditorViewsService);
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _dialogSrv = inject(ObDialogService);
    private readonly _viewContainerRef = inject(ViewContainerRef);
    private readonly _messageDialogSrv = inject(MessageDialogService);
    private _link: VenueTplEditorViewLink;

    readonly operatorMode$ = this._editorSrv.modes.getOperatorMode$();

    @ViewChild('viewsSelectSearch')
    readonly viewsSelectSearchComponent: SelectSearchComponent<VenueTemplateView>;

    readonly element$ = this._selectionSrv.getSelection$().pipe(map(selection => selection.elements[0]));

    readonly views$ = combineLatest([
        this._viewSrv.getViewDatas$(),
        this._viewSrv.getViewData$(),
        this.element$
    ])
        .pipe(
            filter(sources => sources.every(source => !!source)),
            map(([viewDatas, viewData, element]) => {
                const linkViewId = viewData.links.find(link => link.ref_id === element.id)?.view_id;
                const linkedViewIds = viewDatas.flatMap(vd => vd.links).filter(link => !link.delete).map(link => link.view_id);
                const currentViewId = viewData.view.id;
                return viewDatas
                    .filter(vd => !vd.delete && !vd.view.root)
                    .map(vd => vd.view)
                    .filter(view => view.id !== currentViewId && (linkViewId === view.id || !linkedViewIds.includes(view.id)));
            })
        );

    readonly form = this._fb.group({ view: [null as number] });

    ngOnInit(): void {
        combineLatest([
            this.element$,
            this._viewSrv.getViewData$()
        ])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([element, viewData]) => {
                if (element && viewData) {
                    const refId = element.id;
                    this._link = viewData.links.find(link => !link.delete && link.ref_id === refId);
                    if (this._link) {
                        this.form.patchValue({
                            view: this._link.view_id ?? null
                        }, { emitEvent: false });
                    }
                }
            });
        this.form.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value =>
                this._editorSrv.history.enqueue(new EditLinkAction(
                    this._link.id, value.view, this._editorSrv, this._viewSrv, this._domSrv, this._selectionSrv
                ))
            );
    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }

    gotoSelectedView(): void {
        this._editorSrv.history.enqueue(new EditCurrentViewAction(this.form.value.view, this._viewSrv));
    }

    deleteFormItem(): void {
        this._messageDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'VENUE_TPL_EDITOR.DELETE_LINK_WARNING_TITLE',
            message: 'VENUE_TPL_EDITOR.DELETE_LINK_WARNING'
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.element$),
                take(1)
            )
            .subscribe(element => {
                this._editorSrv.history.enqueue(
                    new DeleteItemsAction({ elements: [element] }, this._venueMapSrv, this._viewSrv, this._domSrv, this._selectionSrv)
                );
            });
    }

    openEditSvgDialog(): void {
        this.element$.pipe(take(1))
            .subscribe(element => this._dialogSrv.open(VenueTplEditorSvgEditDialogComponent, { target: element }, this._viewContainerRef));
    }
}

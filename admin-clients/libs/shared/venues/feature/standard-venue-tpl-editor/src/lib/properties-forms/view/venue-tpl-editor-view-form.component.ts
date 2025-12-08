import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize, MessageDialogService, ObDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { noDuplicateValuesAsyncValidator, urlFriendlyValidator } from '@admin-clients/shared/utility/utils';
import { VenueTemplateView } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplateFieldsRestrictions } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnDestroy, OnInit, ViewContainerRef } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Svg } from '@svgdotjs/svg.js';
import { combineLatest, distinctUntilChanged, Observable, of, startWith, Subject, switchMap, tap } from 'rxjs';
import { debounceTime, filter, map, shareReplay, take, takeUntil, withLatestFrom } from 'rxjs/operators';
import { DeleteLinksAction } from '../../actions/delete-links-action';
import { DeleteViewAction } from '../../actions/delete-view-action';
import { EditRootViewAction } from '../../actions/edit-root-view-action';
import { EditSvgViewBoxAction } from '../../actions/edit-svg-view-box-action';
import { EditViewAction } from '../../actions/edit-view-action';
import { VenueTplEditorSvgEditDialogComponent } from '../../dialogs/svg-code/venue-tpl-editor-svg-edit-dialog.component';
import { SVGDefs } from '../../models/SVGDefs.enum';
import { CapacityEditionCapability } from '../../models/venue-tpl-editor-capacity-edition-capability';
import { EdVenueMapMaps } from '../../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorViewData } from '../../models/venue-tpl-editor-view-data.model';
import { VenueTplEditorViewLink } from '../../models/venue-tpl-editor-view-link.model';
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
        SharedUtilityDirectivesModule
    ],
    selector: 'app-venue-tpl-editor-view-form',
    templateUrl: './venue-tpl-editor-view-form.component.html',
    styleUrls: ['../venue-tpl-editor-properties-forms-common-styles.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorViewFormComponent implements OnInit, OnDestroy, DeletableFormItem {
    private readonly _onDestroy = new Subject<void>();
    private readonly _fb = inject(FormBuilder);
    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _selectionSrv = inject(VenueTplEditorSelectionService);
    private readonly _viewSrv = inject(VenueTplEditorViewsService);
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _dialogSrv = inject(ObDialogService);
    private readonly _messageDialogSrv = inject(MessageDialogService);
    private readonly _viewContainerRef = inject(ViewContainerRef);
    private readonly _changeDetector = inject(ChangeDetectorRef);

    private readonly _otherViewsData$ = this._viewSrv.getViewData$()
        .pipe(
            distinctUntilChanged(),
            withLatestFrom(this._viewSrv.getViewDatas$()),
            map(([viewData, viewDataList]) => {
                const otherViewDataList = viewDataList.filter(vd => vd.view.id !== viewData.view.id);
                return {
                    names: otherViewDataList.map(vd => vd.view.name),
                    codes: otherViewDataList.map(vd => vd.view.code)
                };
            }),
            takeUntil(this._onDestroy),
            shareReplay(1),
            debounceTime(0) // small delay to get new values after validate form value set, otherwise, it will validate with previous values
        );

    readonly mmcIntegrationEnabled$ = this._editorSrv.mmcIntegrationEnabled.get$();

    readonly isNotRootView$ = this._viewSrv.getViewData$().pipe(map(viewData => !viewData?.view?.root));

    readonly operatorMode$ = this._editorSrv.modes.getOperatorMode$();

    readonly viewData$ = this._viewSrv.getViewData$();

    readonly viewSearchControl = new FormControl('');

    readonly hasLinks$ = this._viewSrv.getViewData$().pipe(map(viewData => !!viewData?.links?.filter(link => !link.delete).length));

    readonly viewDataLinks$: Observable<(VenueTplEditorViewLink & { view: VenueTemplateView; hasGraphicShape: boolean })[]>
        = combineLatest([
            combineLatest([
                this._viewSrv.getViewData$()
                    .pipe(
                        filter(Boolean),
                        map(viewData => viewData.links.filter(link => !link.delete)),
                        withLatestFrom(this._viewSrv.getViewDatas$()),
                        map(([links, viewDataList]) => links.map(link => ({
                            ...link,
                            view: viewDataList.find(viewData => viewData.view.id === link.view_id)?.view,
                            hasGraphicShape: false
                        }))),
                        shareReplay({ refCount: true, bufferSize: 1 })
                    ),
                this._domSrv.getSvgSvgElement$().pipe(filter(Boolean))
            ])
                .pipe(
                    map(([links, mainSvg]) => {
                        const svgElements = new Set(
                            Array.from(mainSvg.children)
                                .filter(child => child.classList.contains(SVGDefs.classes.interactive))
                                .map(child => child.id)
                        );
                        links.map(link => link.hasGraphicShape = svgElements.has(link.ref_id));
                        return links;
                    }),
                    shareReplay({ refCount: true, bufferSize: 1 })
                ),
            this.viewSearchControl.valueChanges.pipe(startWith(null))
        ])
            .pipe(
                map(([links, q]) => {
                    if (q) {
                        q = q.toLowerCase();
                        return links.filter(link => link.view.name.toLowerCase().includes(q));
                    } else {
                        return links;
                    }
                })
            );

    readonly form = this._fb.group({
        view: this._fb.group({
            name: [
                '',
                [Validators.required, Validators.maxLength(VenueTemplateFieldsRestrictions.viewNameLength)],
                noDuplicateValuesAsyncValidator(this._otherViewsData$.pipe(map(d => d.names)))
            ],
            code: [
                '',
                [Validators.required, Validators.maxLength(VenueTemplateFieldsRestrictions.viewCodeLength),
                    urlFriendlyValidator],
                noDuplicateValuesAsyncValidator(this._otherViewsData$.pipe(map(d => d.codes)))],
            aggregatedView: [false],
            display3D: false,
            vip: false
        }),
        svg: this._fb.group({
            width: [0, Validators.required],
            height: [0, Validators.required]
        })
    });

    readonly aggregatedViewSelected$ = this._viewSrv.getViewData$()
        .pipe(map(viewData => viewData?.view?.aggregated_view));

    readonly itemsInView$ = combineLatest([
        this._viewSrv.getViewData$().pipe(filter(Boolean)),
        this._venueMapSrv.getVenueItems$()
            .pipe(
                filter(Boolean),
                map(items => [...Array.from(items.nnzs.values()), ...Array.from(items.seats.values())].filter(item => !item.delete))
            )
    ])
        .pipe(
            map(([viewData, items]) =>
                items.some(nnz => nnz.view === viewData.view.id)
            ),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly viewNotLinked$ = combineLatest([
        this._viewSrv.getViewData$(),
        this._viewSrv.getViewDatas$()
    ])
        .pipe(
            filter(sources => sources.every(Boolean)),
            map(([currentViewData, viewDataList]) =>
                !!currentViewData.links.length || !currentViewData.view.root
                || viewDataList.some(viewData => viewData.links.find(link => link.view_id === currentViewData.view.id))
            ),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly seatsInViewWarning$ = combineLatest([this.aggregatedViewSelected$, this.itemsInView$])
        .pipe(map(([aggregatedViewSelected, itemsInView]) => aggregatedViewSelected && itemsInView));

    readonly viewNotLinkedWarning$ = combineLatest([this.aggregatedViewSelected$, this.viewNotLinked$])
        .pipe(map(([aggregatedViewSelected, viewNotLinked]) => aggregatedViewSelected && viewNotLinked));

    ngOnInit(): void {
        this.form.markAllAsTouched();
        this.form.markAsDirty();
        this._viewSrv.getViewData$()
            .pipe(
                filter(Boolean),
                map(viewData => ({
                    name: viewData.view.name,
                    code: viewData.view.code,
                    aggregatedView: viewData.view.aggregated_view ?? false,
                    display3D: viewData.view.display_3D ?? false,
                    vip: viewData.view.vip ?? false
                })),
                distinctUntilChanged(),
                tap(viewFormValue => this.form.controls.view.setValue(viewFormValue, { emitEvent: false })),
                debounceTime(0), // what a tricky, this makes markForCheck really works when fields are invalid!
                takeUntil(this._onDestroy)
            )
            .subscribe(() => this._changeDetector.markForCheck());
        this._domSrv.getSvgSvgElement$()
            .pipe(
                filter(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(svgElement => {
                const svg = new Svg(svgElement.cloneNode() as SVGSVGElement);
                this.form.controls.svg.setValue({
                    width: svg.viewbox().width,
                    height: svg.viewbox().height
                }, { emitEvent: false });
                this._changeDetector.markForCheck();
            });

        this.form.controls.view.valueChanges
            .pipe(
                withLatestFrom(this._viewSrv.getViewData$()),
                takeUntil(this._onDestroy)
            )
            .subscribe(([value, viewData]) => {
                this._editorSrv.history.enqueue(
                    new EditViewAction({
                        id: viewData.view.id,
                        name: value.name,
                        code: value.code,
                        aggregated_view: value.aggregatedView,
                        // eslint-disable-next-line @typescript-eslint/naming-convention
                        display_3D: value.display3D,
                        vip: value.vip
                    }, this._viewSrv, this._selectionSrv)
                );
            });
        this.form.controls.svg.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => this._editorSrv.history.enqueue(new EditSvgViewBoxAction(value, this._viewSrv, this._domSrv)));
    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }

    setDefaultView(viewData: VenueTplEditorViewData): void {
        this._messageDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'VENUE_TPL_EDITOR.ROOT_VIEW_CHANGE_WARNING_TITLE',
            message: 'VENUE_TPL_EDITOR.ROOT_VIEW_CHANGE_WARNING',
            actionLabel: 'FORMS.ACTIONS.OK'
        })
            .pipe(filter(Boolean))
            .subscribe(() => this._editorSrv.history.enqueue(new EditRootViewAction(viewData, this._viewSrv)));
    }

    deleteFormItem(): void {
        combineLatest([
            this._viewSrv.getViewData$(),
            this._viewSrv.getViewDatas$(),
            this._editorSrv.getCapacityEditionCapability$(),
            this._venueMapSrv.getVenueItems$()
        ])
            .pipe(
                take(1),
                switchMap(([viewData, viewDataList, editionCapability, venueItems]) => {
                    if (!this.viewIsReferenced(viewData, viewDataList)
                        && this.canDeleteViewItems(viewData, editionCapability, venueItems)) {
                        return this._messageDialogSrv.showWarn({
                            size: DialogSize.SMALL,
                            title: 'VENUE_TPL_EDITOR.DELETE_VIEW_WARNING_TITLE',
                            message: 'VENUE_TPL_EDITOR.DELETE_VIEW_WARNING',
                            actionLabel: 'FORMS.ACTIONS.DELETE'
                        })
                            .pipe(filter(Boolean), map(() => viewData));
                    } else {
                        return of(null);
                    }
                }),
                filter(Boolean)
            )
            .subscribe(viewData => this._editorSrv.history.enqueue(new DeleteViewAction(viewData, this._viewSrv, this._venueMapSrv)));
    }

    selectLink(link: VenueTplEditorViewLink): void {
        this._domSrv.getSvgSvgElement$()
            .pipe(take(1))
            .subscribe(mainSvg => {
                const linkElement: SVGElement = Array.from(mainSvg.children)
                    .find(child => child.id === link.ref_id && child.classList.contains(SVGDefs.classes.interactive)) as SVGElement;
                if (linkElement) {
                    this._selectionSrv.selectElements([linkElement]);
                }
            });
    }

    deleteLink(link: VenueTplEditorViewLink): void {
        this._messageDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'VENUE_TPL_EDITOR.DELETE_LINK_WARNING_TITLE',
            message: 'VENUE_TPL_EDITOR.DELETE_LINK_WARNING'
        })
            .pipe(filter(Boolean))
            .subscribe(() =>
                this._editorSrv.history.enqueue(new DeleteLinksAction([link.id], this._viewSrv, this._domSrv, this._selectionSrv))
            );
    }

    deleteAllLinks(): void {
        this._messageDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'VENUE_TPL_EDITOR.DELETE_ALL_VIEW_LINKS_WARNING_TITLE',
            message: 'VENUE_TPL_EDITOR.DELETE_ALL_VIEW_LINKS_WARNING'
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this._viewSrv.getViewData$()),
                take(1)
            )
            .subscribe(viewData =>
                this._editorSrv.history.enqueue(
                    new DeleteLinksAction(viewData.links.map(l => l.id), this._viewSrv, this._domSrv, this._selectionSrv)
                )
            );
    }

    openEditSvgDialog(): void {
        this._domSrv.getSvgSvgElement$().pipe(take(1))
            .subscribe(mainSvg => this._dialogSrv.open(VenueTplEditorSvgEditDialogComponent, { target: mainSvg }, this._viewContainerRef));
    }

    private viewIsReferenced(viewData: VenueTplEditorViewData, viewDataList: VenueTplEditorViewData[]): boolean {
        const result = viewDataList
            .filter(viewData => !viewData.delete)
            .some(vd => !!vd.links.some(link => !link.delete && link.view_id === viewData.view.id));
        if (result) {
            this._messageDialogSrv.showAlert({
                size: DialogSize.SMALL,
                title: 'TITLES.ERROR_DIALOG',
                message: 'VENUE_TPL_EDITOR.LINKED_VIEW_ALERT'
            });
        }
        return result;
    }

    private canDeleteViewItems(
        viewData: VenueTplEditorViewData, editionCapability: CapacityEditionCapability, venueItems: EdVenueMapMaps
    ): boolean {
        const viewId = viewData.view.id;
        if (editionCapability !== CapacityEditionCapability.total) {
            const flatVenueItems = [...Array.from(venueItems.seats.values()), ...Array.from(venueItems.nnzs.values())];
            if (flatVenueItems.some(seat => seat.view === viewId && !IdGenerator.isTempId(seat.id))) {
                this._messageDialogSrv
                    .showAlert({
                        size: DialogSize.SMALL,
                        title: 'VENUE_TPL_EDITOR.CAPACITY_DECREMENT_NOT_AVAILABLE_BY_SESSIONS_ERROR_TITLE',
                        message: 'VENUE_TPL_EDITOR.CAPACITY_DECREMENT_NOT_AVAILABLE_BY_SESSIONS_ERROR'
                    });
                return false;
            }
        }
        return true;
    }
}

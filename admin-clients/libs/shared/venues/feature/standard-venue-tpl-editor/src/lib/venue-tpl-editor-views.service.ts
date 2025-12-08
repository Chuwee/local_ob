/* eslint-disable @typescript-eslint/naming-convention */
import { StdVenueTplsApi, VenueTemplateView } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplateFieldsRestrictions } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { inject, Injectable } from '@angular/core';
import { combineLatest, finalize, Observable } from 'rxjs';
import { map, take, withLatestFrom } from 'rxjs/operators';
import { defaultSVG, VenueTplSvgData } from './models/venue-tpl-editor-svg-data.model';
import { VenueTplEditorSvgTriggerType } from './models/venue-tpl-editor-svg-trigger-type.enum';
import { EdNotNumberedZone, EdSeat } from './models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorViewData } from './models/venue-tpl-editor-view-data.model';
import { VenueTplEditorState } from './state/venue-tpl-editor.state';

@Injectable()
export class VenueTplEditorViewsService {

    private readonly _stdVenueTplsApi = inject(StdVenueTplsApi);
    private readonly _venueTplEdState = inject(VenueTplEditorState);

    constructor() { }

    isDirty$(): Observable<boolean> {
        return combineLatest([
            this._venueTplEdState.viewDatas.getValue$(),
            this._venueTplEdState.SVGDatas.getValue$()
        ])
            .pipe(map(([viewDatas, svgDatas]) =>
                viewDatas?.some(viewData => viewData.create || viewData.delete || viewData.modify
                    || (viewData.links.some(link => link.create || link.delete)))
                || svgDatas?.some(svgWrapper => svgWrapper.modify)
            ));
    }

    checkViewsChanged(tplId: number): Observable<boolean> {
        this._venueTplEdState.viewDatas.setInProgress(true);
        return this._stdVenueTplsApi.getVenueTplViews(tplId)
            .pipe(
                withLatestFrom(this._venueTplEdState.viewDatas.getValue$()),
                map(([views, viewDataList]) => this.viewsHasChanged(views.data, viewDataList.map(vd => vd.view))),
                finalize(() => this._venueTplEdState.viewDatas.setInProgress(false))
            );
    }

    loadViews(tplId: number): void {
        this._venueTplEdState.SVGData.setValue(null);
        this._venueTplEdState.SVGDatas.setValue(null);
        this._venueTplEdState.viewDatas.setInProgress(true);
        this._stdVenueTplsApi.getVenueTplViews(tplId)
            .pipe(finalize(() => this._venueTplEdState.viewDatas.setInProgress(false)))
            .subscribe(views => {
                this._venueTplEdState.SVGDatas.setValue([]);
                views.data = views.data.sort((a, b) => a.root ? -1 : (b.root ? 1 : 0));
                this._venueTplEdState.viewDatas.setValue(views.data.map(view => ({
                    view,
                    links: view.links ? view.links.map(link => ({ ...link })) : []
                })));
            });
    }

    isViewsLoading$(): Observable<boolean> {
        return this._venueTplEdState.viewDatas.isInProgress$();
    }

    getViewDatas$(): Observable<VenueTplEditorViewData[]> {
        return this._venueTplEdState.viewDatas.getValue$();
    }

    setCurrentView(viewId: number): void {
        this._venueTplEdState.viewDatas.getValue$()
            .pipe(take(1))
            .subscribe(viewDataList => this._venueTplEdState.viewData.setValue(viewDataList.find(vd => vd.view.id === viewId)));
    }

    getViewData$(): Observable<VenueTplEditorViewData> {
        return this._venueTplEdState.viewData.getValue$();
    }

    loadSvgData(viewData: VenueTplEditorViewData): void {
        this._venueTplEdState.SVGData.setValue(null);
        this._venueTplEdState.SVGDatas.getValue$()
            .pipe(take(1))
            .subscribe(svgDatas => {
                const svgData = svgDatas?.find(svgData => svgData.viewId === viewData.view.id);
                if (!svgData) {
                    if (viewData.view.url) {
                        this._venueTplEdState.SVGData.setInProgress(true);
                        this._stdVenueTplsApi.getVenueTplSVG(viewData.view.url)
                            .pipe(finalize(() => this._venueTplEdState.SVGData.setInProgress(false)))
                            .subscribe(svg => this.addSvgData(viewData.view.id, svg));
                    } else {
                        this.addSvgData(viewData.view.id);
                    }
                } else {
                    svgData.triggerType = VenueTplEditorSvgTriggerType.load;
                    this._venueTplEdState.SVGData.setValue(svgData);
                }
            });
    }

    getSvgData$(): Observable<VenueTplSvgData> {
        return this._venueTplEdState.SVGData.getValue$();
    }

    isSVGDataLoading$(): Observable<boolean> {
        return this._venueTplEdState.SVGData.isInProgress$();
    }

    refreshSvgData(): void {
        this._venueTplEdState.SVGData.getValue$()
            .pipe(take(1))
            .subscribe(svgData => {
                svgData.triggerType = VenueTplEditorSvgTriggerType.textChange;
                this._venueTplEdState.SVGData.setValue(svgData);
            });
    }

    addView({ id, name, code }: { id: number; name: string; code: string }): Observable<VenueTplEditorViewData> {
        return this._venueTplEdState.viewDatas.getValue$()
            .pipe(
                take(1),
                map(views => {
                    const viewData: VenueTplEditorViewData = {
                        view: {
                            id, name, code,
                            url: null,
                            vip: false,
                            aggregated_view: false,
                            display_3D: false,
                            root: false,
                            links: []
                        },
                        links: [],
                        create: true
                    };
                    views.push(viewData);
                    this._venueTplEdState.viewDatas.setValue(views);
                    return viewData;
                }),
                take(1)
            );
    }

    undoAddView(viewId: number): void {
        this._venueTplEdState.viewDatas.getValue$()
            .pipe(take(1))
            .subscribe(viewDatas => this._venueTplEdState.viewDatas.setValue(viewDatas.filter(v => v.view.id !== viewId)));
    }

    removeView(viewData: VenueTplEditorViewData, undo = false): void {
        this._venueTplEdState.viewDatas.getValue$()
            .pipe(take(1))
            .subscribe(viewDatas => {
                let viewIdToNavigate: number;
                if (!undo) {
                    const notDeletedViewDatas = viewDatas.filter(vd => !vd.delete);
                    const viewIndex = notDeletedViewDatas.indexOf(viewData);
                    if (viewIndex === 0) {
                        viewIdToNavigate = notDeletedViewDatas[1].view.id;
                    } else {
                        viewIdToNavigate = notDeletedViewDatas[viewIndex - 1].view.id;
                    }
                } else {
                    viewIdToNavigate = viewData.view.id;
                }
                viewData.delete = !undo;
                this._venueTplEdState.viewDatas.setValue(viewDatas);
                this.setCurrentView(viewIdToNavigate);
            });
    }

    // REMEMBER to run domService.parseSvg method to the new SVGs
    changeSvg(viewId: number, svg: string, options?: { changer?: VenueTplEditorSvgTriggerType; resultModify?: boolean }): void {
        options = {
            changer: VenueTplEditorSvgTriggerType.textChange,
            resultModify: true,
            ...options
        };
        combineLatest([
            this._venueTplEdState.SVGDatas.getValue$(),
            this._venueTplEdState.SVGData.getValue$()
        ])
            .pipe(take(1))
            .subscribe(([svgDatas, svgData]) => {
                const svgDataToEdit = svgData.viewId === viewId ? svgData : svgData = svgDatas.find(sd => sd.viewId === viewId);
                svgDataToEdit.svg = svg;
                svgDataToEdit.triggerType = options.changer;
                svgDataToEdit.modify = options.resultModify;
                this._venueTplEdState.SVGDatas.setValue(svgDatas);
                if (svgDataToEdit === svgData) {
                    this._venueTplEdState.SVGData.setValue(svgData);
                }
            });
    }

    revertSvgChanges(): void {
        combineLatest([
            this._venueTplEdState.SVGDatas.getValue$(),
            this._venueTplEdState.SVGData.getValue$()
        ])
            .pipe(take(1))
            .subscribe(([svgDatas, svgData]) => {
                svgData.triggerType = VenueTplEditorSvgTriggerType.textChange;
                this._venueTplEdState.SVGDatas.setValue(svgDatas);
                this._venueTplEdState.SVGData.setValue(svgData);
            });
    }

    changeRootView(viewData: VenueTemplateView, prevRootViewModify = true, newRootViewModify = true): void {
        this._venueTplEdState.viewDatas.getValue$()
            .pipe(take(1))
            .subscribe(viewDatas => {
                const rootViewData = viewDatas.find(vd => vd.view.root);
                rootViewData.view.root = false;
                rootViewData.modify = prevRootViewModify;
                const newRootViewData = viewDatas.find(vd => vd.view.id === viewData.id);
                newRootViewData.view.root = true;
                newRootViewData.modify = newRootViewModify;
                this._venueTplEdState.viewDatas.setValue(viewDatas);
                this._venueTplEdState.viewData.setValue(newRootViewData);
            });
    }

    updateView(view: Partial<VenueTemplateView>, modify: boolean): void {
        this._venueTplEdState.viewDatas.getValue$()
            .pipe(take(1))
            .subscribe(viewDataList => {
                const viewData = viewDataList.find(viewData => viewData.view.id === view.id);
                viewData.modify = modify;
                Object.keys(view).forEach(field => viewData.view[field] = view[field]);
                this._venueTplEdState.viewDatas.setValue(viewDataList);
                this._venueTplEdState.viewData.setValue(viewData);
            });
    }

    addLink(linkId: number, refId: string, viewId: number = undefined, undo = false): void {
        combineLatest([
            this._venueTplEdState.viewData.getValue$(),
            this._venueTplEdState.viewDatas.getValue$()
        ])
            .pipe(take(1))
            .subscribe(([viewData, viewDatas]) => {
                if (!undo) {
                    viewData.links.push({
                        id: linkId,
                        view_id: viewId,
                        ref_id: refId,
                        create: true
                    });
                } else {
                    viewData.links = viewData.links.filter(link => link.id !== linkId);
                }
                this._venueTplEdState.viewDatas.setValue(viewDatas);
                this._venueTplEdState.viewData.setValue(viewData);
            });
    }

    editLinkView(oldLinkId: number, newLinkId: number, newRefId: string, viewId: number, undo = false): void {
        this._venueTplEdState.viewDatas.getValue$()
            .pipe(take(1))
            .subscribe(viewDatas => {
                const link = viewDatas.flatMap(vd => vd.links).find(link => link.id === oldLinkId);
                const linkViewData = viewDatas.find(viewData => viewData.links.includes(link));
                if (!undo) {
                    link.delete = true;
                    linkViewData.links.push({
                        id: newLinkId,
                        view_id: viewId,
                        ref_id: newRefId,
                        create: true
                    });
                } else {
                    link.delete = false;
                    linkViewData.links = linkViewData.links.filter(l => l.id !== newLinkId);
                }
                this._venueTplEdState.viewDatas.setValue(viewDatas);
                this._venueTplEdState.viewData.setValue(linkViewData);
            });
    }

    deleteLink(linkId: number, undo = false): void {
        combineLatest([
            this._venueTplEdState.viewDatas.getValue$(),
            this._venueTplEdState.viewData.getValue$()
        ])
            .pipe(take(1))
            .subscribe(([viewDatas, viewData]) => {
                viewDatas.flatMap(vd => vd.links).find(link => link.id === linkId).delete = !undo;
                this._venueTplEdState.viewDatas.setValue(viewDatas);
                this._venueTplEdState.viewData.setValue(viewData);
            });
    }

    checkViewNames(): Observable<VenueTplEditorViewData> {
        return this._venueTplEdState.viewDatas.getValue$()
            .pipe(
                take(1),
                map(viewDatas => {
                    const invalidViews = [
                        viewDatas.find(viewData =>
                            !viewData?.view.name.length
                            || viewData.view.name.length > VenueTemplateFieldsRestrictions.viewNameLength
                        ), //invalid name
                        viewDatas.find(viewData =>
                            !!viewDatas.find(vdToCompare => vdToCompare !== viewData && vdToCompare.view.name === viewData.view.name)
                        ) // duplicate name
                    ]
                        .filter(Boolean);
                    return invalidViews.length && invalidViews[0];
                })
            );
    }

    checkViewCodes(): Observable<VenueTplEditorViewData> {
        return this._venueTplEdState.viewDatas.getValue$()
            .pipe(
                take(1),
                map(viewDatas => {
                    const invalidViews = [
                        viewDatas.find(viewData =>
                            !viewData?.view.code.length
                            || viewData.view.code.length > VenueTemplateFieldsRestrictions.viewCodeLength
                            || viewData.view.code !== encodeURIComponent(viewData.view.code)
                        ), //invalid code
                        viewDatas.find(viewData =>
                            !!viewDatas.find(vdToCompare => vdToCompare !== viewData && vdToCompare.view.code === viewData.view.code)
                        ) // duplicate code
                    ]
                        .filter(Boolean);
                    return invalidViews.length && invalidViews[0];
                })
            );
    }

    checkMmcIntegration(nnz: Map<number, EdNotNumberedZone>, seats: Map<number, EdSeat>): Observable<VenueTplEditorViewData> {
        return this._venueTplEdState.viewDatas.getValue$()
            .pipe(
                take(1),
                map(viewDataList => {
                    const items = [...Array.from(nnz.values()), ...Array.from(seats.values())].filter(item => !item.delete);
                    // finds any invalid aggregated_view configured view
                    return viewDataList
                        .filter(viewData => !viewData.delete && viewData.view.aggregated_view)
                        .find(viewData =>
                            // aggregated view has seats
                            items.every(item => item.view === viewData.view.id)
                            // no links in the aggregated view
                            || !viewData.links?.length
                            // no view points to the aggregated view
                            || viewDataList
                                .filter(listViewData => listViewData !== viewData)
                                .every(listViewData => listViewData.links.every(link => link.view_id !== viewData.view.id))
                        );
                })
            );
    }

    private addSvgData(viewId: number, svg: string = null): void {
        this._venueTplEdState.SVGDatas.getValue$()
            .pipe(take(1))
            .subscribe(svgDataList => {
                svg = svg || defaultSVG;
                const svgData: VenueTplSvgData = { svg, viewId };
                svgDataList.push(svgData);
                this._venueTplEdState.SVGDatas.setValue(svgDataList);
                this._venueTplEdState.SVGData.setValue(svgData);
            });
    }

    private viewsHasChanged(loadedViews: VenueTemplateView[], memoryViews: VenueTemplateView[]): boolean {
        return loadedViews.length !== memoryViews.length // new or deleted views
            || loadedViews.some(lView => { // searches an unequal view
                const mView = memoryViews.find(v => v.id === lView.id); //search memory view
                // not found (deleted and created one)
                return !mView
                    //url change (svg change)
                    || mView.url !== lView.url
                    // created or deleted link
                    || mView.links?.length !== lView.links?.length
                    // new link
                    || lView.links?.some(lLink => mView.links.every(mLink => mLink.id !== lLink.id));
            });
    }
}

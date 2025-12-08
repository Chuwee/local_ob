import { Id } from '@admin-clients/shared/data-access/models';
import { Row, StdVenueTplsApi, VenueTemplateView } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplate, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { inject, Injectable } from '@angular/core';
import { bufferCount, combineLatest, concat, concatAll, finalize, from, Observable, of, switchMap } from 'rxjs';
import { last, map, take, tap } from 'rxjs/operators';
import { VenueTplEditorImage } from './models/venue-tpl-editor-image.model';
import { VenueTplSvgData } from './models/venue-tpl-editor-svg-data.model';
import { EdVenueMap, EdVenueMapMaps } from './models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorViewData } from './models/venue-tpl-editor-view-data.model';
import { VenueTplEditorState } from './state/venue-tpl-editor.state';

interface StepsData {
    venueTemplate: VenueTemplate;
    viewDataList: VenueTplEditorViewData[];
    svgDataList: VenueTplSvgData[];
    venueMap: EdVenueMap;
    items: EdVenueMapMaps;
    images: VenueTplEditorImage[];
}

@Injectable()
export class VenueTplEditorSaveService {

    readonly #MAX_DELETABLE_ITEMS = 200; // the winds tell me the number

    private readonly _venueTplEdState = inject(VenueTplEditorState);
    private readonly _stdVenueTplsApi = inject(StdVenueTplsApi);
    private readonly _venueTplSrv = inject(VenueTemplatesService);

    constructor() { }

    isSaving$(): Observable<boolean> {
        return this._venueTplEdState.save.isInProgress$();
    }

    save(): Observable<void> {
        this._venueTplEdState.save.setInProgress(true);
        return combineLatest({
            venueTemplate: this._venueTplSrv.venueTpl.get$(),
            viewDataList: this._venueTplEdState.viewDatas.getValue$(),
            svgDataList: this._venueTplEdState.SVGDatas.getValue$(),
            venueMap: this._venueTplEdState.venueMap.getValue$(),
            items: this._venueTplEdState.venueItems.getValue$(),
            images: this._venueTplEdState.images.getValue$()
        })
            .pipe(
                take(1),
                switchMap(data => {
                    const stepsData: StepsData = data;
                    const steps = [
                        //views steps
                        this.setDefaultView(stepsData),
                        this.deleteLinks(stepsData),
                        this.deleteViews(stepsData),
                        this.editViews(stepsData),
                        this.createViews(stepsData),
                        this.createLinks(stepsData),
                        // delete steps
                        this.deleteNNZ(stepsData),
                        this.deleteSeats(stepsData),
                        this.deleteRows(stepsData),
                        this.deleteSectors(stepsData),
                        // edit steps
                        this.editSectors(stepsData),
                        this.editRows(stepsData),
                        this.editSeats(stepsData),
                        this.editNNZ(stepsData),
                        // create steps
                        this.createSectors(stepsData),
                        this.createRows(stepsData),
                        this.createSeats(stepsData),
                        this.createNNZ(stepsData),
                        //images
                        this.createImages(stepsData),
                        this.deleteImages(stepsData),
                        // svg step
                        this.updateSVGs(stepsData)
                    ].filter(Boolean);
                    return concat(...steps).pipe(bufferCount(steps.length), take(1));
                }),
                map(() => null),
                finalize(() => this._venueTplEdState.save.setInProgress(false))
            );
    }

    private setDefaultView({ venueTemplate, viewDataList, svgDataList, venueMap }: StepsData): Observable<void> {
        const rootView = viewDataList.find(vd => vd.view.root);
        if (rootView.create || rootView.modify) {
            console.log('root view guard', rootView);
            return of(null)
                .pipe(
                    tap(() => console.log('executing root view guard')),
                    switchMap(() => {
                        if (rootView.create) {
                            const emptyRootView: Omit<VenueTemplateView, 'id'> = {
                                root: true,
                                name: '_root_view_temp_name',
                                code: '_root_view_temp_code'
                            };
                            return this._stdVenueTplsApi.postVenueTplView(venueTemplate.id, emptyRootView)
                                .pipe(tap(idWrapper => {
                                    this.propagateNewViewId(venueMap, viewDataList, svgDataList, rootView, idWrapper.id);
                                    rootView.create = false;
                                    rootView.modify = true;
                                }));
                        } else {
                            const emptyRootView: VenueTemplateView = {
                                ...rootView.view,
                                name: undefined,
                                code: undefined
                            };
                            return this._stdVenueTplsApi.putVenueTplView(venueTemplate.id, emptyRootView);
                        }
                    }),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private createViews({ venueTemplate, viewDataList, svgDataList, venueMap }: StepsData): Observable<void> {
        const viewsToCreate = viewDataList.filter(viewData => viewData.create && !viewData.delete && !viewData.view.root);
        if (viewsToCreate.length) {
            console.log('views to create', viewsToCreate);
            return of(null)
                .pipe(
                    tap(() => console.log('creating views', viewsToCreate)),
                    switchMap(() =>
                        concat(...viewsToCreate.map(viewData =>
                            this._stdVenueTplsApi.postVenueTplView(venueTemplate.id, viewData.view)
                                .pipe(map(resultId => ({ viewData, id: resultId.id })))
                        ))
                    ),
                    bufferCount(viewsToCreate.length),
                    take(1),
                    tap(wrappers =>
                        wrappers.forEach(wrapper =>
                            this.propagateNewViewId(venueMap, viewDataList, svgDataList, wrapper.viewData, wrapper.id)
                        )
                    ),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private propagateNewViewId(
        venueMap: EdVenueMap, viewDataList: VenueTplEditorViewData[],
        svgDataList: VenueTplSvgData[], viewData: VenueTplEditorViewData, id: number
    ): void {
        const itemsInView = [
            ...venueMap.sectors.flatMap(s => s.notNumberedZones),
            ...venueMap.sectors.flatMap(s => s.rows).flatMap(r => r.seats)
        ].filter(item => item.view === viewData.view.id);
        itemsInView.forEach(item => item.view = id);
        viewDataList.forEach(vd => {
            vd.links.forEach(link => {
                if (link.view_id === viewData.view.id) {
                    link.view_id = id;
                }
            });
        });
        svgDataList.find(svgData => svgData.viewId === viewData.view.id).viewId = id;
        viewData.view.id = id;
    }

    private editViews({ venueTemplate, viewDataList }: StepsData): Observable<void> {
        const viewsToEdit = viewDataList
            .filter(viewData => !viewData.delete && ((!viewData.create && viewData.modify) || (viewData.view.root && viewData.create)))
            .map(viewData => viewData.view);
        if (viewsToEdit.length) {
            console.log('views to edit', viewsToEdit);
            return of(null)
                .pipe(
                    tap(() => console.log('editing views', viewsToEdit)),
                    switchMap(() => this._stdVenueTplsApi.putVenueTplViews(venueTemplate.id, viewsToEdit)),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private deleteViews({ venueTemplate, viewDataList }: StepsData): Observable<void> {
        const toDelete = viewDataList.filter(viewData => viewData.delete && !viewData.create);
        if (toDelete.length) {
            console.log('views to delete', toDelete);
            return of(null)
                .pipe(
                    tap(() => console.log('deleting views', toDelete)),
                    switchMap(() =>
                        concat(...toDelete.map(view => this._stdVenueTplsApi.deleteVenueTplView(venueTemplate.id, view.view.id)))
                    ),
                    bufferCount(toDelete.length),
                    take(1),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private createSectors({ venueTemplate, venueMap, items }: StepsData): Observable<void> {
        const sectorsToCreate = venueMap.sectors.filter(sector => sector.create && !sector.delete);
        if (sectorsToCreate.length) {
            console.log('sectors to create', sectorsToCreate);
            return of(null)
                .pipe(
                    tap(() => console.log('creating sectors', sectorsToCreate)),
                    switchMap(() =>
                        concat(...sectorsToCreate.map(
                            sector => this._stdVenueTplsApi.postSector(venueTemplate.id, sector)
                                .pipe(map(resultId => ({ sector, id: resultId.id })))
                        ))
                    ),
                    bufferCount(sectorsToCreate.length),
                    take(1),
                    tap(wrappers => {
                        wrappers.forEach(wrapper => {
                            const sectorItems = [
                                ...venueMap.sectors.flatMap(s => s.notNumberedZones),
                                ...venueMap.sectors.flatMap(s => s.rows)
                            ].filter(item => item.sector === wrapper.sector.id);
                            sectorItems.forEach(item => item.sector = wrapper.id);
                            items.sectors.delete(wrapper.sector.id);
                            wrapper.sector.id = wrapper.id;
                            items.sectors.set(wrapper.id, wrapper.sector);
                        });
                    }),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private deleteSectors({ venueTemplate, venueMap }: StepsData): Observable<void> {
        const sectorsToDelete = venueMap.sectors.filter(sector => sector.delete && !sector.create);
        if (sectorsToDelete.length) {
            console.log('sectors to delete', sectorsToDelete);
            return of(null)
                .pipe(
                    tap(() => console.log('deleting sectors', sectorsToDelete)),
                    switchMap(() =>
                        concat(...sectorsToDelete.map(sector => this._stdVenueTplsApi.deleteSector(venueTemplate.id, sector.id)))
                    ),
                    bufferCount(sectorsToDelete.length),
                    take(1),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private editSectors({ venueTemplate, venueMap }: StepsData): Observable<void> {
        const sectorsToEdit = venueMap.sectors.filter(sector => !sector.delete && !sector.create && sector.modify);
        if (sectorsToEdit.length) {
            console.log('sectors to edit', sectorsToEdit);
            return of(null)
                .pipe(
                    tap(() => console.log('editing sectors', sectorsToEdit)),
                    switchMap(() => concat(...sectorsToEdit.map(sector => this._stdVenueTplsApi.putSector(venueTemplate.id, sector)))),
                    bufferCount(sectorsToEdit.length),
                    take(1),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private createRows({ venueTemplate, venueMap }: StepsData): Observable<void> {
        const rowsToCreate = venueMap.sectors.flatMap(sector => sector.rows).filter(row => row.create && !row.delete);
        if (rowsToCreate.length) {
            console.log('rows to create', rowsToCreate);
            return of(null)
                .pipe(
                    tap(() => console.log('creating rows', rowsToCreate)),
                    switchMap(() =>
                        concat(...rowsToCreate.map(
                            row => this._stdVenueTplsApi.postRow(venueTemplate.id, row).pipe(map(resultId => ({ row, id: resultId.id })))
                        ))
                    ),
                    bufferCount(rowsToCreate.length),
                    take(1),
                    tap(resultWrappers => {
                        resultWrappers.forEach(wrapper => {
                            wrapper.row.id = wrapper.id;
                            wrapper.row.create = wrapper.row.modify = wrapper.row.delete = undefined;
                        });
                    }),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private editRows({ venueTemplate, venueMap }: StepsData): Observable<void> {
        const rowsToEdit = venueMap.sectors.flatMap(sector => sector.rows).filter(row => row.modify && !row.create && !row.delete);
        if (rowsToEdit.length) {
            console.log('rows to edit', rowsToEdit);
            return of(null)
                .pipe(
                    tap(() => console.log('editing rows', rowsToEdit)),
                    switchMap(() => concat(...rowsToEdit.map(row => this._stdVenueTplsApi.putRow(venueTemplate.id, row)))),
                    bufferCount(rowsToEdit.length),
                    take(1),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private deleteRows({ venueTemplate, venueMap }: StepsData): Observable<void> {
        const rowsToDelete = venueMap.sectors
            .filter(sector => !sector.delete)
            .flatMap(sector => sector.rows)
            .filter(row => row.delete && !row.create)
            .map(row => row.id);
        if (rowsToDelete?.length) {
            console.log('rows to delete', rowsToDelete);
            return of(null)
                .pipe(
                    tap(() => console.log('deleting rows')),
                    switchMap(() => concat(...rowsToDelete.map(rowId => this._stdVenueTplsApi.deleteRow(venueTemplate.id, rowId)))),
                    bufferCount(rowsToDelete.length),
                    take(1),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private createSeats({ venueTemplate, venueMap, items, svgDataList }: StepsData): Observable<void> {
        const seatsToCreateByRow = venueMap.sectors
            .flatMap(sector => sector.rows)
            .map(row => ({
                    row,
                    seats: row.seats.filter(seat => seat.create && !seat.delete)
                }))
            .filter(row => row.seats.length);
        if (seatsToCreateByRow.length) {
            console.log('seats to create found', seatsToCreateByRow);
            return of(null)
                .pipe(
                    tap(() => console.log('creating seats', seatsToCreateByRow)),
                    switchMap(() =>
                        this._stdVenueTplsApi.postSeats(
                            venueTemplate.id,
                            seatsToCreateByRow.map(rowWrapper => ({ id: rowWrapper.row.id, seats: rowWrapper.seats } as Row))
                        )
                    ),
                    tap(seatIds => {
                        const createdSeats = seatsToCreateByRow.flatMap(rowWrapper => rowWrapper.seats);
                        for (let i = 0; i < createdSeats.length; i++) {
                            const modelSeat = createdSeats[i];
                            const newSeatId = seatIds[i].id;
                            const svgData = svgDataList.find(svgData => svgData.viewId === modelSeat.view);
                            svgData.svg = svgData.svg.replace(`id="${modelSeat.id}"`, `id="${newSeatId}"`);
                            items.seats.delete(modelSeat.id);
                            items.seats.set(newSeatId, modelSeat);
                            modelSeat.id = newSeatId;
                        }
                    }),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private editSeats({ venueTemplate, venueMap }: StepsData): Observable<void> {
        const seatsToUpdate = venueMap.sectors
            .filter(sector => !sector.delete)
            .flatMap(sector => sector.rows)
            .filter(row => !row.delete)
            .flatMap(row => row.seats)
            .filter(seat => seat.modify && !seat.create && !seat.delete);
        if (seatsToUpdate.length) {
            console.log('seats to edit', seatsToUpdate);
            return of(null)
                .pipe(
                    tap(() => console.log('editing seats', seatsToUpdate)),
                    switchMap(() => this._stdVenueTplsApi.putSeats(venueTemplate.id, seatsToUpdate))
                );
        } else {
            return null;
        }
    }

    private deleteSeats({ venueTemplate, venueMap }: StepsData): Observable<void> {
        const seatsToDelete = venueMap.sectors
            .filter(sector => !sector.delete)
            .flatMap(sector => sector.rows)
            .filter(row => !row.delete)
            .flatMap(row => row.seats)
            .filter(seat => seat.delete && !seat.create)
            .map(seat => seat.id);
        if (seatsToDelete?.length) {
            console.log('seats to delete', seatsToDelete);
            return of(null)
                .pipe(
                    tap(() => console.log('deleting seats')),
                    switchMap(() => {
                        const deleteReqs = Array.from({ length: Math.ceil(seatsToDelete.length / this.#MAX_DELETABLE_ITEMS) })
                            .map((_, index) => {
                                const startItem = index * this.#MAX_DELETABLE_ITEMS;
                                return seatsToDelete.slice(startItem , startItem + this.#MAX_DELETABLE_ITEMS)
                            })
                            .map(seatsPage => this._stdVenueTplsApi.deleteSeats(venueTemplate.id, seatsPage));
                        return from(deleteReqs).pipe(concatAll(), last());
                    })
                );
        } else {
            return null;
        }
    }

    private createNNZ({ venueTemplate, items, svgDataList }: StepsData): Observable<void> {
        const nnzToCreate = Array.from(items.nnzs.values()).filter(nnz => !nnz.delete && nnz.create);
        if (nnzToCreate.length) {
            console.log('nnzs to create', nnzToCreate);
            return of(null)
                .pipe(
                    tap(() => console.log('creating nnzs')),
                    switchMap(() => concat(...nnzToCreate.map(nnz =>
                        this._stdVenueTplsApi.postNotNumberedZone(venueTemplate.id, nnz)
                            .pipe(map(idObject => ({ id: idObject.id, nnz })))
                    ))),
                    bufferCount(nnzToCreate.length),
                    take(1),
                    tap(results => {
                        results.forEach(result => {
                            const svgData = svgDataList.find(svg => svg.viewId === result.nnz.view);
                            this.replaceSVGTempId(svgData, result.nnz.id, result.id);
                            result.nnz.id = result.id;
                        });
                    }),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private editNNZ({ venueTemplate, items }: StepsData): Observable<void> {
        const nnzToEdit = Array.from(items.nnzs.values()).filter(nnz => nnz.modify && !nnz.delete && !nnz.create);
        if (nnzToEdit.length) {
            console.log('nnzs to edit', nnzToEdit);
            return of(null)
                .pipe(
                    tap(() => console.log('editing nnzs', nnzToEdit)),
                    switchMap(() => concat(...nnzToEdit.map(nnz => this._stdVenueTplsApi.putNotNumberedZone(venueTemplate.id, nnz)))),
                    bufferCount(nnzToEdit.length),
                    take(1),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private deleteNNZ({ venueTemplate, items }: StepsData): Observable<void> {
        const nnzToDelete = Array.from(items.nnzs.values()).filter(nnz => nnz.delete && !nnz.create);
        if (nnzToDelete.length) {
            console.log('nnzs to delete', nnzToDelete);
            return of(null)
                .pipe(
                    tap(() => console.log('deleting nnzs', nnzToDelete)),
                    switchMap(
                        () => concat(...nnzToDelete.map(nnz => this._stdVenueTplsApi.deleteNotNumberedZone(venueTemplate.id, nnz.id)))
                    ),
                    bufferCount(nnzToDelete.length),
                    take(1),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private createLinks({ venueTemplate, viewDataList, svgDataList }: StepsData): Observable<void> {
        const linksToCreateByView = viewDataList
            .map(vd => ({ view: vd.view, links: vd.links.filter(link => link.create && !link.delete) }))
            .filter(wrapper => wrapper.links.length);
        const numLinksToCreate = linksToCreateByView.flatMap(wrapper => wrapper.links).length;
        if (linksToCreateByView.length) {
            console.log('links to create', linksToCreateByView);
            return of(null)
                .pipe(
                    tap(() => console.log('creating links', linksToCreateByView)),
                    switchMap(() => concat(
                        ...linksToCreateByView.flatMap(wrapper =>
                            wrapper.links.map(link =>
                                this._stdVenueTplsApi.postLink(venueTemplate.id, wrapper.view.id, link.view_id)
                                    .pipe(
                                        switchMap(({ id }: Id) =>
                                            this._stdVenueTplsApi.getVenueTplView(venueTemplate.id, wrapper.view.id)
                                                .pipe(map(view => view.links.find(link => link.id === id)))
                                        ),
                                        tap(newLink => {
                                            const svgData = svgDataList.find(svgData => svgData.viewId === wrapper.view.id);
                                            this.replaceSVGTempId(svgData, link.ref_id, newLink.ref_id);
                                            link.id = newLink.id;
                                            link.ref_id = newLink.ref_id;
                                        })
                                    )
                            )
                        )
                    )),
                    bufferCount(numLinksToCreate),
                    take(1),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private deleteLinks({ venueTemplate, viewDataList }: StepsData): Observable<void> {
        const linksToDelete = viewDataList
            .map(viewData => viewData.links.map(link => ({ viewData, link })))
            .flatMap(value => value)
            .filter(wrapper => !wrapper.link.create && (wrapper.viewData.delete || wrapper.link.delete));
        if (linksToDelete.length) {
            console.log('links to delete', linksToDelete);
            return of(null)
                .pipe(
                    tap(() => console.log('deleting links', linksToDelete)),
                    switchMap(() => concat(...linksToDelete.map(wrapper => this._stdVenueTplsApi.deleteLink(
                        venueTemplate.id, wrapper.viewData.view.id, wrapper.link.id
                    )))),
                    bufferCount(linksToDelete.length),
                    take(1),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private updateSVGs({ venueTemplate, viewDataList, svgDataList }: StepsData): Observable<void> {
        const svgsToUpdate = viewDataList
            .filter(viewData => !viewData.delete)
            .map(viewData => svgDataList.find(svgData => svgData.viewId === viewData.view.id))
            .filter(svgData => svgData?.modify);
        if (svgsToUpdate.length) {
            console.log('svgs to update', svgsToUpdate);
            return of(null)
                .pipe(
                    tap(() => console.log('updating svgs', svgsToUpdate)),
                    switchMap(() =>
                        concat(...svgsToUpdate.map(
                            svgData => this._stdVenueTplsApi.putVenueTplSVG(venueTemplate.id, svgData.viewId, svgData.svg)
                        ))
                    ),
                    bufferCount(svgsToUpdate.length),
                    take(1),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private replaceSVGTempId(svgData: VenueTplSvgData, tempId: string | number, newId: string | number): void {
        svgData.svg = svgData.svg.replace(`id="${tempId.toString()}"`, `id="${newId.toString()}"`);
    }

    private createImages({ venueTemplate, images, svgDataList }: StepsData): Observable<void> {
        const imagesToCreate = images.filter(image => image.create && !image.delete);
        if (imagesToCreate.length) {
            console.log('images to create', imagesToCreate);
            return of(null)
                .pipe(
                    tap(() => console.log('creating images', imagesToCreate)),
                    switchMap(() =>
                        concat(...imagesToCreate.map(image =>
                            this._stdVenueTplsApi.postVenueTplImage(
                                venueTemplate.id,
                                {
                                    filename: image.fileName,
                                    image_binary: image.data
                                }
                            )
                                .pipe(map(templateImage => [image, templateImage]))
                        ))
                    ),
                    tap(([tempImage, createdImage]) => {
                        svgDataList.forEach(svgData => {
                            while (svgData.svg.includes(tempImage.url)) {
                                svgData.svg = svgData.svg.replace(tempImage.url, createdImage.url);
                            }
                        });
                    }),
                    bufferCount(imagesToCreate.length),
                    take(1),
                    map(() => null)
                );
        } else {
            return null;
        }
    }

    private deleteImages({ venueTemplate, images }: StepsData): Observable<void> {
        const imagesToDelete = images.filter(image => !image.create && image.delete);
        if (imagesToDelete.length) {
            console.log('images to delete', imagesToDelete);
            return of(null)
                .pipe(
                    tap(() => console.log('Deleting images', imagesToDelete)),
                    switchMap(() =>
                        concat(...imagesToDelete.map(image => this._stdVenueTplsApi.deleteVenueTplImage(venueTemplate.id, image.id)))
                    ),
                    bufferCount(imagesToDelete.length),
                    take(1),
                    map(() => null)
                );
        } else {
            return null;
        }
    }
 }

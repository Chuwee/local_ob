import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { SVGDefs } from './models/SVGDefs.enum';
import { VenueTplEditorSelection } from './models/venue-tpl-editor-selection.model';
import { VenueTplEditorState } from './state/venue-tpl-editor.state';

@Injectable()
export class VenueTplEditorSelectionService {

    private _graphicSelectionTrigger = new BehaviorSubject(null);

    private _venueTplEdState = inject(VenueTplEditorState);

    getSelection$(): Observable<VenueTplEditorSelection> {
        return this._venueTplEdState.selection.getValue$();
    }

    graphicSelection$(): Observable<VenueTplEditorSelection> {
        return combineLatest([
            this.getSelection$(),
            this._graphicSelectionTrigger.asObservable()
        ])
            .pipe(map(([selection]) => selection));
    }

    refreshSelection(): void {
        this._venueTplEdState.selection.getValue$().pipe(take(1))
            .subscribe(selection => this._venueTplEdState.selection.setValue(selection));
    }

    // used to refresh text selection rectangle when it's value is changed
    refreshSelectionAppearance(): void {
        this._graphicSelectionTrigger.next(null);
    }

    unselectAll(): void {
        this._venueTplEdState.selection.setValue({
            seats: new Set<number>(),
            nnzs: new Set<number>(),
            elements: []
        });
    }

    selectItems({ seatIds, nnzIds }: { seatIds?: number[]; nnzIds?: number[] }, select = true): void {
        seatIds ??= [];
        nnzIds ??= [];
        if (seatIds.length || nnzIds.length) {
            this._venueTplEdState.selection.getValue$()
                .pipe(take(1))
                .subscribe(selection => {
                    if (select) {
                        seatIds = seatIds.filter(seatId => !selection.seats.has(seatId));
                        nnzIds = nnzIds.filter(nnzId => !selection.nnzs.has(nnzId));
                        if (seatIds?.length || nnzIds?.length) {
                            seatIds.forEach(seatId => selection.seats.add(seatId));
                            nnzIds.forEach(nnzId => selection.nnzs.add(nnzId));
                            this._venueTplEdState.selection.setValue(selection);
                        }
                    } else {
                        seatIds = seatIds.filter(seatId => selection.seats.has(seatId));
                        nnzIds = nnzIds.filter(nnzId => selection.nnzs.has(nnzId));
                        if (seatIds?.length || nnzIds?.length) {
                            seatIds.forEach(seatId => selection.seats.delete(seatId));
                            nnzIds.forEach(nnzId => selection.nnzs.delete(nnzId));
                            this._venueTplEdState.selection.setValue(selection);
                        }
                    }
                });
        }
    }

    selectElements(elements: SVGElement[], options?: { resetSelection?: boolean; refreshSelectionAppearance?: boolean }): void {
        elements = elements?.filter(Boolean);
        if (elements) {
            combineLatest([
                this._venueTplEdState.selection.getValue$(),
                this._venueTplEdState.venueItems.getValue$()
            ])
                .pipe(take(1))
                .subscribe(([selection, items]) => {
                    if (options?.resetSelection) {
                        selection = {
                            seats: new Set<number>(),
                            nnzs: new Set<number>(),
                            elements: []
                        };
                    }
                    elements.forEach(element => {
                        if (element.classList.contains(SVGDefs.classes.interactive) && String(Number(element.id)) === element.id) {
                            const id = Number(element.id);
                            if (element.tagName === SVGDefs.nodeTypes.seat) {
                                if (!selection.seats.has(id)) {
                                    selection.seats.add(items.seats.get(id).id);
                                }
                            } else if (!selection.nnzs.has(id)) {
                                selection.nnzs.add(items.nnzs.get(id).id);
                            }
                        } else if (!selection.elements.includes(element)) {
                            selection.elements.push(element);
                        }
                    });
                    this._venueTplEdState.selection.setValue(selection);
                    if (options?.refreshSelectionAppearance) {
                        this.refreshSelectionAppearance();
                    }
                });
        }
    }

    invertElementSelection(element: SVGElement): void {
        if (element) {
            combineLatest([this._venueTplEdState.selection.getValue$(), this._venueTplEdState.venueItems.getValue$()])
                .pipe(take(1))
                .subscribe(([selection, items]) => {
                    if (element.classList.contains(SVGDefs.classes.interactive) && String(Number(element.id)) === element.id) {
                        // seat or not numbered zone
                        const isSeat = element.tagName === SVGDefs.nodeTypes.seat;
                        const selectionItemsSet = isSeat ? selection.seats : selection.nnzs;
                        const item = isSeat ? items.seats.get(Number(element.id)) : items.nnzs.get(Number(element.id));
                        if (!selectionItemsSet.has(Number(element.id))) {
                            selectionItemsSet.add(item.id);
                        } else {
                            selectionItemsSet.delete(item.id);
                        }
                    } else {
                        const item = element;
                        const select = !selection.elements.includes(element);
                        if (select) {
                            selection.elements.push(item);
                        } else {
                            selection.elements = selection.elements.filter(e => e !== item);
                        }
                    }
                    this._venueTplEdState.selection.setValue(selection);
                });
        }
    }
}

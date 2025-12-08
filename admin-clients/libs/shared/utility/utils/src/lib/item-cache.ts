import { EMPTY, forkJoin, mapTo, Observable, of, tap } from 'rxjs';
import { map } from 'rxjs/operators';

export class ItemCache<T extends { id: unknown }> {
    private _itemsMap = new Map<T['id'], T>();

    constructor() {
    }

    clear(): void {
        this._itemsMap.clear();
    }

    cacheItems(items: T[]): void {
        items.forEach(item => this._itemsMap.set(item.id, item));
    }

    getItems$(
        ids: T['id'][],
        apiRequest: (id: T['id']) => Observable<T>
    ): Observable<T[]> {
        return forkJoin(
            ids.map(id => {
                if (this._itemsMap.has(id)) {
                    return of(this._itemsMap.get(id));
                } else {
                    return apiRequest(id).pipe(
                        map(loadedItem => {
                            if (loadedItem) {
                                this._itemsMap.set(loadedItem.id, loadedItem);
                                return loadedItem;
                            } else {
                                return null;
                            }
                        })
                    );
                }
            })
        );
    }

    getItemsBulk$(ids: T['id'][], apiRequest: (id: T['id'][]) => Observable<T[]>): Observable<T[]> {
        const pendingIds = ids.filter(id => !this._itemsMap.has(id));
        let result: Observable<void>;
        if (pendingIds.length) {
            result = apiRequest(pendingIds)
                .pipe(
                    tap(result => this.cacheItems(result)),
                    mapTo(null)
                );
        } else {
            result = EMPTY;
        }
        return result.pipe(map(() => ids.map(id => this._itemsMap.get(id))));
    }
}

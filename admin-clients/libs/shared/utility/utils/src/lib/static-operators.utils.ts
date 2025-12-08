import { ListResponse, Metadata } from '@OneboxTM/utils-state';
import { Params } from '@angular/router';
import { combineLatest, forkJoin, Observable, of } from 'rxjs';
import { catchError, delay, distinctUntilChanged, finalize, first, flatMap, map, switchMap, tap } from 'rxjs/operators';

export function booleanOrMerge(booleans: Observable<boolean>[]): Observable<boolean> {
    return combineLatest(booleans)
        .pipe(
            map(bools => bools.some(isSomething => !!isSomething)),
            distinctUntilChanged()
        );
}

export function fetchAll<T>(
    fetch: (offset: number) => Observable<ListResponse<T>>,
    downloaded?: (progressAmount: number) => void
): Observable<ListResponse<T>> {
    return fetch(0)
        .pipe(
            switchMap(itemsFirstPage => {
                const meta = itemsFirstPage.metadata;
                const result$: Observable<ListResponse<T>>[] = [of(itemsFirstPage)];
                let progress = meta.limit / meta.total;
                downloaded?.(progress > 1 ? 1 : progress);
                for (let incrOffset = meta.limit; incrOffset < meta.total; incrOffset += meta.limit) {
                    result$.push(fetch(incrOffset).pipe(finalize(() => {
                        progress += meta.limit / meta.total;
                        downloaded?.(progress > 1 ? 1 : progress);
                    })));
                }
                return forkJoin(result$);
            }),
            map(responseList => {
                const resultCase: ListResponse<T> = {
                    data: [],
                    metadata: Object.assign(new Metadata(), {
                        ...responseList[0].metadata,
                        limit: responseList[0].metadata.total
                    })
                };
                return responseList.reduce<ListResponse<T>>((accum, current) => {
                    accum.data.push(...current.data);
                    return accum;
                }, resultCase);
            })
        );
}

export function applyAsyncFieldValue$<T>(
    obj: object, field: string, value: string, data$: Observable<T[]>, dataKey: string
): Observable<T[]> {
    if (value) {
        return data$.pipe(
            first(items => !!items),
            tap(items => {
                obj[field] = items
                    .find(item => {
                        const itemKey = item[dataKey];
                        if (typeof itemKey === 'number') {
                            return itemKey === Number(value);
                        }
                        return itemKey === value;
                    }) || null;
            }),
            catchError(error => of(error))
        );
    }
    return of(null);
}

export function applyAsyncFieldOnServerStream$<T extends { id: string | number }>(
    obj: Record<string, unknown>, field: string, value: string | string[] | number, options$: Observable<T[]>
): Observable<T[]> {
    if (value) {
        if (!Array.isArray(value)) {
            return options$.pipe(
                first(items => !!items),
                tap(items => obj[field] = items.find(item => String(item?.id) === value) || { id: value, name: `NA(${value})` }),
                catchError(error => of(error))
            );
        } else {
            return options$.pipe(
                first(items => !!items),
                tap(items =>
                    obj[field] = value.map(val => items.find(item => String(item?.id) === val) || { id: val, name: `NA(${val})` })
                ),
                catchError(error => of(error))
            );
        }
    }
    return of(null);
}

export function applyAsyncFieldWithServerReq$<T extends { id: string | number }>(
    formFields: Record<string, unknown>,
    field: string,
    params: Params,
    filterNameGetter: (ids: string[]) => Observable<T[]>,
    multiple = false
): Observable<T[]> {
    if (params[field]) {
        const ids = params[field].split(',');
        return applyAsyncFieldOnServerStream$(formFields, field, multiple ? ids : ids[0], filterNameGetter(ids));
    } else {
        return of(null);
    }
}

export function runWithRetriesIfNull$<T>(
    runnableFunction: () => Observable<T>, maxAttempts: number, delayInMillis: number
): Observable<T> {
    return retryIfNull$(runnableFunction, 1, maxAttempts, delayInMillis);
}

function retryIfNull$<T>(
    runnableFunction: () => Observable<T>, attempt: number, maxAttempts: number, delayInMillis: number
): Observable<T> {
    return runnableFunction().pipe(
        flatMap(response => {
            if (response === null) {
                if (attempt >= maxAttempts) {
                    return of(null);
                }
                return of(true).pipe(
                    delay(delayInMillis),
                    flatMap(() => retryIfNull$(runnableFunction, ++attempt, maxAttempts, delayInMillis))
                );
            }
            return of(response);
        })
    );
}

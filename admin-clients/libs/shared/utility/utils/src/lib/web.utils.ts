import { finalize, Observable, Subject } from 'rxjs';

/**
 * In order to use MutationObserver as a rxjs Observable
 */
export function getNodeMutations$(target: Node, options?: MutationObserverInit): Observable<MutationRecord[]> {
    const subject = new Subject<MutationRecord[]>();
    const mutationObserver = new MutationObserver(mutationsList => subject.next(mutationsList));
    mutationObserver.observe(target, options);
    return subject.asObservable()
        .pipe(finalize(() => {
            mutationObserver.disconnect();
            subject.complete();
        }));
}

/**
 * In order to use IntersectionObserver as a rxjs Observable
 */
export function getIntersectionObserverEntries$(
    target: Element,
    options?: IntersectionObserverInit
): Observable<IntersectionObserverEntry[]> {
    const subject = new Subject<IntersectionObserverEntry[]>();
    const intersectionObserver = new IntersectionObserver(intersectionObserverEntry => subject.next(intersectionObserverEntry), options);
    intersectionObserver.observe(target);
    return subject.asObservable()
        .pipe(finalize(() => {
            intersectionObserver.disconnect();
            subject.complete();
        }));
}

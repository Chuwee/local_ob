import { BehaviorSubject, Observable } from 'rxjs';

/** @deprecated Use StatePropertyGroup instead*/
export class BaseStatePropGroup<T> {
    private _currentSetter = this.completeSetter.bind(this);
    private _currentGetter$ = this.completeGetter$.bind(this);
    private _behavior: BehaviorSubject<T>;
    private _observable: Observable<T>;

    getSetterFunction(): (value: T) => void {
        return this.setter.bind(this);
    }

    getGetterFunction(): () => Observable<T> {
        return this.getter$.bind(this);
    }

    private setter(value: T): void {
        this._currentSetter(value);
    }

    private getter$(): Observable<T> {
        return this._currentGetter$();
    }

    // MAIN VALUE

    private completeSetter(value: T): void {
        this.initValueStructure();
        this._currentSetter(value);
    }

    private completeGetter$(): Observable<T> {
        this.initValueStructure();
        return this._currentGetter$();
    }

    private initValueStructure(): void {
        this._behavior = new BehaviorSubject<T>(null);
        this._observable = this._behavior.asObservable();
        this._currentSetter = this.simpleSetter.bind(this);
        this._currentGetter$ = this.simpleGetter$.bind(this);
    }

    private simpleSetter(value: T): void {
        this._behavior.next(value);
    }

    private simpleGetter$(): Observable<T> {
        return this._observable;
    }
}

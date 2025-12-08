import { Category } from '@admin-clients/shared/common/data-access';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable()
export class CategoriesState {
    private _categoriesList: BehaviorSubject<Category[]> = new BehaviorSubject(null);
    private _categoriesList$: Observable<Category[]> = this._categoriesList.asObservable();

    private _categoriesListLoading: BehaviorSubject<boolean> = new BehaviorSubject(false);
    private _categoriesListLoading$: Observable<boolean> = this._categoriesListLoading.asObservable();

    getCategoriesList$(): Observable<Category[]> {
        return this._categoriesList$;
    }

    setCategoriesList(list: Category[]): void {
        this._categoriesList.next(list);
    }

    getCategoriesListLoading$(): Observable<boolean> {
        return this._categoriesListLoading$;
    }

    setCategoriesListLoading(value: boolean): void {
        this._categoriesListLoading.next(value);
    }
}

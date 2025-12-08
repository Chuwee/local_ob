import { Category } from '@admin-clients/shared/common/data-access';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { CategoriesApi } from './api/categories.api';
import { CategoriesState } from './state/categories.state';

@Injectable()
export class CategoriesService {

    constructor(private _categoriesApi: CategoriesApi, private _categoriesState: CategoriesState) { }

    loadCategories(): void {
        this._categoriesState.setCategoriesListLoading(true);
        this._categoriesApi.getCategories()
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._categoriesState.setCategoriesListLoading(false))
            )
            .subscribe(categories => {
                this._categoriesState.setCategoriesList(categories);
                this._categoriesState.setCategoriesListLoading(false);
            });
    }

    getCategories$(): Observable<Category[]> {
        return this._categoriesState.getCategoriesList$();
    }

    isCategoriesLoading$(): Observable<boolean> {
        return this._categoriesState.getCategoriesListLoading$();
    }

    clearCategories(): void {
        this._categoriesState.setCategoriesList(null);
    }
}

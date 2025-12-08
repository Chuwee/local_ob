import { Category } from '@admin-clients/shared/common/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class CategoriesApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly CATEGORIES_API = `${this.BASE_API}/mgmt-api/v1/base-categories`;

    private readonly _http = inject(HttpClient);

    getCategories(): Observable<Category[]> {
        return this._http.get<Category[]>(this.CATEGORIES_API);
    }
}

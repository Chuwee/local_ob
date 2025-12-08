import { Provider } from '@angular/core';
import { CategoriesApi } from './api/categories.api';
import { CategoriesService } from './categories.service';
import { CategoriesState } from './state/categories.state';

export const categoriesProviders: Provider[] = [
    CategoriesApi,
    CategoriesState,
    CategoriesService
];

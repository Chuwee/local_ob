import { FilterComponent } from './filter.component';

export abstract class FilterWrapped extends FilterComponent {
    applyFilters(): void {
        this.filtersSubject.next(this.getFilters());
    }
}

import {
    EmptyStateTinyComponent, SearchInputComponent
} from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, map, filter, combineLatest, shareReplay, Observable } from 'rxjs';
import { SalesSettingsService } from '../../../sales-settings.service';

@Component({
    imports: [
        TranslatePipe, ReactiveFormsModule, MatExpansionModule, MatIcon, MatPaginatorModule, MatButtonModule,
        MatListModule, MatTooltipModule, MatProgressSpinnerModule, AsyncPipe, SearchInputComponent, EmptyStateTinyComponent,
        EllipsifyDirective
    ],
    selector: 'app-error-setting-container',
    templateUrl: './error-setting-container.component.html',
    styleUrls: ['./error-setting-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ErrorSettingContainerComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #salesSettingsService = inject(SalesSettingsService);
    readonly #authService = inject(AuthenticationService);

    readonly #elementSearchText = new BehaviorSubject<string>('');
    readonly #listPage = new BehaviorSubject<number>(0);

    readonly $isLoading = toSignal(this.#salesSettingsService.salesConfiguration.isInProgress$());
    readonly pageLimit = 10;

    readonly form = this.#fb.group({
        countries: [[] as string[]],
        excluded_states: [[] as string[]],
        excluded_taxonomies: [[] as string[]],
        keywords: [[] as string[]]
    });

    readonly listPage$ = this.#listPage.asObservable();

    @Input() listControl: FormControl<string[]>;

    @Input() title: string;
    @Input() placeHolder: string;
    @Input() elementTitle: string;
    @Input() newElementTitle: string;
    @Input() deleteTooltip: string;

    @Output() readonly deleteElement = new EventEmitter<string>();
    @Output() readonly addElement = new EventEmitter<void>();

    readonly $hasWritePermissions = toSignal(this.#authService.getLoggedUser$().pipe(
        map(loggedUser => AuthenticationService.doesUserHaveAllPermissions(loggedUser,
            [UserPermissions.configurationWrite, UserPermissions.salesWrite]))
    ));

    filteredList$: Observable<string[]>;
    pagedList$: Observable<string[]>;

    ngOnInit(): void {
        this.filteredList$ = combineLatest([
            this.listControl?.valueChanges,
            this.#elementSearchText
        ]).pipe(
            filter(Boolean),
            map(([elements, searchText]) => elements?.filter(c => c.toLocaleUpperCase().includes(searchText.toLocaleUpperCase()))),
            shareReplay(1)
        );

        this.pagedList$ = combineLatest([
            this.filteredList$,
            this.#listPage
        ]).pipe(
            filter(Boolean),
            map(([filteredElements, page]) => {
                let pagedElements = filteredElements?.slice(page * this.pageLimit, (page + 1) * this.pageLimit);
                if (filteredElements?.length > 0 && pagedElements?.length === 0) {
                    pagedElements = filteredElements?.slice((page - 1) * this.pageLimit, (page) * this.pageLimit);
                    this.#listPage.next(this.#listPage.getValue() - 1);
                }
                return pagedElements;
            }),
            shareReplay(1)
        );
    }

    changePage(pageEvent: PageEvent): void {
        this.#listPage.next(pageEvent.pageIndex);
    }

    changeSearchText(text: string): void {
        this.#elementSearchText.next(text);
    }

    deleteElementFromList(element: string): void {
        this.deleteElement.emit(element);
    }

    addElementDialog(): void {
        this.addElement.emit();
    }
}

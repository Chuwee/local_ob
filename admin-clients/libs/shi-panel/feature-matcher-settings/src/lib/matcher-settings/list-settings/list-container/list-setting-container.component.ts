import {
    EmptyStateTinyComponent, SearchInputComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, map, filter, combineLatest, shareReplay, Observable } from 'rxjs';
import { MatcherSettingsService } from '../../../matcher-settings.service';

@Component({
    imports: [
        CommonModule, MaterialModule, TranslatePipe, ReactiveFormsModule,
        FlexLayoutModule, SearchInputComponent, EmptyStateTinyComponent, EllipsifyDirective
    ],
    selector: 'app-list-setting-container',
    templateUrl: './list-setting-container.component.html',
    styleUrls: ['./list-setting-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ListSettingContainerComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #matcherSettingsService = inject(MatcherSettingsService);
    readonly #authService = inject(AuthenticationService);

    readonly #elementSearchText = new BehaviorSubject<string>('');
    readonly #listPage = new BehaviorSubject<number>(0);

    readonly isLoading$ = this.#matcherSettingsService.matcherConfiguration.isLoading$();
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

    readonly hasWritePermissions$ = this.#authService.getLoggedUser$().pipe(
        map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.matchingWrite]))
    );

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
                let pagedCountries = filteredElements?.slice(page * this.pageLimit, (page + 1) * this.pageLimit);
                if (filteredElements?.length > 0 && pagedCountries?.length === 0) {
                    pagedCountries = filteredElements?.slice((page - 1) * this.pageLimit, (page) * this.pageLimit);
                    this.#listPage.next(this.#listPage.getValue() - 1);
                }
                return pagedCountries;
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

import {
    EmptyStateTinyComponent, SearchInputComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject, BehaviorSubject, map, filter, combineLatest, shareReplay, Observable } from 'rxjs';
import { IngestorSettingsService } from '../../../ingestor-settings.service';

@Component({
    imports: [
        CommonModule, MaterialModule, TranslatePipe, ReactiveFormsModule,
        FlexLayoutModule, SearchInputComponent, EmptyStateTinyComponent, EllipsifyDirective
    ],
    selector: 'app-ingestor-list-setting-container',
    templateUrl: './ingestor-list-setting-container.component.html',
    styleUrls: ['./ingestor-list-setting-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class IngestorListSettingContainerComponent implements OnInit, OnDestroy {
    private readonly _onDestroy: Subject<void> = new Subject<void>();
    private readonly _fb = inject(FormBuilder);
    private readonly _ingestorSettingsService = inject(IngestorSettingsService);
    private readonly _authService = inject(AuthenticationService);

    private readonly _elementSearchText = new BehaviorSubject<string>('');
    private readonly _listPage = new BehaviorSubject<number>(0);

    readonly isLoading$ = this._ingestorSettingsService.ingestorConfiguration.isInProgress$();
    readonly pageLimit = 10;

    readonly form = this._fb.group({
        excluded_sections: [[] as string[]],
        general_admission: [[] as string[]]
    });

    readonly listPage$ = this._listPage.asObservable();

    @Input() listControl: FormControl<string[]>;

    @Input() title: string;
    @Input() placeHolder: string;
    @Input() elementTitle: string;
    @Input() newElementTitle: string;
    @Input() deleteTooltip: string;

    @Output() readonly deleteElement = new EventEmitter<string>();
    @Output() readonly addElement = new EventEmitter<void>();

    readonly hasWritePermissions$ = this._authService.getLoggedUser$().pipe(
        map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.ingestorWrite]))
    );

    filteredList$: Observable<string[]>;
    pagedList$: Observable<string[]>;

    ngOnInit(): void {
        this.filteredList$ = combineLatest([
            this.listControl?.valueChanges,
            this._elementSearchText
        ]).pipe(
            filter(Boolean),
            map(([elements, searchText]) => elements?.filter(c => c.toLocaleUpperCase().includes(searchText.toLocaleUpperCase()))),
            shareReplay(1)
        );

        this.pagedList$ = combineLatest([
            this.filteredList$,
            this._listPage
        ]).pipe(
            filter(Boolean),
            map(([filteredElements, page]) => {
                let pagedKeywords = filteredElements?.slice(page * this.pageLimit, (page + 1) * this.pageLimit);
                if (filteredElements?.length > 0 && pagedKeywords?.length === 0) {
                    pagedKeywords = filteredElements?.slice((page - 1) * this.pageLimit, (page) * this.pageLimit);
                    this._listPage.next(this._listPage.getValue() - 1);
                }
                return pagedKeywords;
            }),
            shareReplay(1)
        );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._listPage.complete();
        this._elementSearchText.complete();
    }

    changePage(pageEvent: PageEvent): void {
        this._listPage.next(pageEvent.pageIndex);
    }

    changeSearchText(text: string): void {
        this._elementSearchText.next(text);
    }

    deleteElementFromList(element: string): void {
        this.deleteElement.emit(element);
    }

    addElementDialog(): void {
        this.addElement.emit();
    }
}

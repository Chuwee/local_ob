import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EventsService, Event } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent, AttributesComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { AttributeWithValues } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { EMPTY, Observable, Subject } from 'rxjs';
import { filter, first, map, shareReplay, takeUntil, tap } from 'rxjs/operators';

@Component({
    imports: [
        NgIf, AsyncPipe,
        FlexLayoutModule,
        ReactiveFormsModule,
        MaterialModule,
        FormContainerComponent,
        AttributesComponent,
        ArchivedEventMgrComponent
    ],
    selector: 'app-event-attributes',
    templateUrl: './attributes.component.html',
    styleUrls: ['./attributes.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventAttributesComponent implements OnInit, OnDestroy, WritingComponent {
    private _eventId: number;
    private _onDestroy = new Subject<void>();
    private _event$: Observable<Event>;
    @ViewChild(AttributesComponent) private _attributesComponent: AttributesComponent;
    form: UntypedFormGroup;
    isLoadingOrSaving$: Observable<boolean>;
    attributes$: Observable<AttributeWithValues[]>;
    userLanguage$: Observable<string>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _eventsService: EventsService,
        private _auth: AuthenticationService
    ) { }

    ngOnInit(): void {
        this.form = this._fb.group({});

        this._eventsService.eventAttributes.clear();

        this._event$ = this._eventsService.event.get$()
            .pipe(
                filter(event => !!event),
                tap(({ id }) => this._eventId = id),
                tap(({ id }) => this._eventsService.eventAttributes.load(id, true)),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );

        this.attributes$ = this._eventsService.eventAttributes.get$().pipe(
            filter(attributes => !!attributes)
        );

        this.userLanguage$ = this._auth.getLoggedUser$().pipe(first(Boolean), map(user => user.language));

        this.isLoadingOrSaving$ = booleanOrMerge([
            this._eventsService.event.inProgress$(),
            this._eventsService.eventAttributes.loading$()
        ]);

        this._event$.subscribe();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    refresh(): void {
        this.form.markAsPristine();
        this._eventsService.eventAttributes.load(this._eventId, true);
    }

    save(): void {
        this.save$().subscribe(() => this.refresh());
    }

    save$(): Observable<void> {
        const attributes = this._attributesComponent.data();
        if (this.form.valid && attributes) {
            return this._eventsService.eventAttributes.update(this._eventId, attributes);
        }
        return EMPTY;
    }

}

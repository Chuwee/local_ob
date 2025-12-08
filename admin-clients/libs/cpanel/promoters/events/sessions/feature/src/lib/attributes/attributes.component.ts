import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { AttributesComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { EventSessionsService, Session } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { AttributeWithValues } from '@admin-clients/shared/common/data-access';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Observable, Subject, throwError } from 'rxjs';
import { filter, first, map, shareReplay, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-session-attributes',
    templateUrl: './attributes.component.html',
    styleUrls: ['./attributes.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionAttributesComponent implements WritingComponent, OnInit, OnDestroy {
    private _eventId: number;
    private _sessionId: number;
    private _onDestroy = new Subject<void>();
    private _session$: Observable<Session>;
    @ViewChild(AttributesComponent) private _attributesComponent: AttributesComponent;
    form: UntypedFormGroup;
    isLoadingOrSaving$: Observable<boolean>;
    attributes$: Observable<AttributeWithValues[]>;
    userLanguage$: Observable<string>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _sessionsService: EventSessionsService,
        private _auth: AuthenticationService
    ) { }

    ngOnInit(): void {
        this.form = this._fb.group({});

        this._sessionsService.clearSessionAttributes();

        this._session$ = this._sessionsService.session.get$()
            .pipe(
                filter(session => !!session),
                tap(({ id }) => this._sessionId = id),
                tap(({ event }) => this._eventId = event.id),
                tap(({ id }) => this._sessionsService.loadSessionAttributes(this._eventId, id, true)),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );

        this.attributes$ = this._sessionsService.getSessionAttributes$().pipe(
            filter(attributes => !!attributes)
        );

        this.userLanguage$ = this._auth.getLoggedUser$().pipe(first(Boolean), map(user => user.language));

        this.isLoadingOrSaving$ = booleanOrMerge([
            this._sessionsService.session.loading$(),
            this._sessionsService.isSessionAttributesInProgress$()
        ]);

        this._session$.subscribe();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    refresh(): void {
        this.form.markAsPristine();
        this._sessionsService.loadSessionAttributes(this._eventId, this._sessionId, true);
    }

    save(): void {
        this.save$().subscribe(() => this.refresh());
    }

    save$(): Observable<void> {
        const attributes = this._attributesComponent.data();
        if (this.form.valid && attributes) {
            return this._sessionsService.saveSessionAttributes(this._eventId, this._sessionId, attributes);
        } else {
            return throwError(() => 'invalid form');
        }
    }
}

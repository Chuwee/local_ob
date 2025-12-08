import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    EntityUser, EntityUserStatus, EntityUsersService, PutEntityUser, UserFieldsRestrictions
} from '@admin-clients/cpanel/organizations/entity-users/data-access';
import {
    CountriesService, LanguagesService, RegionsService, EntitiesFilterFields
} from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService,
    SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, QueryList, ViewChildren, inject } from
    '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, Observable, throwError } from 'rxjs';
import {
    distinctUntilKeyChanged, filter, finalize, first, map, shareReplay,
    startWith, switchMap, take, tap, withLatestFrom
} from 'rxjs/operators';
import { MYSELF_USER_DETAILS_TOKEN } from '../../entity-user.token';

@Component({
    selector: 'ob-register-data-principal-info',
    templateUrl: './register-data-principal-info.component.html',
    styleUrls: ['./register-data-principal-info.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, CommonModule, ReactiveFormsModule, TranslatePipe, FlexLayoutModule,
        MaterialModule, SelectSearchComponent, FormControlErrorsComponent
    ]
})
export class RegisterDataPrincipalInfoComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #auth = inject(AuthenticationService);
    readonly #entityUsersService = inject(EntityUsersService);
    readonly #entitiesService = inject(EntitiesService);
    readonly #languagesService = inject(LanguagesService);
    readonly #countriesService = inject(CountriesService);
    readonly #regionsService = inject(RegionsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #myself = inject(MYSELF_USER_DETAILS_TOKEN);
    readonly #destroyRef = inject(DestroyRef);
    readonly #translateSrv = inject(TranslateService);

    #userId: number | 'myself';
    #showEntityOperatorWarning = false;

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly form = this.#fb.group({
        userStatus: [null, Validators.required],
        generalData: this.#fb.group({
            entity: [null, Validators.required],
            name: [null, [
                Validators.required,
                Validators.maxLength(UserFieldsRestrictions.userNameLength)
            ]],
            surname: [null, [
                Validators.required,
                Validators.maxLength(UserFieldsRestrictions.userSurnameLength)
            ]],
            position: [null, Validators.maxLength(UserFieldsRestrictions.userPositionLength)],
            language: null
        }),
        contact: this.#fb.group({
            country: null,
            countrySubdivision: { value: null, disabled: true },
            city: [null, Validators.maxLength(UserFieldsRestrictions.userCityMaxLength)],
            address: [null, Validators.maxLength(UserFieldsRestrictions.userAdressMaxLength)],
            postalCode: [null, [
                Validators.maxLength(UserFieldsRestrictions.userPostalCodeMaxLength),
                Validators.pattern(UserFieldsRestrictions.userPostalCodePattern)
            ]],
            phone: [null, [
                Validators.maxLength(UserFieldsRestrictions.userPhoneLength),
                Validators.pattern(UserFieldsRestrictions.userPhonePattern)
            ]],
            cellphone: [null, [
                Validators.maxLength(UserFieldsRestrictions.userPhoneLength),
                Validators.pattern(UserFieldsRestrictions.userPhonePattern)
            ]]
        }),
        notes: [null, Validators.maxLength(UserFieldsRestrictions.notesMaxLength)]
    });

    readonly isOperatorMode$ = combineLatest([
        this.#auth.getLoggedUser$(),
        this.#entityUsersService.getEntityUser$()
    ])
        .pipe(
            filter(sources => sources.every(Boolean)),
            map(([authUser, entityUser]) =>
                authUser.id !== entityUser.id && AuthenticationService.isSomeRoleInUserRoles(authUser, [UserRoles.OPR_MGR])
            )
        );

    readonly user$ = this.#entityUsersService.getEntityUser$()
        .pipe(
            filter(Boolean),
            tap(user => this.#updateForm(user)),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly $canEditUserStatus = toSignal(combineLatest([
        this.#auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR, UserRoles.OPR_MGR, UserRoles.ENT_MGR]),
        this.user$,
        this.#auth.getLoggedUser$()
    ])
        .pipe(
            filter(sources => sources.every(Boolean)),
            map(([hasRoles, user, loggedUser]) => hasRoles && user.id !== loggedUser.id)
        ));

    readonly entities$ = this.#auth.getLoggedUser$().pipe(
        filter(Boolean),
        switchMap(user => {
            const isSysAdminAndOperator = AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.SYS_MGR, UserRoles.OPR_MGR]);
            if (isSysAdminAndOperator) {
                this.#entitiesService.entityList.load({
                    limit: 999,
                    sort: 'name:asc',
                    include_entity_admin: true,
                    fields: [EntitiesFilterFields.name]
                });
                return this.#entitiesService.entityList.getData$();
            } else {
                this.#entitiesService.loadEntity(user.entity.id);
                return this.#entitiesService.getEntity$().pipe(
                    filter(Boolean),
                    map(entity => [entity])
                );
            }
        }),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly isSysAdmin$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR]);
    readonly languages$ = this.#languagesService.getLanguages$().pipe(map(languages => [null].concat(languages)));
    readonly countries$ = this.#countriesService.getCountries$();
    readonly regions$ = this.#regionsService.getRegions$();
    readonly filteredRegions$ = combineLatest([
        this.form.get('contact.country').valueChanges.pipe(startWith(null as string)),
        this.regions$
    ]).pipe(
        filter(([_, regions]) => !!regions),
        map(([countryCode, regions]) =>
            regions.filter(region => region.code.startsWith(countryCode + '-'))
        ),
        tap(regions => {
            const countrySubdivisionField = this.form.get('contact.countrySubdivision');
            if (regions.length > 0) {
                countrySubdivisionField.enable();
            } else {
                countrySubdivisionField.disable();
                countrySubdivisionField.setValue(null);
            }
        })
    );

    readonly isLoading$ = booleanOrMerge([
        this.#entityUsersService.isEntityUserLoading$(),
        this.#entityUsersService.isApiKeyRefreshing$(),
        this.#languagesService.isLanguagesInProgress$(),
        this.#countriesService.isCountriesLoading$(),
        this.#regionsService.isRegionsLoading$(),
        this.#entityUsersService.isApiKeyRefreshing$(),
        this.#entitiesService.isEntityLoading$()
    ]);

    readonly userStatus = EntityUserStatus;
    readonly userStatusList = Object.values(EntityUserStatus);

    ngOnInit(): void {
        if (this.#myself) {
            this.#userId = 'myself';
            // Reload with translations when language changes
            this.#entityUsersService.getEntityUser$().pipe(
                distinctUntilKeyChanged('language'),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(user => this.#translateSrv.use(user.language));
        } else {
            this.#entityUsersService.getEntityUser$().pipe(first(Boolean)).subscribe(user => this.#userId = user.id);
        }
        // list of languages
        this.#languagesService.loadLanguages(true); // true argument filters languages list for only available languages in cpanel
        // list of countries
        this.#countriesService.loadCountries();
        // list of regions
        this.#regionsService.loadRegions();

        //When logged user is operator, must load the selected entity for checking if it is of type operator
        this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR])
            .pipe(
                take(1),
                filter(Boolean),
                switchMap(() => this.form.get('generalData.entity').valueChanges),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(entityId => this.#entitiesService.loadEntity(entityId));

        this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR])
            .pipe(
                take(1),
                filter(Boolean),
                switchMap(() => this.#entitiesService.getEntity$().pipe(filter(Boolean))),
                withLatestFrom(this.#entityUsersService.getEntityUser$().pipe(filter(Boolean))),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([entity, entityUser]) => this.#showEntityOperatorWarning =
                entity.settings?.types?.includes('OPERATOR') && entityUser.entity.id !== entity.id);

    }

    ngOnDestroy(): void {
        this.#languagesService.clearLanguages();
        this.#entitiesService.clearEntity();
    }

    cancel(): void {
        this.#entityUsersService.loadEntityUser(this.#userId);
    }

    save(): void {
        this.save$().pipe(first()).subscribe();
    }

    save$(): Observable<EntityUser> {
        if (this.form.valid && this.form.dirty) {
            const fv = this.form.value;
            const userChanges: PutEntityUser = {
                status: fv.userStatus,
                entity_id: fv.generalData.entity,
                name: fv.generalData.name,
                last_name: fv.generalData.surname,
                job_title: fv.generalData.position || '',
                language: fv.generalData.language || '',
                notes: fv.notes || '',
                location: {
                    country: fv.contact.country ? { code: fv.contact.country } : undefined,
                    country_subdivision: fv.contact.countrySubdivision ? { code: fv.contact.countrySubdivision } : undefined,
                    city: fv.contact.city || '',
                    address: fv.contact.address || '',
                    postal_code: fv.contact.postalCode || ''
                },
                contact: {
                    primary_phone: fv.contact.phone || '',
                    secondary_phone: fv.contact.cellphone || ''
                }
            };

            let response: Observable<EntityUser>;
            if (this.#showEntityOperatorWarning) {
                response = this.#entityUsersService.getEntityUser$()
                    .pipe(
                        take(1),
                        switchMap(user => this.#msgDialogService.showWarn({
                            size: DialogSize.MEDIUM,
                            title: 'TITLES.ALERT',
                            message: 'USER.ENTITY_OPERATOR_WARNING',
                            actionLabel: 'FORMS.ACTIONS.SAVE',
                            showCancelButton: true
                        })
                            .pipe(
                                switchMap(accepted => {
                                    if (accepted) {
                                        return this.#entityUsersService.updateEntityUser(this.#userId, userChanges)
                                            .pipe(map(() => user));
                                    } else {
                                        /* Throw error to cancel navigation if alert appears when we save changes
                                        on UnsavedChangesDialog and then cancel */
                                        return throwError(() => 'save canceled');
                                    }
                                })
                            )
                        ),
                        tap(user => {
                            /* "If" block necessary for when we cancel after UnsavedChangesDialog appears
                            (if we don't check "if (user)", an "Uncaught (in promise): EmptyError: no elements in sequence"
                            error will appear) */
                            if (user) {
                                this.#ephemeralSrv.showSuccess({ msgKey: 'USER.UPDATE_SUCCESS', msgParams: { userEmail: user.username } });
                            }
                        }),
                        takeUntilDestroyed(this.#destroyRef)
                    );
            } else {
                response = this.#entityUsersService.getEntityUser$()
                    .pipe(
                        take(1),
                        switchMap(user =>
                            this.#entityUsersService.updateEntityUser(this.#userId, userChanges).pipe(map(() => user))
                        ),
                        tap(user => {
                            this.#ephemeralSrv.showSuccess({ msgKey: 'USER.UPDATE_SUCCESS', msgParams: { userEmail: user.username } });
                        })
                    );
            }
            return response.pipe(finalize(() => this.#entityUsersService.loadEntityUser(this.#userId)));
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    #updateForm(user: EntityUser): void {
        this.form.patchValue({
            userStatus: user.status,
            generalData: {
                entity: user.entity.id,
                name: user.name,
                surname: user.last_name,
                position: user.job_title,
                language: user.language
            },
            contact: {
                country: user.location.country?.code,
                countrySubdivision: user.location.country_subdivision?.code,
                city: user.location.city,
                address: user.location.address,
                postalCode: user.location.postal_code,
                phone: user.contact.primary_phone,
                cellphone: user.contact.secondary_phone
            },
            notes: user.notes
        });

        this.form.markAsPristine();
        this.form.markAllAsTouched();
    }
}

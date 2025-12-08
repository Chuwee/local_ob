import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    EntitiesService, EntityFieldRestriction, PostEntity, entitiesProviders
} from '@admin-clients/cpanel/organizations/entities/data-access';
import { CountriesService, Entity } from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { I18nService } from '@admin-clients/shared/core/data-access';
import {
    atLeastOneRequiredInFormGroup,
    nonZeroValidator
} from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, filter, map, takeUntil } from 'rxjs';

const selectableTypes = ['VENUE_ENTITY', 'EVENT_ENTITY', 'CHANNEL_ENTITY', 'INSURANCER'];

@Component({
    selector: 'app-new-entity-dialog',
    templateUrl: './new-entity-dialog.component.html',
    styleUrls: ['./new-entity-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [...entitiesProviders],
    imports: [
        ReactiveFormsModule,
        TranslatePipe,
        CommonModule,
        MaterialModule,
        FormControlErrorsComponent,
        SelectSearchComponent,
        FlexLayoutModule
    ]
})
export class NewEntityDialogComponent implements OnInit, OnDestroy {
    readonly #countriesSrv = inject(CountriesService);
    private _onDestroy = new Subject<void>();
    private _fb = inject(FormBuilder);
    types = Object.values(selectableTypes);
    form = this._fb.group({
        name: ['', [Validators.required, Validators.maxLength(EntityFieldRestriction.maxNameLength)]],
        short_name: ['', [Validators.required, Validators.maxLength(EntityFieldRestriction.maxShortNameLength)]],
        social_reason: ['', [Validators.required, Validators.maxLength(EntityFieldRestriction.maxSocialReasonLength)]],
        nif: ['', [Validators.required, Validators.maxLength(EntityFieldRestriction.maxNifLength)]],
        email: ['', [Validators.required, Validators.email, Validators.maxLength(EntityFieldRestriction.maxEmailLength)]],
        default_language: ['', [Validators.required]],
        country: ['', [Validators.required]],
        city: ['', [Validators.required]],
        settings: this._fb.group({
            managed_entities: [{ value: [] as Entity[], disabled: true }, [Validators.minLength(1), Validators.required]]
        }),
        external_avet_club_code: [{ value: null as number, disabled: true }, [
            Validators.required,
            nonZeroValidator,
            Validators.min(EntityFieldRestriction.minAvetClubCodeValue),
            Validators.max(EntityFieldRestriction.maxAvetClubCodeValue)
        ]],
        types: this._fb.group(this.types.reduce((acc, type) => (acc[type] = false, acc), {})
            , { validators: [atLeastOneRequiredInFormGroup()] })
    });

    languages = this._i18n.getSupportedLanguages();
    isSaving$: Observable<boolean>;
    avetType: FormControl<boolean>;
    isEntityAdmin = this._fb.control(false);
    entities$: Observable<Entity[]>;
    showClearSelection$: Observable<boolean>;

    readonly countries$ = this.#countriesSrv.getCountries$()
        .pipe(
            filter(countries => !!countries),
            map(countries => countries.map(country => ({ name: country.name, code: country.code })))
        );

    constructor(
        private _dialogRef: MatDialogRef<NewEntityDialogComponent>,
        private _i18n: I18nService,
        private _entitiesSrv: EntitiesService,
        private _elemRef: ElementRef
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this.entities$ = this._entitiesSrv.entityList.getData$();
    }

    ngOnInit(): void {
        this.isSaving$ = this._entitiesSrv.isEntityLoading$();
        this._entitiesSrv.entityList.load({ limit: 999 });
        this.#countriesSrv.loadCountries();
        this.isEntityAdmin.valueChanges.pipe(takeUntil(this._onDestroy)).subscribe(isEntityAdmin => {
            if (isEntityAdmin) {
                this.form.controls.settings.controls.managed_entities.enable();
                this.form.controls.types?.disable();
            } else {
                this.form.controls.settings.controls.managed_entities.disable();
                this.form.controls.types?.enable();
            }
        });
        this.showClearSelection$ = this.form.controls.settings.controls.managed_entities.valueChanges
            .pipe(map(value => Boolean(value?.length)));
        this.avetType = this._fb.control(false);
        this.avetType.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(selected => {
                if (selected) {
                    this.form.controls.external_avet_club_code.enable();
                } else {
                    this.form.controls.external_avet_club_code.disable();
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }

    createEntity(): void {
        if (this.form.valid) {
            const { country, city, ...formData } = this.form.value;
            const entity = {
                ...formData,
                contact: {
                    country: { code: country },
                    city
                },
                ...(this.isEntityAdmin.value ? {
                    types: ['ENTITY_ADMIN'],
                    settings: {
                        managed_entities: this.form.value.settings.managed_entities.map(entity => ({ id: entity.id, name: entity.name }))
                    }
                } :
                    { types: Object.keys(this.form.value.types).filter(key => this.form.value.types[key]) }
                )
            };
            this._entitiesSrv.entity.create(entity as PostEntity).subscribe(id => this.close(id));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this._elemRef.nativeElement);
        }
    }

    deselectAll(): void {
        this.form.controls.settings.controls.managed_entities.reset([]);
    }

    close(id: number = null): void {
        this._dialogRef.close(id);
    }
}

import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    MessageDialogService, EphemeralMessageService, MessageDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LiteralsTableComponent } from '@admin-clients/shared/literals/ui';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, firstValueFrom, Observable, of } from 'rxjs';
import { filter, first, map, shareReplay, tap } from 'rxjs/operators';

@Component({
    selector: 'app-entity-contents',
    templateUrl: './entity-contents.component.html',
    styleUrls: ['./entity-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, ReactiveFormsModule, MatProgressSpinner, MatButtonToggleModule,
        TranslatePipe, MatSelectModule, LiteralsTableComponent
    ]
})
export class EntityContentsComponent {
    readonly #fb = inject(FormBuilder);
    readonly #entitiesService = inject(EntitiesService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #authSrv = inject(AuthenticationService);

    readonly form = this.#fb.group({
        literals: this.#fb.group({})
    });

    readonly $isInProgress = toSignal(this.#entitiesService.entityTextContents.inProgress$());
    readonly entity$ = this.#entitiesService.getEntity$();
    readonly $isUserOprMgr = toSignal(this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]));

    readonly languageList$ = this.entity$
        .pipe(
            first(Boolean),
            map(entity => entity.settings?.languages?.available),
            filter(Boolean),
            tap(languages => {
                this.selectedLanguage.next(languages[0]);
                this.loadTextContents(languages[0]);
            })
        );

    readonly literals$ = this.#entitiesService.entityTextContents.get$()
        .pipe(
            filter(Boolean),
            tap(contents => {
                this.form.setControl('literals', this.#fb.group(
                    contents.reduce<Record<string, FormControl<string>>>((acc, content) =>
                        (acc[content.key] = this.#fb.control(content.value), acc), {}
                    )
                ));
            }),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    selectedLanguage = new BehaviorSubject<string>(null);

    async save(): Promise<void> {
        if (this.form.valid) {
            const literals = await firstValueFrom(this.literals$);
            const entity = await firstValueFrom(this.entity$);
            const formliterals = this.form.get('literals').value as Record<string, string>;

            const dirtyLiterals = literals.reduce<typeof literals>((result, literal) => {
                const value = formliterals?.[literal.key];
                if (value != null && literal.value !== value) {
                    result.push({ ...literal, value });
                }
                return result;
            }, []);

            if (dirtyLiterals.length) {
                this.#entitiesService.entityTextContents.update(entity.id, this.selectedLanguage.getValue(), dirtyLiterals)
                    .subscribe(() => {
                        this.loadTextContents(this.selectedLanguage.getValue());
                        this.#ephemeralSrv.showSaveSuccess();
                    });
            } else {
                this.form.markAsPristine();
            }
        }
    }

    cancel(): void {
        this.loadTextContents(this.selectedLanguage.getValue());
    }

    async loadTextContents(lang: string): Promise<void> {
        this.selectedLanguage.next(lang);
        const entity = await firstValueFrom(this.entity$);
        this.#entitiesService.entityTextContents.load(entity.id, lang);
        this.form.markAsPristine();
    }

    validateIfCanDismissChanges(msg?: MessageDialogConfig): Observable<boolean> {
        if (this.form.dirty) {
            return this.#messageDialogService.defaultUnsavedChangesWarn(msg);
        }
        return of(true);
    }
}

import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    LiteralsService, Applications, MembersLanguages, PlatformLanguages
} from '@admin-clients/cpanel-channels-literals-data-access';
import {
    EphemeralMessageService, MessageDialogConfig, MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { CreateLiteralDialogComponent, LiteralDialogData, LiteralsTableComponent } from '@admin-clients/shared/literals/ui';
import { AsyncPipe, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, EMPTY, firstValueFrom, forkJoin, Observable, of, Subject } from 'rxjs';
import { filter, shareReplay, switchMap, tap } from 'rxjs/operators';
import { ImportLiteralsDialogComponent } from './import/import-dialog/import-literals-dialog.component';

@Component({
    selector: 'app-literals',
    imports: [
        FormContainerComponent, MaterialModule, ReactiveFormsModule, TranslatePipe,
        LiteralsTableComponent, AsyncPipe, KeyValuePipe
    ],
    templateUrl: './literals.component.html',
    styleUrls: ['./literals.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class LiteralsComponent implements OnDestroy {
    private _onDestroy = new Subject<void>();

    private readonly _fb = inject(FormBuilder);
    private readonly _literalsService = inject(LiteralsService);
    private readonly _messageDialogService = inject(MessageDialogService);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);
    private readonly _matDialog = inject(MatDialog);
    private readonly _authSrv = inject(AuthenticationService);
    private readonly _languageList = new BehaviorSubject<PlatformLanguages[] | MembersLanguages[]>(Object.values(PlatformLanguages));

    readonly form = this._fb.group({
        literals: this._fb.group({}),
        application: [Applications.channels]
    });

    readonly isLiteralCreationAllowed$ = this._authSrv.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR, UserRoles.SYS_ANS]);
    readonly isInProgress$ = this._literalsService.literals.loading$();
    readonly applications = Applications;
    readonly languageList$ = this._languageList.asObservable().pipe(
        tap(languages => {
            this.selectedLanguage.next(languages[0]);
            this.loadTextContents(languages[0]);
        })
    );

    readonly literals$ = this._literalsService.literals.get$()
        .pipe(
            filter(Boolean),
            tap(contents => {
                this.form.setControl('literals', this._fb.group(
                    contents.reduce<Record<string, FormControl<string>>>((acc, content) =>
                        (acc[content.key] = this._fb.control(content.value), acc), {}
                    )
                ));
            }),
            shareReplay(1)
        );

    selectedLanguage = new BehaviorSubject<string>(null);

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    applicationChanged(): void {
        this._languageList.next(this.form.controls.application.value === Applications.members ? Object.values(MembersLanguages) :
            Object.values(PlatformLanguages));
    }

    async save(): Promise<void> {
        if (this.form.valid) {
            const literals = await firstValueFrom(this.literals$);
            const formliterals = this.form.get('literals').value as Record<string, string>;

            const dirtyLiterals = literals.reduce<typeof literals>((result, literal) => {
                const value = formliterals?.[literal.key];
                if (value != null && literal.value !== value) {
                    result.push({ ...literal, value });
                }
                return result;
            }, []);

            if (dirtyLiterals.length) {
                this._literalsService.literals.create(
                    this.form.value.application,
                    this.selectedLanguage.getValue(),
                    dirtyLiterals)
                    .subscribe(() => {
                        this.loadTextContents(this.selectedLanguage.getValue());
                        this._ephemeralSrv.showSaveSuccess();
                    });
            } else {
                this.form.markAsPristine();
            }
        }
    }

    cancel(): void {
        this.loadTextContents(this.selectedLanguage.getValue());
    }

    openCreateLiteralDialog(): void {
        this.validateIfCanDismissChanges({ message: 'FORMS.ACTIONS.UNSAVED_CHANGES' })
            .pipe(
                switchMap(canChange => {
                    if (canChange) {
                        if (this.form.dirty) {
                            this.loadTextContents(this.selectedLanguage.getValue());
                        }

                        const data = new ObMatDialogConfig<LiteralDialogData>({
                            languages: this._languageList.getValue(),
                            allowHtmlEditor: true,
                            deepKeysAllowed: this.form.value.application !== Applications.portal
                        });

                        return this._matDialog.open(CreateLiteralDialogComponent, data).beforeClosed();
                    } else {
                        return EMPTY;
                    }
                })
            ).subscribe(values => {
                if (values) {
                    const obs$: Observable<void>[] = values.map(value =>
                        this._literalsService.literals.create(
                            this.form.value.application,
                            value.lang,
                            value.textContents
                        ));
                    forkJoin(obs$).subscribe(() => {
                        this._ephemeralSrv.showSaveSuccess();
                        this.loadTextContents(this.selectedLanguage.getValue());
                    });
                }
            });
    }

    loadTextContents(lang: string): void {
        this.selectedLanguage.next(lang);
        this._literalsService.literals.load(this.form.value.application, lang);
        this.form.markAsPristine();
    }

    validateIfCanDismissChanges(msg?: MessageDialogConfig): Observable<boolean> {
        if (this.form.dirty) {
            return this._messageDialogService.defaultUnsavedChangesWarn(msg);
        }
        return of(true);
    }

    importLiterals(): void {
        const data = new ObMatDialogConfig<LiteralDialogData>({
            languages: this._languageList.getValue(),
            deepKeysAllowed: this.form.value.application !== Applications.portal
        });
        this._matDialog.open(
            ImportLiteralsDialogComponent,
            data
        )
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(literals => {
                const obs$: Observable<void>[] = Object.keys(literals).map(lang =>
                    this._literalsService.literals.create(
                        this.form.value.application,
                        lang,
                        literals[lang]
                    ));
                forkJoin(obs$).subscribe(() => {
                    this._ephemeralSrv.showSaveSuccess();
                    this.loadTextContents(this.selectedLanguage.getValue());
                });
            });
    }
}

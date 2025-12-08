import {
    Channel, ChannelsExtendedService, ChannelsService, ChannelType, IsWebChannelPipe
} from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, MessageDialogConfig, MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { CreateLiteralDialogComponent, LiteralDialogData, LiteralsTableComponent, TextContent } from '@admin-clients/shared/literals/ui';
import { AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import {
    BehaviorSubject, combineLatest, EMPTY, filter, first, firstValueFrom, forkJoin, map, Observable, of, shareReplay,
    switchMap, tap, throwError, withLatestFrom
} from 'rxjs';
import { ChannelCommunicationNotifierService } from '../container/channel-communication-notifier.service';

@Component({
    selector: 'app-channel-literals',
    templateUrl: './channel-literals.component.html',
    styleUrls: ['./channel-literals.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, TranslatePipe, AsyncPipe, MaterialModule, ReactiveFormsModule, FlexLayoutModule,
        IsWebChannelPipe, LiteralsTableComponent
    ]
})
export class ChannelLiteralsComponent implements AfterViewInit, WritingComponent {
    readonly #onDestroy = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #channelsService = inject(ChannelsService);
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #communicationNotifierService = inject(ChannelCommunicationNotifierService);
    readonly #matDialog = inject(MatDialog);
    readonly #authSrv = inject(AuthenticationService);
    readonly #route = inject(ActivatedRoute);
    readonly #isNewLiterals = this.#route.snapshot.routeConfig.path === 'literals';

    readonly form = this.#fb.group({
        literals: this.#fb.group({})
    });

    readonly isInProgress$ = this.#channelsExtSrv.isTextContentsInProgress$();
    readonly channel$ = this.#channelsService.getChannel$();
    readonly isLiteralCreationAllowed$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);

    readonly languageList$ = this.channel$
        .pipe(
            first(Boolean),
            map(channel => channel.languages.selected),
            filter(Boolean),
            tap(languages => {
                this.selectedLanguage.next(languages[0]);
                this.loadTextContents(languages[0]);
            })
        );

    readonly literals$ = this.#channelsExtSrv.getTextContents$()
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

    readonly isV4$ = combineLatest([
        this.#entitiesSrv.getEntity$(),
        this.#channelsService.getChannel$()
    ])
        .pipe(
            filter(resp => resp.every(Boolean)),
            map(([entity, channel]) =>
                !!entity.settings.enable_v4_configs || !!channel.settings.v4_config_enabled)
        );

    selectedLanguage = new BehaviorSubject<string>(null);

    ngAfterViewInit(): void {
        this.#communicationNotifierService.getRefreshDataSignal$()
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(() => this.loadTextContents(this.selectedLanguage.getValue()));
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const formliterals = this.form.get('literals').value as Record<string, string>;
            return forkJoin([
                this.literals$.pipe(first(Boolean)),
                this.channel$.pipe(first(Boolean))
            ]).pipe(
                map<[TextContent[], Channel], [TextContent[], Channel]>(([literals, channel]) => {
                    const dirtyLiterals = literals.reduce<typeof literals>((result, literal) => {
                        const value = formliterals?.[literal.key];
                        if (value != null && literal.value !== value) {
                            result.push({ ...literal, value });
                        }
                        return result;
                    }, []);
                    return [dirtyLiterals, channel];
                }),
                switchMap(([dirtyLiterals, channel]) => {
                    if (dirtyLiterals.length) {
                        return this.#channelsExtSrv.updateTextContents(
                            channel.id, this.selectedLanguage.getValue(), dirtyLiterals, this.#isNewLiterals
                        ).pipe(
                            tap(() => {
                                this.#ephemeralSrv.showSaveSuccess();
                            })
                        );
                    } else {
                        this.form.markAsPristine();
                        return throwError(() => 'invalid form');
                    }
                })
            );
        } else {
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this.loadTextContents(this.selectedLanguage.getValue());
        });
    }

    cancel(): void {
        this.loadTextContents(this.selectedLanguage.getValue());
    }

    async openCreateLiteralDialog(): Promise<void> {
        const languages = await firstValueFrom(this.languageList$);
        const channel = await firstValueFrom(this.channel$);
        const isV4 = await firstValueFrom(this.isV4$);
        this.validateIfCanDismissChanges({ message: 'FORMS.ACTIONS.UNSAVED_CHANGES' })
            .pipe(
                withLatestFrom(this.isLiteralCreationAllowed$),
                switchMap(([canChange, allowHtmlEditor]) => {
                    if (canChange) {
                        if (this.form.dirty) {
                            this.loadTextContents(this.selectedLanguage.getValue());
                        }
                        const data = new ObMatDialogConfig<LiteralDialogData>({
                            languages,
                            allowHtmlEditor,
                            deepKeysAllowed: isV4 && this.#isNewLiterals
                        });
                        return this.#matDialog.open(CreateLiteralDialogComponent, data).beforeClosed();
                    } else {
                        return EMPTY;
                    }
                })
            ).subscribe(values => {
                if (values) {
                    const obs$: Observable<void>[] = values.map(value =>
                        this.#channelsExtSrv.updateTextContents(channel.id, value.lang, value.textContents, this.#isNewLiterals));
                    forkJoin(obs$).subscribe(() => {
                        this.#ephemeralSrv.showSaveSuccess();
                        this.loadTextContents(this.selectedLanguage.getValue());
                    });
                }
            });
    }

    async loadTextContents(lang: string): Promise<void> {
        this.selectedLanguage.next(lang);
        const channel = await firstValueFrom(this.channel$);
        if (channel.type !== ChannelType.boxOffice) {
            this.#channelsExtSrv.loadTextContents(channel.id, lang, this.#isNewLiterals);
        }
        this.form.markAsPristine();
    }

    validateIfCanDismissChanges(msg?: MessageDialogConfig): Observable<boolean> {
        if (this.form.dirty) {
            return this.#messageDialogService.defaultUnsavedChangesWarn(msg);
        }
        return of(true);
    }
}

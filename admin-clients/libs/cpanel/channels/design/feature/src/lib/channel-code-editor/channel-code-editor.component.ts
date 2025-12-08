import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelsService, ChannelCustomResources, CssResourceTypes, HtmlResourceTypes, CustomAssetElement, IsExternalWhitelabelPipe
} from '@admin-clients/cpanel/channels/data-access';
import {
    CopyTextComponent, DialogSize, EphemeralMessageService, LanguageBarComponent, MessageDialogService, SearchablePaginatedListComponent,
    TabDirective, TabsMenuComponent, UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CodeEditorComponent, CodeEditorService } from '@admin-clients/shared-common-ui-code-editor';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { NgIf, AsyncPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
    ChangeDetectionStrategy, Component, DestroyRef, ElementRef, OnDestroy, OnInit, QueryList, ViewChild,
    ViewChildren, inject
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import {
    BehaviorSubject, Observable, catchError, combineLatest, filter, first, map, of, switchMap, tap, throwError
} from 'rxjs';

const PAGE_LIMIT = 10;
const MAX_FILE_SIZE = 4000000;

@Component({
    selector: 'app-channel-code-editor',
    templateUrl: './channel-code-editor.component.html',
    styleUrls: ['./channel-code-editor.component.scss'],
    imports: [
        NgIf, FormContainerComponent, ReactiveFormsModule, FlexLayoutModule, TranslatePipe, MaterialModule,
        AsyncPipe, LanguageBarComponent, TabsMenuComponent, TabDirective,
        CopyTextComponent, SearchablePaginatedListComponent, EllipsifyDirective, CodeEditorComponent, IsExternalWhitelabelPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelCodeEditorComponent implements OnInit, OnDestroy {
    readonly #codeEditorService = inject(CodeEditorService);

    private readonly _channelsService = inject(ChannelsService);
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);
    private readonly _messageDialogService = inject(MessageDialogService);
    private readonly _onDestroy = inject(DestroyRef);
    private readonly _selectedLanguage = new BehaviorSubject<string>(null);
    private _channelId: number;
    private _filter = { limit: PAGE_LIMIT, offset: 0 };

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild('fileUpload') fileUpload: ElementRef;

    readonly $channel = toSignal(this._channelsService.getChannel$()
        .pipe(
            first(Boolean),
            tap(channel => {
                this._channelId = channel.id;
                this._channelsService.customResources.load(channel.id);
            })
        ));

    readonly $languageList = toSignal(this._channelsService.getChannel$()
        .pipe(
            first(Boolean),
            map(channel => channel.languages.selected),
            filter(Boolean),
            tap(languages => this._selectedLanguage.next(languages[0]))
        ));

    readonly selectedLanguage$ = this._selectedLanguage.asObservable();
    readonly inProgress$ = this._channelsService.customResources.loading$();
    readonly customResourcesData$ = this._channelsService.customAssets.getCustomAssetsData$();
    readonly customResourcesMetadata$ = this._channelsService.customAssets.getCustomAssetsMetadata$();
    readonly customResourcesInProgress$ = this._channelsService.customAssets.loading$();

    readonly isHandsetOrTablet$ = inject(BreakpointObserver)
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    form = this._fb.group({
        css: this._fb.group({
            customStyles: null as string
        }),
        html: this._fb.group({
            header: [null as string, Validators.maxLength(50000)],
            footer: [null as string, Validators.maxLength(50000)]
        })
    });

    ngOnInit(): void {
        combineLatest([
            this.selectedLanguage$,
            this._channelsService.customResources.get$()
        ]).pipe(
            filter(Boolean),
            takeUntilDestroyed(this._onDestroy)
        ).subscribe(([lang, customResources]) => {
            this.updateFormValues(lang, customResources);
        });
    }

    ngOnDestroy(): void {
        this._channelsService.customResources.clear();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    save(): void {
        this.save$().subscribe(() => {
            this._ephemeralSrv.showSaveSuccess();
            this.loadData();
        });
    }

    save$(): Observable<void> {
        this.form.updateValueAndValidity();
        if (this.form.valid && this.form.dirty) {
            const data = this.form.value;
            const resources: ChannelCustomResources = {
                html_resources: [{
                    type: HtmlResourceTypes.headerHtml,
                    language: this._selectedLanguage.getValue(),
                    content: data.html.header ?? data.html.header
                },
                {
                    type: HtmlResourceTypes.footerHtml,
                    language: this._selectedLanguage.getValue(),
                    content: data.html.footer ?? data.html.footer
                }],
                css_resources: [{
                    type: CssResourceTypes.customStyles,
                    content: data.css.customStyles ?? data.css.customStyles
                }]
            };

            return this._channelsService.customResources.update(this._channelId, resources);
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.loadData();
    }

    changeLanguage(newLanguage: string): void {
        this._selectedLanguage.next(newLanguage);
    }

    loadChannelCustomAssets(filters?: PageableFilter): void {
        this._channelsService.customAssets.load(this._channelId, filters);
    }

    importFile(event): void {
        const assets = [];
        [...event.target.files].forEach(async element => {
            if (element.size <= MAX_FILE_SIZE) {
                const asset = await this.readFile(element);
                assets.push(asset);
                if (event.target.files.length === assets.length) {
                    this._channelsService.customAssets.create(this._channelId, assets).subscribe({
                        next: () => {
                            this._channelsService.customAssets.load(this._channelId, this._filter);
                        },
                        error: (error: HttpErrorResponse) => {
                            if (error.status === 409 || (error.status === 400 && error.error.code === 'INVALID_FILENAME')) {
                                this._messageDialogService.showAlert({
                                    size: DialogSize.SMALL,
                                    title: 'CHANNELS.DESIGN.ADD_RESOURCE_ERROR.TITLE_' + error.error.code,
                                    message: 'CHANNELS.DESIGN.ADD_RESOURCE_ERROR.' + error.error.code
                                });
                            }
                        }
                    });
                }
            } else {
                this._messageDialogService.showAlert({
                    size: DialogSize.SMALL,
                    title: 'CHANNELS.DESIGN.ADD_RESOURCE_ERROR.TITLE_MAX_SIZE',
                    message: 'CHANNELS.DESIGN.ADD_RESOURCE_ERROR.MAX_SIZE'
                });
                return;
            }
        });
    }

    openDelete(filename: string): void {
        this._messageDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'CHANNELS.DESIGN.DELETE_RESOURCE.WARNING_TITLE',
            message: 'CHANNELS.DESIGN.DELETE_RESOURCE.WARNING_MESSAGE',
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(ok => {
                if (ok) {
                    this._channelsService.customAssets.delete(this._channelId, filename).subscribe(() => {
                        this._channelsService.customAssets.load(this._channelId, this._filter);
                        this._ephemeralSrv.showSuccess({
                            msgKey: 'CHANNELS.DESIGN.DELETE_RESOURCE.DELETE_OK'
                        });
                    });
                }
            });
    }

    openFullScreenEditor(path: string[]): void {
        const control = this.form.get(path) as FormControl<string>;
        const language = path[0];

        this.#codeEditorService.openFullScreenEditor({ control, language });
    }

    private async readFile(element): Promise<CustomAssetElement> {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(element);
            reader.onload = () => {
                resolve({
                    filename: element.name, binary: reader.result.toString().replace(/^.+?;base64,/, '')
                });
            };
            reader.onerror = () => {
                reject(new Error('Unable to read..'));
            };
        });
    }

    private updateFormValues(lang: string, customResources: ChannelCustomResources): void {
        const headerResources = customResources?.html_resources?.filter(resource => resource.type === HtmlResourceTypes.headerHtml
            && resource.language === lang);
        const footerResources = customResources?.html_resources?.filter(resource => resource.type === HtmlResourceTypes.footerHtml
            && resource.language === lang);
        this.form.patchValue({
            css: {
                customStyles: customResources?.css_resources ? customResources?.css_resources[0].content : null
            },
            html: {
                header: headerResources?.length ? headerResources[0].content : null,
                footer: footerResources?.length ? footerResources[0].content : null
            }
        });
        this.form.markAsPristine();
    }

    private loadData(): void {
        this._channelsService.customResources.load(this._channelId);
        this.form.markAsPristine();
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        this.form.updateValueAndValidity();
        if (this.form.dirty) {
            return this._messageDialogService.openRichUnsavedChangesWarn().pipe(
                switchMap(res => {
                    if (res === UnsavedChangesDialogResult.cancel) {
                        return of(false);
                    } else if (res === UnsavedChangesDialogResult.continue) {
                        return of(true);
                    } else {
                        return this.save$().pipe(
                            switchMap(() => of(true)),
                            catchError(() => of(false))
                        );
                    }
                }));
        }
        return of(true);
    }
}

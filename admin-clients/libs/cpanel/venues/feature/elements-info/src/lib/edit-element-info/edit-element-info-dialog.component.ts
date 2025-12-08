import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    entitiesProviders, EntitiesService, EntityZoneTemplate, GetZoneTemplatesRequest, ZoneTemplateStatus
} from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    BadgeComponent,
    DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig, SelectServerSearchComponent, TabDirective, TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    ElementInfoContentOption, ElementsInfoFilterRequest, PostVenueTemplateElementInfoRequest, PutVenueTemplateElementInfoRequest,
    VenueTemplateElementInfo, VenueTemplateElementInfoAction, VenueTemplateElementInfoContents,
    VenueTemplateElementInfoDefaultInfo, VenueTemplateElementInfoDetail, VenueTemplateElementInfoImage
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, ElementRef, OnDestroy, OnInit, inject, viewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog, MatDialogModule } from '@angular/material/dialog';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject, filter, first, take, of, switchMap, tap, map } from 'rxjs';
import { EditElementFeaturesListComponent } from '../edit-element-features-list/edit-element-features-list.component';
import { EditElementInfo3dViewComponent } from '../edit-element-info-3d-view/edit-element-info-3d-view.component';
import { EditElementInfoBadgeComponent } from '../edit-element-info-badge/edit-element-info-badge.component';
import {
    EditElementInfoHighlightedImageComponent
} from '../edit-element-info-highlighted-image/edit-element-info-highlighted-image.component';
import { EditElementInfoImagesComponent } from '../edit-element-info-images/edit-element-info-images.component';
import { EditElementInfoRestrictionComponent } from '../edit-element-info-restriction/edit-element-info-restriction.component';
import { EditElementInfoTextComponent } from '../edit-element-info-text/edit-element-info-text.component';
import { EditElementOptionsDialogComponent } from '../edit-element-options-dialog/edit-element-options-dialog.component';

@Component({
    selector: 'app-edit-element-info-dialog',
    templateUrl: './edit-element-info-dialog.component.html',
    styleUrls: ['./edit-element-info-dialog.component.scss'],
    imports: [
        TranslatePipe, MaterialModule, CommonModule, ReactiveFormsModule, FlexLayoutModule, FormControlErrorsComponent,
        TabsMenuComponent, TabDirective, MatDialogModule, EditElementInfoTextComponent, EditElementInfo3dViewComponent,
        EditElementFeaturesListComponent, EditElementInfoImagesComponent, EditElementInfoHighlightedImageComponent,
        EditElementInfoBadgeComponent, EditElementInfoRestrictionComponent, BadgeComponent, SelectServerSearchComponent, RouterLink
    ],
    providers: [entitiesProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class EditElementInfoDialogComponent implements OnInit, OnDestroy {
    private readonly _textInfo = viewChild(EditElementInfoTextComponent);
    private readonly _3dView = viewChild(EditElementInfo3dViewComponent);
    private readonly _featuresList = viewChild(EditElementFeaturesListComponent);
    private readonly _imageList = viewChild(EditElementInfoImagesComponent);
    private readonly _highlightedImage = viewChild(EditElementInfoHighlightedImageComponent);
    private readonly _badge = viewChild(EditElementInfoBadgeComponent);
    private readonly _restriction = viewChild(EditElementInfoRestrictionComponent);
    private readonly _namesTabs = viewChild<TabsMenuComponent>('namesTabs');

    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #authService = inject(AuthenticationService);
    readonly #dialogRef = inject(MatDialogRef<EditElementInfoDialogComponent>);
    readonly #matDialog = inject(MatDialog);
    readonly #fb = inject(FormBuilder);
    readonly #elemRef = inject(ElementRef);
    readonly #message = inject(MessageDialogService);
    readonly #entitiesSrv = inject(EntitiesService);

    readonly #destroyRef = inject(DestroyRef);
    readonly #disableAddContentButton = new Subject<boolean>();
    readonly #currentContentOptions = new Subject<VenueTemplateElementInfoContents[]>();

    readonly #elementsInfoService = inject(MAT_DIALOG_DATA).elementsInfoService;
    readonly #id: number = inject(MAT_DIALOG_DATA).id;

    readonly #elementsInfos: VenueTemplateElementInfo[] = inject(MAT_DIALOG_DATA).elementsInfos;
    readonly #filters: ElementsInfoFilterRequest = inject(MAT_DIALOG_DATA).filters;

    readonly singleElementInfo: VenueTemplateElementInfo = this.#elementsInfos?.[0];
    readonly currentAction: VenueTemplateElementInfoAction = inject(MAT_DIALOG_DATA).currentAction;
    readonly status: boolean = inject(MAT_DIALOG_DATA).status;
    readonly interactiveVenue: boolean = inject(MAT_DIALOG_DATA).interactiveVenue;
    readonly languages = inject(MAT_DIALOG_DATA).languages;
    readonly isFromParent = inject(MAT_DIALOG_DATA).isFromParent;
    readonly entityId = inject(MAT_DIALOG_DATA).entityId;
    readonly $loggedUserEntityId = toSignal(this.#authService.getLoggedUser$().pipe(map(user => user.entity.id)));

    readonly currentContentOptions$ = this.#currentContentOptions.asObservable();

    readonly disableAddContentButton$ = this.#disableAddContentButton.asObservable();

    readonly isInProgress$ = booleanOrMerge([
        this.#elementsInfoService.venueTplElementInfo.inProgress$(),
        this.#entitiesSrv.zoneTemplates.loading$()
    ]);

    readonly defaultInfoGroup = this.#fb.group({});

    readonly form = this.#fb.group({
        templates_zones: [[] as EntityZoneTemplate[]],
        status: [null as boolean],
        name: this.#fb.group(this.languages.reduce((acc, lang) =>
            (acc[lang] = this.#fb.control(null as string), acc), {})),
        default_info: this.defaultInfoGroup
    });

    readonly elementInfoContentOptions: Record<VenueTemplateElementInfoContents, ElementInfoContentOption> = {
        [VenueTemplateElementInfoContents.badge]: {
            value: VenueTemplateElementInfoContents.badge,
            description: 'VENUE_TPLS.ELEMENT_INFO.OPTIONS.TAG.DESCRIPTION',
            label: 'VENUE_TPLS.ELEMENT_INFO.OPTIONS.TAG.LABEL',
            image: 'assets/view-info-contents-options/tag.svg',
            disabled: false,
            beta: true
        },
        [VenueTemplateElementInfoContents.highlightedImage]: {
            value: VenueTemplateElementInfoContents.highlightedImage,
            description: 'VENUE_TPLS.ELEMENT_INFO.OPTIONS.HIGHLIGHTED_IMAGE.DESCRIPTION',
            label: 'VENUE_TPLS.ELEMENT_INFO.OPTIONS.HIGHLIGHTED_IMAGE.LABEL',
            image: 'assets/view-info-contents-options/highlighted.svg',
            disabled: false,
            beta: true
        },
        [VenueTemplateElementInfoContents.textInfo]: {
            value: VenueTemplateElementInfoContents.textInfo,
            description: 'VENUE_TPLS.ELEMENT_INFO.OPTIONS.TEXT_INFO.DESCRIPTION',
            label: 'VENUE_TPLS.ELEMENT_INFO.OPTIONS.TEXT_INFO.LABEL',
            image: 'assets/view-info-contents-options/informative-text.svg',
            disabled: false,
            beta: true
        },
        [VenueTemplateElementInfoContents.featuresList]: {
            value: VenueTemplateElementInfoContents.featuresList,
            description: 'VENUE_TPLS.ELEMENT_INFO.OPTIONS.FEATURES_LIST.DESCRIPTION',
            label: 'VENUE_TPLS.ELEMENT_INFO.OPTIONS.FEATURES_LIST.LABEL',
            image: 'assets/view-info-contents-options/features-list.svg',
            disabled: false,
            beta: false
        },
        [VenueTemplateElementInfoContents.view3d]: {
            value: VenueTemplateElementInfoContents.view3d,
            description: 'VENUE_TPLS.ELEMENT_INFO.OPTIONS.3DVIEW.DESCRIPTION',
            label: 'VENUE_TPLS.ELEMENT_INFO.OPTIONS.3DVIEW.LABEL',
            image: 'assets/view-info-contents-options/3D-view.svg',
            disabled: !this.interactiveVenue,
            beta: true
        },
        [VenueTemplateElementInfoContents.imageList]: {
            value: VenueTemplateElementInfoContents.imageList,
            description: 'VENUE_TPLS.ELEMENT_INFO.OPTIONS.IMAGES.DESCRIPTION',
            label: 'VENUE_TPLS.ELEMENT_INFO.OPTIONS.IMAGES.LABEL',
            image: 'assets/view-info-contents-options/images.svg',
            disabled: false,
            beta: true
        },
        [VenueTemplateElementInfoContents.restriction]: {
            value: VenueTemplateElementInfoContents.restriction,
            description: 'VENUE_TPLS.ELEMENT_INFO.OPTIONS.RESTRICTION.DESCRIPTION',
            label: 'VENUE_TPLS.ELEMENT_INFO.OPTIONS.RESTRICTION.LABEL',
            image: 'assets/view-info-contents-options/restriction.svg',
            disabled: false,
            beta: true
        }
    };

    readonly venueTemplateElementInfoContents = VenueTemplateElementInfoContents;
    readonly venueTemplateElementInfoAction = VenueTemplateElementInfoAction;

    zoneTemplatesRequest: GetZoneTemplatesRequest = {
        limit: 999,
        offset: 0,
        status: ZoneTemplateStatus.enabled
    };

    readonly $entity = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean),
        tap(entity => this.#entitiesSrv.zoneTemplates.load(entity.id, this.zoneTemplatesRequest))
    ));

    readonly entityZoneTemplates$ = this.#entitiesSrv.zoneTemplates.getData$().pipe(
        filter(Boolean),
        tap(templates => {
            if (templates.length > 0) {
                this.form.controls.templates_zones.enable();
            } else {
                this.form.controls.templates_zones.disable();
            }
        })
    );

    readonly moreTemplatesAvailable$ = this.#entitiesSrv.zoneTemplates.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    #imagesToDelete: VenueTemplateElementInfoImage[] = [];

    singleElementInfoDetail: VenueTemplateElementInfoDetail = null;
    currentContentOptions: VenueTemplateElementInfoContents[] = [];

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.LATERAL);
        this.#dialogRef.disableClose = true;
    }

    ngOnInit(): void {
        this.#entitiesSrv.loadEntity(this.entityId);

        if (this.currentAction === VenueTemplateElementInfoAction.editSingle ||
            (this.currentAction === VenueTemplateElementInfoAction.createSingle && this.isFromParent)
        ) {
            this.#elementsInfoService.venueTplElementInfo.load(this.#id, this.singleElementInfo?.id, this.singleElementInfo?.type);
            this.#elementsInfoService.venueTplElementInfo.get$()
                .pipe(first(Boolean))
                .subscribe(elementInfo => {
                    this.form.controls.templates_zones.patchValue(elementInfo?.default_info?.templates_zones);
                    this.form.controls.name.patchValue(elementInfo?.default_info?.name);
                    this.form.controls.status.patchValue(elementInfo?.status === 'ENABLED');
                    if (elementInfo?.default_info) {
                        this.addExistentInfoContentOptions(elementInfo.default_info);
                    }
                    this.singleElementInfoDetail = elementInfo;
                });
        }

        this.currentContentOptions$
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(currentContentOptions => {
                this.updateContentOptionsDisabledState(currentContentOptions);
            });
    }

    ngOnDestroy(): void {
        this.#elementsInfoService.venueTplElementInfo.clear();
    }

    async saveElementsInfo(): Promise<void> {
        if (this.form.valid) {
            const req: Partial<PostVenueTemplateElementInfoRequest & PutVenueTemplateElementInfoRequest> = {
                default_info: {}
            };

            const name = this.form.controls.name.value as Record<string, string>;

            if (this.status) {
                req.status = this.form.controls.status.value ? 'ENABLED' : 'DISABLED';
            }

            if (this.form.controls.templates_zones.value) {
                req.default_info.templates_zones_ids = this.form.controls.templates_zones.value.map(template => template.id);
            }

            const el3dView = this._3dView();
            if (el3dView) {
                const view3dFormValue = el3dView.getValue();
                req.default_info.config_3D = { enabled: view3dFormValue.enabled };
                if (this.currentAction === VenueTemplateElementInfoAction.createSingle
                    || this.currentAction === VenueTemplateElementInfoAction.editSingle) {
                    req.default_info.config_3D.codes = view3dFormValue.codes;
                }
            }

            const elFeaturesList = this._featuresList();
            if (elFeaturesList) {
                req.default_info.feature_list = elFeaturesList.getValue();
            }

            const elTextInfo = this._textInfo();
            if (elTextInfo) {
                req.default_info.description = elTextInfo.getValue();
            }

            const elImageList = this._imageList();
            const elHighlightedImage = this._highlightedImage();
            if (elImageList || elHighlightedImage) {
                req.default_info.image_settings = {} as Record<VenueTemplateElementInfoImage['type'],
                    {
                        enabled: true;
                        images: Record<string, VenueTemplateElementInfoImage[]>;
                    }>;
            }
            if (elImageList) {
                const imageList = await elImageList.getValue();

                if (imageList.itemsToDelete.length) {
                    this.#elementsInfoService.venueTplElementInfoImages
                        .delete(this.#id, this.singleElementInfo.id, this.singleElementInfo.type, imageList.itemsToDelete)
                        .subscribe();
                }
                req.default_info.image_settings.SLIDER = {
                    enabled: imageList.enabled
                };

                if (Object.keys(imageList.items).length) {
                    req.default_info.image_settings.SLIDER.images = imageList.items;
                }
            }

            if (elHighlightedImage) {
                const highlightedImage = elHighlightedImage.getValue();

                if (highlightedImage.itemsToDelete.length) {
                    this.#elementsInfoService.venueTplElementInfoImages
                        .delete(this.#id, this.singleElementInfo.id, this.singleElementInfo.type, highlightedImage.itemsToDelete)
                        .subscribe();
                }

                req.default_info.image_settings.HIGHLIGHTED = {
                    enabled: highlightedImage.enabled
                };

                if (Object.keys(highlightedImage.items).length) {
                    req.default_info.image_settings.HIGHLIGHTED.images = highlightedImage.items;
                }
            }

            let saveAction$ = of(true);
            if (this.#imagesToDelete.length) {
                saveAction$ = this.#elementsInfoService.venueTplElementInfoImages
                    .delete(this.#id, this.singleElementInfo.id, this.singleElementInfo.type, this.#imagesToDelete)
                    .pipe(tap(() => this.#imagesToDelete = []));
            }

            const elBadge = this._badge();
            if (elBadge) {
                req.default_info.badge = elBadge.getValue();
            }

            const elRestriction = this._restriction();
            if (elRestriction) {
                req.default_info.restriction = elRestriction.getValue();
            }

            switch (this.currentAction) {
                case VenueTemplateElementInfoAction.createSingle:
                    if (this.form.controls.name.dirty) {
                        req.default_info.name = this.parseNames(name);
                    }
                    req.id = this.singleElementInfo.id;
                    req.type = this.singleElementInfo.type;
                    saveAction$.pipe(switchMap(() =>
                        this.#elementsInfoService.venueTplElementInfo
                            .create(this.#id, req as PostVenueTemplateElementInfoRequest)
                    )).subscribe(() => {
                        this.#ephemeralMessageService.showSaveSuccess();
                        this.close(true);
                    });
                    break;
                case VenueTemplateElementInfoAction.editSingle:
                    if (Object.keys(name).some(lang => name[lang])) {
                        req.default_info.name = this.parseNames(name);
                    }
                    saveAction$.pipe(switchMap(() =>
                        this.#elementsInfoService.venueTplElementInfo.update(
                            this.#id, this.singleElementInfo.id, this.singleElementInfo.type, req as PutVenueTemplateElementInfoRequest
                        )
                    )).subscribe(() => {
                        this.#ephemeralMessageService.showSaveSuccess();
                        this.close(true);
                    });
                    break;
                case VenueTemplateElementInfoAction.editMulti:
                    this.#elementsInfoService.venueTplElementInfo
                        .updateMultiple(this.#id, this.#elementsInfos, req as PutVenueTemplateElementInfoRequest)
                        .subscribe(() => {
                            this.#ephemeralMessageService.showSaveSuccess();
                            this.close(true);
                        });
                    break;
                case VenueTemplateElementInfoAction.editAll:
                    this.#elementsInfoService.venueTplElementInfo
                        .updateMultiple(this.#id, this.#elementsInfos, req as PutVenueTemplateElementInfoRequest, true, this.#filters)
                        .subscribe(() => {
                            this.#ephemeralMessageService.showSaveSuccess();
                            this.close(true);
                        });
                    break;
            }
        } else {
            this.form.markAllAsTouched();
            this._3dView()?.markForCheck();
            this.showValidationErrors();
            this._textInfo()?.showValidationErrors();
            this._featuresList()?.showValidationErrors();
            this._badge()?.showValidationErrors();
            this._restriction()?.showValidationErrors();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
        }
    }

    close(edited = false): void {
        if (!edited && this.form.dirty) {
            this.#message.defaultDiscardChangesWarn()
                .pipe(take(1))
                .subscribe(() => this.#dialogRef.close());
        } else {
            this.#dialogRef.close(edited);
        }
    }

    openContentOptionsModal(): void {
        this.#matDialog.open<
            EditElementOptionsDialogComponent,
            { contents: Record<VenueTemplateElementInfoContents, ElementInfoContentOption> },
            VenueTemplateElementInfoContents
        >(EditElementOptionsDialogComponent, new ObMatDialogConfig({ contents: this.elementInfoContentOptions }))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(contentToAdd => {
                this.currentContentOptions.push(contentToAdd);
                this.#currentContentOptions.next(this.currentContentOptions);
            });
    }

    deleteContent(infoContent: VenueTemplateElementInfoContents): void {
        const infoContentIndex = this.currentContentOptions.indexOf(infoContent);
        if (infoContentIndex > -1) {
            const elImageList = this._imageList();
            if (infoContent === VenueTemplateElementInfoContents.imageList && elImageList) {
                const itemsToDelete = elImageList.getDeletedImages();
                if (itemsToDelete.length) {
                    this.#imagesToDelete = this.#imagesToDelete.concat(itemsToDelete);
                }
            }
            const elHighlightedImage = this._highlightedImage();
            if (infoContent === VenueTemplateElementInfoContents.highlightedImage && elHighlightedImage) {
                const itemsToDelete = elHighlightedImage.getDeletedImages();
                if (itemsToDelete.length) {
                    this.#imagesToDelete = this.#imagesToDelete.concat(itemsToDelete);
                }
            }
            this.currentContentOptions.splice(infoContentIndex, 1);
            this.#currentContentOptions.next(this.currentContentOptions);
            //Delete existing formGroup
            this.defaultInfoGroup.removeControl(infoContent);
        }
    }

    loadTemplates(q?: string, nextPage?: boolean): void {
        const request = {
            ...this.zoneTemplatesRequest,
            q
        };
        if (!nextPage) {
            this.#entitiesSrv.zoneTemplates.load(this.$entity().id, request);
        } else {
            this.#entitiesSrv.zoneTemplates.loadMore(this.$entity().id, request);
        }
    }

    private parseNames(name: Record<string, string>): Record<string, string> {
        this.languages.forEach(lang => {
            if (!name[lang]) {
                name[lang] = this.singleElementInfo?.name;
            }
        });
        return name;
    }

    private addExistentInfoContentOptions(defaultInfo: VenueTemplateElementInfoDefaultInfo): void {
        if (defaultInfo.description) {
            this.currentContentOptions.push(VenueTemplateElementInfoContents.textInfo);
        }
        if (defaultInfo.feature_list) {
            this.currentContentOptions.push(VenueTemplateElementInfoContents.featuresList);
        }
        if (defaultInfo.config_3D) {
            this.currentContentOptions.push(VenueTemplateElementInfoContents.view3d);
        }
        if (defaultInfo.image_settings?.SLIDER) {
            this.currentContentOptions.push(VenueTemplateElementInfoContents.imageList);
        }
        if (defaultInfo.image_settings?.HIGHLIGHTED) {
            this.currentContentOptions.push(VenueTemplateElementInfoContents.highlightedImage);
        }
        if (defaultInfo.badge) {
            this.currentContentOptions.push(VenueTemplateElementInfoContents.badge);
        }
        if (defaultInfo.restriction) {
            this.currentContentOptions.push(VenueTemplateElementInfoContents.restriction);
        }

        this.#currentContentOptions.next(this.currentContentOptions);
    }

    private updateContentOptionsDisabledState(currentContentOptions: VenueTemplateElementInfoContents[]): void {
        //Update disable state of infoContents options
        this.elementInfoContentOptions[VenueTemplateElementInfoContents.textInfo].disabled =
            currentContentOptions.includes(VenueTemplateElementInfoContents.textInfo);

        this.elementInfoContentOptions[VenueTemplateElementInfoContents.featuresList].disabled =
            currentContentOptions.includes(VenueTemplateElementInfoContents.featuresList);

        this.elementInfoContentOptions[VenueTemplateElementInfoContents.badge].disabled =
            currentContentOptions.includes(VenueTemplateElementInfoContents.badge);

        this.elementInfoContentOptions[VenueTemplateElementInfoContents.restriction].disabled =
            currentContentOptions.includes(VenueTemplateElementInfoContents.restriction);

        this.elementInfoContentOptions[VenueTemplateElementInfoContents.imageList].disabled =
            currentContentOptions.includes(VenueTemplateElementInfoContents.imageList);

        this.elementInfoContentOptions[VenueTemplateElementInfoContents.highlightedImage].disabled =
            currentContentOptions.includes(VenueTemplateElementInfoContents.highlightedImage);

        this.elementInfoContentOptions[VenueTemplateElementInfoContents.view3d].disabled =
            !this.interactiveVenue || currentContentOptions.includes(VenueTemplateElementInfoContents.view3d);

        //Update disable state of addContentButton
        this.#disableAddContentButton.next(Object.values(this.elementInfoContentOptions).every(infoContent => infoContent.disabled));
    }

    private showValidationErrors(): void {
        // change language tab if invalid fields found
        const namesGroup = this.form.controls.name;
        if (namesGroup.invalid) {
            this._namesTabs().goToInvalidCtrlTab();
        }
    }
}

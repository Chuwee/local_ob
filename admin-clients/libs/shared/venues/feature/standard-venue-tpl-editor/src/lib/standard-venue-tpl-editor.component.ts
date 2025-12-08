import {
    ContextNotificationComponent, DialogSize, IconManagerService, MessageDialogService, starIcon, svgBlocksSetupIcon, svgFileIcon,
    svgGroupIcon, svgImageIcon, svgRectIcon, svgSeatMatrixIcon, svgTextIcon, svgUngroupIcon, svgWeightsSetupIcon, UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { first, Observable, of, pairwise, switchMap } from 'rxjs';
import { catchError, filter, map, take, withLatestFrom } from 'rxjs/operators';
import { EditCurrentViewAction } from './actions/edit-current-view-action';
import { VenueTplEditorActionsBarComponent } from './actions-bar/venue-tpl-editor-actions-bar.component';
import { EditorMode } from './models/venue-tpl-editor-modes.enum';
import { VenueTplEditorPropertiesFormsComponent } from './properties-forms/venue-tpl-editor-properties-forms.component';
import { VenueTplEditorState } from './state/venue-tpl-editor.state';
import { VenueTplEditorSvgComponent } from './svg-view/venue-tpl-editor-svg.component';
import { VenueTplEditorTreeComponent } from './tree/venue-tpl-editor-tree.component';
import { VenueTplEditorBlocksSetupService } from './venue-tpl-editor-blocks-setup.service';
import { VenueTplEditorDomService } from './venue-tpl-editor-dom.service';
import { VenueTplEditorImagesService } from './venue-tpl-editor-images.service';
import { VenueTplEditorSaveService } from './venue-tpl-editor-save.service';
import { VenueTplEditorSeatMatrixService } from './venue-tpl-editor-seat-matrix.service';
import { VenueTplEditorSelectionService } from './venue-tpl-editor-selection.service';
import { VenueTplEditorVenueMapService } from './venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from './venue-tpl-editor-views.service';
import { VenueTplEditorWeightsSetupService } from './venue-tpl-editor-weights-setup.service';
import { VenueTplEditorService } from './venue-tpl-editor.service';
import { VenueTplEditorViewsComponent } from './view-nav/venue-tpl-editor-views.component';

@Component({
    imports: [
        CommonModule,
        TranslatePipe,
        MaterialModule,
        FlexLayoutModule,
        VenueTplEditorViewsComponent,
        VenueTplEditorTreeComponent,
        VenueTplEditorSvgComponent,
        VenueTplEditorActionsBarComponent,
        VenueTplEditorPropertiesFormsComponent,
        ContextNotificationComponent
    ],
    selector: 'app-standard-venue-tpl-editor',
    templateUrl: './standard-venue-tpl-editor.component.html',
    styleUrls: ['./standard-venue-tpl-editor.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        VenueTplEditorState,
        VenueTplEditorService,
        VenueTplEditorVenueMapService,
        VenueTplEditorViewsService,
        VenueTplEditorDomService,
        VenueTplEditorSaveService,
        VenueTplEditorSelectionService,
        VenueTplEditorSeatMatrixService,
        VenueTplEditorBlocksSetupService,
        VenueTplEditorWeightsSetupService,
        VenueTplEditorImagesService
    ]
})
export class StandardVenueTplEditorComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #editorSrv = inject(VenueTplEditorService);
    readonly #venueMapSrv = inject(VenueTplEditorVenueMapService);
    readonly #viewsSrv = inject(VenueTplEditorViewsService);
    readonly #imagesSrv = inject(VenueTplEditorImagesService);
    readonly #selectionSrv = inject(VenueTplEditorSelectionService);
    readonly #saveSrv = inject(VenueTplEditorSaveService);
    readonly #iconManagerSrv = inject(IconManagerService);

    readonly baseMode$ = this.#editorSrv.modes.getEditorMode$().pipe(map(mode => mode === EditorMode.base));
    readonly isNotDirty$ = booleanOrMerge([
        this.#viewsSrv.isDirty$(),
        this.#venueMapSrv.isDirty$(),
        this.#imagesSrv.isDirty$()
    ])
        .pipe(map(v => !v));

    readonly inProgress$ = booleanOrMerge([
        this.#viewsSrv.isViewsLoading$(),
        this.#viewsSrv.isSVGDataLoading$(),
        this.#venueMapSrv.isVenueMapLoading$(),
        this.#imagesSrv.isInProgress$(),
        this.#saveSrv.isSaving$()
    ]);

    readonly inCapacityIncrease$ = this.#editorSrv.capacityIncrease.isInSetup();
    readonly increasingCapacity$ = this.#editorSrv.capacityIncrease.isInProgress$();

    @Input()
    set operatorMode(value: boolean) {
        this.#editorSrv.modes.setOperatorMode(value);
    }

    @Input()
    set mmcIntegrationEnabled(value: boolean) {
        this.#editorSrv.mmcIntegrationEnabled.set(value);
    }

    @Input()
    set inUse(value: boolean) {
        this.#editorSrv.inUse.set(value);
    }

    @Input()
    set capacityIncreaseEnabled(value: boolean) {
        this.#editorSrv.capacityIncrease.setEnabled(value);
    }

    @Input()
    set capacityIncreaseInProgress(value: boolean) {
        this.#editorSrv.capacityIncrease.setInProgress(value);
    }

    constructor() {
        this.#iconManagerSrv.addIconDefinition(
            svgGroupIcon, svgUngroupIcon, svgFileIcon, starIcon, svgSeatMatrixIcon,
            svgBlocksSetupIcon, svgWeightsSetupIcon, svgRectIcon, svgTextIcon, svgImageIcon
        );
    }

    ngOnInit(): void {
        this.loadData();
        this.#editorSrv.capacityIncrease.isInProgress$()
            .pipe(
                pairwise(),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([prevInProgress, inProgress]) => {
                if (prevInProgress && !inProgress) {
                    this.#venueTemplatesSrv.venueTpl.get$().pipe(take(1))
                        .subscribe(venueTpl => this.#venueTemplatesSrv.venueTpl.load(venueTpl.id));
                }
            });
        this.#venueTemplatesSrv.venueTpl.get$()
            .pipe(
                pairwise(),
                filter(tpls => tpls.every(Boolean)),
                takeUntilDestroyed(this.#destroyRef),
                switchMap(([prevVenueTpl, venueTpl]) =>
                    prevVenueTpl.capacity !== venueTpl.capacity ? of(true) : this.#viewsSrv.checkViewsChanged(venueTpl.id)
                ),
                withLatestFrom(this.isNotDirty$),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([changed, notDirty]) => {
                if (changed) {
                    this.loadData();
                    if (!notDirty) {
                        this.#msgDialogSrv.showAlert({
                            size: DialogSize.SMALL,
                            title: 'VENUE_TPL_EDITOR.CHANGES_LOST_BY_USER_CHANGES_ERROR_TITLE',
                            message: 'VENUE_TPL_EDITOR.CHANGES_LOST_BY_USER_CHANGES_ERROR'
                        });
                    }
                }
            });
        this.#editorSrv.history.errors$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(errorActionStatus => {
                let title: string;
                let message: string;
                if (errorActionStatus === 'svgMaxLengthRaised') {
                    title = 'VENUE_TPL_EDITOR.FORMS.ERRORS.SVG_EDIT_ERROR';
                    message = 'VENUE_TPL_EDITOR.FORMS.ERRORS.SVG_MAX_LENGTH_RAISED';
                } else {
                    // not cool, it wouldn't execute this line, never, malfunction signal
                    title = 'TITLES.ERROR_DIALOG';
                    console.error('Template editor uncontrolled history error', errorActionStatus);
                }
                this.#msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message });
            });
    }

    cancel(): void {
        this.isNotDirty$
            .pipe(
                take(1),
                switchMap(isNotDirty => isNotDirty ? of(false) : this.#msgDialogSrv.defaultDiscardChangesWarn())
            )
            .subscribe(requiresReload => requiresReload ? this.loadData() : this.#editorSrv.resetState());
    }

    save(): void {
        this.save$().subscribe(() => this.loadData());
    }

    save$(): Observable<void> {
        this.#selectionSrv.unselectAll();
        return this.isValid$().pipe(
            take(1),
            switchMap(() => this.showCapacityIncreaseWarning()),
            switchMap(() => this.#saveSrv.save()),
            catchError(e => {
                this.loadData();
                throw e;
            })
        );
    }

    canDeactivate(): Observable<boolean> {
        return booleanOrMerge([this.#viewsSrv.isDirty$(), this.#venueMapSrv.isDirty$(), this.#imagesSrv.isDirty$()])
            .pipe(
                switchMap(isDirty => {
                    if (isDirty) {
                        return this.#msgDialogSrv.openRichUnsavedChangesWarn()
                            .pipe(
                                switchMap(result => {
                                    if (result === UnsavedChangesDialogResult.cancel) {
                                        return of(false);
                                    } else if (result === UnsavedChangesDialogResult.continue) {
                                        return of(true);
                                    } else if (result === UnsavedChangesDialogResult.save) {
                                        return this.save$().pipe(
                                            switchMap(() => of(true)),
                                            catchError(() => of(false))
                                        );
                                    }
                                })
                            );
                    } else {
                        return of(!isDirty);
                    }
                })
            );
    }

    private loadData(): void {
        this.#venueTemplatesSrv.venueTpl.get$()
            .pipe(first(Boolean))
            .subscribe(venueTemplate => {
                this.#editorSrv.resetState();
                this.#selectionSrv.unselectAll();
                this.#viewsSrv.loadViews(venueTemplate.id);
                this.#venueMapSrv.loadVenueMap(venueTemplate.id);
                this.#imagesSrv.loadImages(venueTemplate.id);
            });
    }

    private isValid$(): Observable<unknown> {
        // Remember that every check must be a finite observable
        return [
            this.checkViewNames(),
            this.checkViewCodes(),
            this.checkViewMmcIntegration(),
            this.checkNNZNames(),
            this.checkSeatNames()
        ]
            .reduce((accumulatedChecks, check) =>
                accumulatedChecks?.pipe(switchMap(error => error ? of(error) : check)) || check
            )
            .pipe(
                filter(error => !error),
                map(() => null)
            );
    }

    private showCapacityIncreaseWarning(): Observable<void> {
        return this.#editorSrv.capacityIncrease.isInSetup()
            .pipe(
                take(1),
                withLatestFrom(this.#venueMapSrv.getVenueItems$()),
                map(([inSetup, venueItems]) =>
                    inSetup
                    && (Array.from(venueItems.seats.values()).some(seat => seat.create)
                        || Array.from(venueItems.nnzs.values()).some(nnz => nnz.create || nnz.modify))
                ),
                switchMap(showWarning => showWarning ?
                    this.#msgDialogSrv.showWarn({
                        size: DialogSize.MEDIUM,
                        title: 'TITLES.WARNING',
                        message: 'VENUE_TPL_MGR.DIALOGS.CAPACITY_INCREASE_WARNING',
                        actionLabel: 'VENUE_TPL_MGR.ACTIONS.CONFIRM_INCREASE'
                    })

                        .pipe(
                            filter(Boolean),
                            map(() => null)
                        )
                    : of(null))
            );
    }

    // pre save validations

    private checkViewNames(): Observable<boolean> {
        return this.#viewsSrv.checkViewNames()
            .pipe(map(viewData => {
                if (viewData) {
                    this.#editorSrv.history.enqueue(new EditCurrentViewAction(viewData.view.id, this.#viewsSrv));
                }
                return !!viewData;
            }));
    }

    private checkViewCodes(): Observable<boolean> {
        return this.#viewsSrv.checkViewCodes()
            .pipe(map(viewData => {
                if (viewData) {
                    this.#editorSrv.history.enqueue(new EditCurrentViewAction(viewData.view.id, this.#viewsSrv));
                }
                return !!viewData;
            }));
    }

    private checkViewMmcIntegration(): Observable<boolean> {
        return this.#venueMapSrv.getVenueItems$()
            .pipe(
                take(1),
                switchMap(venueItems => this.#viewsSrv.checkMmcIntegration(venueItems.nnzs, venueItems.seats)),
                map(viewData => {
                    if (viewData) {
                        this.#editorSrv.history.enqueue(new EditCurrentViewAction(viewData.view.id, this.#viewsSrv));
                    }
                    return !!viewData;
                })
            );
    }

    private checkNNZNames(): Observable<boolean> {
        return this.#venueMapSrv.checkNNZNames()
            .pipe(map(zone => {
                if (zone) {
                    this.#editorSrv.history.enqueue(new EditCurrentViewAction(zone.view, this.#viewsSrv));
                    this.#selectionSrv.unselectAll();
                    this.#selectionSrv.selectItems({ nnzIds: [zone.id] });
                }
                return !!zone;
            }));
    }

    private checkSeatNames(): Observable<boolean> {
        return this.#venueMapSrv.checkSeatNames()
            .pipe(map(seat => {
                if (seat) {
                    this.#editorSrv.history.enqueue(new EditCurrentViewAction(seat.view, this.#viewsSrv));
                    this.#selectionSrv.unselectAll();
                    this.#selectionSrv.selectItems({ seatIds: [seat.id] });
                }
                return !!seat;
            }));
    }
}

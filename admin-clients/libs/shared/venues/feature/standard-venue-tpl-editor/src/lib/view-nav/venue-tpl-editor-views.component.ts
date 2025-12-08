import { ObDialogService, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild, ViewContainerRef } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, pairwise, Subject } from 'rxjs';
import { filter, map, take, takeUntil } from 'rxjs/operators';
import { EditCurrentViewAction } from '../actions/edit-current-view-action';
import { VenueTplEditorViewDialogComponent } from '../dialogs/view/venue-tpl-editor-view-dialog.component';
import { EditorMode } from '../models/venue-tpl-editor-modes.enum';
import { VenueTplEditorViewData } from '../models/venue-tpl-editor-view-data.model';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../venue-tpl-editor.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        SelectSearchComponent,
        FormsModule,
        ReactiveFormsModule,
        SharedUtilityDirectivesModule
    ],
    selector: 'app-venue-tpl-editor-views',
    templateUrl: './venue-tpl-editor-views.component.html',
    styleUrls: ['./venue-tpl-editor-views.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorViewsComponent implements OnInit, OnDestroy {

    private readonly _onDestroy = new Subject<void>();
    private readonly _viewCrf = inject(ViewContainerRef);
    private readonly _dialogSrv = inject(ObDialogService);
    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _viewSrv = inject(VenueTplEditorViewsService);

    private readonly _views$ = this._viewSrv.getViewDatas$().pipe(map(views => views?.filter(view => !view.delete)));

    private readonly _viewInfo$ = combineLatest([this._views$, this._viewSrv.getViewData$()])
        .pipe(
            filter(sources => sources.every(Boolean)),
            map(([views, view]) => ({
                currentIndex: views.indexOf(view),
                total: views.length
            }))
        );

    readonly editorModes = EditorMode;

    @ViewChild('viewSelectSearch') viewSelectSearchComponent: SelectSearchComponent<VenueTplEditorViewData>;
    readonly control = new FormControl<VenueTplEditorViewData>(null);
    readonly views$ = this._views$;
    readonly mode$ = this._editorSrv.modes.getEditorMode$();

    readonly $hasNextView = toSignal(this._viewInfo$.pipe(map(info => info.currentIndex < info.total - 1)));
    readonly $hasPrevView = toSignal(this._viewInfo$.pipe(map(info => info.currentIndex > 0)));

    ngOnInit(): void {
        this._editorSrv.modes.getEditorMode$().pipe(takeUntil(this._onDestroy))
            .subscribe(mode => {
                if (mode === EditorMode.base) {
                    this.control.enable({ emitEvent: false });
                } else {
                    this.control.disable({ emitEvent: false });
                }
            });
        // auto view selection when views are loaded or reloaded
        this.views$
            .pipe(
                pairwise(),
                takeUntil(this._onDestroy)
            )
            .subscribe(([prevViews, views]) => {
                if (views && prevViews !== views) {
                    if (this.control.value) {
                        if (!views.includes(this.control.value)) {
                            new EditCurrentViewAction(this.control.value.view.id, this._viewSrv);
                        }
                    } else {
                        const viewToSelect = views.find(viewData => viewData.view.root)?.view || views[0].view;
                        new EditCurrentViewAction(viewToSelect.id, this._viewSrv);
                    }
                }
            });
        // service to control bind
        this._viewSrv.getViewData$()
            .pipe(filter(Boolean), takeUntil(this._onDestroy))
            .subscribe(viewData => this.control.setValue(viewData, { emitEvent: false }));
        // control to service bind
        this.control.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(view => this._editorSrv.history.enqueue(new EditCurrentViewAction(view.view.id, this._viewSrv)));
    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }

    openNewViewDialog(): void {
        this._dialogSrv.open(VenueTplEditorViewDialogComponent, null, this._viewCrf);
    }

    goToPrevView(): void {
        this.gotoView(-1);
    }

    goToNextView(): void {
        this.gotoView(1);
    }

    private gotoView(incr: 1 | -1 = 1): void {
        combineLatest([this._views$, this._viewInfo$])
            .pipe(take(1))
            .subscribe(([views, viewInfo]) =>
                this._editorSrv.history.enqueue(new EditCurrentViewAction(views[viewInfo.currentIndex + incr].view.id, this._viewSrv))
            );
    }
}

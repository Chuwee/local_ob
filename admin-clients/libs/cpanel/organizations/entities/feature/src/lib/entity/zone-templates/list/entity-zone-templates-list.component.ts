import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, debounceTime, filter, first, map, shareReplay, switchMap } from 'rxjs';
import { AddEntityZoneTemplateDialogComponent } from '../add/add-entity-zone-template-dialog.component';

@Component({
    selector: 'app-entity-zone-templates-list',
    imports: [
        CommonModule, TranslatePipe, MaterialModule, FlexModule, FlexLayoutModule,
        LastPathGuardListenerDirective, EllipsifyDirective
    ],
    templateUrl: './entity-zone-templates-list.component.html',
    styleUrls: ['./entity-zone-templates-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityZoneTemplatesListComponent implements OnInit {
    readonly #entitySrv = inject(EntitiesService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #matDialog = inject(MatDialog);
    readonly #router = inject(Router);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #cdRef = inject(ChangeDetectorRef);
    readonly #destroyRef = inject(DestroyRef);

    get #idPath(): string | undefined {
        return this.#activatedRoute.snapshot.children[0]?.params['templateId'];
    }

    get #innerPath(): string {
        return this.#activatedRoute.snapshot.children[0]?.children[0]?.routeConfig.path;
    }

    get #currentPath(): string {
        return this.#innerPath ?
            this.$selectedZoneTemplateId().toString() + '/' + this.#innerPath : this.$selectedZoneTemplateId().toString();
    }

    readonly canWrite$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]);
    readonly isLoading$ = this.#entitySrv.zoneTemplates.loading$();

    readonly zoneTemplates$ = this.#entitySrv.zoneTemplates.getData$()
        .pipe(
            filter(Boolean),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly $entity = toSignal(this.#entitySrv.getEntity$());
    readonly $selectedZoneTemplateId = signal(Number(this.#idPath));

    ngOnInit(): void {
        this.loadZoneTemplates();
        this.handleSelectedTemplateChanges();
        this.handleZoneTemplatesChangesForScroll();
        this.handleZoneTemplatesChangesToNavigate();
    }

    openAddTemplateDialog(): void {
        this.#matDialog.open<AddEntityZoneTemplateDialogComponent>(
            AddEntityZoneTemplateDialogComponent, new ObMatDialogConfig({ entityId: this.$entity().id })
        ).beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(id => {
                if (id) {
                    this.#ephemeralSrv.showSuccess({ msgKey: 'ENTITY.ZONE_TEMPLATES.ADD_ZONE_TEMPLATE_SUCCESS' });
                    this.loadZoneTemplates();
                    this.selectionChangeHandler(id);
                }
            });
    }

    openDeleteTemplateDialog(): void {
        this.#entitySrv.zoneTemplate.get$()
            .pipe(
                first(),
                switchMap(template =>
                    this.#msgDialogSrv.showWarn({
                        size: DialogSize.SMALL,
                        title: 'TITLES.DELETE_ZONE_TEMPLATE',
                        message: 'ENTITY.ZONE_TEMPLATES.DELETE_ZONE_TEMPLATE_WARNING',
                        messageParams: { templateName: template.name },
                        actionLabel: 'FORMS.ACTIONS.DELETE',
                        showCancelButton: true
                    })
                        .pipe(map(accepted => ({ accepted, template })))
                ),
                filter(({ accepted }) => !!accepted),
                switchMap(({ template }) =>
                    this.#entitySrv.zoneTemplate.delete(this.$entity().id, template.id)
                        .pipe(map(_ => template)))
            ).subscribe(() => {
                this.#ephemeralSrv.showSuccess({
                    msgKey: 'ENTITY.ZONE_TEMPLATES.DELETE_ZONE_TEMPLATE_SUCCESS'
                });
                this.#router.navigate(['.'], { relativeTo: this.#activatedRoute });
                this.loadZoneTemplates();
            });
    }

    selectionChangeHandler(templateId: number): void {
        this.$selectedZoneTemplateId.set(templateId);
        this.#router.navigate([this.#currentPath], { relativeTo: this.#activatedRoute });
    }

    private loadZoneTemplates(): void {
        this.#entitySrv.zoneTemplate.get$()
            .pipe(first(Boolean))
            .subscribe(() => {
                this.#entitySrv.zoneTemplates.load(this.$entity().id, { limit: 999, offset: 0, sort: 'name:asc' });
            });
    }

    private handleSelectedTemplateChanges(): void {
        combineLatest([
            this.#entitySrv.zoneTemplate.get$(),
            this.#entitySrv.zoneTemplate.error$()
        ]).pipe(
            takeUntilDestroyed(this.#destroyRef))
            .subscribe(([zoneTemplate, error]) => {
                this.$selectedZoneTemplateId.set(error || !zoneTemplate ? null : zoneTemplate.id);
                this.#cdRef.markForCheck();
            });
    }

    private handleZoneTemplatesChangesForScroll(): void {
        this.zoneTemplates$.pipe(
            filter(entityZoneTemplates => !!entityZoneTemplates.length),
            debounceTime(500), takeUntilDestroyed(this.#destroyRef)
        ).subscribe(() => {
            const zoneTemplate = this.$selectedZoneTemplateId();
            const element = document.getElementById('zone-templates-list-option-' + zoneTemplate);
            element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        });
    }

    private handleZoneTemplatesChangesToNavigate(): void {
        this.#entitySrv.zoneTemplates.getData$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(templates => {
                if (!templates.length) {
                    this.#entitySrv.zoneTemplate.clear();
                    this.#router.navigate(['.'], { relativeTo: this.#activatedRoute });
                } else if (!this.#idPath) {
                    this.#router.navigate([templates.at(0).id], { relativeTo: this.#activatedRoute });
                }
            });
    }
}

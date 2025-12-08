import { EntitiesService, ZoneTemplateStatus } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogConfig, MessageDialogService, NavTabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, inject, OnInit, DestroyRef } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, delay, filter, first, map, of, switchMap, tap } from 'rxjs';

const unsavedChangesDialogData: MessageDialogConfig = {
    actionLabel: 'FORMS.ACTIONS.UPDATE',
    showCancelButton: true,
    message: 'ENTITY.ZONE_TEMPLATE.STATUS_CHANGE_WARNING.DESCRIPTION',
    title: 'ENTITY.ZONE_TEMPLATE.STATUS_CHANGE_WARNING.TITLE',
    size: DialogSize.MEDIUM
};

@Component({
    selector: 'app-entity-zone-template-details',
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule, RouterOutlet,
        NavTabsMenuComponent, AsyncPipe, NgClass
    ],
    templateUrl: './entity-zone-template-details.component.html',
    styleUrls: ['./entity-zone-template-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityZoneTemplateDetailsComponent implements OnDestroy, OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMdgSrv = inject(EphemeralMessageService);

    #childComponent: WritingComponent;

    readonly isInProgress$ = this.#entitiesSrv.zoneTemplates.loading$();

    readonly selectedTemplate$ = combineLatest([
        this.#entitiesSrv.zoneTemplates.getData$(),
        this.#entitiesSrv.zoneTemplate.get$()
    ]).pipe(
        filter(data => data.every(Boolean)),
        map(([templates, currentTemplate]) => templates?.find(template => template.id === currentTemplate.id))
    );

    readonly $template = toSignal(this.#entitiesSrv.zoneTemplate.get$());
    readonly $entity = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean)));

    readonly statusCtrl = this.#fb.control({ value: null, disabled: true });

    ngOnInit(): void {
        combineLatest([
            this.#entitiesSrv.zoneTemplate.get$(),
            this.#entitiesSrv.getEntity$().pipe(first(Boolean), map(entity => entity.settings?.languages?.available))
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([template, langs]) => {
            const isActive = template.status === 'ENABLED';
            this.statusCtrl.patchValue(isActive);

            if (template.contents_texts === undefined || template.contents_texts?.length < langs.length) {
                this.statusCtrl.disable();
            } else {
                this.statusCtrl.enable();
            }
        });
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.zoneTemplate.clear();
        this.#entitiesSrv.zoneTemplates.clear();
    }

    handleStatusChange(isActive: boolean): void {
        if (this.#childComponent?.form?.dirty) {
            of(null).pipe(
                delay(100),
                tap(() => this.statusCtrl.setValue(!isActive)),
                switchMap(() =>
                    this.#msgDialogSrv.showWarn(unsavedChangesDialogData)
                ),
                switchMap(saveAccepted =>
                    saveAccepted ? this.#childComponent.save$() : of(false)
                )
            ).subscribe();
        } else {
            this.saveStatus(isActive);
        }
    }

    childComponentChange(component: WritingComponent): void {
        this.#childComponent = component;
    }

    private saveStatus(isActive: boolean): void {
        if (!isActive) {
            this.#msgDialogSrv.showWarn({
                size: DialogSize.SMALL,
                title: 'ENTITY.ZONE_TEMPLATE.DISABLE_WARNING_TITLE',
                message: 'ENTITY.ZONE_TEMPLATE.DISABLE_WARNING_MESSAGE',
                showCancelButton: false
            }).subscribe(() => {
                this.#changeStatus(isActive);
            });
        } else {
            this.#changeStatus(isActive);
        }
    }

    #changeStatus(isActive: boolean): void {
        const status = isActive ? ZoneTemplateStatus.enabled : ZoneTemplateStatus.disabled;

        this.#entitiesSrv.zoneTemplate.update(this.$entity().id, this.$template().id, { status })
            .subscribe({
                complete: () => {
                    this.#entitiesSrv.zoneTemplate.load(this.$entity().id, this.$template().id);
                    this.#entitiesSrv.zoneTemplates.load(this.$entity().id, { limit: 999, offset: 0 });
                    this.#ephemeralMdgSrv.showSaveSuccess();
                },
                error: () => this.statusCtrl.patchValue(!isActive)
            });
    }

}

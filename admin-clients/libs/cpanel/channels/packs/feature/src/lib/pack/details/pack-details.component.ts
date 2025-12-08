import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogConfig, MessageDialogService, NavTabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { AsyncPipe, NgClass, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject, OnDestroy } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, of } from 'rxjs';
import { delay, filter, map, shareReplay, switchMap, tap, withLatestFrom } from 'rxjs/operators';

const unsavedChangesDialogData: MessageDialogConfig = {
    actionLabel: 'FORMS.ACTIONS.UPDATE',
    showCancelButton: true,
    message: 'CHANNELS.PACKS.STATUS_CHANGE_WARNING.DESCRIPTION',
    title: 'CHANNELS.PACKS.STATUS_CHANGE_WARNING.TITLE',
    size: DialogSize.MEDIUM
};

@Component({
    selector: 'app-pack-details',
    templateUrl: './pack-details.component.html',
    styleUrls: ['./pack-details.component.scss'],
    imports: [
        FlexLayoutModule, AsyncPipe, MatTabsModule, NavTabsMenuComponent, TranslatePipe, RouterModule, MatProgressBarModule,
        NgIf, MatTooltipModule, MatSlideToggleModule, ReactiveFormsModule, NgClass
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackDetailsComponent implements OnInit, OnDestroy {
    readonly #onDestroy = inject(DestroyRef);
    readonly #packsSrv = inject(PacksService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #fb = inject(FormBuilder);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);

    #childComponent: WritingComponent;
    #channelId: number;
    #packId: number;

    readonly statusCtrl = this.#fb.control({ value: null, disabled: true });
    readonly loading$ = this.#packsSrv.pack.loading$();
    readonly pack$ = this.#packsSrv.pack.get$()
        .pipe(
            filter(Boolean),
            withLatestFrom(this.#channelsSrv.getChannel$()),
            tap(([pack, channel]) => {
                this.#channelId = channel.id;
                this.#packId = pack.id;
            }),
            map(([pack]) => pack),
            takeUntilDestroyed(this.#onDestroy),
            shareReplay(1)
        );

    ngOnInit(): void {
        // status ctrl activation logic
        combineLatest([
            this.pack$,
            this.#packsSrv.packItems.get$()
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([pack, items]) => {
            this.statusCtrl.patchValue(pack.active);
            if (items.length < 2 || !items.find(item => item.type === 'SESSION' || item.type === 'EVENT')) {
                this.statusCtrl.disable();
            } else {
                this.statusCtrl.enable();
            }
        });
    }

    ngOnDestroy(): void {
        this.#packsSrv.packItems.clear();
    }

    handleStatusChange(isActive: boolean): void {
        if (this.#childComponent?.form?.dirty) {
            of(null).pipe(
                delay(100),
                tap(() => this.statusCtrl.setValue(!isActive)),
                switchMap(() => this.#msgDialogSrv.showWarn(unsavedChangesDialogData)),
                switchMap(saveAccepted =>
                    saveAccepted ? this.#childComponent.save$() : of(false)
                )
            ).subscribe();
        } else {
            this.savePackStatus(isActive);
        }
    }

    savePackStatus(isActive: boolean): void {
        const pack = { active: isActive };

        this.#packsSrv.pack.update(this.#channelId, this.#packId, pack)
            .subscribe({
                complete: () => {
                    this.#packsSrv.pack.load(this.#channelId, this.#packId);
                    this.#packsSrv.packItems.load(this.#channelId, this.#packId);
                    this.#packsSrv.packList.load(this.#channelId);
                    this.#ephemeralMsgSrv.showSaveSuccess();
                },
                error: () => this.statusCtrl.patchValue(!isActive)
            });
    }

    childComponentChange(child: WritingComponent): void {
        this.#childComponent = child;
    }
}

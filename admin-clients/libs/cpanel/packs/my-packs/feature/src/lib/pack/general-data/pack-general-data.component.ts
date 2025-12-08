import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { PutPack } from '@admin-clients/cpanel/channels/packs/data-access';
import { categoriesProviders, CategoriesService } from '@admin-clients/cpanel/organizations/data-access';
import { Pack, PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { CategoriesSelectionComponent } from '@admin-clients/cpanel/shared/ui/components';
import { DialogSize, EphemeralMessageService, MessageDialogConfig, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { Component, OnInit, ChangeDetectionStrategy, inject, DestroyRef } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, of, throwError } from 'rxjs';
import { tap, filter, delay, switchMap, map, shareReplay } from 'rxjs/operators';

const unsavedChangesDialogData: MessageDialogConfig = {
    actionLabel: 'FORMS.ACTIONS.UPDATE',
    showCancelButton: true,
    message: 'CHANNELS.PACKS.STATUS_CHANGE_WARNING.DESCRIPTION',
    title: 'CHANNELS.PACKS.STATUS_CHANGE_WARNING.TITLE',
    size: DialogSize.MEDIUM
};

@Component({
    selector: 'app-pack-general-data',
    templateUrl: './pack-general-data.component.html',
    styleUrls: ['./pack-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, TranslatePipe, ReactiveFormsModule, CategoriesSelectionComponent,
        FormContainerComponent, MaterialModule, FormControlErrorsComponent
    ],
    providers: [categoriesProviders]
})
export class PackGeneralDataComponent implements OnInit {
    readonly #packsSrv = inject(PacksService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #categoriesSrv = inject(CategoriesService);
    readonly #onDestroy = inject(DestroyRef);
    readonly #msgDialogSrv = inject(MessageDialogService);

    #packId: number;

    readonly form = this.#fb.group({
        name: [null as string, [Validators.required]],
        putCategoriesCtrl: [null as Partial<PutPack>]
    });

    readonly statusCtrl = this.#fb.control({ value: null, disabled: true });

    readonly loading$ = booleanOrMerge([
        this.#packsSrv.pack.loading$(),
        this.#categoriesSrv.isCategoriesLoading$()
    ]);

    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(
        filter(Boolean),
        tap((pack: Pack) => {
            this.#packId = pack.id;
            this.updateFormValues(pack);
        }),
        map(pack => pack),
        takeUntilDestroyed(this.#onDestroy),
        shareReplay(1)
    ));

    ngOnInit(): void {
        // status ctrl activation logic
        combineLatest([
            this.#packsSrv.pack.get$(),
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

    handleStatusChange(isActive: boolean): void {
        if (this.form.dirty) {
            of(null).pipe(
                delay(100),
                tap(() => this.statusCtrl.setValue(!isActive)),
                switchMap(() => this.#msgDialogSrv.showWarn(unsavedChangesDialogData)),
                switchMap(saveAccepted =>
                    saveAccepted ? this.save$() : of(false)
                )
            ).subscribe();
        } else {
            this.savePackStatus(isActive);
        }
    }

    savePackStatus(isActive: boolean): void {
        const pack = { active: isActive };

        this.#packsSrv.pack.update(this.#packId, pack)
            .subscribe({
                complete: () => {
                    this.#packsSrv.pack.load(this.#packId);
                    this.#packsSrv.packItems.load(this.#packId);
                    const message = isActive ? 'PACK.ENABLE_PACK_SUCCESS' : 'PACK.DISABLE_PACK_SUCCESS';
                    this.#ephemeralMessageSrv.showSuccess({
                        msgKey: message,
                        msgParams: { packName: this.$pack().name }
                    });
                },
                error: () => this.statusCtrl.patchValue(!isActive)
            });
    }

    cancel(): void {
        this.loadPack();
    }

    save(): void {
        this.save$().subscribe({
            next: () => {
                this.#ephemeralMessageSrv.showSaveSuccess();
                this.loadPack();
                this.form.markAsPristine();
            }
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            // Makes child component set the value of putEventCtrl
            this.form.controls['putCategoriesCtrl'].setValue({});
            const data = { ...this.form.controls['putCategoriesCtrl'].value, name: this.form.value.name };
            return this.#packsSrv.pack.update(this.$pack().id, data);
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => new Error('Invalid form'));
        }
    }

    private loadPack(): void {
        this.#packsSrv.pack.load(this.$pack()?.id);
    }

    private updateFormValues(pack: Pack): void {
        this.form.reset({ name: pack.name });
        this.form.markAsPristine();
    }
}


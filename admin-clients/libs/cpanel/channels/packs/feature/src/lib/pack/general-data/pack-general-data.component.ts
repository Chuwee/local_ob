import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { PacksService, Pack, PutPack } from '@admin-clients/cpanel/channels/packs/data-access';
import { categoriesProviders, CategoriesService } from '@admin-clients/cpanel/organizations/data-access';
import { CategoriesSelectionComponent } from '@admin-clients/cpanel/shared/ui/components';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { Component, OnInit, ChangeDetectionStrategy, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, throwError } from 'rxjs';
import { tap, filter } from 'rxjs/operators';

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

    readonly form = this.#fb.group({
        name: [null as string, [Validators.required]],
        putCategoriesCtrl: [null as Partial<PutPack>]
    });

    readonly loading$ = booleanOrMerge([
        this.#packsSrv.pack.loading$(),
        this.#categoriesSrv.isCategoriesLoading$()
    ]);

    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(
        filter(Boolean),
        tap((pack: Pack) => this.updateFormValues(pack))
    ));

    ngOnInit(): void {
        this.loadPack();
    }

    cancel(): void {
        this.loadPack();
    }

    save(): void {
        this.save$().subscribe({
            next: () => {
                this.#ephemeralMessageSrv.showSaveSuccess();
                this.loadPack();
                this.#packsSrv.packList.load(this.$pack()?.channel_id);
                this.form.markAsPristine();
            }
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            // Makes child component set the value of putEventCtrl
            this.form.controls['putCategoriesCtrl'].setValue({});
            const data = { ...this.form.controls['putCategoriesCtrl'].value, name: this.form.value.name };
            return this.#packsSrv.pack.update(this.$pack()?.channel_id, this.$pack().id, data);
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => new Error('Invalid form'));
        }
    }

    private loadPack(): void {
        this.#packsSrv.pack.load(this.$pack()?.channel_id, this.$pack()?.id);
    }

    private updateFormValues(pack: Pack): void {
        this.form.reset({ name: pack.name });
        this.form.markAsPristine();
    }
}


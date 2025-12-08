import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatLabel } from '@angular/material/form-field';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, Observable, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-pack-design',
    imports: [
        AsyncPipe, FormContainerComponent, TranslatePipe, MatLabel, MatCheckbox, MatProgressSpinner, ReactiveFormsModule, FlexModule,
        MatRadioButton, MatRadioGroup
    ],
    styleUrl: './pack-design.component.scss',
    templateUrl: './pack-design.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackDesignComponent implements OnInit {
    readonly #packsSrv = inject(PacksService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    readonly inProgress$ = this.#packsSrv.pack.loading$();
    readonly form = this.#fb.group({
        show_main_venue: false as boolean,
        date_format: 'DEFAULT' as 'DEFAULT' | 'MAIN' | 'NONE',
        show_date_time: false as boolean
    });

    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(
        filter(Boolean),
        tap(pack => this.form.patchValue({
            show_main_venue: pack.ui_settings?.show_main_venue,
            date_format: pack.ui_settings?.show_main_date ? 'MAIN' : pack.ui_settings?.show_date ? 'DEFAULT' : 'NONE',
            show_date_time: pack.ui_settings?.show_date_time
        }))
    ));

    ngOnInit(): void {
        this.reloadModels();
    }

    cancel(): void {
        this.reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const updatedPack = {
                ui_settings: {
                    show_main_venue: !!this.form.value.show_main_venue,
                    show_date: this.form.value.date_format !== 'NONE',
                    show_date_time: !!this.form.value.show_date_time,
                    show_main_date: this.form.value.date_format === 'MAIN'
                }
            };
            return this.#packsSrv.pack.update(this.$pack().id, updatedPack)
                .pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
            return throwError(() => 'invalid form');
        }
    }

    private reloadModels(): void {
        this.#packsSrv.pack.load(this.$pack().id);
        this.form.markAsPristine();
    }
}

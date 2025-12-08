import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, Observable, tap, throwError } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, FormContainerComponent, TranslatePipe, MatCheckbox, MatProgressSpinner, ReactiveFormsModule, MatExpansionModule
    ],
    selector: 'app-pack-channel-operative',
    templateUrl: './pack-channel-operative.component.html',
    styleUrls: ['./pack-channel-operative.component.scss']
})
export class PackChannelOperativeComponent implements OnInit {
    readonly #packsSrv = inject(PacksService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);

    readonly $pack = toSignal(this.#packsSrv.pack.get$());
    readonly $channel = toSignal(this.#channelsSrv.getChannel$());
    readonly packChannel$ = this.#packsSrv.pack.channel.get$().pipe(
        filter(Boolean),
        tap(packChannel => {
            this.form.controls.suggested.setValue(packChannel.settings.suggested);
            this.form.controls.on_sale_for_logged_users.setValue(packChannel.settings.on_sale_for_logged_users);
        })
    );

    readonly inProgress$ = booleanOrMerge([
        this.#packsSrv.pack.loading$(),
        this.#packsSrv.pack.channel.loading$(),
        this.#channelsSrv.isChannelLoading$()
    ]);

    readonly form = this.#fb.group({
        suggested: null as boolean,
        on_sale_for_logged_users: null as boolean
    });

    ngOnInit(): void {
        combineLatest([
            this.#packsSrv.pack.get$(),
            this.#channelsSrv.getChannel$()
        ]).pipe(
            takeUntilDestroyed(this.#destroyRef),
            filter(resp => resp.every(Boolean)),
            tap(([pack, channel]) => {
                if (pack.id && channel.id) {
                    this.#packsSrv.pack.channel.load(pack.id, channel.id);
                }
            })).subscribe();
    }

    cancel(): void {
        this.load();
    }

    save(): void {
        this.save$().subscribe(() => this.load());
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const updatedPackChannel = {
                suggested: !!this.form.value.suggested,
                on_sale_for_logged_users: !!this.form.value.on_sale_for_logged_users
            };
            return this.#packsSrv.pack.channel.update(this.$pack().id, this.$channel().id, updatedPackChannel)
                .pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
            return throwError(() => 'invalid form');
        }
    }

    private load(): void {
        this.#packsSrv.pack.channel.load(this.$pack().id, this.$channel().id);
        this.form.markAsPristine();
    }
}

import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Entity } from '@admin-clients/shared/common/data-access';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, input, OnDestroy, OnInit, Output } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { defer, filter, firstValueFrom, map, Observable, Subject, switchMap, tap } from 'rxjs';
import { ExternalEntityConfiguration } from '../models/configuration.model';
import { ExternalEntityService } from '../service/external.service';

@Component({
    selector: 'app-club-code-selector',
    imports: [CommonModule, MaterialModule, TranslatePipe, FormsModule, ReactiveFormsModule, FlexLayoutModule],
    templateUrl: './club-code-selector.component.html',
    styleUrls: ['./club-code-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ClubCodeSelectorComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #externalService = inject(ExternalEntityService);
    readonly #entitiesService = inject(EntitiesService);
    readonly #message = inject(MessageDialogService);
    readonly #onDestroy = new Subject<void>();

    @Output() connection = new EventEmitter<string>();
    readonly $formConnection = input<FormControl<ExternalEntityConfiguration['avet_connection_type']>>();
    entity: Entity;
    codes: string[];
    readonly connections: ExternalEntityConfiguration['avet_connection_type'][] = ['SOCKET', 'WEBSERVICES', 'APIM'];
    readonly form = this.#fb.group({
        control: [null]
    });

    ngOnInit(): void {

        this.init();
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    async init(): Promise<void> {
        this.entity = await firstValueFrom(this.#entitiesService.getEntity$());

        this.#externalService.configuration.load(this.entity.id);

        this.#externalService.clubCodes.load();

        let { club_code: clubCode = null } = await firstValueFrom(this.#externalService.configuration.get$().pipe(
            filter(Boolean),
            tap(value => {
                if (value.avet_connection_type) {
                    this.$formConnection().setValue(value.avet_connection_type);
                }
            })));

        this.codes = [...(await firstValueFrom(this.#externalService.clubCodes.get$().pipe(filter(Boolean))))];

        if (clubCode) {
            this.codes.push(clubCode);
            this.$formConnection().enable();
        } else {
            this.$formConnection().disable();
        }

        this.form.get('control').reset(clubCode);

        this.form.get('control').valueChanges.pipe(
            filter(code => code !== clubCode),
            switchMap(code => this.showWarningMessage().pipe(map(ok => ({ ok, code })))),
            tap(({ ok }) => !ok ? this.form.get('control').reset(clubCode) : this.form.get('control').disable({ emitEvent: false })),
            filter(({ ok }) => ok),
            switchMap(({ code }) => this.unlinkLinkAction(code, clubCode).pipe(map(() => ({ code })))),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(({ code }) => {
            if (code) {
                this.$formConnection().enable();
            } else {
                this.$formConnection().disable();
            }

            clubCode = code;
            this.#externalService.clubCodes.reload();
            this.#externalService.configuration.reload(this.entity.id);
            this.form.get('control').enable({ emitEvent: false });
        });

    }

    private unlinkLinkAction(code: string, clubCode: string): Observable<void> {
        return defer(() => {
            if (!!code && !!clubCode && code !== clubCode) {
                return this.unlink().pipe(switchMap(() => this.link(code)));
            } else if (!code) {
                return this.unlink();
            } else {
                return this.link(code);
            }
        });
    }

    private showWarningMessage(): Observable<boolean> {
        return this.#message.showWarn({
            size: DialogSize.SMALL,
            title: 'ENTITY.EXTERNAL.CLUB_CODE.CHANGE_TITLE',
            message: 'ENTITY.EXTERNAL.CLUB_CODE.CHANGE_DESCRIPTION',
            actionLabel: 'ENTITY.EXTERNAL.CLUB_CODE.CHANGE'
        });
    }

    private unlink(): Observable<void> {
        return this.#externalService.clubCodes.unlink(this.entity.id);
    }

    private link(code: string): Observable<void> {
        return this.#externalService.clubCodes.link(this.entity.id, code);
    }

}

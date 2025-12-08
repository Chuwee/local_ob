import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { Session } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { Presale, PresalePut, PRESALES_SERVICE } from '@admin-clients/cpanel/shared/data-access';
import { EntitiesBaseService, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EmptyStateTinyComponent,
    EphemeralMessageService, MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import {
    booleanAttribute, ChangeDetectionStrategy, Component, ElementRef, inject, input, viewChild,
    viewChildren, ViewContainerRef, OnDestroy, effect, output
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, switchMap, tap } from 'rxjs';
import { NewPresaleDialogComponent } from '../create/new-presale-dialog.component';
import { PresaleDetailComponent } from '../presale-detail/presale-detail.component';
import { PresaleNameDialogComponent } from '../presale-name-dialog/presale-name-dialog.component';

@Component({
    selector: 'app-presales-list',
    templateUrl: './presales-list.component.html',
    styleUrls: ['./presales-list.component.scss'],
    imports: [
        TranslatePipe, PrefixPipe, MatIconModule, MatExpansionModule, MatMenuModule,
        EmptyStateTinyComponent, PresaleDetailComponent, MatButtonModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PresalesListComponent implements OnDestroy {
    private _$presalesContainer = viewChild('presalesContainer', { read: ElementRef });
    readonly $presaleDetailElements = viewChildren(PresaleDetailComponent);

    readonly presalesFormChanged = output<PresaleDetailComponent[]>();

    readonly $session = input<Session>(null, { alias: 'session' });
    readonly $externalInventoryProvider = input<ExternalInventoryProviders>(null, { alias: 'externalInventoryProvider' });
    readonly $isAvetEvent = input.required<boolean>({ alias: 'isAvetEvent' });
    readonly $isSmartBooking = input.required<boolean>({ alias: 'isSmartBooking' });
    readonly $onlyCustomersValidation = input(false, { alias: 'onlyCustomersValidation', transform: booleanAttribute });

    readonly #presalesSrv = inject(PRESALES_SERVICE);
    readonly #matDialog = inject(MatDialog);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #auth = inject(AuthenticationService);
    readonly #viewContainerRef = inject(ViewContainerRef);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);

    readonly $presales = toSignal(this.#presalesSrv.get$());
    readonly #$entity = toSignal(this.#entitiesSrv.getEntity$());
    readonly #$isOperator = toSignal(this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS]));
    readonly $isLoading = toSignal(this.#presalesSrv.isLoading$());

    openedId: string = null;

    constructor() {
        effect(() => {
            this.presalesFormChanged.emit(this.$presaleDetailElements() as PresaleDetailComponent[]);
        });
    }

    ngOnDestroy(): void {
        this.#presalesSrv.clear();
    }

    openNewPresaleDialog(): void {
        this.#matDialog.open(NewPresaleDialogComponent, new ObMatDialogConfig(
            {
                entityId: this.#$isOperator() ? this.#$entity().id : null,
                onlyCustomersValidation: this.$onlyCustomersValidation(),
                externalInventoryProvider: this.$externalInventoryProvider()
            }, this.#viewContainerRef
        )).beforeClosed()
            .subscribe(presale => {
                if (presale) {
                    this.#presalesSrv.load();
                    this.#openPresale(presale.id);
                }
            });
    }

    rename(presale: Presale): void {
        const data = new ObMatDialogConfig(presale, this.#viewContainerRef);
        this.#matDialog.open<PresaleNameDialogComponent, Presale, PresalePut>(PresaleNameDialogComponent, data)
            .beforeClosed().pipe(
                filter(Boolean),
                switchMap(changes => this.#presalesSrv.update(presale.id, changes)),
                tap(() => this.#ephemeralMessageService.showSuccess({
                    msgKey: 'EVENTS.SESSION.PRESALES.RENAME_PRESALE_SUCCESS'
                }))
            ).subscribe(() => this.#presalesSrv.load());
    }

    remove(presale: Presale): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_PRESALE',
            message: 'EVENTS.SESSION.PRESALES.DELETE_PRESALE_WARNING',
            messageParams: { name: presale.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#presalesSrv.delete(presale.id))
            ).subscribe(() => {
                this.#ephemeralMessageService.showSuccess({
                    msgKey: 'EVENTS.SESSION.PRESALES.DELETE_PRESALE_SUCCESS'
                });
                this.#presalesSrv.load();
            });
    }

    #openPresale(id: string): void {
        this.openedId = id;
        setTimeout(() => this.#scrollToBottom(), 1500);
    }

    #scrollToBottom(): void {
        this._$presalesContainer()?.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'end', inline: 'nearest' });
    }

}

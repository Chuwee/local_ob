import type { DomainConfiguration, DomainSettingsEntry } from '@admin-clients/cpanel/shared/data-access';
import { DialogSize, EmptyStateComponent, MessageDialogService, openDialog } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, input, output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatButtonToggle } from '@angular/material/button-toggle';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatList, MatListItem, MatListItemMeta, MatListItemTitle } from '@angular/material/list';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, Observable } from 'rxjs';
import { DomainSettingsDialogComponent } from '../dialog-settings/domain-settings-dialog.component';

const MAX_ITEMS = 5;

@Component({
    selector: 'app-domain-settings-list',
    templateUrl: './domain-settings-list.component.html',
    styleUrl: './domain-settings-list.component.scss',
    imports: [
        MatList, MatListItem, MatListItemTitle, MatListItemMeta, MatButtonToggle, TranslatePipe, MatButton,
        MatIconButton, MatIcon, EmptyStateComponent, MatTooltip, ReactiveFormsModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DomainSettingsListComponent {
    readonly #matDialog = inject(MatDialog);
    readonly #msgDialogSrv = inject(MessageDialogService);

    readonly $loading = input<boolean>(false, { alias: 'loading' });
    readonly $domains = input<DomainSettingsEntry[]>([], { alias: 'domains' });
    readonly $canConfigure = input<boolean>(false, { alias: 'canConfigure' });
    readonly domainsChange = output<DomainSettingsEntry[]>();
    readonly configureRequested = output<{ index: number; domain: string }>();
    readonly configurationChange = output<{ domain: string; configuration: DomainConfiguration }>();

    readonly maxItems = MAX_ITEMS;

    updateDefault(index: number): void {
        const updatedDomains = this.$domains().map((value, i) => ({ ...value, default: i === index }));
        this.domainsChange.emit(updatedDomains);
    }

    onConfigure(domain: DomainSettingsEntry, index: number): void {
        const item = this.$domains()[index];
        if (!item) return;
        this.configureRequested.emit({ index, domain: domain.domain });
    }

    openCreateDialog(): void {
        this.#openDialog()
            .subscribe(domain => {
                const updatedDomains = [...this.$domains()];
                updatedDomains.unshift({ domain, default: updatedDomains.length === 0 });
                this.domainsChange.emit(updatedDomains);
            });
    }

    openDeleteDialog(domain: DomainSettingsEntry, domainIndex: number): void {
        this.#msgDialogSrv
            .showWarn({
                size: DialogSize.SMALL,
                title: 'ADMIN_CHANNEL.CONFIGURATION.SUBDOMAIN.ACTIONS.DELETE_TITLE',
                message: 'ADMIN_CHANNEL.CONFIGURATION.SUBDOMAIN.ACTIONS.DELETE_MESSAGE',
                messageParams: { domain: domain.domain },
                actionLabel: 'FORMS.ACTIONS.DELETE',
                showCancelButton: true
            })
            .pipe(filter(success => !!success))
            .subscribe(result => {
                if (result) {
                    const updatedDomains = this.$domains().filter((_, index) => index !== domainIndex);
                    this.domainsChange.emit(updatedDomains);
                }
            });
    }

    #openDialog(entry?: DomainSettingsEntry): Observable<string> {
        return openDialog(this.#matDialog, DomainSettingsDialogComponent, {
            existingValues: this.$domains()?.map(value => value.domain) || [],
            domain: entry?.domain || ''
        }).beforeClosed().pipe(filter(Boolean));
    }
}

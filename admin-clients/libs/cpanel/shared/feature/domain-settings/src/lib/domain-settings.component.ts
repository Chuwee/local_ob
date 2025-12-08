
import { AdminChannelsService } from '@admin-clients/cpanel/migration/channels/data-access';
import { type DomainConfiguration, type DomainSettings, type DomainSettingsEntry, type DomainSettingsMode } from '@admin-clients/cpanel/shared/data-access';
import { openDialog } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, computed, effect, inject, input, output } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatFormField, MatOption, MatSelect } from '@angular/material/select';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, map, switchMap, take } from 'rxjs';
import { ConfigurationDialogComponent } from './dialog-config/configuration-dialog.component';
import { DomainSettingsListComponent } from './list/domain-settings-list.component';

@Component({
    standalone: true,
    selector: 'app-domain-settings',
    templateUrl: './domain-settings.component.html',
    styleUrl: './domain-settings.component.scss',
    imports: [
        TranslatePipe, MatSlideToggle, MatFormField, MatSelect, MatOption, MatDivider,
        ReactiveFormsModule, DomainSettingsListComponent, MatIcon, MatProgressSpinner
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DomainSettingsComponent {
    readonly #fb = inject(FormBuilder);
    readonly #admin = inject(AdminChannelsService);
    readonly #dialog = inject(MatDialog);

    readonly $initData = input.required<DomainSettings>({ alias: 'initData' });
    readonly $loading = input.required<boolean>({ alias: 'loading' });
    readonly $canConfigure = input<boolean>(false, { alias: 'canConfigure' });
    readonly dataChange = output<Partial<DomainSettings>>({ alias: 'dataChange' });
    readonly configureRequested = output<{ index: number; domain: string }>();
    readonly configurationChange = output<{ domain: string; configuration: DomainConfiguration }>();

    readonly form = this.#fb.group({
        enabled: false,
        mode: 'REDIRECT' as DomainSettingsMode,
        domains: [[] as DomainSettingsEntry[]]
    });

    readonly #$formChanges = toSignal(
        combineLatest([this.form.valueChanges, this.form.statusChanges]).pipe(
            filter(([_, status]) => status === 'VALID'),
            map(([value, _]) => value)
        )
    );

    readonly $domainsValue = toSignal(this.form.controls.domains.valueChanges, { initialValue: [] });
    readonly $hasDomains = computed(() => (this.$domainsValue() ?? []).length > 0);

    constructor() {
        effect(() => {
            const initData = this.$initData();
            if (initData) {
                this.#initializeForm(this.$initData());
            }
        });

        effect(() => {
            if (this.#$formChanges() && this.form.dirty) {
                const formData = this.form.getRawValue();
                this.#updateDomainSettings(formData);
            }
        });
        effect(() => this.#toggleHeaderCtrls(this.$hasDomains()));
    }

    updateDomains(domains: DomainSettingsEntry[]): void {
        this.form.patchValue({ domains });
        this.form.markAsDirty();
    }

    onConfigureRequested({ index, domain }: { index: number; domain: string }): void {
        this.#admin.channelDomainConfiguration.clear();
        this.#admin.channelDomainConfiguration.load(domain);

        this.#admin.channelDomainConfiguration.get$()
            .pipe(
                filter(Boolean),
                take(1),
                switchMap(cfg =>
                    openDialog(this.#dialog, ConfigurationDialogComponent, {
                        configuration: cfg,
                        domainIndex: index,
                        domainName: domain
                    }).beforeClosed().pipe(filter(Boolean), take(1))
                )
            )
            .subscribe((updated: DomainConfiguration) => {
                this.configurationChange.emit({ domain, configuration: updated });
            });
    }

    #updateDomainSettings(data: Partial<DomainSettings>): void {
        this.dataChange.emit(data);
        this.form.markAsPristine();
    }

    #initializeForm(domainSettings: DomainSettings): void {
        this.form.setValue({
            enabled: domainSettings.enabled,
            mode: domainSettings.mode,
            domains: domainSettings.domains
        });
    }

    #toggleHeaderCtrls(enabled: boolean): void {
        const opts = { emitEvent: false };
        if (enabled) {
            this.form.controls.enabled.enable(opts);
            this.form.controls.mode.enable(opts);
        } else {
            this.form.controls.enabled.disable(opts);
            this.form.controls.mode.disable(opts);
        }
    }
}

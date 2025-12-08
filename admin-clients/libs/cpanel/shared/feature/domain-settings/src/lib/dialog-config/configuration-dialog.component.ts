import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelsAllowed, DomainConfiguration, FallbackRule, RuleType } from '@admin-clients/cpanel/shared/data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    imports: [
        MatDialogTitle, MatDialogContent, MatDialogActions, MatLabel, MatInput, MatFormField, MatError,
        MatSelect, MatOption, MatIcon, MatIconButton, MatButton, ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent,
        MatDivider, MatSlideToggle
    ],
    templateUrl: './configuration-dialog.component.html',
    styleUrls: ['./configuration-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigurationDialogComponent extends ObDialog<ConfigurationDialogComponent, {
    configuration: DomainConfiguration;
    domainIndex?: number;
    domainName: string;
}, DomainConfiguration> implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<ConfigurationDialogComponent>);
    readonly #destroyRef = inject(DestroyRef);
    readonly domainName = this.data.domainName;
    readonly configurationFormGroup = this.#fb.group({
        googleClientId: this.#fb.control(''),
        fallbackEnabled: this.#fb.control(false),
        fallbackConfig: this.#fb.group({
            fallbackUrl: this.#fb.control(''),
            channelNames: this.#fb.control<ChannelsAllowed>('ALL'),
            rules: this.#fb.array<FormGroup<{
                type: FormControl<RuleType | null>;
                value: FormControl<string>;
            }>>([])
        })
    });

    constructor() {
        super(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        const config = this.data.configuration;
        this.configurationFormGroup.reset({
            googleClientId: config.social_login?.google_client_id ?? '',
            fallbackEnabled: config.domain_fallback?.enabled ?? false
        });
        this.configurationFormGroup.controls.fallbackConfig.patchValue({
            fallbackUrl: config.domain_fallback?.default_redirection_url ?? '',
            channelNames: config.domain_fallback?.channels_allowed ?? 'ALL'
        });
        this.configurationFormGroup.controls.fallbackConfig.controls.rules.clear();
        this.#flattenRulesToRows(config);
        this.#setupConditionalValidators();
    }

    addRule(): void {
        this.configurationFormGroup.controls.fallbackConfig.controls.rules.push(this.#createRuleRow(null, ''));
    }

    removeRule(i: number): void {
        this.configurationFormGroup.controls.fallbackConfig.controls.rules.removeAt(i);
        this.configurationFormGroup.controls.fallbackConfig.controls.rules.markAsDirty();
    }

    onChannelNameToggle(checked: boolean): void {
        const channelNames = this.configurationFormGroup.controls.fallbackConfig.controls.channelNames;
        channelNames.setValue(checked ? 'RESTRICTED' : 'ALL');
        channelNames.markAsDirty();
    }

    save(): void {
        if (this.configurationFormGroup.invalid) {
            this.configurationFormGroup.markAllAsTouched();
            return;
        }

        const raw = this.configurationFormGroup.getRawValue();
        const rules = this.#groupRowsToApiRules();
        const result: DomainConfiguration = {
            social_login: { google_client_id: raw.googleClientId },
            domain_fallback: {
                enabled: raw.fallbackEnabled,
                channels_allowed: raw.fallbackConfig.channelNames,
                default_redirection_url: raw.fallbackConfig.fallbackUrl,
                rules
            }
        };
        this.#dialogRef.close(result);
    }

    close(): void {
        this.#dialogRef.close();
    }

    #flattenRulesToRows(config: DomainConfiguration): void {
        const apiRules = config.domain_fallback?.rules ?? [];
        if (apiRules.length === 0) {
            return this.configurationFormGroup.controls.fallbackConfig.controls.rules.push(this.#createRuleRow(null, ''));
        }
        const seen = new Set<string>();
        for (const { type, values } of apiRules) {
            for (const raw of values ?? []) {
                const v = (raw ?? '').trim();
                if (!type || !v) continue;
                const key = `${type}::${v}`;
                if (seen.has(key)) continue;
                seen.add(key);
                this.configurationFormGroup.controls.fallbackConfig.controls.rules.push(this.#createRuleRow(type, v));
            }
        }
    }

    #createRuleRow(type: RuleType | null, value = ''):
        FormGroup<{ type: FormControl<RuleType | null>; value: FormControl<string> }> {
        const group = this.#fb.group({
            type: this.#fb.control<RuleType | null>(type),
            value: this.#fb.control<string>(value)
        });

        this.#setupRuleValueRequired(group);

        group.controls.type.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                this.#setupRuleValueRequired(group);
            });

        return group;
    }

    #groupRowsToApiRules(): FallbackRule[] {
        const raw = this.configurationFormGroup.getRawValue();
        const byType = new Map<RuleType, string[]>();
        for (const r of raw.fallbackConfig.rules ?? []) {
            const type = r.type;
            const value = (r.value ?? '').trim();
            if (!type || !value) continue;
            const bucket = byType.get(type) ?? [];
            if (!bucket.includes(value)) bucket.push(value);
            byType.set(type, bucket);
        }
        return Array.from(byType.entries()).map(([type, values]) => ({ type, values }));
    }

    #setupConditionalValidators(): void {
        const fg = this.configurationFormGroup;
        const fallbackUrlControl = fg.controls.fallbackConfig.controls.fallbackUrl;
        const channelNamesControl = fg.controls.fallbackConfig.controls.channelNames;

        fg.controls.fallbackEnabled.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(enabled => {
                if (enabled) {
                    fallbackUrlControl.setValidators([Validators.required]);
                } else {
                    fallbackUrlControl.clearValidators();
                }
                fallbackUrlControl.updateValueAndValidity();
            });

        fg.controls.fallbackEnabled.updateValueAndValidity({ emitEvent: true });
        channelNamesControl.updateValueAndValidity({ emitEvent: true });
    }

    #setupRuleValueRequired(ruleGroup: FormGroup<{ type: FormControl<RuleType | null>; value: FormControl<string> }>): void {
        const valueCtrl = ruleGroup.controls.value;
        const isRequired = !!ruleGroup.controls.type.value;
        if (isRequired) {
            valueCtrl.setValidators([Validators.required]);
        } else {
            valueCtrl.clearValidators();
        }

        valueCtrl.updateValueAndValidity({ emitEvent: false });
    }

}

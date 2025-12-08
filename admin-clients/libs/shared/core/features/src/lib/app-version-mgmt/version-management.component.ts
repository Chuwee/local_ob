import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { NgIf, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, signal, inject, HostListener } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { VersionManagementDialogComponent, VersionManagementDialogResult } from './version-management-dialog.component';

@Component({
    selector: 'ob-version-management',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MatIconButton, MatIcon, TranslatePipe, UpperCasePipe, NgIf],
    template: `
        <mat-icon class="ob-icon version-warning xsmall">warning</mat-icon>
        {{'TITLES.WARNING_LOW_ENV' | translate | uppercase}}
        <span *ngIf="branch()"> - Client: {{branch()}}
            <button mat-icon-button class="ob-button xsmall" (click)="clear('branch'); $event.stopPropagation();">
                <mat-icon>cancel</mat-icon>
            </button>
        </span>
        <span *ngIf="sp()"> - SP: {{sp()}}
            <button mat-icon-button class="ob-button xsmall" (click)="clear('sp'); $event.stopPropagation();">
                <mat-icon>cancel</mat-icon>
            </button>
        </span>
        <span *ngIf="fm()"> - Forward Mode {{fm()}}
            <button mat-icon-button class="ob-button xsmall" (click)="clear('fm'); $event.stopPropagation();">
                <mat-icon>cancel</mat-icon>
            </button>
        </span>
    `,
    styles: [`
        :host {
            cursor: pointer;
            background-color:  var(--ob-theme-color-alert);
            font: var(--ob-theme-font-subheading-1);
            font-weight: bold;
            padding: 4px 8px;
            border-radius: 2px;
        }
        .ob-button {
            vertical-align: middle;
            display: inline-block;
        }
        .ob-icon.version-warning {
            vertical-align: middle;
        }

    `]
})
export class VersionManagementComponent {

    readonly #env = inject(ENVIRONMENT_TOKEN);
    readonly #dialog = inject(MatDialog);

    branch = signal(this.#env?.branch !== 'default' ? this.#env.branch : undefined);
    sp = signal(sessionStorage.getItem('sp'));
    fm = signal(sessionStorage.getItem('fm'));

    @HostListener('click') onClick(): void {
        this.open();
    }

    clear(option: 'sp' | 'fm' | 'branch'): void {
        this[option]?.set(undefined);
        sessionStorage.removeItem(option);
        this.deleteQueryParam(option);
    }

    open(): void {
        this.#dialog.open(VersionManagementDialogComponent, new ObMatDialogConfig()).beforeClosed().
            subscribe((result: VersionManagementDialogResult) => {
                if (result) {
                    const branch = result.branch?.id ? result.branch.type + '-' + result.branch.id : undefined;
                    const sp = result.sp?.id ? result.sp.type + '-' + result.sp.id : undefined;
                    const fm = result.sp?.id && result.sp?.id && result.sp.fm !== 'NORMAL' ? result.sp.fm : undefined;
                    this.addQueryParams([
                        { name: 'branch', value: branch },
                        { name: 'sp', value: sp },
                        { name: 'fm', value: fm }
                    ]);
                }
            });
    }

    private deleteQueryParam(option: string): void {
        const url = new URL(window.location.href);
        url.searchParams.delete(option);
        window.location.href = url.toString();
    }

    private addQueryParams(options: { name: string; value: string }[]): void {
        const url = new URL(window.location.href);
        options.forEach(option => {
            url.searchParams.delete(option.name);
            sessionStorage.removeItem(option.name);
            if (option.value) {
                url.searchParams.append(option.name, option.value);
            }
        });
        window.location.href = url.toString();
    }

}

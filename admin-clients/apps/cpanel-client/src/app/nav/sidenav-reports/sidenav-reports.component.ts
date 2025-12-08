import { Component, ChangeDetectionStrategy, Input, OnInit, ViewChild, ElementRef, ChangeDetectorRef, AfterViewInit } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, of, switchMap } from 'rxjs';
import { withLatestFrom } from 'rxjs/operators';
import type { BiImpersonationDialogComponent } from '@admin-clients/cpanel/bi/feature';
import { AuthenticationService, MstrUrls } from '@admin-clients/cpanel/core/data-access';
import { DialogSize, MessageDialogService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';

@Component({
    imports: [MatIcon, MatTooltip, MatButton, TranslatePipe],
    selector: 'app-sidenav-reports',
    templateUrl: './sidenav-reports.component.html',
    styleUrls: ['./sidenav-reports.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SidenavReportsComponent implements OnInit, AfterViewInit {
    @ViewChild('reportsForm') reportsFormElement: ElementRef<HTMLFormElement>;
    @ViewChild('loginInput') loginInput: ElementRef<HTMLInputElement>;
    @ViewChild('logoutInput') logoutInput: ElementRef<HTMLInputElement>;

    isImperson = false;

    @Input() sidenavOpened: boolean;
    @Input() reports: MstrUrls;

    constructor(
        private _msgDialogSrv: MessageDialogService,
        private _changeDet: ChangeDetectorRef,
        private _auth: AuthenticationService,
        private _matDialog: MatDialog
    ) { }

    ngOnInit(): void {
        this.isImperson = this.reports.can_impersonate;
    }

    ngAfterViewInit(): void {
        this.logoutInput.nativeElement.value = this.reports.logout;
        this.loginInput.nativeElement.value = this.reports.login;
    }

    async submitReports(): Promise<void> {
        if (this.isImperson) {
            const dialogComponent = await import('@admin-clients/cpanel/bi/feature').then(m => m.BiImpersonationDialogComponent);
            this._matDialog.open<BiImpersonationDialogComponent, null, { urls: MstrUrls; userId: number }>(
                dialogComponent, new ObMatDialogConfig())
                .beforeClosed()
                .pipe(
                    filter(resp => !!resp),
                    withLatestFrom(this._auth.getLoggedUser$()),
                    switchMap(([{ urls: { login, logout }, userId }, loggedUser]) => {
                        this.loginInput.nativeElement.value = login;
                        this.logoutInput.nativeElement.value = logout;
                        this._changeDet.markForCheck();
                        if (userId !== loggedUser.id && loggedUser.reports?.mstr_user_has_subscriptions) {
                            return this._msgDialogSrv.showWarn({
                                size: DialogSize.SMALL,
                                title: 'TITLES.WARNING',
                                message: 'BI_REPORTS.IMPERSONATION.SUBSCRIPTIONS_WARN',
                                actionLabel: 'FORMS.ACTIONS.AGREED',
                                showCancelButton: true
                            });
                        } else {
                            return of(true);
                        }
                    }),
                    filter(accepted => accepted)
                )
                .subscribe(() => this.reportsFormElement.nativeElement.submit());
        } else {
            this.reportsFormElement.nativeElement.submit();
        }
    }
}

import { InsurersService } from '@admin-clients/cpanel-configurations-insurers-data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, ViewChild, OnInit, DestroyRef, OnDestroy } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatDrawer, MatDrawerContainer, MatDrawerContent } from '@angular/material/sidenav';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, tap } from 'rxjs';
import { PoliciesListComponent } from '../list/policies-list.component';

@Component({
    selector: 'app-policies-container',
    imports: [
        TranslatePipe, RouterModule, PoliciesListComponent, MatDrawer, MatDrawerContainer, MatDrawerContent,
        EmptyStateComponent, MatDrawer, MatIcon, MatButton, MatDialogModule
    ],
    templateUrl: './policies-container.component.html',
    styleUrls: ['./policies-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PoliciesContainerComponent implements OnInit, OnDestroy {

    @ViewChild(PoliciesListComponent) listComponent: PoliciesListComponent;
    readonly #insurerSrv = inject(InsurersService);
    readonly #onDestroy = inject(DestroyRef);
    readonly #matDialog = inject(MatDialog);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    readonly $policiesList = toSignal(this.#insurerSrv.policiesList.getData$().pipe(filter(Boolean)));

    ngOnInit(): void {
        this.#insurerSrv.insurer.get$().pipe(
            takeUntilDestroyed(this.#onDestroy),
            tap(insurer => {
                this.#insurerSrv.policiesList.load(insurer.id);
            })
        ).subscribe();
    }

    ngOnDestroy(): void {
        this.#insurerSrv.policiesList.clear();
    }

    openNewPolicyDialog(): void {
        this.listComponent.openNewPolicyDialog();
    }

}

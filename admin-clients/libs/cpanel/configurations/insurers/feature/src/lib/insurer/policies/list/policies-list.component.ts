import { InsurersService } from '@admin-clients/cpanel-configurations-insurers-data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatDivider, MatListOption, MatSelectionList } from '@angular/material/list';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, debounceTime, filter, map, startWith, switchMap, tap } from 'rxjs';
import { NewPolicyDialogComponent } from '../new-policy-dialog/new-policy-dialog.component';

@Component({
    selector: 'app-policies-list',
    templateUrl: './policies-list.component.html',
    imports: [
        TranslatePipe, AsyncPipe, EllipsifyDirective, MatTooltip, MatIcon, MatSelectionList,
        MatListOption, MatDivider, MatProgressSpinner, MatIconButton, NgClass
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PoliciesListComponent implements OnInit, OnDestroy {
    readonly #insurerSrv = inject(InsurersService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #onDestroy = inject(DestroyRef);
    readonly #cdRef = inject(ChangeDetectorRef);
    readonly #matDialog = inject(MatDialog);

    selectedPolicyId: number | null;

    get #idPath(): string | undefined {
        return this.#route.snapshot.children[0].params['policyId'];
    }

    get #innerPath(): string {
        return this.#route.snapshot.children[0]?.children[0]?.routeConfig.path;
    }

    readonly policiesList$ = this.#insurerSrv.policiesList.getData$().pipe(filter(Boolean));

    readonly $isLoading = toSignal(this.#insurerSrv.policiesList.loading$());
    readonly $totalPolicies = toSignal(this.#insurerSrv.policiesList.getData$().pipe(filter(Boolean), map(pl => pl.length || 0)));

    ngOnInit(): void {
        this.handleSelectedPolicyChanges();
        this.handleRouterPolicyChanges();
        this.handlePolicyChangesForScroll();
        this.handlePolicyChangesToNavigate();
    }

    ngOnDestroy(): void {
        this.#insurerSrv.policy.clear();
        this.#insurerSrv.policiesList.clear();
    }

    selectionChangeHandler(policyId: number): void {
        if (!!policyId && this.selectedPolicyId !== policyId) {
            this.selectedPolicyId = policyId;
            const path = this.currentPath();
            this.#router.navigate([path], { relativeTo: this.#route });
        }
    }

    openNewPolicyDialog(): void {
        this.#matDialog.open<NewPolicyDialogComponent, null>(
            NewPolicyDialogComponent, new ObMatDialogConfig()
        )
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe((response: { insurerId: number; policyId: number }) => {
                this.#insurerSrv.policiesList.load(response?.insurerId);
                this.#router.navigate([response?.policyId, 'general-data'], { relativeTo: this.#route });
            });
        ;
    }

    /*
    // TODO: Once the delete policy request is done, uncomment and finalize the delete policy implementation
    openDeletePolicyDialog(): void {
        this.#msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'INSURERS.POLICIES.DELETE_POLICY',
            message: 'CHANNELS.REVIEWS.CONFIG.ACTIONS.DELETE_MESSAGE',
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
    }*/

    private handleSelectedPolicyChanges(): void {
        combineLatest([
            this.#insurerSrv.policy.get$(),
            this.#insurerSrv.policy.error$()
        ])
            .pipe(
                filter(([policy, error]) => !!policy || !!error),
                takeUntilDestroyed(this.#onDestroy))
            .subscribe(([policy, error]) => {
                this.selectedPolicyId = error || !policy ? null : policy.id;
                this.#cdRef.markForCheck();
            });
    }

    private handleRouterPolicyChanges(): void {
        this.#router.events.pipe(
            filter(event => event instanceof NavigationEnd),
            startWith(null as NavigationEnd),
            switchMap(() => this.policiesList$),
            filter(policiesList => !this.#idPath && !!policiesList.length),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([firstPolicy]) => {
            this.#router.navigate([firstPolicy.id], { relativeTo: this.#route });
        });
    }

    private handlePolicyChangesForScroll(): void {
        this.policiesList$
            .pipe(
                filter(policiesList => !!policiesList.length),
                debounceTime(500), takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(() => {
                const policy = this.selectedPolicyId;
                const element = document.getElementById('policy-list-option-' + policy);
                element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
            });
    }

    private handlePolicyChangesToNavigate(): void {
        this.policiesList$
            .pipe(
                tap(policiesList => {
                    if (!policiesList.length) {
                        setTimeout(() =>
                            this.#router.navigate(['.'], { relativeTo: this.#route })
                        );
                    }
                }),
                filter(policiesList =>
                    !!policiesList.length &&
                    this.#idPath &&
                    !policiesList.find(policy => policy.id.toString() === this.#idPath)),
                takeUntilDestroyed(this.#onDestroy)
            ).subscribe(([firstPolicy]) => {
                this.#router.navigate([firstPolicy.id], { relativeTo: this.#route });
            });
    }

    private currentPath(): string {
        return this.#innerPath ?
            this.selectedPolicyId.toString() + '/' + this.#innerPath : this.selectedPolicyId.toString();
    }

}

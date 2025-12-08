import { MessageDialogService, UnsavedChangesDialogResult } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanDeactivateFn, RouterStateSnapshot } from '@angular/router';
import { catchError, of, switchMap } from 'rxjs';
import { WritingComponent } from './writing-component.model';

export const unsavedChangesGuard: (paramsToCheck?: string[]) => CanDeactivateFn<WritingComponent> =
    (paramsToCheck?: string[]) => (
        component: WritingComponent,
        currentRoute: ActivatedRouteSnapshot,
        currentState: RouterStateSnapshot,
        nextState: RouterStateSnapshot
    ) => {
        if (!paramsToCheck || paramsToCheck.some(p => currentState.root.queryParams[p] !== nextState.root.queryParams[p])) {
            const msgDialogSrv = inject(MessageDialogService);
            const canSave = component?.form?.dirty && (!component?.canSave || component?.canSave());

            if (component?.canDeactivate) {
                return component.canDeactivate();
            } else if (canSave) {
                if (component.save$) {
                    return msgDialogSrv.openRichUnsavedChangesWarn()
                        .pipe(
                            switchMap(result => {
                                if (result === UnsavedChangesDialogResult.continue) {
                                    return of(true);
                                } else if (result === UnsavedChangesDialogResult.save) {
                                    return component.save$().pipe(
                                        switchMap(() => of(true)),
                                        catchError(() => of(false))
                                    );
                                }
                                return of(false); // cancel
                            })
                        );
                }
                return msgDialogSrv.defaultUnsavedChangesWarn();
            }
        }
        return true;
    };

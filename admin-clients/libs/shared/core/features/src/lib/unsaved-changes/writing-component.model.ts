import { FormGroup, UntypedFormGroup } from '@angular/forms';
import { Observable } from 'rxjs';

export interface WritingComponent {
    /**
     * Optional reactive form whose `dirty` status is used by the unsaved changes guard
     * to determine if navigation should be intercepted.
     */
    form?: UntypedFormGroup | FormGroup;

    /**
     * Optional custom deactivation logic. If provided, it will be called by the guard
     * before any other checks (form dirty state or save$).
     *
     * - If it returns true (or a resolved Promise/Observable of true), navigation is allowed.
     * - If it returns false (or resolves to false), navigation is cancelled.
     * - This allows full control over deactivation, useful for multi-step forms or additional checks.
     */
    canDeactivate?: () => Observable<boolean> | Promise<boolean> | boolean;

    /**
     * Optional method to allow the unsaved changes guard to check if the form is valid before saving.
     *
     * - This is only called if the `form` is dirty and `canDeactivate` is not defined.
     * - If not provided, the guard will assume the form is valid.
     * - If provided, the guard will check if the form is valid and return true if it is, false otherwise.
     */
    canSave?: () => boolean;

    /**
     * Optional method to allow the unsaved changes guard to trigger a save before navigating away.
     *
     * - This is only called if the `form` is dirty and `canDeactivate` is not defined.
     * - If not provided, the guard will show a basic warning dialog with "leave" / "cancel".
     * - If provided, the guard will show a dialog with "save", "discard" and "cancel" options.
     * - If the user chooses "save", this method will be called.
     * - Should return an Observable that resolves (or emits) `true` if the save was successful, or `false` otherwise.
     */
    save$?: (...args: unknown[]) => Observable<unknown>;
}

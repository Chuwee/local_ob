import { EventQuestionsService, PostBookingQuestion } from '@admin-clients/cpanel-promoters-events-design-questions-data-access';
import {
    DialogSize, ObDialog, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogActions, MatDialogContent, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, startWith } from 'rxjs/operators';

@Component({
    selector: 'ob-post-booking-questions-dialog',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule, ReactiveFormsModule, TranslatePipe, SearchablePaginatedSelectionModule, MatIcon,
        MatDialogTitle, MatIconButton, MatDialogContent, MatButton, MatDialogActions
    ],
    templateUrl: './post-booking-questions-dialog.component.html'
})
export class PostBookingQuestionsDialogComponent
    extends ObDialog<PostBookingQuestionsDialogComponent, PostBookingQuestion[], PostBookingQuestion[]> {
    readonly #questionsService = inject(EventQuestionsService);

    readonly pageSize = 10;

    readonly control = new FormControl<PostBookingQuestion[]>(this.data ?? []);

    readonly loading$ = this.#questionsService.postBookingQuestionsList.loading$();

    readonly questions$ = this.#questionsService.postBookingQuestionsList.getData$().pipe(filter(Boolean), startWith([]));

    readonly metadata$ = this.#questionsService.postBookingQuestionsList.getMetadata$().pipe(filter(Boolean));

    constructor() {
        super(DialogSize.LARGE);
    }

    changeFilter(filters: SearchablePaginatedSelectionLoadEvent): void {
        this.#questionsService.postBookingQuestionsList.load(filters);
    }

    close(): void {
        this.dialogRef.close();
    }

    save(): void {
        this.dialogRef.close(this.control.value);
    }
}

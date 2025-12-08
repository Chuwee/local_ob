import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EventChannelRequestStatus, eventChannelsProviders, EventChannelsRequest, EventChannelsService }
    from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventChannelsScopeType, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventPostBookingQuestionsSettings, EventQuestionsService, PostBookingQuestion, PutEventPostBookingQuestionsSettings }
    from '@admin-clients/cpanel-promoters-events-design-questions-data-access';
import {
    DialogSize, EmptyStateTinyComponent, EphemeralMessageService, MessageDialogService, ObMatDialogConfig,
    SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { Id, IdName } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, computed, inject, viewChildren } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, throwError } from 'rxjs';
import { filter, first, map, shareReplay, startWith } from 'rxjs/operators';
import { PostBookingQuestionsDialogComponent } from './post-booking-questions-dialog/post-booking-questions-dialog.component';

const STATIC_FILTERS: EventChannelsRequest = {
    request_status: [EventChannelRequestStatus.accepted, EventChannelRequestStatus.pending, EventChannelRequestStatus.pendingRequest],
    type: ChannelType.web,
    limit: 10
};
@Component({
    imports: [
        ReactiveFormsModule, MaterialModule, TranslatePipe, EmptyStateTinyComponent,
        FormContainerComponent, SearchablePaginatedSelectionModule
    ],
    selector: 'ob-events-design-post-booking-questions',
    templateUrl: './events-design-post-booking-questions.component.html',
    styleUrls: ['./events-design-post-booking-questions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [eventChannelsProviders]
})
export class EventsDesignPostBookingQuestionsComponent implements WritingComponent {
    readonly #fb = inject(FormBuilder);
    readonly #matDialog = inject(MatDialog);

    readonly #eventsService = inject(EventsService);
    readonly #eventChannelsService = inject(EventChannelsService);
    readonly #questionsService = inject(EventQuestionsService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #auth = inject(AuthenticationService);

    readonly pageSize = STATIC_FILTERS.limit;
    readonly channelsScopeType = EventChannelsScopeType;

    $matExpansionPanelQueryList = viewChildren(MatExpansionPanel);

    readonly form = this.#fb.group({
        enablePbQuestions: this.#fb.nonNullable.control(false),
        questions: this.#fb.nonNullable.control<PostBookingQuestion[]>([]),
        channelsSelectionType: this.#fb.nonNullable.control(EventChannelsScopeType.all),
        channels: this.#fb.nonNullable.control<Id[]>([], { validators: [Validators.required] })
    });

    readonly formControls = this.form.controls;

    readonly channels$: Observable<IdName[]> = this.#eventChannelsService.eventChannelsList.getData$().pipe(
        filter(Boolean),
        map(ec => ec.map(eventChannel => eventChannel.channel)),
        shareReplay(1)
    );

    readonly channelsMetadata$ = this.#eventChannelsService.eventChannelsList.getMetaData$();
    readonly channelsLoading$ = this.#eventChannelsService.eventChannelsList.inProgress$();

    readonly $event = toSignal(this.#eventsService.event.get$().pipe(filter(Boolean)));
    readonly $isLoadingOrSaving = toSignal(this.#questionsService.eventPbQuestionsSettings.loading$());
    readonly $currentLanguage = toSignal(this.#auth.getLoggedUser$().pipe(first(Boolean), map(user => user.language)));

    readonly $selectedQuestions = toSignal(
        this.formControls.questions.valueChanges.pipe(filter(Boolean), map(questions => this.#getDescription(questions))),
        { initialValue: this.#getDescription(this.formControls.questions.value ?? []) }
    );

    readonly $hasSelectedQuestions = computed(() => this.$selectedQuestions().length > 0);

    constructor() {
        this.#initFormHandlers();
        this.#initPbQuestionsData();
    }

    openPbQuestionsDialog(): void {
        this.#matDialog.open(
            PostBookingQuestionsDialogComponent,
            new ObMatDialogConfig(this.formControls.questions.value)
        ).afterClosed().pipe(first(), filter(Boolean)).subscribe((questions: PostBookingQuestion[]) => {
            if (this.#areSameQuestions(this.formControls.questions.value ?? [], questions)) return;

            this.formControls.questions.setValue(questions);
            this.formControls.questions.markAsDirty();
        });
    }

    refresh(): void {
        this.form.markAsPristine();
        this.form.markAsUntouched();
        this.#questionsService.eventPbQuestionsSettings.load(this.$event().id);
    }

    save(): void {
        this.save$().subscribe({
            next: () => {
                this.#ephemeralMessage.showSaveSuccess();
                this.refresh();
            },
            error: () => this.#cancelEnabledToggle()
        });
    }

    save$(): Observable<void> {
        if (this.form.invalid) {
            return this.#cancelSaveAndShowErrors();
        }

        return this.#questionsService.eventPbQuestionsSettings.update$(
            this.$event().id,
            this.#getPutQuestionsSettingsFromRawValue()
        );
    }

    channelsFilterChangeHandler(filters: SearchablePaginatedSelectionLoadEvent): void {
        this.#eventChannelsService.eventChannelsList.load(this.$event().id, {
            ...filters,
            ...STATIC_FILTERS
        });
    }

    #initPbQuestionsData(): void {
        this.#questionsService.eventPbQuestionsSettings.load(this.$event().id);

        this.#questionsService.eventPbQuestionsSettings.get$().pipe(filter(Boolean))
            .pipe(takeUntilDestroyed())
            .subscribe(questionsSettings => this.#resetFormWithSettings(questionsSettings));
    }

    #initFormHandlers(): void {
        FormControlHandler.getValueChanges(this.formControls.enablePbQuestions)
            .subscribe(() => this.#changeEnabled());

        FormControlHandler.getValueChanges(this.formControls.channelsSelectionType)
            .pipe(startWith(EventChannelsScopeType.all))
            .subscribe(type => type === EventChannelsScopeType.list
                ? this.formControls.channels.enable() : this.formControls.channels.disable()
            );
    }

    #changeEnabled(): void {
        if (this.#isOnlyEnableToggledDirty()) {
            this.save();
        } else if (this.form.dirty) {
            this.#msgDialogSrv.showWarn({
                actionLabel: 'FORMS.ACTIONS.UPDATE',
                showCancelButton: true,
                message: 'EVENTS.PROMOTIONS.STATUS_CHANGE_WARNING.DESCRIPTION',
                title: 'EVENTS.PROMOTIONS.STATUS_CHANGE_WARNING.TITLE',
                size: DialogSize.MEDIUM
            }).subscribe(result => result ? this.save() : this.#cancelEnabledToggle());
        }
    }

    #isOnlyEnableToggledDirty(): boolean {
        return Object.keys(FormControlHandler.getDirtyValues(this.form)).length === 1
            && this.formControls.enablePbQuestions.dirty;
    }

    #resetFormWithSettings(pbQuestionsSettings: EventPostBookingQuestionsSettings): void {
        this.form.reset({
            enablePbQuestions: pbQuestionsSettings?.enabled ?? false,
            questions: pbQuestionsSettings?.questions ?? [],
            channelsSelectionType: pbQuestionsSettings?.channels?.selection_type ?? EventChannelsScopeType.all,
            channels: pbQuestionsSettings?.channels?.ids?.map(id => ({ id })) ?? []
        });
    }

    #getPutQuestionsSettingsFromRawValue(): PutEventPostBookingQuestionsSettings {
        const values = this.form.getRawValue();
        const putSettings = {
            enabled: values.enablePbQuestions,
            questions: values.questions.map(q => q.id),
            channels: {
                selection_type: values.channelsSelectionType,
                ids: values.channels.map(c => c.id)
            }
        };

        if (putSettings.channels.selection_type === EventChannelsScopeType.all) {
            delete putSettings.channels.ids;
        }

        return putSettings;
    }

    #areSameQuestions(current: PostBookingQuestion[], next: PostBookingQuestion[]): boolean {
        if (current.length !== next.length) return false;

        const nextQuestionsIds = new Set(next.map(q => q.id));
        return current.every(q => nextQuestionsIds.has(q.id));
    }

    #getDescription(questions: PostBookingQuestion[]): (PostBookingQuestion & { description: string })[] {
        const currentLanguage = this.$currentLanguage();
        return questions.map(q => ({
            ...q,
            description: q.name ?? q.label.translations?.[currentLanguage] ?? q.label.default_value
        }));
    }

    #cancelSaveAndShowErrors(): Observable<never> {
        this.#cancelEnabledToggle();
        this.form.markAllAsTouched();
        scrollIntoFirstInvalidFieldOrErrorMsg(document, this.$matExpansionPanelQueryList());
        return throwError(() => 'invalid form');
    }

    #cancelEnabledToggle(): void {
        const control = this.formControls.enablePbQuestions;
        if (control.pristine) return;
        control.setValue(!control.value, { emitEvent: false });
        control.markAsPristine();
    }
}

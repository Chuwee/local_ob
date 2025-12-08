import { ChannelSuggestion, ChannelSuggestionType, ChannelsExtendedService, PostChannelSuggestionReq } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { CurrencySingleSelectorComponent, DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, ViewChild, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, concat } from 'rxjs';
import { first, map, toArray } from 'rxjs/operators';
import { ChannelOperativeService } from '../../channel-operative.service';
import { NewCrossSellingItemDialogFormType } from './form-type';
import { ChannelCrossSellingSelectorTableComponent } from './selector-list/channel-cross-selling-selector-table.component';

@Component({
    selector: 'app-new-cross-selling-item-dialog',
    templateUrl: './new-cross-selling-item-dialog.component.html',
    styleUrls: ['./new-cross-selling-item-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule,
        CommonModule, MatDividerModule, WizardBarComponent,
        ChannelCrossSellingSelectorTableComponent, DateTimePipe, CurrencySingleSelectorComponent
    ],
    providers: [ChannelOperativeService]
})
export class NewCrossSellingItemDialogComponent
    extends ObDialog<
        NewCrossSellingItemDialogComponent,
        { channelId: number; source: ChannelSuggestion; targets: ChannelSuggestion[] },
        { type: ChannelSuggestionType; id: number }
    >
    implements OnInit {
    readonly #destroyRef = inject(DestroyRef);

    readonly #channelService = inject(ChannelsExtendedService);
    readonly #auth = inject(AuthenticationService);
    readonly #fb = inject(FormBuilder);

    @ViewChild(WizardBarComponent) private readonly _wizardBar: WizardBarComponent;

    currentStep = 2;
    readonly currencyControl = inject(FormBuilder).control(null as string);
    readonly maxSelection = 10;
    readonly dateTimeFormats = DateTimeFormats;
    readonly channelSuggestionType = ChannelSuggestionType;
    readonly originsForm = this.createForm();
    readonly destinationsForm = this.createForm();

    readonly isLoading$ = this.#channelService.channelSuggestions.loading$();
    readonly suggestionsData$ = this.#channelService.channelSuggestions.get$().pipe(
        map(channelSuggestionsRes => channelSuggestionsRes?.data || [])
    );

    readonly currencies$ = this.#auth.getLoggedUser$()
        .pipe(first(), map(AuthenticationService.operatorCurrencies));

    constructor() {
        super(DialogSize.EXTRA_LARGE);
        this.dialogRef.addPanelClass('no-padding');
    }

    ngOnInit(): void {
        this.currencies$.pipe(first()).subscribe(currencies => {
            this.currentStep = currencies?.length > 1 ? 1 : 2;
        });
        if (this.data.source) {
            this.setStep3();
            this.currencyControl.setValue(this.data.source.currency);
            if (this.data.source.type === ChannelSuggestionType.event) {
                this.originsForm.setValue({
                    events: [{ ...this.data.source, saleRequestId: null }],
                    sessions: [],
                    allSessions: true
                });
            } else {
                this.originsForm.setValue({
                    events: [{ id: null, name: this.data.source.parent_name, saleRequestId: null }],
                    allSessions: false,
                    sessions: [{ ...this.data.source, startDate: null }]
                });
            }
            this.destinationsForm.controls.allSessions.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(() => {
                this.destinationsForm.controls.sessions.setValue([]);
            });
        }

        this.originsForm.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(() => {
            this.destinationsForm.setValue({ events: [], sessions: [], allSessions: true });
        });
    }

    close(): void {
        this.dialogRef.close();
    }

    setStep3(): void {
        let filters = {};
        if (this.data.source) {
            this.currentStep = 3;
            filters = this.data.source.type === ChannelSuggestionType.event ?
                { event_id: [this.data.source.id] } :
                { session_id: [this.data.source.id] };
        } else if (this.originsForm.value.sessions?.length > 0) {
            filters = { session_id: this.originsForm.value.sessions.map(session => session.id) };
        } else {
            filters = { event_id: this.originsForm.value.events.map(event => event.id) };
        }

        this.#channelService.channelSuggestions.load(this.data.channelId, filters);
    }

    setStep2(): void {
        this.destinationsForm.controls.events.markAsUntouched();
        this.destinationsForm.controls.sessions.markAsUntouched();
        this.destinationsForm.setErrors(null);
        this.destinationsForm.controls.events.setValue([]);
    }

    handleNextClick(): void {
        this.currentStep = this.currentStep === 1 ? 2 : 3;
        this._wizardBar?.nextStep();
    }

    handleBackClick(): void {
        this.currentStep = this.currentStep - 1;
        this._wizardBar?.previousStep();
    }

    save(): void {
        const req: PostChannelSuggestionReq[] = [];

        if (this.destinationsForm.value.events.length > 1) {
            req.push(...this.destinationsForm.value.events.map(event => ({
                id: event.id,
                type: ChannelSuggestionType.event
            })));
        } else if (this.destinationsForm.value.allSessions) {
            req.push({
                id: this.destinationsForm.value.events[0].id,
                type: ChannelSuggestionType.event
            });
        } else {
            req.push(...this.destinationsForm.value.sessions.map(session => ({
                id: session.id,
                type: ChannelSuggestionType.session
            })));
        }

        const saves: Observable<void>[] = [];

        if (this.originsForm.value.events.length > 1) {
            saves.push(...this.originsForm.value.events.map(event => this.#channelService.channelSuggestions.save(
                this.data.channelId,
                ChannelSuggestionType.event,
                event.id,
                req
            )));
        } else if (this.originsForm.value.allSessions) {
            saves.push(this.#channelService.channelSuggestions.save(
                this.data.channelId,
                ChannelSuggestionType.event,
                this.originsForm.value.events[0].id,
                req
            ));
        } else {
            saves.push(...this.originsForm.value.sessions.map(session => this.#channelService.channelSuggestions.save(
                this.data.channelId,
                ChannelSuggestionType.session,
                session.id,
                req
            )));
        }

        concat(...saves)
            .pipe(toArray())
            .subscribe(() => {
                if (this.originsForm.value.sessions?.length > 0) {
                    this.dialogRef.close({ id: this.originsForm.value.sessions[0].id, type: this.channelSuggestionType.session });
                } else {
                    this.dialogRef.close({ id: this.originsForm.value.events[0].id, type: this.channelSuggestionType.event });
                }
            });
    }

    private createForm(): NewCrossSellingItemDialogFormType {
        const form = this.#fb.group({
            events: [[] as { id: number; name: string; saleRequestId: number }[], Validators.required],
            sessions: [{
                value: [] as { id: number; name: string; startDate: string }[],
                disabled: true
            }, Validators.required],
            allSessions: true
        });

        form.controls.events.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(value => {
            form.controls.sessions.setValue([]);
            if (value.length !== 1) {
                form.controls.allSessions.setValue(true);
            }
        });

        form.controls.allSessions.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(value => {
            if (value) {
                form.controls.sessions.disable();
            } else {
                form.controls.sessions.enable();
            }
            form.controls.events.updateValueAndValidity({ emitEvent: false });
            form.controls.sessions.setValue([]);
            form.controls.sessions.markAsUntouched();
        });
        return form;
    }

}

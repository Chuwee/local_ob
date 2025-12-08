import { channelWebTypes } from '@admin-clients/cpanel/channels/data-access';
import {
    EventChannelRequestStatus,
    EventChannelsService, EventChannelTicketContentFormat, EventChannelTicketTemplateType
} from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventChannelsScopeType, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { ContentLinkType } from '@admin-clients/cpanel/shared/data-access';
import {
    GetTicketTemplatesRequest, TicketTemplateFormat,
    TicketTemplatesService
} from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { CopyTextComponent, EmptyStateComponent, LanguageBarComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatAnchor, MatButton } from '@angular/material/button';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatOption, MatSelectModule } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable, throwError } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';
import {
    EventChannelSessionsLinkListComponent
} from './content-sessions-link-list/event-channel-sessions-link-list.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent,
        LanguageBarComponent,
        ReactiveFormsModule,
        AsyncPipe,
        FlexModule,
        CopyTextComponent,
        TranslatePipe,
        EventChannelSessionsLinkListComponent,
        MatIcon,
        MatExpansionPanel,
        MatExpansionPanelTitle,
        MatFormField,
        MatSelectModule,
        MatOption,
        EllipsifyDirective,
        MatTooltip,
        EmptyStateComponent,
        MatProgressSpinner,
        MatAnchor,
        MatExpansionPanelHeader,
        MatButton
    ],
    selector: 'app-event-channel-channel-content',
    templateUrl: './event-channel-channel-content.component.html',
    styleUrls: ['./event-channel-channel-content.component.scss']
})
export class EventChannelChannelContentComponent implements OnDestroy {
    protected readonly eventChannelSessionsContentLinkType = ContentLinkType;

    readonly #fb = inject(FormBuilder);
    readonly #eventChannelsService = inject(EventChannelsService);
    readonly #eventsService = inject(EventsService);
    readonly #entityService = inject(EntitiesBaseService);
    readonly #ticketTemplatesService = inject(TicketTemplatesService);
    readonly #language = new BehaviorSubject<string>(null);

    readonly form = this.#fb.nonNullable.group({
        venueTemplateId: this.#fb.nonNullable.control(null as number)
    });

    $event = toSignal(this.#eventsService.event.get$().pipe(filter(Boolean)));
    $entity = toSignal(this.#entityService.getEntity$().pipe(filter(Boolean)));
    $eventChannel = toSignal(this.#eventChannelsService.eventChannel.get$()
        .pipe(filter(Boolean), tap(() => {
            this.#eventChannelsService.clearContentLinkRequest();
            this.#eventChannelsService.clearEventChannelAttendantsLinks();
        })));

    $showLinks = computed(() => {
        const showLinks = channelWebTypes.includes(this.$eventChannel()?.channel.type);
        if (showLinks) {
            this.#eventChannelsService.loadContentLinkRequest(this.$eventChannel().event.id, this.$eventChannel().channel.id);
            if (this.isEditAttendantEnabled(this.$eventChannel().channel.id)) {
                this.#eventChannelsService.loadEventChannelAttendantsLinks(this.$eventChannel().event.id, this.$eventChannel().channel.id);
            }
        }
        return showLinks;
    });

    $showTemplates = computed(() => {
        const showTemplates = (this.$eventChannel()?.channel.entity.id === this.$event()?.entity.id &&
            this.$entity()?.settings?.external_integration?.custom_managements.find(management => management.type === 'SMART_BOOKING_INTEGRATION'))
            || this.$entity()?.settings?.allow_config_multiple_templates;
        if (showTemplates) {
            this.#ticketTemplatesService.loadTicketTemplates(
                { entity_id: this.$event().entity.id, limit: 999, sort: 'name:asc' } as GetTicketTemplatesRequest);
            this.#eventChannelsService.ticketTemplates
                .load(this.$eventChannel().event.id, this.$eventChannel().channel.id);
        }
        return showTemplates;
    });

    $requestAccepted = computed(() => this.$eventChannel()?.status.request === EventChannelRequestStatus.accepted);

    $languageList = computed(() => {
        const languages = this.$eventChannel()?.settings?.languages;
        if (!languages?.default && languages?.selected.length) {
            languages.default = languages.selected[0];
        }
        this.#language.next(languages?.default);
        return languages?.selected || [];
    });

    language$ = this.#language.asObservable();
    templates$ = combineLatest([
        this.#ticketTemplatesService.getTicketTemplates$(),
        this.#eventChannelsService.ticketTemplates.get$()
    ]).pipe(
        filter(data => data.every(Boolean)),
        map(([ticketTemplates, selectedTicketTemplates]) => {
            const venueTemplateId = selectedTicketTemplates
                .find(template => template.type === EventChannelTicketTemplateType.single && template.format === EventChannelTicketContentFormat.pdf)?.id;
            this.form.patchValue({ venueTemplateId: Number(venueTemplateId) });
            this.form.markAsPristine();
            return ticketTemplates?.filter(template => template.design?.format === TicketTemplateFormat.pdf) || [];
        })
    );

    editAttendantLink$ = combineLatest([
        this.language$,
        this.#eventChannelsService.getEventChannelAttendantsLinks$()
    ]).pipe(
        filter(([languages]) => !!languages),
        map(([language, links]) => links?.find(elem => elem.language === language)));

    eventChannelLinks$ = combineLatest([
        this.language$,
        this.#eventChannelsService.getContentLinkRequest$()
    ]).pipe(
        filter(data => data.every(item => !!item)),
        map(([language, links]) => links.find(links => links.language === language) ?? { language })
    );

    isLoading$ = booleanOrMerge([
        this.#eventChannelsService.eventChannel.inProgress$(),
        this.#eventChannelsService.isContentLinkRequestLoading$(),
        this.#eventChannelsService.isEventChannelAttendantsLinksLoading$(),
        this.#eventChannelsService.isTicketPdfPreviewDownloading$()
    ]);

    ngOnDestroy(): void {
        this.#eventChannelsService.clearContentLinkRequest();
        this.#eventChannelsService.clearEventChannelAttendantsLinks();
        this.#eventChannelsService.ticketTemplates.clear();
    }

    changeLanguage(newLanguage: string): void {
        this.#language.next(newLanguage);
    }

    openTicketPreview(): void {
        this.#eventChannelsService.downloadTicketPdfPreview$(
            this.$eventChannel().event.id, this.$eventChannel().channel.id, this.#language.getValue()
        ).subscribe(res => window.open(res?.url, '_blank'));
    }

    cancel(): void {
        this.#eventChannelsService.eventChannel.load(this.$eventChannel().event.id, this.$eventChannel().channel.id);
    }

    save(): void {
        this.save$().subscribe(() =>
            this.#eventChannelsService.eventChannel.load(this.$eventChannel().event.id, this.$eventChannel().channel.id));
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return this.#eventChannelsService.ticketTemplates
                .save(this.$eventChannel().event.id, this.$eventChannel().channel.id, this.form.value.venueTemplateId,
                    EventChannelTicketTemplateType.single, EventChannelTicketContentFormat.pdf);
        } else {
            return throwError(() => 'invalid form');
        }
    }

    private isEditAttendantEnabled(channelId: number): boolean {
        return this.$event().settings?.attendant_tickets?.edit_attendant &&
            (this.$event().settings?.attendant_tickets?.channels_scope?.type === EventChannelsScopeType.all ||
                this.isChannelInScopeList(channelId));
    }

    private isChannelInScopeList(channelId: number): boolean {
        return !!(this.$event().settings?.attendant_tickets?.channels_scope?.channels
            .find(channel => channel.id === channelId));
    }

}

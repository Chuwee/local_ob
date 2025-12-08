import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelsExtendedService, ChannelSuggestionType } from '@admin-clients/cpanel/channels/data-access';
import { ChannelOperativeService } from '@admin-clients/cpanel/channels/my-channels/feature';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { AttendantsService } from '@admin-clients/cpanel/platform/data-access';
import {
    EventChannelReleaseStatus, EventChannelRequestStatus, EventChannelSaleStatus, eventChannelsProviders, EventChannelsService
} from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { AutomaticSale, AutomaticSalesPost, EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { EventType } from '@admin-clients/shared/common/data-access';
import { CsvFile, CsvHeaderMapping, CsvHeaderMappingField, CsvModule } from '@admin-clients/shared/common/feature/csv';
import {
    DialogSize, MessageDialogService, ObDialog, SelectSearchComponent, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Papa, ParseError } from 'ngx-papaparse';
import { BehaviorSubject, combineLatest, Subject } from 'rxjs';
import { filter, map, shareReplay, take, takeUntil, tap } from 'rxjs/operators';
import { ImportAutomaticSaleHeaderMatchComponent } from './header-match/import-automatic-sale-header-match.component';
import {
    CsvAutomaticSalesToImport, CsvAutomaticSalesToImportValueTypes,
    createCsvAutomaticSalesToImportMappingFields
} from './import-automatic-sale-mapping-data';
import { ImportAutomaticSaleComponent } from './selection/import-automatic-sale.component';

@Component({
    selector: 'app-new-automatic-sale-dialog',
    templateUrl: './new-automatic-sale-dialog.component.html',
    styleUrls: ['./new-automatic-sale-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule, CsvModule, ImportAutomaticSaleComponent,
        CommonModule, MatDividerModule, FlexLayoutModule, ImportAutomaticSaleHeaderMatchComponent,
        WizardBarComponent, SelectServerSearchComponent, FormControlErrorsComponent,
        SelectSearchComponent
    ],
    providers: [eventChannelsProviders]
})
export class NewAutomaticSaleDialogComponent
    extends ObDialog<
        NewAutomaticSaleDialogComponent,
        void,
        AutomaticSalesPost
    >
    implements OnInit, OnDestroy {

    readonly #onDestroy: Subject<void> = new Subject();
    readonly #eventChannelsService = inject(EventChannelsService);
    readonly #channelService = inject(ChannelsExtendedService);
    readonly #eventSessionsSrv = inject(EventSessionsService);
    readonly #auth = inject(AuthenticationService);
    readonly #eventsSrv = inject(EventsService);
    readonly #fb = inject(FormBuilder);
    readonly #translate = inject(TranslateService);
    readonly #csvParser = inject(Papa);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #attendantsSrv = inject(AttendantsService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #channelOperativeService = inject(ChannelOperativeService);

    #isCsvParserLoadingBS = new BehaviorSubject<boolean>(false);
    #isAvet = false;
    #parsedHeaders: string[];
    #file: File;
    #mappingFields: CsvHeaderMappingField<CsvAutomaticSalesToImport>[];
    @ViewChild(WizardBarComponent) private readonly _wizardBar: WizardBarComponent;
    #eventId: number;

    readonly formStep1 = this.#fb.group({
        filename: [null as string, Validators.required],
        channel_id: [null as number, Validators.required],
        receipt_email: [null as string, [Validators.required, Validators.email]]
    });

    readonly formStep2 = this.#fb.group({
        use_seat_mappings: [null as boolean, Validators.required],
        use_ob_ids_for_seat_mappings: [{ value: null as boolean, disabled: true }, Validators.required],
        allow_skip_non_adjacent_seats: [false],
        default_purchase_language: [true],
        use_locators: [false],
        sort: [false],
        force_multi_ticket: [false],
        allow_break_adjacent_seats: [false],
        skip_add_attendant: [false],
        add_extra_attendee_information: [false],
        extra_field_value: [null as string],
        preview_token: [null as string]
    });

    readonly channelCtrl = this.#fb.control(null as IdName, Validators.required);

    readonly typeCtrl = this.#fb.control({ value: null as 'SECTOR' | 'PRICE_ZONE', disabled: true }, Validators.required);

    readonly formCsv = this.#fb.group({
        sales: [null as any[], Validators.minLength(1)]
    });

    readonly formHeaders = this.#fb.group({
        headers: [null]
    });

    $formStep2 = toSignal(this.formStep2.valueChanges);
    $typeCtrl = toSignal(this.typeCtrl.valueChanges);
    $config = computed(() => ({ ...this.$formStep2(), automatic_type: this.$typeCtrl() }));
    $hasProcessedFile = signal(false);
    $currentStep = signal(1);
    isAvet$ = this.#eventsSrv.event.get$()
        .pipe(
            take(1),
            map(event => event.type === EventType.avet),
            takeUntil(this.#onDestroy),
            tap(isAvet => this.#isAvet = isAvet),
            shareReplay(1)
        );

    readonly maxSelection = 10;
    readonly dateTimeFormats = DateTimeFormats;
    readonly channelSuggestionType = ChannelSuggestionType;

    readonly isLoading$ = this.#channelService.channelSuggestions.loading$();
    readonly suggestionsData$ = this.#channelService.channelSuggestions.get$().pipe(
        map(channelSuggestionsRes => channelSuggestionsRes?.data || [])
    );

    readonly eventSession$ = this.#eventSessionsSrv.session.get$();

    channels$ = this.#eventChannelsService.eventChannelsList.getData$().pipe(map(options => options?.map(option => option.channel)));

    moreChannelsAvailable$ = this.#eventChannelsService.eventChannelsList.getMetaData$()
        .pipe((map(metadata => metadata?.offset + metadata?.limit < metadata?.total)));

    readonly attendantFields$ = this.#attendantsSrv.attendantFields.getData$().pipe(map(fields => {
        if (fields) {
            return fields.map(field => ({
                sid: field.sid,
                name: this.#translate.instant('ATTENDANT_FIELD.' + field.sid)
            }));
        } else {
            return [];
        }
    }));

    readonly $mandatoryFields = toSignal(combineLatest([
        this.#eventsSrv.eventAttendantFields.get$(),
        this.#channelOperativeService.getChannelForms$()
    ]).pipe(
        filter(data => data.every(Boolean)),
        map(([eventFields, channelForm]) => {
            const mandatoryeventFields = eventFields?.data.filter(field => field?.mandatory).map(field => field?.sid);
            const mandatoryChannelForm = channelForm.purchase?.filter(purchase => purchase?.mandatory).map(purchase => purchase?.key);
            return [
                ...mandatoryeventFields,
                ...mandatoryChannelForm
            ];
        })
    ));

    constructor() {
        super(DialogSize.LARGE);
        this.dialogRef.addPanelClass('no-padding');
    }

    ngOnInit(): void {
        this.channelCtrl.valueChanges.pipe(takeUntil(this.#onDestroy))
            .subscribe(value => this.formStep1.controls.channel_id.patchValue(value?.id));
        this.#eventSessionsSrv.session.get$().pipe(filter(Boolean), takeUntil(this.#onDestroy)).subscribe(session => {
            this.#eventId = session.event?.id;
            this.#eventsSrv.eventAttendantFields.load(session.event?.id);
        });
        this.formStep2.controls.use_seat_mappings.valueChanges.pipe(takeUntil(this.#onDestroy)).subscribe(useSeatMapping => {
            if (useSeatMapping && (this.formStep2.controls.use_ob_ids_for_seat_mappings.disabled || this.typeCtrl.enabled)) {
                this.formStep2.controls.use_ob_ids_for_seat_mappings.enable();
                if (!this.#isAvet) {
                    this.formStep2.controls.use_ob_ids_for_seat_mappings.patchValue(true);
                }
                this.typeCtrl.disable();
            } else if (!useSeatMapping && (this.formStep2.controls.use_ob_ids_for_seat_mappings.enabled || this.typeCtrl.disabled)) {
                this.formStep2.controls.use_ob_ids_for_seat_mappings.disable();
                this.typeCtrl.enable();
            }
        });

        this.#auth.getLoggedUser$().pipe(
            filter(Boolean), takeUntil(this.#onDestroy)
        ).subscribe(user => {
            this.formStep1.controls.receipt_email.patchValue(user.email);
        });

        this.selectionChangeHandler();
        this.headerMatchChangeHandler();

        this.#attendantsSrv.attendantFields.load();
    }

    loadChannels(q: string, next: boolean = false): void {
        if (!next) {
            return this.#eventChannelsService.eventChannelsList.load(this.#eventId,
                {
                    q,
                    release_status: EventChannelReleaseStatus.released,
                    request_status: EventChannelRequestStatus.accepted,
                    sale_status: EventChannelSaleStatus.sale
                });

        }
        return this.#eventChannelsService.eventChannelsList.loadMore(this.#eventId, {
            q,
            release_status: EventChannelReleaseStatus.released,
            request_status: EventChannelRequestStatus.accepted,
            sale_status: EventChannelSaleStatus.sale
        });
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    close(): void {
        this.dialogRef.close();
    }

    nextStep(): void {
        switch (this.$currentStep()) {
            case 1:
                if (this.formStep1.valid) {
                    this.#channelOperativeService.loadChannelForms(this.formStep1.controls.channel_id.value);
                    this._wizardBar?.nextStep();
                    this.$currentStep.set(this.$currentStep() + 1);
                } else {
                    this.formStep1.markAllAsTouched();
                    Object.values(this.formStep1.controls).forEach(control => {
                        control.updateValueAndValidity();
                        control.markAsTouched();
                    });
                    this.channelCtrl.markAsTouched();
                }
                break;
            case 2:
                this.formStep2.controls.extra_field_value.markAsTouched();
                if (this.formStep2.valid && ((this.typeCtrl.enabled && this.typeCtrl.valid) || this.typeCtrl.disabled)) {
                    this._wizardBar?.nextStep();
                    this.$currentStep.set(this.$currentStep() + 1);
                } else {
                    this.formStep2.controls.use_seat_mappings.markAsTouched();
                    if (this.formStep2.controls.use_ob_ids_for_seat_mappings.enabled) {
                        this.formStep2.controls.use_ob_ids_for_seat_mappings.markAsTouched();
                    }
                    if (this.typeCtrl.enabled) {
                        this.typeCtrl.markAsTouched();
                    }
                }
                break;
            case 3:
                if (this.formCsv.get('sales').dirty && this.formCsv.get('sales').valid) {
                    this._wizardBar?.nextStep();
                    this.$currentStep.set(this.$currentStep() + 1);
                }
                break;
            default:
                break;
        }

    }

    previousStep(): void {
        this._wizardBar?.previousStep();
        this.$currentStep.set(this.$currentStep() - 1);
    }

    save(): void {
        this.#isCsvParserLoadingBS.next(true);
        const setOfKeys = new Set<keyof CsvAutomaticSalesToImport>(
            new Set<keyof CsvAutomaticSalesToImport>(
                createCsvAutomaticSalesToImportMappingFields(this.$config(), this.$mandatoryFields())
                    .map(csvAutomaticSales => csvAutomaticSales.key))
        );

        this.formStep2.controls.add_extra_attendee_information.disable();

        this.parseFile(
            this.getMapOfParsedHeadersWithKeys<CsvAutomaticSalesToImport>(
                (parsedHeader => setOfKeys.has(parsedHeader))
            ),
            automaticSalesToImport => this.successParse(automaticSalesToImport)
        );
    }

    private selectionChangeHandler(): void {

        this.formStep2.controls.add_extra_attendee_information.valueChanges.pipe(takeUntil(this.#onDestroy)).subscribe(isChecked => {
            if (isChecked) {
                this.formStep2.controls.extra_field_value.addValidators([Validators.required]);
            } else {
                this.formStep2.controls.extra_field_value.clearValidators();
                this.formStep2.controls.extra_field_value.setValue(null);
            }
            this.formStep2.updateValueAndValidity();
        });

        this.formCsv.get('sales').valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe((csvFileProcessed: CsvFile) => {
                const { processedFile, file } = csvFileProcessed;
                this.#file = file;
                this.#parsedHeaders = processedFile as string[];
                this.$hasProcessedFile.set(!!csvFileProcessed.file);
                this.initHeaderMatch();
                this.updateHeaderMatch();
            });
    }

    private headerMatchChangeHandler(): void {
        this.formHeaders.controls.headers.valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe(({ mappingFields }: CsvHeaderMapping<AutomaticSale>) => {
                this.#mappingFields = mappingFields;
            });
    }

    private updateHeaderMatch(): void {
        const initialMapping = createCsvAutomaticSalesToImportMappingFields(this.$config(), this.$mandatoryFields());
        const updatedMapping = this.getUpdatedMappingFields(initialMapping);
        const sortedMapping = this.sortMappingFields(updatedMapping);
        this.setHeaderMatchGroupValue(sortedMapping);
    }

    private getUpdatedMappingFields(
        initialMapping: CsvHeaderMappingField<CsvAutomaticSalesToImport>[]
    ): CsvHeaderMappingField<CsvAutomaticSalesToImport>[] {
        const mapOfMappingValues = new Map(this.#mappingFields?.map(mappingField => [mappingField.key, mappingField.columnIndex]));
        const updatedMapping: CsvHeaderMappingField<CsvAutomaticSalesToImport>[] = initialMapping
            .map(mappingField => {
                if (mapOfMappingValues.has(mappingField.key)) {
                    return {
                        ...mappingField,
                        columnIndex: mapOfMappingValues.get(mappingField.key)
                    };
                } else {
                    return mappingField;
                }
            });
        return updatedMapping;
    }

    private initHeaderMatch(): void {
        const initialMapping = this.getInitialMappingFields();
        const sortedMapping = this.sortMappingFields(initialMapping);
        this.setHeaderMatchGroupValue(sortedMapping);
    }

    private sortMappingFields(mapping): CsvHeaderMappingField<AutomaticSale>[] {
        return mapping.sort((mappingFieldA, mappingFieldB) => {
            if ((mappingFieldA.required && mappingFieldB.required) || (!mappingFieldA.required && !mappingFieldB.required)) {
                return 0;
            } else if (mappingFieldB.required) {
                return 1;
            } else if (mappingFieldA.required) {
                return -1;
            }
            return 0;
        });
    }

    private getInitialMappingFields(): CsvHeaderMappingField<CsvAutomaticSalesToImport>[] {
        return createCsvAutomaticSalesToImportMappingFields(this.$config(), this.$mandatoryFields());
    }

    private setHeaderMatchGroupValue(mappingFields: CsvHeaderMappingField<CsvAutomaticSalesToImport>[]): void {
        const csvHeaderMapping: CsvHeaderMapping<CsvAutomaticSalesToImport> = {
            parsedHeaders: this.#parsedHeaders,
            mappingFields
        };
        this.formHeaders.controls.headers.reset(csvHeaderMapping);
    }

    private successParse(automaticSalesToImport: AutomaticSale[]): void {
        this.#isCsvParserLoadingBS.next(false);
        const request: AutomaticSalesPost = {
            config: { ...this.formStep1.value, ...this.formStep2.value },
            sales: automaticSalesToImport
        };
        this.dialogRef.close(request);
    }

    private getMapOfParsedHeadersWithKeys<T>(condition: (parsedHeader: keyof CsvAutomaticSalesToImport) => boolean): Map<string, keyof T> {
        return new Map<string, keyof T>(
            this.#parsedHeaders
                .reduce((acc, parsedHeader, parsedIndex) => {
                    const mappingField =
                        this.#mappingFields?.find(mappingField => mappingField.columnIndex === parsedIndex && condition(mappingField.key));
                    if (mappingField) {
                        acc.push([parsedHeader, mappingField.key]);
                    }
                    return acc;
                }, [])
        );
    }

    private parseFile(
        mapOfParsedHeadersWithKeys: Map<string, keyof CsvAutomaticSalesToImport>,
        cb: (importAutomaticSales: AutomaticSale[]) => void
    ): void {
        const automaticSalesToImport: AutomaticSale[] = [];
        let hasErrors = false;
        let accErrors: ParseError[] = [];

        this.#file.arrayBuffer().then(buffer => {
            let fileContent: string;

            try {
                fileContent = new TextDecoder('utf-8', { fatal: true }).decode(buffer);
            } catch (e) {
                this.#messageDialogService.showAlert({
                    size: DialogSize.SMALL,
                    title: 'TITLES.ERROR_DIALOG',
                    message: 'EVENTS.SESSION.NEW_AUTOMATIC_SALES.ERROR_UTF8'
                });
            }

            this.#csvParser.parse(fileContent, {
                delimiter: ',', skipEmptyLines: true, header: true, worker: true, dynamicTyping: false,
                step: (({ data, errors }) => {

                    const automaticSaleToImport: AutomaticSale = this.getImport(
                        mapOfParsedHeadersWithKeys,
                        data,
                        {} as CsvAutomaticSalesToImport
                    );
                    automaticSalesToImport.push(automaticSaleToImport);

                    if ((!hasErrors && errors?.length)) {
                        hasErrors = true;
                        accErrors = errors;
                    }
                }),
                complete: (() => {
                    if (hasErrors) {
                        this.showParseError('CSV.IMPORT.PROCESS_ERROR', accErrors);
                    } else {
                        cb(automaticSalesToImport);
                    }
                })
            });
        });
    }

    private getImport<T>(
        mapOfParsedHeadersWithKeys: Map<string, keyof T>,
        data: Record<string, CsvAutomaticSalesToImportValueTypes>,
        initialImport: T
    ): T {
        const result = [...mapOfParsedHeadersWithKeys.keys()]
            .reduce<T>((acc, header) => {
                if (data[header] !== null) {
                    acc[mapOfParsedHeadersWithKeys.get(header) as string] = data[header];
                }
                return acc;
            }, initialImport);
        return result;
    }

    private showParseError(message: string, errors: ParseError[]): void {
        this.#isCsvParserLoadingBS.next(false);
        const title = this.#translate.instant('TITLES.ERROR_DIALOG');
        this.#msgDialogSrv.showAlert({
            size: DialogSize.SMALL, title, message: this.#translate.instant(message),
            subMessages: errors.map(err => (err.row + 1) + ' - ' + err.message)
        });
    }
}

import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    VenueTemplatePriceTypeChannelContentType, VenueTemplatePriceTypeChannelContent, VenueTemplatePriceTypesService
} from '@admin-clients/cpanel/venues/venue-templates/data-access';
import { RichTextAreaComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge, htmlMaxLengthValidator } from '@admin-clients/shared/utility/utils';
import { VenueTemplatePriceType, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    ReactiveFormsModule,
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators
} from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, forkJoin, Observable, of, Subject } from 'rxjs';
import { mapTo, takeUntil, tap } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        MaterialModule,
        ReactiveFormsModule,
        TranslatePipe,
        FlexLayoutModule,
        FormControlErrorsComponent,
        TabDirective,
        RichTextAreaComponent,
        TabsMenuComponent
    ],
    selector: 'app-price-type-translations',
    templateUrl: './price-type-translations.component.html',
    styleUrls: ['./price-type-translations.component.scss']
})
export class PriceTypeTranslationsComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _venueTplPriceTypesSrv = inject(VenueTemplatePriceTypesService);
    private readonly _venueTplSrv = inject(VenueTemplatesService);

    private readonly _onDestroy = new Subject<void>();
    private readonly _venueTplIdBehavior = new BehaviorSubject<number>(null);
    private readonly _venueTplId$ = this._venueTplIdBehavior.asObservable();
    private _priceTypes: VenueTemplatePriceType[];
    private _venueTplId: number;

    readonly priceTypes$ = this._venueTplSrv.getVenueTemplatePriceTypes$();
    readonly requestsInProgress$ = booleanOrMerge([
        this._venueTplPriceTypesSrv.isVenueTemplatePriceTypeChannelContentLoading$(),
        this._venueTplPriceTypesSrv.isPriceTypeChannelContentSaving$()
    ]);

    priceTypeControl: UntypedFormControl;
    currentFormGroup: UntypedFormGroup;
    currentPriceType: VenueTemplatePriceType;

    @Input() languages$: Observable<string[]>;
    @Input() form: UntypedFormGroup;

    @Input()
    set venueTplId(value: number) {
        if (this._venueTplId) {
            this._venueTplSrv.clearVenueTemplatePriceTypes();
        }
        this._venueTplId = value;
        this._venueTplPriceTypesSrv.clearPriceTypesChannelContent();
        this._venueTplIdBehavior.next(value);
    }

    ngOnInit(): void {
        this._venueTplPriceTypesSrv.clearPriceTypesChannelContent();
        this.initForms();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._venueTplPriceTypesSrv.clearPriceTypesChannelContent();
    }

    save(): Observable<void> {
        return this.form.dirty ? this.saveVenueTemplates() : of(null);
    }

    reset(): void {
        this.form.markAsPristine();
        this.priceTypeControl.setValue(this.priceTypeControl.value);
    }

    // FORM AND VIEW LOGIC

    private initForms(): void {
        this.priceTypeControl = this._fb.control(null);
        // price type change handler
        this.priceTypeControl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(currentPriceType => {
                this.changePriceType(currentPriceType);
                this.setNamePlaceholder(currentPriceType);
            });
        // dynamic fields,
        combineLatest([this._venueTplId$, this._venueTplSrv.getVenueTemplatePriceTypes$(), this.languages$])
            .pipe(
                takeUntil(this._onDestroy)
            )
            .subscribe(([venueTplId, priceTypes, languages]) => {
                this._priceTypes = priceTypes;
                if (venueTplId && priceTypes?.length && languages?.length) {
                    this.initDynamicFormFields(venueTplId, priceTypes, languages);
                    this.priceTypeControl.setValue(priceTypes[0].id);
                } else {
                    this.priceTypeControl.setValue(null);
                }
            });
        // loaded values setting
        this._venueTplPriceTypesSrv.getPriceTypeChannelContent$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(cc => cc && this.setLoadedFormValues(cc.priceTypeId, cc.contents));
    }

    private initDynamicFormFields(venueTplId: number, priceTypes: VenueTemplatePriceType[], languages: string[]): void {
        this.currentFormGroup = undefined;
        if (priceTypes.length && languages.length) {
            let templateGroup = this.form.get(String(venueTplId)) as UntypedFormGroup;
            if (!templateGroup) {
                templateGroup = this._fb.group({});
                this.form.addControl(String(venueTplId), templateGroup);
            }
            priceTypes.forEach(priceType => {
                let languagesGroup = templateGroup.get(String(priceType.id)) as UntypedFormGroup;
                if (!languagesGroup) {
                    languagesGroup = this._fb.group({});
                    templateGroup.addControl(String(priceType.id), languagesGroup);
                }
                languages.forEach(language => {
                    let languageGroup = languagesGroup.get(language) as UntypedFormGroup;
                    if (!languageGroup) {
                        languageGroup = this._fb.group({});
                        languagesGroup.addControl(language, languageGroup);
                        languageGroup.addControl('name', this._fb.control('', Validators.maxLength(100)));
                        languageGroup.addControl('description', this._fb.control('', htmlMaxLengthValidator(200)));
                    }
                });
            });
        }
    }

    private setLoadedFormValues(priceTypeId: number, contents: VenueTemplatePriceTypeChannelContent[]): void {
        const control = this.form.get([String(this._venueTplId), String(priceTypeId)]);

        if (control && !control.dirty) {
            control.reset();
            contents.forEach(content => {
                const fieldControl = control.get([
                    content.language,
                    content.type === VenueTemplatePriceTypeChannelContentType.name ? 'name' : 'description'
                ]);
                fieldControl?.reset(content.value, { emitEvent: false });
            });
        }
    }

    private changePriceType(priceTypeId: number): void {
        this.currentFormGroup = undefined;
        if (priceTypeId) {
            this._venueTplPriceTypesSrv.loadPriceTypeChannelContent(this._venueTplId, priceTypeId);
        }
        const nextCurrentForm = this.form.get([
            String(this._venueTplId),
            String(priceTypeId)
        ]) as UntypedFormGroup;
        const controlsToClean = !nextCurrentForm ? [] :
            Object.keys(nextCurrentForm.value)
                .map(key => nextCurrentForm.get([key, 'description']))
                .filter(control => !control.dirty);
        this.currentFormGroup = nextCurrentForm;
        controlsToClean?.forEach(control => control.markAsPristine());
    }

    private setNamePlaceholder(priceTypeId: number): void {
        this.currentPriceType = this._priceTypes?.find(priceType => priceType.id === priceTypeId);
    }

    // SAVE FUNCTIONS

    private saveVenueTemplates(): Observable<void> {
        return forkJoin(
            Object.keys(this.form.value)
                .filter(venueTemplateId => this.form.get(venueTemplateId).dirty)
                .map(venueTemplateId =>
                    Object.keys(this.form.get(venueTemplateId).value)
                        .filter(priceTypeId => this.form.get([venueTemplateId, priceTypeId]).dirty)
                        .map(priceTypeId => this.savePriceType(venueTemplateId, priceTypeId))
                )
                .reduce((previousValue, currentValue) => currentValue.concat(previousValue))
        )
            .pipe(
                tap(() => this.reset()),
                mapTo(null)
            );
    }

    private savePriceType(venueTplId: string, priceTypeId: string): Observable<void> {
        return this._venueTplPriceTypesSrv.savePriceTypeChannelContent(
            Number(venueTplId),
            priceTypeId,
            Object.keys(this.form.get([venueTplId, priceTypeId]).value)
                .map(language => {
                    const elementsToSave: VenueTemplatePriceTypeChannelContent[] = [];
                    const controlsGroup = this.form.get([venueTplId, priceTypeId, language]) as UntypedFormGroup;
                    if (controlsGroup.get('name').dirty) {
                        elementsToSave.push({
                            type: VenueTemplatePriceTypeChannelContentType.name,
                            language,
                            value: controlsGroup.get('name').value ?? ''
                        });
                    }
                    if (controlsGroup.get('description').dirty) {
                        elementsToSave.push({
                            type: VenueTemplatePriceTypeChannelContentType.description,
                            language,
                            value: controlsGroup.get('description').value ?? ''
                        });
                    }
                    return elementsToSave;
                })
                .filter(elementsToSave => elementsToSave.length)
                .reduce((acum, current) => acum.push(...current) && acum)
        );
    }
}

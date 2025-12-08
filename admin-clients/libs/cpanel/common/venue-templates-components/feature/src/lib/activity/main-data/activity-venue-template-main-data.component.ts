import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { VenuesService } from '@admin-clients/cpanel/venues/data-access';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplate, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, first, switchMap, take } from 'rxjs/operators';

@Component({
    selector: 'app-activity-venue-template-main-data',
    templateUrl: './activity-venue-template-main-data.component.html',
    styleUrls: ['./activity-venue-template-main-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, AsyncPipe, MatFormField, MatLabel, MatInput, MatError, MatSelect, MatOption,
        EllipsifyDirective, TranslatePipe, FormControlErrorsComponent, MatTooltip, FlexLayoutModule
    ]
})
export class ActivityVenueTemplateMainDataComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #venuesSrv = inject(VenuesService);
    #form: FormGroup;

    readonly venueTemplate$ = this.#venueTemplatesSrv.venueTpl.get$();
    readonly venues$ = this.#venuesSrv.venuesList.getData$();
    readonly venue$ = this.#venuesSrv.getVenue$();
    readonly requestInProgress$ = booleanOrMerge([
        this.#venuesSrv.venuesList.isLoading$(),
        this.#venuesSrv.isVenueLoading$(),
        this.#venueTemplatesSrv.venueTpl.inProgress$(),
        this.#venueTemplatesSrv.isVenueTemplateSaving$()
    ]);

    @Input()
    isEventTemplate: boolean;

    get form(): FormGroup {
        return this.#form;
    }

    @Input()
    set form(value: FormGroup) {
        this.#form = value;
        this.initForm();
    }

    ngOnInit(): void {
        // data loading
        this.#venueTemplatesSrv.venueTpl.get$()
            .pipe(first(Boolean))
            .subscribe(tpl =>
                this.#venuesSrv.venuesList.load({
                    limit: 999,
                    entityId: tpl.entity.id,
                    includeOwnTemplateVenues: this.isEventTemplate,
                    includeThirdPartyVenues: !this.isEventTemplate
                })
            );
        // form data set
        this.venueTemplate$
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(venueTemplate => this.setFormData(venueTemplate));
    }

    reset(): void {
        this.#venueTemplatesSrv.venueTpl.get$().pipe(take(1)).subscribe(tpl => this.setFormData(tpl));
    }

    saveData(): Observable<void> {
        if (this.form.dirty && this.form.valid) {
            return this.#venueTemplatesSrv.venueTpl.get$()
                .pipe(
                    take(1),
                    switchMap(tpl => this.#venueTemplatesSrv.updateVenueTemplate(
                        tpl.id,
                        {
                            name: this.form.get('name').value,
                            venue_id: this.form.get('venueId').value,
                            space_id: this.form.get('spaceId').value
                        }
                    ))
                );
        } else {
            return null;
        }
    }

    private initForm(): void {
        this.#form.addControl('id', this.#fb.control(null));
        this.#form.addControl('name', this.#fb.control(null, [Validators.required]));
        this.#form.addControl('venueId', this.#fb.control(null, [Validators.required]));
        this.#form.addControl('spaceId', this.#fb.control(null, [Validators.required]));
        // form side effects
        this.#form.get('venueId').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(venueId => {
                if (venueId) {
                    this.#venuesSrv.loadVenue(venueId);
                } else {
                    this.#venuesSrv.clearVenue();
                }
                this.form.get('spaceId').setValue(null);
                this.form.get('spaceId').markAsTouched();
            });
        this.reset();
    }

    private setFormData(tpl: VenueTemplate): void {
        this.form.reset({ id: tpl.id, name: tpl.name, venueId: tpl.venue?.id, spaceId: tpl.space?.id });
    }
}

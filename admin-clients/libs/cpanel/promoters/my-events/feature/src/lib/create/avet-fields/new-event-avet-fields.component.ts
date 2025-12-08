import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { EventAvetConnection } from '@admin-clients/cpanel/promoters/events/data-access';
import { Entity } from '@admin-clients/shared/common/data-access';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import {
    StdVenueTplService, VenueTemplateAvetCompetition
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import {
    VenueTemplate, VenueTemplatesService, VenueTemplateStatus, VenueTemplateType
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, of, Subject } from 'rxjs';
import { distinctUntilChanged, filter, first, map, shareReplay, startWith, switchMap, takeUntil, tap } from 'rxjs/operators';

export interface AvetFieldsForm {
    entity: FormControl<Entity>;
    template: FormControl<VenueTemplate>;
    competition: FormControl<VenueTemplateAvetCompetition>;
    connection: FormControl<EventAvetConnection>;
}
@Component({
    selector: 'app-new-event-avet-fields',
    templateUrl: './new-event-avet-fields.component.html',
    styleUrls: ['./new-event-avet-fields.component.scss'],
    imports: [
        MaterialModule, FlexLayoutModule, ReactiveFormsModule, TranslatePipe,
        FormControlErrorsComponent, SelectSearchComponent, NgIf, AsyncPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewEventAvetFieldsComponent implements OnInit, OnDestroy {
    private readonly _venueTemplatesService = inject(VenueTemplatesService);
    private readonly _stdVenueTplSrv = inject(StdVenueTplService);
    private readonly _fb = inject(FormBuilder);

    private _onDestroy = new Subject<void>();

    @Input() form: FormGroup;
    @Input() entities: Entity[];

    avetEntities$: Observable<Entity[]>;
    avetTemplates$: Observable<VenueTemplate[]>;
    avetCompetitions$: Observable<VenueTemplateAvetCompetition[]>;
    avetConnections = Object.values(EventAvetConnection);

    ngOnInit(): void {
        this.form.setControl('avet', this._fb.group({
            entity: [null, Validators.required],
            template: [{ value: null, disabled: true }, Validators.required],
            competition: [{ value: null, disabled: true }, Validators.required],
            connection: [null, Validators.required]
        }));

        this.avetTemplates$ = this.form.get('avet.entity').valueChanges
            .pipe(
                distinctUntilChanged(),
                tap(() => this.form.get('avet.template').disable()),
                filter(value => value !== null),
                switchMap(entity => {
                    this._venueTemplatesService.clearVenueTemplateList();
                    this._venueTemplatesService.loadVenueTemplatesList({
                        entityId: entity.id, type: VenueTemplateType.avet,
                        limit: 999, offset: 0, sort: 'name:asc',
                        status: [VenueTemplateStatus.active]
                    });
                    return this._venueTemplatesService.getVenueTemplatesList$().pipe(
                        first(value => value != null)
                    );
                }),
                map(list => list.data),
                tap(templates => {
                    this.form.get('avet.template').enable();
                    this.form.get('avet.template').patchValue(templates.length === 1 ? templates[0] : null);
                    this.form.get('avet.competition').patchValue(null);
                }),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );

        this.avetCompetitions$ = combineLatest([
            this.form.get('avet.template').valueChanges.pipe(distinctUntilChanged()),
            this.form.get('entity').valueChanges.pipe(startWith(this.form.value.entity), distinctUntilChanged())
        ]).pipe(
            tap(() => this.form.get('avet.competition').disable()),
            filter(([template, entity]) => template !== null && entity !== null),
            switchMap(([template, entity]: [VenueTemplate, Entity]) => {
                this._stdVenueTplSrv.clearVenueTemplateAvetCompetitions();
                this._stdVenueTplSrv.loadVenueTemplateAvetCompetitions(template.id, entity.id, true);
                return this._stdVenueTplSrv.getVenueTemplateAvetCompetitions$().pipe(
                    first(value => value != null)
                );
            }),
            tap(competitions => {
                this.form.get('avet.competition').enable();
                this.form.get('avet.competition')
                    .patchValue(competitions.length === 1 ? competitions[0] : null);
            }),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );

        this.avetEntities$ = of(this.entities)
            .pipe(
                filter(value => value !== null),
                map(entities => entities.filter(entity => entity.settings?.allow_avet_integration)),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );

        this.avetEntities$.pipe(
            filter(entities => entities.length === 1),
            takeUntil(this._onDestroy)
        ).subscribe(entities => {
            this.avetTemplates$.subscribe();
            this.form.get('avet.entity').setValue(entities[0]);
        });

    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this.form.removeControl('avet');
    }

}

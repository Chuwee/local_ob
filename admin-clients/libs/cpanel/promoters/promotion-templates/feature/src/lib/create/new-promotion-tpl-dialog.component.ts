import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { PromotionFieldRestrictions } from '@admin-clients/cpanel/promoters/data-access';
import { PostPromotionTpl, PromotionTplsService } from '@admin-clients/cpanel/promoters/promotion-templates/data-access';
import { PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { Entity, EntitiesBaseService, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-new-promotion-tpl-dialog',
    templateUrl: './new-promotion-tpl-dialog.component.html',
    styleUrls: ['./new-promotion-tpl-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, MaterialModule, TranslatePipe, SelectSearchComponent, AsyncPipe,
        FlexLayoutModule, EllipsifyDirective
    ]
})
export class NewPromotionTemplateDialogComponent implements OnInit {
    readonly nameRestrictions = PromotionFieldRestrictions;
    readonly creationTypes = {
        automatic: PromotionType.automatic,
        manual: 'MANUAL'
    };

    readonly creationSubtypes = {
        basic: PromotionType.basic,
        plus: PromotionType.plus
    };

    readonly canSelectEntity$ = this._auth.canReadMultipleEntities$();

    form: UntypedFormGroup;
    entities$: Observable<Entity[]>;
    saving$: Observable<boolean>;

    constructor(
        private _dialogRef: MatDialogRef<NewPromotionTemplateDialogComponent>,
        private _auth: AuthenticationService,
        private _entitiesService: EntitiesBaseService,
        private _promotionTplsSrv: PromotionTplsService,
        private _fb: UntypedFormBuilder,
        private _elemRef: ElementRef
    ) {
        this._dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.form = this._fb.group({
            entity: [null, Validators.required],
            name: [null, [
                Validators.required,
                Validators.minLength(this.nameRestrictions.minNameLength),
                Validators.maxLength(this.nameRestrictions.maxNameLength)
            ]],
            type: [null, [Validators.required]],
            manualType: [{ value: null, disabled: true }, [Validators.required]]
        });
        this.entities$ = this._auth.getLoggedUser$().pipe(
            first(user => user !== null),
            withLatestFrom(this.canSelectEntity$),
            switchMap(([user, canSelectEntity]) => {
                if (canSelectEntity) {
                    this._entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [
                            EntitiesFilterFields.name
                        ],
                        type: 'EVENT_ENTITY'
                    });
                    return this._entitiesService.entityList.getData$();
                } else {
                    this._entitiesService.loadEntity(user.entity.id);
                    return this._entitiesService.getEntity$().pipe(
                        filter(value => value !== null),
                        tap(entity => this.form.get('entity').patchValue(entity)),
                        map(entity => [entity])
                    );
                }
            }),
            tap((entities: Entity[]) => {
                if (entities && entities.length === 1) {
                    this.form.patchValue({ entity: entities[0] });
                }
            }),
            shareReplay(1)
        );
        this.saving$ = this._promotionTplsSrv.isPromotionTemplateSaving$();

        this.form.get('type').valueChanges
            .subscribe(value => {
                if (value === this.creationTypes.manual) {
                    this.form.get('manualType').reset();
                    this.form.get('manualType').enable();
                } else {
                    this.form.get('manualType').disable();
                }
            });
    }

    create(): void {
        if (this.form.valid) {
            const data = this.form.value;
            const promo: PostPromotionTpl = {
                entity_id: data.entity.id,
                name: data.name,
                type: data.type === this.creationTypes.manual ? data.manualType : data.type
            };
            this._promotionTplsSrv.createPromotionTemplate(promo)
                .subscribe(id => this.close(id));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this._elemRef.nativeElement);
        }
    }

    close(promotionId: number = null): void {
        this._dialogRef.close(promotionId);
    }
}

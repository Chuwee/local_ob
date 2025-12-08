import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { ChannelMemberExternalService, Restriction } from '@admin-clients/cpanel-channels-member-external-data-access';
import { MemberOrderType } from '@admin-clients/cpanel-sales-data-access';
import { EphemeralMessageService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { StdVenueTplService } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject, Input, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import { Subject } from 'rxjs';
import { catchError, filter, first, tap } from 'rxjs/operators';
import { MemberExternalRestrictionFieldsComponent } from '../fields/restriction-fields.component';
import {
    MemberExternalRestrictionTranslationsDialogComponent as TranslationsDialog,
    RestrictionTranslationData as TranslationData
} from '../translations/translations-dialog.component';

@Component({
    selector: 'app-members-restriction',
    templateUrl: './restriction.component.html',
    styleUrls: ['./restriction.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MemberExternalRestrictionComponent implements OnInit, OnDestroy, WritingComponent {

    private readonly _destroy = new Subject<void>();
    private readonly _fb = inject(FormBuilder);
    private readonly _channelsService = inject(ChannelsService);
    private readonly _memberExtSrv = inject(ChannelMemberExternalService);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);
    private readonly _dialog = inject(MatDialog);
    private readonly _venueTplService = inject(StdVenueTplService);

    private _channelId: number;
    private _languages: string[];
    private _restriction!: Partial<Restriction>;
    private _form = this._fb.group({
        venue_template_sectors: [this.restriction?.venue_template_sectors],
        member_periods: [this.restriction?.member_periods],
        activated: this.restriction?.activated,
        translations: this.restriction?.translations || {},
        fields: this.restriction?.fields
    });

    @ViewChild(MemberExternalRestrictionFieldsComponent)
    private _fields: MemberExternalRestrictionFieldsComponent;

    @Input() set form(value: FormGroup) {
        this._form.setParent(value);
    }

    get form(): FormGroup {
        return this._form;
    }

    @Input() set restriction(value: Partial<Restriction>) {
        if (this.equals(this._restriction, value)) return;

        this._restriction = value;

        this.form.reset(value);

        if (value.editing) {
            this.form.enable();
        } else {
            this.form.disable();
        }

        this.updateActivationStatus();
    }

    get restriction(): Partial<Restriction> {
        return this._restriction;
    }

    readonly loading$ = this._memberExtSrv.restrictions.loading$();
    readonly sectors$ = this._venueTplService.getSectors$();
    readonly periods = [MemberOrderType.renewal, MemberOrderType.change, MemberOrderType.buy];

    constructor() { }

    async ngOnInit(): Promise<void> {
        this._channelsService.getChannel$().pipe(
            first(channel => !!channel)
        ).subscribe(channel => {
            this._channelId = channel.id;
            this._languages = channel.languages.selected.map(lang => lang);
        });
    }

    selectAll(select: MatSelect): void {
        if (this.allSelected(select)) {
            select.options.forEach(opt => opt.value && opt.select());
        } else {
            select.options.forEach(opt => opt.value && opt.deselect());
        }
    }

    allSelected(select: MatSelect): boolean {
        return select?.options?.filter(opt => !!opt.value).some(opt => !opt.selected);
    }

    ngOnDestroy(): void {
        this._destroy.next(null);
        this._destroy.complete();
    }

    translate(): void {
        const translations = this.form.value.translations;
        const data = { translations, languages: this._languages };
        const config = new ObMatDialogConfig(data);
        this._dialog.open<TranslationsDialog, TranslationData, Restriction['translations']>(TranslationsDialog, config)
            .beforeClosed()
            .pipe(filter(result => !!result))
            .subscribe(translations => {
                this.form.get('translations').markAsDirty();
                this.form.get('translations').setValue(translations);
            });
    }

    save(): void {
        const restriction = {
            sid: this.restriction.sid,
            ...this.form.value,
            translations: this.form.value.translations || {},
            fields: this._fields.values()
        };
        this._memberExtSrv.restrictions.update(this._channelId, restriction)
            .pipe(tap(() => this.cancel()))
            .subscribe(() => this._ephemeralSrv.showSaveSuccess());
    }

    cancel(): void {
        this._memberExtSrv.restrictions.set({ sid: this.restriction.sid, editing: false });
    }

    edit(): void {
        this._memberExtSrv.restrictions.set({ sid: this.restriction.sid, editing: true });
    }

    activate(activated: boolean): void {
        const restriction = { sid: this.restriction.sid, activated };
        this._memberExtSrv.restrictions.update(this._channelId, restriction)
            .pipe(catchError(error => {
                this.form.get('activated').reset(this.restriction.activated);
                throw error;
            }))
            .subscribe(() => {
                this.form.get('activated').markAsPristine();
                this._ephemeralSrv.showSaveSuccess();
            });
    }

    /**
     * all languages have to have a translation otherwise will return false
     */
    missingTranslations(): boolean {
        const languages = this._languages;
        const translations: Record<string, string> = this.restriction.translations;

        if (!translations) return false;

        return languages.some(lang => !translations[lang]);
    }

    private updateActivationStatus(): void {
        const restriction = this.form.value;
        this._memberExtSrv.restrictions.structure.fields$(this.restriction.restriction_type).pipe(
            first(val => !!val)
        ).subscribe(fields => {
            const missingFields = (): boolean => fields.some(field =>
                !Object.keys(restriction.fields).includes(field.id) || !restriction.fields[field.id]
            );
            if (!restriction.fields || missingFields() ||
                !restriction.member_periods ||
                !restriction.venue_template_sectors ||
                !restriction.translations || this.missingTranslations()) {
                this.form.get('activated').disable();
            } else {
                this.form.get('activated').enable();
            }
        });
    }

    private equals(a: Partial<Restriction>, b: Partial<Restriction>): boolean {
        return !!a && !!b &&
            a.editing === b.editing &&
            a.member_periods === b.member_periods &&
            a.venue_template_sectors === b.venue_template_sectors &&
            JSON.stringify(a.translations) === JSON.stringify(b.translations) &&
            JSON.stringify(a.fields) === JSON.stringify(b.fields);
    }
}

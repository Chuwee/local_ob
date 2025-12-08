import { TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { Component, ChangeDetectionStrategy, Input, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ValidatorFn } from '@angular/forms';

export type CommunicationFields = {
    type: 'html' | 'text';
    name: string;
    label?: string;
    placeholder?: string;
    validators?: ValidatorFn[];
    value?: string;
}[];

type CommunicationForm = FormGroup<Record<string, FormGroup<Record<string, FormControl<string>>>>>;

@Component({
    selector: 'app-communication-fields',
    templateUrl: './communication.component.html',
    styleUrls: ['./communication.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CommunicationFieldsComponent {

    @ViewChild('tabs') private _tabs: TabsMenuComponent;

    @Input() languages: string[];
    @Input() form: CommunicationForm;
    @Input() fields: CommunicationFields;

    constructor() { }

    static formBuilder(languages: string[], fields: CommunicationFields): CommunicationForm {
        return new FormGroup(languages.reduce<Record<string, FormGroup>>(
            (acc, lang) => (acc[lang] = new FormGroup(fields.reduce<Record<string, FormControl<string>>>(
                (acc, { value, validators, name }) =>
                    (acc[name] = new FormControl<string>(value, { validators }), acc),
                {}
            )), acc),
            {}
        ));
    }

    /**
     * Change langugage tab if invalid fields found
     */
    showErrors(): void {
        this._tabs.goToInvalidCtrlTab();
    }

}

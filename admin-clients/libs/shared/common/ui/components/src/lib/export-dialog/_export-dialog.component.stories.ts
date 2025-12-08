import { FORM_CONTROL_ERRORS } from '@OneboxTM/feature-form-control-errors';
import { AUTHENTICATION_SERVICE } from '@admin-clients/shared/core/data-access';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Meta, StoryObj, componentWrapperDecorator, moduleMetadata } from '@storybook/angular';
import { of } from 'rxjs';
import { ExportDialogComponent } from './export-dialog.component';

const meta: Meta<ExportDialogComponent> = {
    title: 'components/ExportDialogComponent',
    component: ExportDialogComponent,
    decorators: [
        moduleMetadata({
            providers: [
                {
                    provide: AUTHENTICATION_SERVICE,
                    useValue: {
                        getLoggedUser$: () => of({ email: 'test@oneboxtds.es' })
                    }
                },
                {
                    provide: MatDialogRef,
                    useValue: { addPanelClass: (_: string | string[]) => this }
                },
                {
                    provide: MAT_DIALOG_DATA,
                    useValue: { exportData: [{ fields: [] }] }
                },
                {
                    provide: FORM_CONTROL_ERRORS,
                    useValue: { formControlErrors: { default: 'INVALID_FIELD', required: 'REQUIRED_FIELD' }, prefix: 'FORMS.ERRORS' }
                }
            ]
        }),
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            ${story}
        `)
    ]
};
export default meta;

export const Primary: StoryObj<ExportDialogComponent> = {
    render: args => ({
        props: args
    }),
    args: {}
};

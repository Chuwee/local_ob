import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Meta, StoryObj, componentWrapperDecorator, moduleMetadata } from '@storybook/angular';
import { DialogSize } from '../dialog/models/dialog-size.enum';
import { MessageDialogComponent } from './message-dialog.component';
import { ObMatDialogConfig } from './models/message-dialog.model';

const meta: Meta<MessageDialogComponent> = {
    title: 'components/MessageDialogComponent',
    component: MessageDialogComponent,
    decorators: [
        moduleMetadata({
            providers: [
                {
                    provide: MatDialogRef,
                    useValue: { addPanelClass: (_: string | string[]) => this }
                },
                {
                    provide: MAT_DIALOG_DATA,
                    useValue: new ObMatDialogConfig({
                        size: DialogSize.MEDIUM,
                        title: 'components/TITLES.NOTICE',
                        message: 'ACTIONS.UNSAVED_CHANGES',
                        actionLabel: 'FORMS.ACTIONS.GO_BACK',
                        showCancelButton: true,
                        cancelLabel: 'FORMS.ACTIONS.DONT_SAVE',
                        invertSuccess: true
                    })
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

export const Primary: StoryObj<MessageDialogComponent> = {
    render: args => ({
        props: args
    }),
    args: {}
};

import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';
import { MessageType } from '../models/message-type.model';
import { NotificationSnackbarComponent } from './notification-snackbar.component';

const meta: Meta<NotificationSnackbarComponent> = {
    title: 'components/Notification Snackbar Component',
    component: NotificationSnackbarComponent,
    decorators: [
        moduleMetadata({
            providers: [{ provide: MatSnackBarRef, useValue: {} }]
        })
    ]
};
export default meta;

export const Success: StoryObj<NotificationSnackbarComponent> = {
    decorators: [
        moduleMetadata({
            providers: [
                {
                    provide: MAT_SNACK_BAR_DATA,
                    useValue: {
                        type: MessageType.success,
                        msgKey: 'FORMS.FEEDBACK.SAVE_SUCCESS'
                    }
                }
            ]
        })
    ],
    render: args => ({
        props: args
    }),
    args: {}
};

export const Warning: StoryObj<NotificationSnackbarComponent> = {
    decorators: [
        moduleMetadata({
            providers: [
                {
                    provide: MAT_SNACK_BAR_DATA,
                    useValue: {
                        type: MessageType.alert,
                        msgKey: 'Some changes have not been saved'
                    }
                }
            ]
        })
    ],
    render: args => ({
        props: args
    }),
    args: {}
};

export const Error: StoryObj<NotificationSnackbarComponent> = {
    decorators: [
        moduleMetadata({
            providers: [
                {
                    provide: MAT_SNACK_BAR_DATA,
                    useValue: {
                        type: MessageType.warn,
                        msgKey: 'The changes have not been saved'
                    }
                }
            ]
        })
    ],
    render: args => ({
        props: args
    }),
    args: {}
};

export const Info: StoryObj<NotificationSnackbarComponent> = {
    decorators: [
        moduleMetadata({
            providers: [
                {
                    provide: MAT_SNACK_BAR_DATA,
                    useValue: {
                        type: MessageType.info,
                        msgKey: 'Just an informative message :)'
                    }
                }
            ]
        })
    ],
    render: args => ({
        props: args
    }),
    args: {}
};

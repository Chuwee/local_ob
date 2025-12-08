import { Meta, StoryObj } from '@storybook/angular';
import { MessageType } from '../models/message-type.model';
import { ContextNotificationComponent } from './context-notification.component';

type StoryType = ContextNotificationComponent & { text?: string };

const meta: Meta<StoryType> = {
    title: 'components/Context Notification Component',
    component: ContextNotificationComponent,
    render: ({ contextType, text }) => ({
        template: `
            <div class="context-notification-container" fxFlex="100%" fxLayout="row" fxLayoutAlign="center start">
                <app-context-notification class="ob-context-notification" [contextType]="'${contextType}'"
                    fxFlex="80%" fxLayoutAlign="center start">
                    ${text}
                </app-context-notification>
            </div>
        `
    }),
    argTypes: {
        contextType: {
            control: 'select', options: Object.keys(MessageType)
        },
        text: {
            control: 'text'
        }
    },
    args: {
        contextType: 'info',
        text: 'Empty list'
    }
};
export default meta;

export const Info: StoryObj<StoryType> = {
    args: {
        contextType: 'info'
    }
};

export const Warn: StoryObj<StoryType> = {
    args: {
        contextType: 'warn'
    }
};

export const Alert: StoryObj<StoryType> = {
    args: {
        contextType: 'alert'
    }
};

export const Success: StoryObj<StoryType> = {
    args: {
        contextType: 'success'
    }
};

import { OverlayRef } from '@angular/cdk/overlay';
import { Meta, StoryObj, componentWrapperDecorator, moduleMetadata } from '@storybook/angular';
import { PreviewerComponent } from './previewer.component';

const meta: Meta<PreviewerComponent> = {
    title: 'components/PreviewerComponent',
    component: PreviewerComponent,
    decorators: [
        moduleMetadata({
            providers: [
                {
                    provide: OverlayRef,
                    useValue: {
                        detach: () => { /* NOOP */ },
                        dispose: () => { /* NOOP */ }
                    }
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

export const Primary: StoryObj<PreviewerComponent> = {
    render: args => ({
        props: args
    }),
    args: {}
};

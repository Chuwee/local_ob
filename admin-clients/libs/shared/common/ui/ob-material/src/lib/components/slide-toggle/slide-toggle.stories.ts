/* eslint-disable @typescript-eslint/explicit-function-return-type */
import { ThemePalette } from '@angular/material/core';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { moduleMetadata, Meta, StoryFn } from '@storybook/angular';

class SlideToggleStory {
    label = 'Slide Toggle';
    color: ThemePalette = 'primary';
    disabled: boolean = false;
    status: boolean = false;
}

function template(): string {
    return `
        <mat-slide-toggle class="ob-slide-toggle" 
            [color]="color" [disabled]="disabled" [checked]="status">
            {{label}}
        </mat-slide-toggle>
    `;
}

export default {
    title: 'OB Material/Slide Toggle',
    component: SlideToggleStory,
    decorators: [
        moduleMetadata({
            imports: [
                MatSlideToggleModule
            ]
        })
    ],
    argTypes: {
        color: {
            name: 'Color',
            description: 'Color of the slider - (Omitted)',
            control: {
                type: 'select',
                labels: { primary: 'Primary', accent: 'Accent', warn: 'Warning' }
            },
            options: ['primary', 'accent', 'warn'],
            defaultValue: 'primary'
        },
        label: {
            name: 'Label'
        },
        disabled: {
            name: 'Disabled',
            control: 'boolean'
        },
        status: {
            name: 'Status',
            control: 'boolean'
        }
    }
} as Meta;

const Template: StoryFn<SlideToggleStory> = (args: SlideToggleStory) => ({
    props: args,
    template: template()
});

export const Basic = Template.bind({});

Basic.args = new SlideToggleStory();

export const Snapshot = Template.bind({});

Snapshot.args = new SlideToggleStory();
Snapshot.args.status = true;


import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatCardModule } from '@angular/material/card';
import { TranslatePipe } from '@ngx-translate/core';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';

export type StoryType = {
    withFooter: boolean;
    withActions: boolean;
    contentNoPadding: boolean;
    smallPaddings: boolean;
};

const meta: Meta<StoryType> = {
    title: 'OB Material/Card Component',
    decorators: [
        moduleMetadata({
            providers: [],
            imports: [CommonModule, TranslatePipe, MatCardModule, FlexLayoutModule]
        })
    ]
};

export default meta;

const template = (args: StoryType): string => {
    const { withFooter, withActions, contentNoPadding, smallPaddings } = args;
    return `
    <mat-card class="ob-card ${contentNoPadding ? 'content-padding-none' : ''} ${smallPaddings ? 'small-paddings' : ''}">
        <mat-card-content>
            Mat Card Content
        </mat-card-content>
        ${withFooter
            ? `
            <mat-card-footer fxLayoutAlign="end center">
                <div class="right-actions">
                    <button>Button 1</button>
                    <button>Button 2</button>
                </div>
            </mat-card-footer>
            ` : ''
        }
        ${withActions
            ? `
            <mat-card-actions fxLayoutAlign="end center">
                <div class="right-actions">
                    <button>Button 1</button>
                    <button>Button 2</button>
                </div>
            </mat-card-actions>
            ` : ''
        }
    </mat-card>
    `;
};

export const Basic: StoryObj<StoryType> = {
    render: ({ ...args }) => ({
        props: args,
        template: template(args)
    }),
    argTypes: {
        withFooter: {
            name: 'withFooter',
            description: 'To specify if the card has footer or not',
            control: 'boolean'
        },
        withActions: {
            name: 'withActions',
            description: 'To specify if the card has actions or not',
            control: 'boolean'
        },
        contentNoPadding: {
            name: 'contentNoPadding',
            description: 'To specify that the mat card content has no padding',
            control: 'boolean'
        },
        smallPaddings: {
            name: 'smallPaddings',
            description: 'To specify that the mat card content and footer have a small padding both',
            control: 'boolean'
        }
    },
    args: {
        withFooter: false,
        withActions: false,
        contentNoPadding: false,
        smallPaddings: false
    }
};

export const Snapshot: StoryObj<StoryType> = {
    tags: ['snapshot'],
    render: ({ ...args }) => ({
        props: args,
        template: `
        <div fxFlex="100%" fxLayout="column" fxLayoutGap="100px">
            <div fxFlex="100%" fxLayout="row" fxLayoutAlign="start start" fxLayoutGap="64px">
                <div fxFlex="33%">
                    <h1>Without footer or actions</h1>
                    ${template({ withFooter: false, withActions: false, contentNoPadding: false, smallPaddings: false })}
                </div>
                <div fxFlex="33%">
                    <h1>Without footer or actions and without padding in content</h1>
                    ${template({ withFooter: false, withActions: false, contentNoPadding: true, smallPaddings: false })}
                </div>
                <div fxFlex="33%">
                    <h1>Without footer or actions and small paddings</h1>
                    ${template({ withFooter: false, withActions: false, contentNoPadding: false, smallPaddings: true })}
                </div>
            </div>
            <div fxFlex="100%" fxLayout="row" fxLayoutAlign="start start" fxLayoutGap="64px">
                <div fxFlex="33%">
                    <h1>With footer</h1>
                    ${template({ withFooter: true, withActions: false, contentNoPadding: false, smallPaddings: false })}
                </div>
                <div fxFlex="33%">
                    <h1>With footer and without padding in content</h1>
                    ${template({ withFooter: true, withActions: false, contentNoPadding: true, smallPaddings: false })}
                </div>
                <div fxFlex="33%">
                    <h1>With footer and small paddings</h1>
                    ${template({ withFooter: true, withActions: false, contentNoPadding: false, smallPaddings: true })}
                </div>
            </div>
            <div fxFlex="100%" fxLayout="row" fxLayoutAlign="start start" fxLayoutGap="64px">
                <div fxFlex="33%">
                    <h1>With actions</h1>
                    ${template({ withFooter: false, withActions: true, contentNoPadding: false, smallPaddings: false })}
                </div>
                <div fxFlex="33%">
                    <h1>With actions and without padding in content</h1>
                    ${template({ withFooter: false, withActions: true, contentNoPadding: true, smallPaddings: false })}
                </div>
                <div fxFlex="33%">
                    <h1>With actions and small paddings</h1>
                    ${template({ withFooter: false, withActions: true, contentNoPadding: false, smallPaddings: true })}
                </div>
            </div>
        </div>
        `
    })
};

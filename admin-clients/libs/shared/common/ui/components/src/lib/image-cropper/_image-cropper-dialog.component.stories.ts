import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Meta, StoryObj, componentWrapperDecorator, moduleMetadata } from '@storybook/angular';
import { ObMatDialogConfig } from '../message-dialog/models/message-dialog.model';
import { ImageCropperDialogComponent } from './image-cropper-dialog.component';

const meta: Meta<ImageCropperDialogComponent> = {
    title: 'components/ImageCropperDialogComponent',
    component: ImageCropperDialogComponent,
    decorators: [
        moduleMetadata({
            providers: [
                {
                    provide: MatDialogRef,
                    useValue: { addPanelClass: (_: string | string[]) => this }
                },
                {
                    provide: MAT_DIALOG_DATA,
                    useValue: new ObMatDialogConfig()
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

export const Primary: StoryObj<ImageCropperDialogComponent> = {
    render: args => ({
        props: args
    }),
    args: {}
};

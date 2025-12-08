import { Meta, StoryObj, componentWrapperDecorator } from '@storybook/angular';
import { ImageCropperComponent } from './image-cropper.component';

const meta: Meta<ImageCropperComponent> = {
    title: 'components/ImageCropperComponent',
    component: ImageCropperComponent,
    decorators: [
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            ${story}
        `)
    ]
};
export default meta;

export const Primary: StoryObj<ImageCropperComponent> = {
    render: args => ({
        props: args
    }),
    args: {
        disabled: false,
        cropBoxData: { width: 0, height: 0, top: 0, left: 0 },
        cropperOptions: {}
    }
};

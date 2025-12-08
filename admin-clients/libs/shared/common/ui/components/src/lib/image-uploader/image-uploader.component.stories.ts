import { FormControl, Validators } from '@angular/forms';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';
import { EphemeralMessageService } from '../ephemeral-message/ephemeral-message.service';
import { MessageDialogService } from '../message-dialog/message-dialog.service';
import { ImageUploaderComponent } from './image-uploader.component';

type StoryType = {
    disabled: boolean;
    hideLabelText: boolean;
    restrictions: object;
};

const meta: Meta<StoryType> = {
    title: 'components/Image Uploader Component',
    component: ImageUploaderComponent,
    decorators: [
        moduleMetadata({
            providers: [MessageDialogService, EphemeralMessageService]
        })
    ]
};

const obImageUrl = 'https://onebox-static-eu.s3.eu-west-1.amazonaws.com/pre/img/logo.png';

export default meta;

const template = ({ hideLabelText, restrictions }, ctrlName: string): string => `
    <div class="grow shrink basis-full">
        <div class="grow shrink basis-1/2">
            <app-image-uploader [imageRestrictions]="${JSON.stringify(restrictions).split('"').join('&quot;')}"
                [hideLabelText]="${hideLabelText}" [formControl]="${ctrlName}">
            </app-image-uploader>
        </div>
    </div>
`;

export const Basic: StoryObj<StoryType> = {
    render: args => ({
        props: {
            ...args,
            ctrlBasic: (() => {
                const ctrl = new FormControl(null);
                ctrl.markAsTouched();
                if (args.disabled) { ctrl.disable(); }
                return ctrl;
            })()
        },
        template: template(args, 'ctrlBasic')
    }),
    argTypes: {
        disabled: {
            name: 'disabled',
            description: 'Wether is in disabled state',
            control: 'boolean'
        },
        hideLabelText: {
            name: 'hideLabelText',
            description: 'Wether we want to hide the image footer or not',
            control: 'boolean'
        },
        restrictions: {
            name: 'imageRestrictions',
            description: 'Image size limitations; height in pixels, weight in pixels and size in bytes',
            control: 'object'
        }
    },
    args: {
        restrictions: { width: 200, height: 286, size: 100000 },
        disabled: false,
        hideLabelText: false
    }
};

export const Snapshot: StoryObj<StoryType> = {
    tags: ['snapshot'],
    render: () => ({
        props: {
            withImageEnabledCtrl: new FormControl(obImageUrl),
            withImageDisabledCtrl: new FormControl({ value: obImageUrl, disabled: true }),
            withoutImageEnabledCtrl: new FormControl(null),
            withoutImageDisabledCtrl: new FormControl({ value: null, disabled: true }),
            withoutImageCtrlRequired: new FormControl(null, Validators.required)
        },
        template: `
        <div class="grow shrink basis-full flex flex-row gap-[64px]">
            <div class="grow shrink basis-full flex flex-col gap-[16px]">
                <h1>With Image</h1>
                <h3>Default</h3>
                ${template({ hideLabelText: 'false', restrictions: { width: 300, height: 286, size: 100000 } }, 'withImageEnabledCtrl')}
                <h3>Disabled</h3>
                ${template({ hideLabelText: 'false', restrictions: { width: 300, height: 286, size: 100000 } }, 'withImageDisabledCtrl')}
                <h3>Without Label</h3>
                ${template({ hideLabelText: 'true', restrictions: { width: 300, height: 286, size: 100000 } }, 'withImageEnabledCtrl')}
            </div>
            <div class="grow shrink basis-full flex flex-col gap-[16px]">
                <h1>Without Image</h1>
                <h3>Default</h3>
                ${template({ hideLabelText: 'false', restrictions: { width: 300, height: 286, size: 100000 } }, 'withoutImageEnabledCtrl')}
                <h3>Disabled</h3>
                ${template({ hideLabelText: 'false', restrictions: { width: 300, height: 286, size: 100000 } }, 'withoutImageDisabledCtrl')}
                <h3>Without Label</h3>
                ${template({ hideLabelText: 'true', restrictions: { width: 300, height: 286, size: 100000 } }, 'withoutImageEnabledCtrl')}
                <h3>With Validator Required</h3>
                ${template({ hideLabelText: 'false', restrictions: { width: 300, height: 286, size: 100000 } }, 'withoutImageCtrlRequired')}
            </div>
        </div>
        `
    })
};
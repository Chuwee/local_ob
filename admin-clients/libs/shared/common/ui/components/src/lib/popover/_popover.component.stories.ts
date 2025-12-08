import { ActivatedRoute, ParamMap } from '@angular/router';
import { Meta, StoryObj, componentWrapperDecorator, moduleMetadata } from '@storybook/angular';
import { of } from 'rxjs';
import { ListFiltersService } from '../list-filters/list-filters.service';
import { PopoverComponent } from './popover.component';

const meta: Meta<PopoverComponent> = {
    title: 'components/PopoverComponent',
    component: PopoverComponent,
    decorators: [
        moduleMetadata({
            providers: [
                ListFiltersService,
                {
                    provide: ActivatedRoute,
                    useValue: {
                        queryParamMap: of({} as ParamMap)
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

export const Primary: StoryObj<PopoverComponent> = {
    render: args => ({
        props: args
    }),
    args: {
        contentNoPadding: false,
        buttonText: 'FORMS.OPEN_FILTER_BTN',
        buttonIconStart: '',
        buttonIconEnd: 'filter_list',
        removeButton: false,
        hideRemoveBtn: false,
        yOffset: ''
    }
};

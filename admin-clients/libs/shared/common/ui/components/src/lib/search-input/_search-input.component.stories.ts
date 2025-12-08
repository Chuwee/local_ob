import { Meta, StoryObj, componentWrapperDecorator } from '@storybook/angular';
import { of } from 'rxjs';
import { SearchInputComponent } from './search-input.component';

const meta: Meta<SearchInputComponent> = {
    title: 'components/SearchInputComponent',
    component: SearchInputComponent,
    decorators: [
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            ${story}
        `)
    ]
};
export default meta;

export const Primary: StoryObj<SearchInputComponent> = {
    render: args => ({
        props: args
    }),
    args: {
        initValue: '',
        placeholder: '',
        canChange$: of(true),
        searchOnInteraction: false
    }
};

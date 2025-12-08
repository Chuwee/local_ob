import { AUTHENTICATION_SERVICE } from '@admin-clients/shared/core/data-access';
import { Meta, StoryObj, componentWrapperDecorator, moduleMetadata } from '@storybook/angular';
import { of } from 'rxjs';
import { CurrencyInputComponent } from './currency-input.component';

const meta: Meta<CurrencyInputComponent> = {
    title: 'components/CurrencyInputComponent',
    component: CurrencyInputComponent,
    decorators: [
        moduleMetadata({
            providers: [
                {
                    provide: AUTHENTICATION_SERVICE,
                    useValue: {
                        getLoggedUser$: () => of({ currency: 'EUR' })
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

export const Primary: StoryObj<CurrencyInputComponent> = {
    render: args => ({
        props: args
    }),
    args: {
        placeholder: '',
        required: false,
        disabled: false
    }
};

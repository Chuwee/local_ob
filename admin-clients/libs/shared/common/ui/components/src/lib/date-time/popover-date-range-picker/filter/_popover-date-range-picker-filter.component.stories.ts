import { ActivatedRoute, ParamMap } from '@angular/router';
import { Meta, StoryObj, componentWrapperDecorator, moduleMetadata } from '@storybook/angular';
import { of } from 'rxjs';
import { ListFiltersService } from '../../../list-filters/list-filters.service';
import { PopoverFilterDirective } from '../../../popover/filter/popover-filter.directive';
import { DateRangePickerFilterDirective } from '../../date-range-picker/filter/date-range-picker-filter.directive';
import { DateTimeModule } from '../../date-time.module';
import { PopoverDateRangePickerFilterComponent } from './popover-date-range-picker-filter.component';

const meta: Meta<PopoverDateRangePickerFilterComponent> = {
    title: 'components/PopoverDateRangePickerFilterComponent',
    component: PopoverDateRangePickerFilterComponent,
    decorators: [
        moduleMetadata({
            imports: [
                PopoverFilterDirective,
                DateRangePickerFilterDirective,
                DateTimeModule
            ],
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

export const Primary: StoryObj<PopoverDateRangePickerFilterComponent> = {
    render: args => ({
        props: args
    }),
    args: {
        hideRemoveFiltersBtn: false
    }
};

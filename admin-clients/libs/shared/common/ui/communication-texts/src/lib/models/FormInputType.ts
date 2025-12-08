import { FormControl, FormGroup, FormRecord } from '@angular/forms';

export type FormInputType = FormGroup<{
    contents: FormRecord<FormGroup<{
        name: FormControl<string>;
        description?: FormControl<string>;
    }>>;
}>;

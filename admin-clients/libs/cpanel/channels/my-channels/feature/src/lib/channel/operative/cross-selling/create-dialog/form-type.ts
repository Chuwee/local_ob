import { FormControl, FormGroup } from '@angular/forms';

export type NewCrossSellingItemDialogFormType = FormGroup<{
    events: FormControl<{ id: number; name: string; saleRequestId: number }[]>;
    sessions: FormControl<{ id: number; name: string; startDate: string }[]>;
    allSessions: FormControl<boolean>;
}>;

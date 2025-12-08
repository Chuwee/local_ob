import { FormControl } from '@angular/forms';
import { MemberPeriods } from './members-options';

export type Surcharges = Partial<{ [key in MemberPeriods]: number }>;

export type SurchargesForm = Partial<{ [key in MemberPeriods]: FormControl<number> }>;

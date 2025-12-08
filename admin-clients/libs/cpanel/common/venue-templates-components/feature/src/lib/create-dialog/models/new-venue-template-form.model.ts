import { Space, VenueCity } from '@admin-clients/cpanel/venues/data-access';
import { Country, Entity } from '@admin-clients/shared/common/data-access';
import { VenueTemplateType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { FormControl, FormGroup } from '@angular/forms';
import { NewVenueTplType } from './new-venue-tpl-type.enum';
import { NewVenueViewType } from './new-venue-view-type.enum';

export interface VenueSelectionForm extends FormGroup<{
    entity: FormControl<Entity>;
    country: FormControl<Country>;
    city: FormControl<VenueCity>;
    keyword: FormControl<string>;
    selectedVenue: FormControl<number>;
}> { }

export interface TemplateSelectionForm extends FormGroup<{
    venueSpace: FormControl<Space>;
    tplType: FormControl<VenueTemplateType>;
    newVenueTplType: FormControl<NewVenueTplType>;
    keyword: FormControl<string>;
    selectedTpl: FormControl<number>;
    tplViewType: FormControl<NewVenueViewType>;
    newTplName: FormControl<string>;
    externalCapacity: FormControl<string>;
    inventory: FormControl<string>;
    externalInventory: FormControl<string>;
}> { }

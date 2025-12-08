import { ImageUploaderComponent } from '@admin-clients/shared/common/ui/components';
import { ImageRestrictions } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

const eventChannelImageRestrictions: ImageRestrictions = { width: 800, height: 800, size: 184320 };
const eventChannelUploadersForm: { control: string; class: string }[] = [
    { control: 'image1', class: 'col-span-2 row-span-2' },
    { control: 'image2', class: 'col-start-3' },
    { control: 'image3', class: 'col-start-4' },
    { control: 'image4', class: 'col-start-3 row-start-2' },
    { control: 'image5', class: 'col-start-4 row-start-2' }
];

@Component({
    selector: 'app-event-channel-image-uploader',
    templateUrl: './event-channel-image-uploader.component.html',
    styleUrls: ['./event-channel-image-uploader.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [ReactiveFormsModule, ImageUploaderComponent, TranslatePipe, MatIcon]
})

export class EventChannelImageUploaderComponent {
    readonly $form = input<FormGroup>(null, { alias: 'formGroup' });
    readonly sliderImageRestrictions = eventChannelImageRestrictions;
    readonly uploaders = eventChannelUploadersForm;
}

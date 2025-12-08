import { Validators } from '@angular/forms';
import { TrackingCredentialConfig } from './credentials/tracking-credentials.component';

export const GA_CONFIG: TrackingCredentialConfig = {
    fields: [
        {
            name: 'measurement_id',
            label: 'Measurement ID',
            placeholder: 'G-XXXXXXXXXX',
            type: 'text',
            required: true,
            validators: [
                Validators.pattern(/^G-[A-Z0-9]{10}$/)
            ]
        },
        {
            name: 'api_secret',
            label: 'Google Analytics API Key',
            placeholder: 'API Key',
            type: 'text',
            required: true,
            validators: [
                Validators.minLength(32)
            ]
        }
    ]
};

export const META_CONFIG: TrackingCredentialConfig = {
    fields: [
        {
            name: 'pixel_id',
            label: 'Pixel ID',
            placeholder: 'Pixel ID',
            type: 'text',
            required: true,
            validators: [
                Validators.pattern(/^\d+$/), // Only numbers
                Validators.minLength(10)
            ]
        },
        {
            name: 'api_secret',
            label: 'Meta API Key',
            placeholder: 'API Key',
            type: 'text',
            required: true,
            validators: [
                Validators.minLength(32)
            ]
        }
    ]
};

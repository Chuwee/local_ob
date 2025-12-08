/* eslint-disable @angular-eslint/no-output-native */
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, output, viewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ThemePalette } from '@angular/material/core';
import { MatRadioButton, MatRadioChange } from '@angular/material/radio';

@Component({
    imports: [CommonModule, MatRadioButton],
    selector: 'app-radio-image-button',
    templateUrl: './radio-image-button.component.html',
    styleUrls: ['./radio-image-button.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RadioImageButtonComponent {

    readonly imageUrl = input<string>();
    readonly imageAlt = input<string>();
    readonly large = input<boolean>(false);
    readonly control = input<FormControl<unknown>>();
    readonly disabled = input<boolean>(false);
    readonly checked = input<boolean>(false);
    readonly value = input<unknown>();
    readonly color = input<ThemePalette>();
    readonly change = output<MatRadioChange>();

    readonly btn = viewChild<MatRadioButton>('btn');

    onImageClick(): void {
        this.btn().checked = true;
        this.control()?.patchValue(this.value());
        this.control()?.markAsDirty();
    }
}

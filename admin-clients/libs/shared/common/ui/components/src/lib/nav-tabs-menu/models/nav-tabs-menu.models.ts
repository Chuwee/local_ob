import { TemplateRef } from '@angular/core';
import { Params } from '@angular/router';

export interface NavTabMenuElement {
    label?: string;
    id?: string;
    param?: string | string[];
    disabled?: boolean;
    hidden?: boolean;
    show?: boolean;
    dataTest?: string;
    templateRef?: TemplateRef<unknown>;
    tooltip?: {
        text: string;
        disabled?: boolean;
    };
    active?: boolean;
    queryParams?: Params | null | undefined;
    badge?: string;
    badgeClass?: string;
}

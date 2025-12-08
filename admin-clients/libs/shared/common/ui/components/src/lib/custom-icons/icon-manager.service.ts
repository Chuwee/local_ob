import { Injectable } from '@angular/core';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { IconDefinition } from './custom-icons';

@Injectable({
    providedIn: 'root'
})
export class IconManagerService {

    private readonly _addedIcons = {};

    constructor(private _matIconRegistry: MatIconRegistry, private _domSanitizer: DomSanitizer) {
    }

    addIconDefinition(...iconDefs: IconDefinition[]): void {
        iconDefs?.forEach(iconDef => {
            if (!iconDef) {
                console.error('Invalid IconDefinition supplied', iconDef);
            } else if (this._addedIcons[iconDef.key] === undefined) {
                this._addedIcons[iconDef.key] = true;
                this._matIconRegistry.addSvgIcon(
                    iconDef.key,
                    this._domSanitizer.bypassSecurityTrustResourceUrl(iconDef.path)
                );
            }
        });
    }
}

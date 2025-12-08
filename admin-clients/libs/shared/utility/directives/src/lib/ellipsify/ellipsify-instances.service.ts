import { Injectable } from '@angular/core';
import { debounceTime, fromEvent } from 'rxjs';
import { EllipsifyDirective } from './ellipsify.directive';

@Injectable({
    providedIn: 'root'
})
export class EllipsifyInstancesService {
    private _instances: EllipsifyDirective[] = [];

    constructor() {
        fromEvent(window, 'resize')
            .pipe(debounceTime(500))
            .subscribe(() => this.refreshInstances());
    }

    registerInstance(instance: EllipsifyDirective): void {
        this._instances.push(instance);
    }

    unregisterInstance(instance: EllipsifyDirective): void {
        this._instances.splice(this._instances.findIndex(i => i === instance));
    }

    refreshInstances(): void {
        this._instances.forEach(instance => instance.setDisableTooltip());
    }
}

import { Injectable, inject } from '@angular/core';
import { Storage } from '@ionic/storage';
import { Observable, from } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class DeviceStorage {
    private _storage = inject(Storage);

    constructor() {
        this.init();
    }

    async init(): Promise<void> {
        const storage = await this._storage.create();
        this._storage = storage;
    }

    setItem(key: string, value: unknown): void {
        this._storage?.set(key, value);
    }

    getItem(key: string): Observable<unknown> {
        return from(this._storage?.get(key));
    }

    //Clear all elements in storage BUT onboarding-executed
    clearStorage(): void {
        this._storage.keys().then(keys => keys.forEach(key => {
            if (key !== 'onboarding-executed') {
                this._storage.remove(key);
            }
        }));
    }
}

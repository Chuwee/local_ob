import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable()
export class ChannelCommunicationNotifierService {
    private _shouldRefreshData = new Subject<void>();

    getRefreshDataSignal$(): Observable<void> {
        return this._shouldRefreshData.asObservable();
    }

    sendRefreshDataSignal(): void {
        this._shouldRefreshData.next();
    }
}

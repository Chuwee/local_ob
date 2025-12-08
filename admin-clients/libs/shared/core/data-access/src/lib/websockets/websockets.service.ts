import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { inject, Injectable } from '@angular/core';
import { RxStomp, RxStompConfig, RxStompState } from '@stomp/rx-stomp';
import { delay, Observable, shareReplay } from 'rxjs';
import { filter, map, startWith } from 'rxjs/operators';
import { Topic } from './model/topics.enum';
import { WsError, WSErrors } from './model/ws-error.model';
import { WsMsg } from './model/ws-msg.model';
import { rxStompConfig } from './rx-stomp.config';

@Injectable({
    providedIn: 'root'
})
export class WebsocketsService extends RxStomp {
    private readonly MAX_RETRIES = 5;
    private readonly UNSUBSCRIPTION_DELAY = 60000;

    private readonly _env = inject(ENVIRONMENT_TOKEN);
    private readonly _connectionsMap = new Map<string, Observable<WsMsg>>();
    private readonly _pendingRemovableSubscriptions = new Set<string>();
    private _numRetries = this.MAX_RETRIES;

    constructor() {
        super();
        this.connectionState$
            .subscribe(state => {
                if (state === RxStompState.CONNECTING) {
                    this.logMessage('Connecting to WS Server... %c remaining attempts:', this._numRetries);
                } else if (state === RxStompState.CLOSED) {
                    this._numRetries--;
                    if (this._numRetries === 0) {
                        if (this.active) {
                            this.logMessage('Connection to WS Server failed');
                            this.deactivate();
                        }
                    }
                } else if (state === RxStompState.OPEN) {
                    // max retries resets with + 1 to compensate the decrement on closed state, this way makes that losing
                    // connection (close) after connection, restarts the connection counter, giving 5 more retries to connect, this happens
                    // on browser deactivation or
                    this._numRetries = this.MAX_RETRIES + 1;
                    this.logMessage('Connected to WS Server');
                }
            });
        // deactivates the connection on unauthorized errors, will be reactivated after reconfiguring
        this.getParsedStompErrors$()
            .pipe(filter(error => error?.code === WSErrors.unauthorizedError))
            .subscribe(() => this.deactivate());
    }

    override configure(token: string | RxStompConfig): void {
        if (token instanceof RxStompConfig) {
            super.configure(token);
        } else {
            const stompConfig: RxStompConfig = {
                ...rxStompConfig(this._env.wsHost),
                connectHeaders: {
                    // eslint-disable-next-line @typescript-eslint/naming-convention
                    Authorization: `Bearer ${token}`
                }
            };
            super.configure(stompConfig);
        }
    }

    setToken(token: string): void {
        if (token) {
            if (this.active) {
                this.deactivate().then(() => {
                    this.configure(token);
                    this.activate();
                });
            } else {
                this.configure(token);
                this.activate();
            }
        } else {
            this.deactivate();
        }
    }

    override activate(): void {
        if (!this.active) {
            this._numRetries = this.MAX_RETRIES;
            super.activate();
        }
    }

    getConnectionStatus$(): Observable<string> {
        return this.connectionState$.pipe(
            map(state => RxStompState[state]) // convert numeric RxStompState to string
        );
    }

    getParsedStompErrors$(): Observable<WsError> {
        return this.stompErrors$
            .pipe(
                map(error => ({
                    errorDescription: error.headers?.['message'],
                    ...JSON.parse(error.body?.length ? error.body : '{}')
                })),
                delay(0)
                // I like delays, remove it under your responsibility
            );
    }

    // The returned observables seems to be triggered outside angular zone, maybe we have to reimplement it to reenter to angular zone.
    // This generates a lot of pending template side effects that are not correctly rendered or processed.
    getMessages$<T extends WsMsg>(topic: Topic, id: number): Observable<T> {
        const connectionKey = this.getConnectionKey(topic, id);
        this._pendingRemovableSubscriptions.delete(connectionKey);
        if (!this._connectionsMap.has(connectionKey)) {
            const obs = this.watch(topic, { id: id.toString() })
                .pipe(
                    startWith(null),
                    map(message => message && JSON.parse(message?.body) as WsMsg),
                    shareReplay({ bufferSize: 1, refCount: true })
                );
            this._connectionsMap.set(connectionKey, obs);
        }
        return this._connectionsMap.get(connectionKey) as Observable<T>;
    }

    unsubscribeMessages(topic: Topic, id: number): void {
        const connectionKey = this.getConnectionKey(topic, id);
        if (this._connectionsMap.has(connectionKey) && !this._pendingRemovableSubscriptions.has(connectionKey)) {
            this._pendingRemovableSubscriptions.add(connectionKey);
            setTimeout(() => {
                if (this._pendingRemovableSubscriptions.has(connectionKey)) {
                    try {
                        this._pendingRemovableSubscriptions.delete(connectionKey);
                        this._connectionsMap.delete(connectionKey);
                        this.stompClient.unsubscribe(id.toString(), { destination: topic });
                    } catch (e: unknown) {
                        console.error(`StompClient ERROR trying to unsubscribe from topic ${connectionKey}: ${e}`);
                    }
                }
            }, this.UNSUBSCRIPTION_DELAY);
        }
    }

    private getConnectionKey(topic: string, id: number): string {
        return topic + '/' + id;
    }

    private logMessage(message: string, param?: unknown): void {
        const infoIcon = String.fromCodePoint(0x2139); // info emoji
        console.log(
            `%c${infoIcon}%c ${message}`,
            'color:white; background-color:DodgerBlue; padding:1px 6px; border-radius:50%',
            'color:DodgerBlue',
            '',
            param ?? ''
        );
    }
}

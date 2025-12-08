import { inject, Injectable } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class RoutingState {
    private _history: string[] = [];
    private readonly _router = inject(Router);

    loadRouting(): void {
        this._router.events
            .pipe(filter((event): event is NavigationEnd => (event instanceof NavigationEnd)))
            .subscribe(({ urlAfterRedirects }: NavigationEnd) => {
                // If we reach the same point in history, we go back to that point. No more loops!.
                for (let i = 0; i < this._history.length; i++) {
                    if (this._history[i] === urlAfterRedirects) {
                        this._history = this._history.slice(0, i);
                        break;
                    }
                }
                this._history.push(urlAfterRedirects);
            });
    }

    getHistory(): string[] {
        return this._history;
    }

    getPreviousUrl(defaultPath = '/', neverPath: string = null): string {
        if (neverPath) {
            for (let i = this._history.length - 2; i >= 0; i--) {
                if (!this._history[i].includes(neverPath)) {
                    return this._history[i].includes('login') ? defaultPath : this._history[i];
                }
            }
            return defaultPath;
        }
        const targetPath = this._history[this._history.length - 2];
        return targetPath && !targetPath.includes('login') ? targetPath : defaultPath;
    }

    getPreviousPath(defaultPath = '/', neverPath: string = null): string {
        return this.getPreviousUrl(defaultPath, neverPath).split('?')[0];
    }

    getPreviousQueryParams(defaultPath = '/', neverPath: string = null): Record<string, string> {
        const previousUrl = this.getPreviousUrl(defaultPath, neverPath);
        return this.queryStringToJSON(previousUrl.split('?')[1]);
    }

    removeLastUrlsWith(urlFragment: string): void {
        while (this._history.length && this._history[this._history.length - 1].includes(urlFragment)) {
            this._history.pop();
        }
    }

    private queryStringToJSON(input: string): Record<string, string> {
        const result: Record<string, string> = {};
        if (input) {
            input.split('&').forEach(pair => {
                const parts = pair.split('=');
                result[parts[0]] = decodeURIComponent(parts[1] || '');
            });
        }
        return result;
    }
}

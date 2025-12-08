import { APP_BASE_HREF, LocationStrategy, PathLocationStrategy, PlatformLocation } from '@angular/common';
import { Inject, Injectable, Optional, Provider } from '@angular/core';
import { UrlSerializer } from '@angular/router';

export const provideApplicationLocationStrategy = (): Provider[] => [{
    provide: LocationStrategy,
    useClass: AppLocationStrategy
}];

@Injectable()
export class AppLocationStrategy extends PathLocationStrategy {

    constructor(
        private _urlSerializer: UrlSerializer,
        private _platformLoc: PlatformLocation,
        @Optional() @Inject(APP_BASE_HREF) href?: string
    ) {
        super(_platformLoc, href);
    }

    override prepareExternalUrl(internal: string): string {
        const path = super.prepareExternalUrl(internal);
        const url = new URL(path);
        const params = new URLSearchParams(this._search);
        const urlTree = this._urlSerializer.parse(url.pathname + url.search);
        const nextQueryParams = urlTree.queryParams;
        urlTree.queryParams = {
            ...(params.has('branch') && { branch: params.get('branch') }),
            ...(params.has('sp') && { sp: params.get('sp') }),
            ...(params.has('fm') && { fm: params.get('fm') }),
            ...(params.has('hl') && { hl: params.get('hl') }),
            ...nextQueryParams
        };
        return url.origin + urlTree.toString();
    }

    private get _search(): string {
        return this._platformLoc?.search ?? '';
    }
}
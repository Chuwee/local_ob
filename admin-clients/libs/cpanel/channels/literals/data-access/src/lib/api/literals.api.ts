import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ChannelLiteralsTextContent } from '../models/post-literals.model';

@Injectable({
    providedIn: 'root'
})
export class LiteralsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly LITERALS_API = `${this.BASE_API}/mgmt-api/v1/apps`;

    constructor(private _http: HttpClient) { }

    getApplicationTextContents(appName: string, language: string): Observable<ChannelLiteralsTextContent[]> {
        return this._http.get<ChannelLiteralsTextContent[]>(`${this.LITERALS_API}/${appName}/text-contents/master-languages/${language}`);
    }

    postApplicationTextContents(appName: string, language: string, textContents: ChannelLiteralsTextContent[]): Observable<void> {
        return this._http.post<void>(`${this.LITERALS_API}/${appName}/text-contents/master-languages/${language}`, textContents);
    }
}

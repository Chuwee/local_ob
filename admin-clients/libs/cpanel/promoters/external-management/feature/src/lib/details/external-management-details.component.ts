import { ExternalManagementService, FlcIncompatibilitiesEngineData } from '@admin-clients/cpanel-promoters-external-management-data-access';
import { AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { first, Observable } from 'rxjs';

@Component({
    selector: 'app-external-management-details',
    imports: [
        TranslatePipe, FlexLayoutModule
    ],
    templateUrl: './external-management-details.component.html',
    styleUrls: ['./external-management-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExternalManagementDetailsComponent implements OnInit, AfterViewInit, OnDestroy {
    flcLoginData$: Observable<FlcIncompatibilitiesEngineData>;
    @ViewChild('incEngIframe') incEngIframe: ElementRef;

    constructor(private _externalManagementSrv: ExternalManagementService) { }

    ngOnInit(): void {
        this._externalManagementSrv.loadFlcIncompatibilitiesEngineData();
    }

    ngAfterViewInit(): void {
        this._externalManagementSrv.getFlcIncompatibilitiesEngineData$()
            .pipe(first(flcLoginData => !!flcLoginData))
            .subscribe(flcLoginData => {
                const incEngIframeElem = this.incEngIframe.nativeElement as HTMLIFrameElement;
                incEngIframeElem.src = flcLoginData.url;
                let counter = 0;
                const credentials = {
                    user: atob(flcLoginData.user),
                    pass: atob(flcLoginData.password)
                };
                incEngIframeElem.onload = () => {
                    incEngIframeElem.contentWindow.postMessage(credentials, '*');
                    if (counter === 1) {
                        incEngIframeElem.removeAttribute('hidden');
                    }
                    counter++;
                };
            });
    }

    ngOnDestroy(): void {
        this._externalManagementSrv.clearFlcIncompatibilitiesEngineData();
    }
}

import { Platform } from '@angular/cdk/platform';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    MAT_DIALOG_DATA,
    MatDialogRef
} from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';

const PLAY_STORE_URL = 'http://google.com';
const APPLE_STORE_URL = 'https://apps.apple.com/es/app/onebox-panel/id6467875616';
export type DownloadMobileAppDialogOutput = boolean;
export interface DownloadMobileAppDialogInput {
    hasRedirectQueryParam$: Observable<boolean>;
    redirectText: string;
}

@Component({
    selector: 'app-download-mobile-app-dialog',
    standalone: true,
    imports: [
        CommonModule,
        MaterialModule,
        TranslatePipe,
        FlexLayoutModule
    ],
    templateUrl: './download-app-dialog.component.html',
    styleUrls: ['./download-app-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DownloadMobileAppDialogComponent implements OnInit {
    private readonly _dialogRef = inject<MatDialogRef<DownloadMobileAppDialogComponent, DownloadMobileAppDialogOutput>>(MatDialogRef);
    private readonly _platform = inject(Platform);
    readonly matDialogData = inject<DownloadMobileAppDialogInput>(MAT_DIALOG_DATA);

    storeUrl: string;

    ngOnInit(): void {
        this.storeUrl = this._platform.ANDROID ? PLAY_STORE_URL : APPLE_STORE_URL;
        this._dialogRef.addPanelClass([DialogSize.MEDIUM, 'handset']);
    }

    close(): void {
        this._dialogRef.close();
    }

    goToDefaultRoute(): void {
        this._dialogRef.close(true);
    }
}

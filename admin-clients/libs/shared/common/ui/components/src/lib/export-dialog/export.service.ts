import { ExportDelivery, ExportDeliveryType, ExportField } from '@admin-clients/shared/data-access/models';
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class ExportService {

    constructor(private _translate: TranslateService) { }

    prepareField(fieldName: string, i18nKey: string): ExportField {
        return {
            field: fieldName,
            name: this._translate.instant(i18nKey)
        };
    }

    prepareDeliveryData(address: string): ExportDelivery {
        return {
            type: ExportDeliveryType.email,
            properties: {
                address
            }
        };
    }
}

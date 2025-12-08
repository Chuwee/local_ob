import { Entity } from '@admin-clients/shared/common/data-access';
import { ImportDateFormats } from './import-date-formats.enum';
import { ImportOptionsEnum } from './import-options.enum';
import { ProductTypeImport } from './product-type-import.enum';

export interface ImportOptions {
    [ImportOptionsEnum.entity]: Entity;
    [ImportOptionsEnum.isOverride]: boolean;
    [ImportOptionsEnum.isProductsImport]: boolean;
    [ImportOptionsEnum.productsType]: ProductTypeImport;
    [ImportOptionsEnum.productsWithVendor]: boolean;
    [ImportOptionsEnum.productsVendor]: string;
    [ImportOptionsEnum.dateFormat]: ImportDateFormats;
}

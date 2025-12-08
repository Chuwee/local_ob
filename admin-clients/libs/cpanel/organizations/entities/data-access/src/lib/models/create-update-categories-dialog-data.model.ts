export interface CreateOrUpdateCategoriesDialogData {
    action: CreateOrUpdateCategoriesDialogActions;
    entityId?: number;
    categoryId?: number;
}

export interface CreateOrUpdateCategoriesDialogReturnData {
    newCategoryId?: number;
    subcatgoryCreated?: boolean;
}

export enum CreateOrUpdateCategoriesDialogActions {
    add = 'add',
    update = 'update'
}

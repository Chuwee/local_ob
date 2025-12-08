export interface CreateOrUpdateB2bClientUserDialogData {
    action: CreateOrUpdateB2bClientUserDialogActions;
    b2bClientUserId?: number;
    b2bClientId: number;
}

export enum CreateOrUpdateB2bClientUserDialogActions {
    add = 'add',
    update = 'update'
}

export interface CreateOrUpdateB2bClientUserDialogReturnData {
    newB2bClientUserId?: number;
    actionPerformed: boolean;
}

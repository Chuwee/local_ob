export interface EditLabelGroupDialogData {
    templateId: number;
    isCreation: boolean;
    currentName?: string;
    currentCode?: string;
    title: string;
    id?: number;
}

export interface EditLabelGroupDialogResponse {
    saved: boolean;
    name?: string;
    id?: number;
}

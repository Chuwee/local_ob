
import { AlertButton } from '@ionic/angular';

export interface AlertType {
    key: string;
    isSelected: boolean;
    header: string;
    buttons: AlertButton[];
}

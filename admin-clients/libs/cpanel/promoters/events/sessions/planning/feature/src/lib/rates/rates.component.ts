import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { NgClass, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, Signal, signal } from '@angular/core';
import { FormControl } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

export type SessionRate = {
    id: number;
    name: string;
    default?: boolean;
    visible?: boolean;
};

@Component({
    selector: 'app-session-rates',
    templateUrl: './rates.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['./rates.component.css'],
    imports: [NgFor, MaterialModule, TranslatePipe, NgClass, NgIf]
})
export class SessionRatesComponent {

    private readonly _rates = signal<SessionRate[]>([]);

    @Input({
        transform: coerceBooleanProperty
    }) hideDefault: boolean = false;

    @Input() form: FormControl<SessionRate[]>;

    @Input()
    set rates(rates: SessionRate[]) {
        this._rates.set(rates);
        this.form.setValue(this.getVisibleRates(), {
            emitEvent: false
        });
    }

    get rates(): Signal<SessionRate[]> {
        return this._rates;
    }

    setDefault(rate: SessionRate): void {
        const rates = this._rates().map(elem => ({
            ...elem,
            default: rate.id === elem.id
        }));
        this.update(rates);
    }

    setVisible(rate: SessionRate): void {
        const rates = this._rates().map(elem => {
            if (elem.id === rate.id) {
                return {
                    ...rate,
                    visible: !rate.visible
                };
            } else {
                return elem;
            }
        });
        this.update(rates);
    }

    private update(rates: SessionRate[]): void {
        this._rates.set(rates);
        this.form.setValue(this.getVisibleRates(), {
            emitEvent: false
        });
        this.form.markAsDirty();
    }

    private getVisibleRates(): SessionRate[] {
        return this.rates()?.map(elem => ({
            ...elem,
            visible: elem.visible || elem.default
        })).filter(elem => !!elem.visible);
    }

}

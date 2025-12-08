import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    imports: [TranslatePipe],
    selector: 'app-wizard-bar',
    templateUrl: './wizard-bar.component.html',
    styleUrls: ['./wizard-bar.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class WizardBarComponent implements OnInit {
    @Output() stepClick = new EventEmitter<number>();
    @Input() enableNavigation = false;
    @Input() steps: string[];

    readonly #$active = signal(0);
    readonly $active = this.#$active.asReadonly();

    constructor() { }

    ngOnInit(): void {
        this.setActiveStep(0);
    }

    setActiveStep(index: number): boolean {
        if (this.steps?.[index]) {
            this.#$active.set(index);
            return true;
        }
        return false;
    }

    nextStep(): boolean {
        return this.setActiveStep(this.#$active() + 1);
    }

    previousStep(): boolean {
        return this.setActiveStep(this.#$active() - 1);
    }

    onStepClick(i: number): void {
        if (this.enableNavigation && i < this.#$active()) {
            this.stepClick.emit(i);
        }
    }
}

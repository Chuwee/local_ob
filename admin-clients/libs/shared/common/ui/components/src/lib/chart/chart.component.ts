/* eslint-disable @typescript-eslint/naming-convention */
import {
    AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, input, viewChild, OnChanges
} from '@angular/core';
import { Chart, ChartConfiguration } from 'chart.js/auto';

@Component({
    selector: 'ob-chart',
    host: {
        '[style.width]': '$width()',
        '[style.height]': '$height()'
    },
    templateUrl: './chart.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChartComponent implements AfterViewInit, OnChanges {
    private readonly _canvas = viewChild<ElementRef>('canvas');

    $config = input<ChartConfiguration>(null, { alias: 'config' });
    $width = input<string>(null, { alias: 'width' });
    $height = input<string>(null, { alias: 'height' });
    #chart: Chart;

    ngAfterViewInit(): void {
        this.createChart();
    }

    ngOnChanges(): void {
        this.createChart();
    }

    update(): void {
        this.#chart.update();
    }

    private createChart(): void {
        if (this.#chart) {
            this.#chart.destroy();
        }

        this.#chart = new Chart(this._canvas().nativeElement, this.$config());
    }
}

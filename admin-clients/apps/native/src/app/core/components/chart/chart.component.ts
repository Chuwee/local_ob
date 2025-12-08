import { ChartComponent } from '@admin-clients/shared/common/ui/components';
import { AfterViewInit, ChangeDetectionStrategy, Component, Input, inject, signal, OnChanges } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ChartConfiguration } from 'chart.js/auto';
import { ChartByDay } from './models/chart.model';

@Component({
    selector: 'chart',
    imports: [ChartComponent],
    templateUrl: './chart.component.html',
    styleUrls: ['./chart.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NativeChartComponent implements AfterViewInit, OnChanges {
    readonly #translateService = inject(TranslateService);
    readonly #originalData = [0, 0, 0, 0, 0, 0, 0];

    #backBarData: number[];
    #frontBarData: number[];
    #frontBackgrounds: string[];
    #colors: string[];
    #labels: string[];

    @Input() readonly chartData: ChartByDay[];

    readonly $config = signal<ChartConfiguration>(null);
    ngOnChanges(): void {
        this.createChart();
    }

    ngAfterViewInit(): void {
        this.createChart();
    }

    private createChart(): void {
        let highestValue = Math.max(
            ...this.chartData.map(chartByDay => chartByDay.value)
        );
        if (highestValue === 0) {
            highestValue = 1;
        }

        this.#backBarData = [...this.#originalData.fill(highestValue)];
        this.#frontBarData = [
            ...this.chartData.map(chartByDay =>
                chartByDay.value === 0 ? null : chartByDay.value
            )
        ];

        this.#colors = [
            ...this.chartData.map(chartByDay => chartByDay.isToday ? '#625AC5' : '#8D8D8F')
        ];

        this.#frontBackgrounds = [
            ...this.chartData.map(chartByDay => chartByDay.isToday ? '#625AC5' : '#3BA0A8')
        ];

        const daysOfWeekKeys = ([
            'WEEK-DAYS.DAY-MO',
            'WEEK-DAYS.DAY-TU',
            'WEEK-DAYS.DAY-WE',
            'WEEK-DAYS.DAY-TH',
            'WEEK-DAYS.DAY-FR',
            'WEEK-DAYS.DAY-SA',
            'WEEK-DAYS.DAY-SU'
        ]);

        const daysOfWeekKeysOrdered = this.chartData.map(data => data.indexOfWeek - 1).map(day => daysOfWeekKeys[day]);

        this.#translateService.get(daysOfWeekKeysOrdered).subscribe(translations => {
            this.#labels = daysOfWeekKeysOrdered.map(key => translations[key]);
        });

        this.$config.set({
            type: 'bar',
            data: {
                labels: this.#labels,
                datasets: [
                    {
                        data: this.#frontBarData,
                        barThickness: 20,
                        backgroundColor: this.#frontBackgrounds
                    },
                    {
                        data: this.#backBarData,
                        barThickness: 20,
                        backgroundColor: '#F3F3F3'
                    }
                ]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        enabled: false
                    }
                },
                elements: {
                    bar: {
                        borderRadius: 10,
                        borderSkipped: false,
                        borderWidth: 0
                    }
                },
                layout: {
                    padding: 0
                },
                scales: {
                    x: {
                        grid: {
                            display: false
                        },
                        border: {
                            display: false
                        },
                        stacked: true,
                        ticks: {
                            color: this.#colors,
                            font: {
                                size: 14,
                                family: 'Nunito Sans',
                                weight: 'normal'
                            }
                        }
                    },
                    y: {
                        grid: {
                            display: false
                        },
                        border: {
                            display: false
                        },
                        ticks: {
                            display: false
                        }
                    }
                }
            }
        });
    }
}

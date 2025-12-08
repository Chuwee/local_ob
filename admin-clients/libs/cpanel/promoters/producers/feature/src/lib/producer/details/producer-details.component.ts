import { ProducerDetails, ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import { GoBackComponent } from '@admin-clients/shared/common/ui/components';
import { NgIf, AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FlexModule } from '@angular/flex-layout/flex';
import { RouterOutlet } from '@angular/router';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-producer-details',
    templateUrl: './producer-details.component.html',
    styleUrls: ['./producer-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [NgIf, FlexModule, GoBackComponent, RouterOutlet, AsyncPipe]
})
export class ProducerDetailsComponent implements OnInit, OnDestroy {
    producer$: Observable<ProducerDetails>;

    constructor(
        private _producerService: ProducersService) { }

    ngOnInit(): void {
        this.producer$ = this._producerService.getProducer$();
    }

    ngOnDestroy(): void {
        this._producerService.clearProducer();
    }
}

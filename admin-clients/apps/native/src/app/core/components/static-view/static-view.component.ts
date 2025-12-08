import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { Router } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'static-view',
    imports: [CommonModule, IonicModule, TranslatePipe],
    templateUrl: './static-view.component.html',
    styleUrls: ['./static-view.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class StaticViewComponent implements OnInit {
    readonly #router = inject(Router);
    @Input() readonly viewContent = {
        title: '',
        description: '',
        isError: false,
        img: '',
        isSales: false
    };

    @Input() readonly errorCallback: () => void;
    @Output() readonly tap: EventEmitter<void> = new EventEmitter();

    ngOnInit(): void {
        this.viewContent.title = 'STATIC-VIEW.' + this.viewContent.title;

        if (this.viewContent.description) {
            this.viewContent.description = 'STATIC-VIEW.' + this.viewContent.description;
        }
        if (!this.viewContent.img) {
            this.viewContent.img = 'assets/media/img/box_picture.svg';
        }
    }

    reTry(): void {
        this.tap.emit();
    }

    goToEvent(): void {
        this.#router.navigate([`/tabs/events/`]);

    }

    goToSales(): void {
        this.#router.navigate([`/tabs/sales/`]);
    }
}

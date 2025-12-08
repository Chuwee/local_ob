import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, EventEmitter, inject, Input, OnInit, Output, ViewChild } from '@angular/core';
import { IonicModule, ModalController, NavController } from '@ionic/angular';
import { ModalComponent } from '../modal/modal.component';
import { BreadcrumbService } from './services/breadcrumbs.service';

@Component({
    selector: 'back-button',
    imports: [CommonModule, IonicModule, ModalComponent],
    templateUrl: './back-button.component.html',
    styleUrls: ['./back-button.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class BackButtonComponent implements OnInit {
    private _modalCtrl = inject(ModalController);
    private _navCtrl = inject(NavController);
    private _breadcrumbService = inject(BreadcrumbService);

    @ViewChild('textContainer', { read: ElementRef }) private readonly _textContainer: ElementRef;
    @ViewChild('text') private readonly _text: ElementRef;

    @Output() readonly tap: EventEmitter<void> = new EventEmitter();
    @Input() readonly viewTitle = '';
    @Input() readonly manualBackActivated = false;
    @Input() readonly isModal = false;
    @Input() readonly breadcrumbs = false;
    @Input() readonly isExpandable = false;
    @Input() readonly titleIcon? = '';

    modalIsOpen = false;
    listOfBreadcrumbs: string[];

    ngOnInit(): void {
        if (this.breadcrumbs) {
            this.listOfBreadcrumbs = this._breadcrumbService.getBreadcrumbs();
        }
    }

    onTap(): void {
        this.tap.emit();
        if (this.isModal) {
            this._modalCtrl.dismiss('cancel').then();
        } else if (!this.manualBackActivated) {
            this._navCtrl.back();
        }
    }

    closeModal(): void {
        this.modalIsOpen = false;
    }

    expandableHandler(): void {
        if (this.isExpandable) {
            if (this._text.nativeElement.offsetHeight > this._textContainer.nativeElement.offsetHeight + 3) {
                this.modalIsOpen = true;
            }
        }
    }
}

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TransactionDetailPage } from './transaction-detail.page';

describe('TicketDetailPage', () => {
    let component: TransactionDetailPage;
    let fixture: ComponentFixture<TransactionDetailPage>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [TransactionDetailPage]
        }).compileComponents();

        fixture = TestBed.createComponent(TransactionDetailPage);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

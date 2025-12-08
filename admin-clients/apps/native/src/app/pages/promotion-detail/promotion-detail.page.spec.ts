import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PromotionDetailPage } from './promotion-detail.page';

describe('TicketDetailPage', () => {
    let component: PromotionDetailPage;
    let fixture: ComponentFixture<PromotionDetailPage>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [PromotionDetailPage]
        }).compileComponents();

        fixture = TestBed.createComponent(PromotionDetailPage);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

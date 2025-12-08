import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WeeklySalesDetailPage } from './weekly-sales-detail.page';

describe('WeeklySalesDetailPage', () => {
    let component: WeeklySalesDetailPage;
    let fixture: ComponentFixture<WeeklySalesDetailPage>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [WeeklySalesDetailPage]
        }).compileComponents();

        fixture = TestBed.createComponent(WeeklySalesDetailPage);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

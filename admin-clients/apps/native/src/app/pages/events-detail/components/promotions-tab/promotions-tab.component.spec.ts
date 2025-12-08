import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PromotionsTabComponent } from './promotions-tab.component';

describe('PromotionsTabComponent', () => {
    let component: PromotionsTabComponent;
    let fixture: ComponentFixture<PromotionsTabComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [PromotionsTabComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(PromotionsTabComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SessionSecondaryMarketComponent } from './session-secondary-market.component';

describe('SessionSecondaryMarketComponent', () => {
    let component: SessionSecondaryMarketComponent;
    let fixture: ComponentFixture<SessionSecondaryMarketComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [SessionSecondaryMarketComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(
            SessionSecondaryMarketComponent
        );
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SeasonTicketsSecondaryMarketComponent } from './season-tickets-secondary-market.component';

describe('SeasonTicketsSecondaryMarketComponent', () => {
    let component: SeasonTicketsSecondaryMarketComponent;
    let fixture: ComponentFixture<SeasonTicketsSecondaryMarketComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [SeasonTicketsSecondaryMarketComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(
            SeasonTicketsSecondaryMarketComponent
        );
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TicketDetailCardComponent } from './ticket-detail-card.component';

describe('TicketCardComponent', () => {
    let component: TicketDetailCardComponent;
    let fixture: ComponentFixture<TicketDetailCardComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [TicketDetailCardComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(TicketDetailCardComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

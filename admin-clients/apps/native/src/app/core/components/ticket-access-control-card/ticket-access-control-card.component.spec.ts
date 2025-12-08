import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TicketAccessControlCardComponent } from './ticket-access-control-card.component';

describe('TicketCardComponent', () => {
    let component: TicketAccessControlCardComponent;
    let fixture: ComponentFixture<TicketAccessControlCardComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [TicketAccessControlCardComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(TicketAccessControlCardComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

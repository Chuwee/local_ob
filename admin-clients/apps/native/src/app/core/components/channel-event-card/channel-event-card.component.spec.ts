import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ChannelEventCardComponent } from './channel-event-card.component';

describe('ChannelEventCardComponent', () => {
    let component: ChannelEventCardComponent;
    let fixture: ComponentFixture<ChannelEventCardComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ChannelEventCardComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(ChannelEventCardComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

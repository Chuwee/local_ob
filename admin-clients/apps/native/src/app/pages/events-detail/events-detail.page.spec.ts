import { ComponentFixture, TestBed } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';
import { EventsDetailPage } from './events-detail.page';

describe('EventsDetailPage', () => {
    let component: EventsDetailPage;
    let fixture: ComponentFixture<EventsDetailPage>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [EventsDetailPage],
            imports: [IonicModule.forRoot()]
        }).compileComponents();

        fixture = TestBed.createComponent(EventsDetailPage);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

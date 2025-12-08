import { ComponentFixture, TestBed } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';
import { SalesPage } from './sales.page';

describe('EventsPage', () => {
    let component: SalesPage;
    let fixture: ComponentFixture<SalesPage>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [SalesPage],
            imports: [IonicModule.forRoot()]
        }).compileComponents();

        fixture = TestBed.createComponent(SalesPage);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

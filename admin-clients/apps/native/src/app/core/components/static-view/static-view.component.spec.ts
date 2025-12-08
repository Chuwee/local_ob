import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StaticViewComponent } from './static-view.component';

describe('StaticViewComponent', () => {
    let component: StaticViewComponent;
    let fixture: ComponentFixture<StaticViewComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [StaticViewComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(StaticViewComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

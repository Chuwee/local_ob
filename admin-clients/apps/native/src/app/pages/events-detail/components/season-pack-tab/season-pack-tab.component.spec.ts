import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SeasonPackTabComponent } from './season-pack-tab.component';

describe('SeasonPackTabComponent', () => {
    let component: SeasonPackTabComponent;
    let fixture: ComponentFixture<SeasonPackTabComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [SeasonPackTabComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(SeasonPackTabComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

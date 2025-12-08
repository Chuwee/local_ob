import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SessionsTabComponent } from './sessions-tab.component';

describe('SessionsTabComponent', () => {
    let component: SessionsTabComponent;
    let fixture: ComponentFixture<SessionsTabComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [SessionsTabComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(SessionsTabComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

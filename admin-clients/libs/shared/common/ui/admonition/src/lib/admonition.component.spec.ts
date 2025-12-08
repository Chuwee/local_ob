import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdmonitionComponent } from './admonition.component';

describe('AdmonitionComponent', () => {
    let component: AdmonitionComponent;
    let fixture: ComponentFixture<AdmonitionComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [AdmonitionComponent],
        }).compileComponents();

        fixture = TestBed.createComponent(AdmonitionComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

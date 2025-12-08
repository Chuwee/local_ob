import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CheckboxListTypeaheadComponent } from './checkbox-list-typeahead.component';

describe('CheckboxListTypeaheadComponent', () => {
    let component: CheckboxListTypeaheadComponent;
    let fixture: ComponentFixture<CheckboxListTypeaheadComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [CheckboxListTypeaheadComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(CheckboxListTypeaheadComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

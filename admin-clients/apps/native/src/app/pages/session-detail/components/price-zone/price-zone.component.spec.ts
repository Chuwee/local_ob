import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PriceZoneComponent } from './price-zone.component';

describe('PriceZoneComponent', () => {
    let component: PriceZoneComponent;
    let fixture: ComponentFixture<PriceZoneComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [PriceZoneComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(PriceZoneComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

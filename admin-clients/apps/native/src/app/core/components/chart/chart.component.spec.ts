import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NativeChartComponent } from './chart.component';

describe('NativeChartComponent', () => {
    let component: NativeChartComponent;
    let fixture: ComponentFixture<NativeChartComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [NativeChartComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(NativeChartComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

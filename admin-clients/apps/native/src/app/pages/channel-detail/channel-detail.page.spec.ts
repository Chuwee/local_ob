import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';
import { ChannelDetailPage } from './channel-detail.page';

describe('ChannelDetailPage', () => {
    let component: ChannelDetailPage;
    let fixture: ComponentFixture<ChannelDetailPage>;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [ChannelDetailPage],
            imports: [IonicModule.forRoot()]
        }).compileComponents();

        fixture = TestBed.createComponent(ChannelDetailPage);
        component = fixture.componentInstance;
        fixture.detectChanges();
    }));

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});

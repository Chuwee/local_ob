import { NgModule } from '@angular/core';
import { IsActivityEventPipe } from './activity-event.pipe';
import { IsAvetEventPipe } from './avet-event.pipe';
import { IsSessionPackEventPipe } from './session-pack-event.pipe';
import { IsSgaEventPipe } from './sga-event.pipe';

@NgModule({
    imports: [
        IsAvetEventPipe,
        IsSessionPackEventPipe,
        IsActivityEventPipe,
        IsSgaEventPipe
    ],
    exports: [
        IsAvetEventPipe,
        IsActivityEventPipe,
        IsSessionPackEventPipe,
        IsSgaEventPipe
    ]
})
export class EventPipesModule { }

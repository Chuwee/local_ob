import { NgModule } from '@angular/core';
import { IsWebB2bPipe } from './channel-b2b-type.pipe';
import { IsBoxOfficePipe } from './channel-box-office.pipe';
import { IsExternalWhitelabelPipe } from './channel-external-type.pipe';
import { IsMembersChannelPipe } from './channel-members-type.pipe';
import { IsWebChannelVouchersPipe } from './channel-vouchers-web-type.pipe';
import { IsWebChannelPipe } from './channel-web-type.pipe';
import { IsWebV4$Pipe } from './channel-V4.pipe';

@NgModule({
    imports: [
        IsMembersChannelPipe,
        IsExternalWhitelabelPipe,
        IsBoxOfficePipe,
        IsWebChannelVouchersPipe,
        IsWebChannelPipe,
        IsWebB2bPipe,
        IsWebV4$Pipe
    ],
    exports: [
        IsMembersChannelPipe,
        IsExternalWhitelabelPipe,
        IsBoxOfficePipe,
        IsWebChannelVouchersPipe,
        IsWebChannelPipe,
        IsWebB2bPipe,
        IsWebV4$Pipe
    ]
})
export class ChannelsPipesModule {
}

import { Pipe, PipeTransform } from '@angular/core';
import { ChannelGatewaySteps } from './channel-gateway-steps.model';

@Pipe({
    standalone: true,
    name: 'channelGatewayNextTextPipe'
})
export class ChannelGatewayNextTextPipe implements PipeTransform {
    transform(steps: ChannelGatewaySteps, activeStep: number): string {
        return steps[activeStep].nextText;
    }
}

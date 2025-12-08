import { Pipe, PipeTransform } from '@angular/core';
import { ChannelGatewaySteps } from './channel-gateway-steps.model';

@Pipe({
    standalone: true,
    name: 'channelGatewayMapSteps'
})
export class ChannelGatewayMapStepsPipe implements PipeTransform {
    transform(steps: ChannelGatewaySteps): string[] {
        return steps.map(step => step.title);
    }
}

import { Provider } from '@angular/core';
import { OperatorsService } from '../index';
import { OperatorsApi } from './api/operators.api';
import { OperatorsState } from './state/operators.state';

export const operatorsProviders: Provider[] = [
    OperatorsState, OperatorsApi, OperatorsService
];

export const provideOperatorsState = (): Provider => operatorsProviders;
import { ErrorHandler, Injectable } from '@angular/core';

@Injectable()
export class GlobalErrorsHandler implements ErrorHandler {
    private readonly _chunkFailedMessage = /Loading chunk [\d]+ failed/;
    handleError(error: Error): void {
        if (this._chunkFailedMessage.test(error.message)) {
            console.warn('ChunkLoadError detected, forced reload');
            window.location.reload();
        }
        console.error(error);
    }
}

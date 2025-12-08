import { Platform } from '@angular/cdk/platform';

export function biSupersetSubmit(loginUrl: string, reportUrl: string, token: string, platform: Platform): void {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = loginUrl;
    if (!((platform.IOS && platform.SAFARI) || platform.FIREFOX)) {
        form.target = '_blank';
    }
    const tokenInput = document.createElement('input');
    tokenInput.type = 'hidden';
    tokenInput.name = 'token';
    tokenInput.value = token;
    form.appendChild(tokenInput);
    const reportUrlInput = document.createElement('input');
    reportUrlInput.type = 'hidden';
    reportUrlInput.name = 'next';
    reportUrlInput.value = reportUrl;
    form.appendChild(reportUrlInput);
    // Append the form to the body and submit it
    document.body.appendChild(form);
    console.log(form);
    form.submit();
    // Remove the form after submission
    form.remove();
}
export type BiSupersetSubmit = typeof biSupersetSubmit;
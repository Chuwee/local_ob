import { Platform } from '@angular/cdk/platform';

export function biSubmit(url: string, load: string, logout: string, platform: Platform): void {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = load;
    if (!((platform.IOS && platform.SAFARI) || platform.FIREFOX)) {
        form.target = '_blank';
    }
    const inputLogin = document.createElement('input');
    inputLogin.type = 'hidden';
    inputLogin.name = 'login';
    inputLogin.value = url;
    form.appendChild(inputLogin);
    const inputLogout = document.createElement('input');
    inputLogout.type = 'hidden';
    inputLogout.name = 'logout';
    inputLogout.value = logout;
    form.appendChild(inputLogout);
    // Append the form to the body and submit it
    document.body.appendChild(form);
    form.submit();
    // Remove the form after submission
    form.remove();
}
export type BiSubmit = typeof biSubmit;
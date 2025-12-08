import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';

@Component({
    imports: [TranslatePipe, MatButtonModule, MatTabsModule],
    selector: 'app-language-bar',
    templateUrl: './language-bar.component.html',
    styleUrls: ['./language-bar.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class LanguageBarComponent {

    @Input()
    languages: string[];

    @Input()
    selected: string;

    @Input()
    noPadding?: boolean;

    @Input()
    changeGuard: () => Observable<boolean>;

    @Output()
    changed = new EventEmitter<string>();

    select(language: string): void {
        if (this.changeGuard) {
            this.changeGuard().subscribe(ok => {
                if (ok) {
                    this.selectLanguage(language);
                }
            });
        } else {
            this.selectLanguage(language);
        }
    }

    private selectLanguage(language: string): void {
        this.selected = language;
        if (this.changed) {
            this.changed.emit(language);
        }
    }
}

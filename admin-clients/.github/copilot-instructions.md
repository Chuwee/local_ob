# Copilot Instructions for Angular 19 Repository

## Architecture & Framework Guidelines

### Angular 19 Standalone Components
- **ALWAYS** create standalone components, avoiding `standalone: false`
- **NEVER** create or use NgModules for new code
- **DEPRECATE** existing NgModules gradually in favor of standalone components

### Component Structure
```typescript
@Component({
  selector: 'app-example',
  imports: [CommonModule, ReactiveFormsModule, /* other standalone imports */],
  templateUrl: './example.component.html',
  styleUrls: ['./example.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExampleComponent {
  // Always use OnPush change detection
}
```

## Angular Material Guidelines

### Standalone Material Imports (PREFERRED)
- **AVOID** importing from `@admin-clients/shared/common/ui/ob-material` (deprecated)
- **USE** individual Angular Material standalone imports:

```typescript
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatInput } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  imports: [MatButton, MatIconButton, MatInput, MatFormFieldModule],
  // ...
})
```

## Layout & Styling Guidelines

### Tailwind CSS (PREFERRED for Layout)
- **REPLACE** FlexLayoutModule with Tailwind classes
- **ONLY** use Tailwind for flex and grid layouts (as configured in tailwind.config.js)
- **AVOID** using `fxLayout`, `fxFlex`, `fxLayoutGap` - replace with Tailwind equivalents:

```html
<!-- OLD (FlexLayoutModule) -->
<div fxLayout="row" fxLayoutGap="16px" fxLayoutAlign="start center">

<!-- NEW (Tailwind) -->
<div class="flex flex-row gap-4 items-center justify-start">
```

### CSS Best Practices
- **USE** CSS custom properties (variables) instead of SCSS variables
- **MINIMIZE** CSS selector specificity to keep bundle sizes small
- **PREFER** utility classes over custom CSS when possible

```scss
/* PREFERRED: CSS Custom Properties */
:host {
  --primary-color: #1976d2;
  --spacing-unit: 8px;
}

/* AVOID: SCSS Variables */
$primary-color: #1976d2;
$spacing-unit: 8px;
```

## Reactivity & State Management

### Signals (PREFERRED)
- **USE** Angular Signals for simple reactivity and state management
- **PREFER** signals over traditional observables for component state

```typescript
export class ExampleComponent {
  // PREFERRED: Using signals
  count = signal(0);
  name = signal('');
  
  // Computed signals for derived state
  displayName = computed(() => this.name() || 'Anonymous');
  
  // Effect for side effects
  constructor() {
    effect(() => {
      console.log('Count changed:', this.count());
    });
  }
  
  increment() {
    this.count.update(value => value + 1);
  }
}
```

## Internationalization (i18n)

### Translation Pipe (PREFERRED)
- **AVOID** importing `TranslateModule` in components
- **USE** `TranslatePipe` directly in standalone components

```typescript
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  imports: [TranslatePipe],
  template: `
    <h1>{{ 'common.welcome' | translate }}</h1>
    <p>{{ 'messages.hello' | translate: { name: userName } }}</p>
  `
})
```

## Component Organization

### File Structure
- **SEPARATE** large components into smaller, focused components
- **COMPONENTIZE** complex templates and logic
- **EXTRACT** reusable logic into services or utilities
- **LIMIT** component files to ~200 lines (template + component combined)

### Component Responsibilities
- **SINGLE** responsibility per component
- **EXTRACT** business logic into services
- **USE** smart/dumb component pattern

```typescript
// Smart Component (Container)
@Component({
  selector: 'app-user-list-container',
  template: `
    <app-user-list 
      [users]="users()" 
      [loading]="loading()"
      (userSelected)="onUserSelected($event)">
    </app-user-list>
  `
})
export class UserListContainerComponent {
  users = signal<User[]>([]);
  loading = signal(false);
  
  constructor(private userService: UserService) {}
}

// Dumb Component (Presentation)
@Component({
  selector: 'app-user-list',
  template: `<!-- Pure presentation logic -->`
})
export class UserListComponent {
  @Input() users: User[] = [];
  @Input() loading = false;
  @Output() userSelected = new EventEmitter<User>();
}
```

## Performance Optimizations

### Change Detection
- **ALWAYS** use `OnPush` change detection strategy
- **USE** signals to trigger change detection automatically
- **AVOID** heavy computations in templates

### Bundle Optimization
- **USE** lazy loading for feature modules
- **AVOID** importing entire libraries when only specific functions are needed
- **TREE-SHAKE** unused code by using specific imports

## Code Quality Guidelines

### TypeScript Best Practices
- **USE** strict TypeScript configuration
- **PREFER** `readonly` arrays and objects when data shouldn't be mutated
- **USE** proper typing instead of `any`

### Angular Best Practices
- **USE** reactive forms over template-driven forms
- **IMPLEMENT** proper error handling
- **USE** Angular's built-in directives when available
- **PREFER** Angular control flow syntax over structural directives

### Control Flow Syntax (PREFERRED)
- **USE** `@if`, `@for`, `@switch` instead of `*ngIf`, `*ngFor`, `ngSwitch`
- **BETTER** performance and type safety with new control flow

```html
<!-- PREFERRED: New Control Flow -->
@if (user()) {
  <div>Welcome, {{ user().name }}!</div>
} @else {
  <div>Please log in</div>
}

@for (item of items(); track item.id) {
  <div>{{ item.name }}</div>
} @empty {
  <div>No items found</div>
}

@switch (status()) {
  @case ('loading') {
    <div>Loading...</div>
  }
  @case ('error') {
    <div>Error occurred</div>
  }
  @default {
    <div>Content loaded</div>
  }
}

<!-- AVOID: Old Structural Directives -->
<div *ngIf="user(); else loginTemplate">
  Welcome, {{ user().name }}!
</div>
<ng-template #loginTemplate>
  <div>Please log in</div>
</ng-template>

<div *ngFor="let item of items(); trackBy: trackByFn">
  {{ item.name }}
</div>

<div [ngSwitch]="status()">
  <div *ngSwitchCase="'loading'">Loading...</div>
  <div *ngSwitchCase="'error'">Error occurred</div>
  <div *ngSwitchDefault>Content loaded</div>
</div>
```

### ESLint Rules Compliance
- **FOLLOW** the existing ESLint configuration
- **AVOID** importing deprecated packages (FlexLayoutModule shows warnings)
- **USE** proper component and directive selectors with approved prefixes: `app`, `ob`, `cpanel`, `shipanel`

## Migration Guidelines

### From NgModules to Standalone
1. Convert components to standalone first
2. Remove module declarations
3. Add direct imports to component
4. Update routing configuration

### From FlexLayoutModule to Tailwind
```typescript
// Before
import { FlexLayoutModule } from '@angular/flex-layout';

// After - Remove import, use Tailwind classes in template
```

### From MaterialModule to Standalone
```typescript
// Before
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';

// After
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
```

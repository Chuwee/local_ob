import { ValidatorFn, Validators } from '@angular/forms';

// eslint-disable-next-line max-len
const URL_PATTERN = '^(https?:\\/\\/)?([a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\\.)+([a-zA-Z]{2,})(\\.[a-zA-Z]{2,})?(\\/\\S*)?$';

export function urlValidator(): ValidatorFn {
    return Validators.pattern(URL_PATTERN);
}

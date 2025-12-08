import { ImageRestrictions } from '@admin-clients/shared/data-access/models';

export const entityLogosImageRestrictions: Record<string, ImageRestrictions> = {
    logo: { width: 152, height: 42, size: 25600 },
    tiny: { width: 22, height: 24, size: 25600 },
    favicon: { width: 16, height: 16, size: 25600 },
    reports: { width: 200, height: 50, size: 25600 }
};

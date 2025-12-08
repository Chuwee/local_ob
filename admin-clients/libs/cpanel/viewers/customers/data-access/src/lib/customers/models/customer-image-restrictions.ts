import { ImageRestrictions } from '@admin-clients/shared/data-access/models';

export const customerImageRestrictions: Record<string, ImageRestrictions> = {
    profilePicture: { width: 180, height: 180, size: 2097152 }
};

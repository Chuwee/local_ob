export enum PromotionStatus {
    /**
     * Promotion is active
     * (could be active and not applicable)
     */
    active = 'ACTIVE',

    /**
     * Promotion is disabled & cannot be applied
     */
    inactive = 'INACTIVE',

    /**
     * Virtual state (not from backend)
     *
     * Active but not applicable anymore
     */
    expired = 'EXPIRED',

    /**
     * Virtual state (not from backend)
     *
     * Active but not applicable yet
     */
    upcoming = 'UPCOMING'
}

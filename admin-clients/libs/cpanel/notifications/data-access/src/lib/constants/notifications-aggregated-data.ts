import { AggregationMetrics } from '@admin-clients/shared/data-access/models';

export const aggDataNotifications: AggregationMetrics = {
    totalSent: {
        addMetrics: ['total_emails_sent'],
        isCurrency: false,
        headerKey: 'NOTIFICATIONS.EMAIL_NOTIFICATION.TOTAL_NOTIFICATIONS_SENT'
    },
    totalNotifications: {
        addMetrics: ['total_notifications_sent'],
        isCurrency: false,
        headerKey: 'NOTIFICATIONS.EMAIL_NOTIFICATION.TOTAL_RECIPIENTS'
    }
};

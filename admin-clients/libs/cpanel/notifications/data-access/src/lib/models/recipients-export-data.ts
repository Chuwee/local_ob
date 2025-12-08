import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataRecipients: FieldDataGroup[] = [
    {
        fieldKey: 'NOTIFICATIONS.EMAIL_NOTIFICATION.EXPORT.RECIPIENTS_DATA.TITLE',
        field: 'recipients_data',
        isDefault: true,
        fields: [
            {
                field: 'name',
                fieldKey: 'NOTIFICATIONS.EMAIL_NOTIFICATION.EXPORT.RECIPIENTS_DATA.RECIPIENT_NAME',
                isDefault: true
            },
            {
                field: 'surname',
                fieldKey: 'NOTIFICATIONS.EMAIL_NOTIFICATION.EXPORT.RECIPIENTS_DATA.RECIPIENT_SURNAME',
                isDefault: true
            },
            {
                field: 'email',
                fieldKey: 'NOTIFICATIONS.EMAIL_NOTIFICATION.EXPORT.RECIPIENTS_DATA.RECIPIENT_EMAIL',
                isDefault: true
            },
            {
                field: 'code',
                fieldKey: 'NOTIFICATIONS.EMAIL_NOTIFICATION.EXPORT.RECIPIENTS_DATA.CODE',
                isDefault: true
            },
            {
                field: 'channel.name',
                fieldKey: 'NOTIFICATIONS.EMAIL_NOTIFICATION.EXPORT.RECIPIENTS_DATA.CHANNEL',
                isDefault: true
            },
            {
                field: 'channel.id',
                fieldKey: 'NOTIFICATIONS.EMAIL_NOTIFICATION.EXPORT.RECIPIENTS_DATA.CHANNEL_ID',
                isDefault: true
            },
            {
                field: 'status',
                fieldKey: 'NOTIFICATIONS.EMAIL_NOTIFICATION.EXPORT.RECIPIENTS_DATA.STATUS',
                isDefault: true
            }
        ]
    }
];


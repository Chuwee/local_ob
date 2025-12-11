# API Management Models

This directory contains TypeScript models propagated from the admin-clients codebase for use in API management.

## Channels

The `channels` directory contains ALL channel-related models that define the structure and types used for managing channels across the platform. This is a complete propagation of all channel models from `admin-clients/libs/cpanel/channels/data-access/src/lib/models/`.

### Key Files:
- `channel.model.ts` - Core Channel interface with all properties
- `channel-type.model.ts` - Enum defining different channel types (WEB, BOX_OFFICE, etc.)
- `channel-status.model.ts` - Enum for channel status (ACTIVE, BLOCKED, etc.)
- `channel-build.model.ts` - Enum for channel build types
- `common-types.ts` - Common TypeScript types and interfaces (IdName, Currency, PageableFilter, etc.)
- Plus 70+ additional model files covering all channel functionality

### Model Categories:
- **Core Models**: channel.model.ts, channel-list-element.model.ts
- **Channel Configuration**: channel-booking-settings.model.ts, channel-delivery-settings.model.ts, etc.
- **Channel Types & Status**: channel-type.model.ts, channel-status.model.ts, channel-build.model.ts
- **Forms & Rules**: channel-forms.model.ts, channel-forms-rules.model.ts
- **Gateway & Payment**: channel-gateway.model.ts, channel-gateway-config.model.ts
- **Surcharges & Commissions**: channel-surcharge.model.ts, channel-commission.model.ts
- **Events & Sessions**: channel-event.model.ts, channel-session.model.ts
- **Request/Response Models**: get-channels-request.model.ts, get-channels-response.model.ts, etc.
- **Email Configuration**: email-server-conf.model.ts, notification-email-template.model.ts

### Usage:
```typescript
import { Channel, ChannelType, ChannelStatus } from './models/channels';

const myChannel: Channel = {
  id: 1,
  name: 'My Channel',
  type: ChannelType.web,
  status: ChannelStatus.active
};
```

## Maintenance

These models are a complete copy from `admin-clients/libs/cpanel/channels/data-access/src/lib/models/`.

**Important Notes:**
- All `@admin-clients` imports have been replaced with local imports from `common-types.ts`
- The models are framework-agnostic and can be used in any TypeScript project
- When updating, ensure compatibility with the source models in admin-clients


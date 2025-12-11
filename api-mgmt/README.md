# API Management Models

This directory contains TypeScript models propagated from the admin-clients codebase for use in API management.

## Channels

The `channels` directory contains core channel models that define the structure and types used for managing channels across the platform.

### Files:
- `channel.model.ts` - Core Channel interface with all properties
- `channel-type.model.ts` - Enum defining different channel types (WEB, BOX_OFFICE, etc.)
- `channel-status.model.ts` - Enum for channel status (ACTIVE, BLOCKED, etc.)
- `channel-build.model.ts` - Enum for channel build types
- `invitations-options.ts` - Settings for channel invitations
- `whitelabel-type.model.ts` - Type for whitelabel channels

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

These models are derived from `admin-clients/libs/cpanel/channels/data-access/src/lib/models/`.
When updating, ensure compatibility with the source models.

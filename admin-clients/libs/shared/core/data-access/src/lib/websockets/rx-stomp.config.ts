import { RxStompConfig } from '@stomp/rx-stomp';

export const rxStompConfig: (host: string) => RxStompConfig = host => ({
    brokerURL: `${host}/stream-api`,
    // How often to heartbeat?
    heartbeatIncoming: 0,       // Typical value 0 - disabled
    heartbeatOutgoing: 20000,   // Typical value 20000 - every 20 seconds
    connectionTimeout: 30000,
    // Wait in milliseconds before attempting auto reconnect
    reconnectDelay: 5000 // Typical value 2000 - 2 seconds
    //debug: (log) => console.log(log) //
});

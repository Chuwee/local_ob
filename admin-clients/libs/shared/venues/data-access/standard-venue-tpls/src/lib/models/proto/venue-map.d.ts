/* eslint-disable */
import * as $protobuf from "protobufjs";
/** Namespace es. */
export namespace es {

    /** Namespace onebox. */
    namespace onebox {

        /** Namespace venue. */
        namespace venue {

            /** Namespace venuetemplates. */
            namespace venuetemplates {

                /** Properties of a VenueMap. */
                interface IVenueMap {

                    /** VenueMap sectorMap */
                    sectorMap?: (es.onebox.venue.venuetemplates.ISectorMap[]|null);
                }

                /** Represents a VenueMap. */
                class VenueMap implements IVenueMap {

                    /**
                     * Constructs a new VenueMap.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: es.onebox.venue.venuetemplates.IVenueMap);

                    /** VenueMap sectorMap. */
                    public sectorMap: es.onebox.venue.venuetemplates.ISectorMap[];

                    /**
                     * Creates a new VenueMap instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns VenueMap instance
                     */
                    public static create(properties?: es.onebox.venue.venuetemplates.IVenueMap): es.onebox.venue.venuetemplates.VenueMap;

                    /**
                     * Encodes the specified VenueMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.VenueMap.verify|verify} messages.
                     * @param message VenueMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: es.onebox.venue.venuetemplates.IVenueMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified VenueMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.VenueMap.verify|verify} messages.
                     * @param message VenueMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: es.onebox.venue.venuetemplates.IVenueMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a VenueMap message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns VenueMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): es.onebox.venue.venuetemplates.VenueMap;

                    /**
                     * Decodes a VenueMap message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns VenueMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): es.onebox.venue.venuetemplates.VenueMap;

                    /**
                     * Verifies a VenueMap message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a VenueMap message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns VenueMap
                     */
                    public static fromObject(object: { [k: string]: any }): es.onebox.venue.venuetemplates.VenueMap;

                    /**
                     * Creates a plain object from a VenueMap message. Also converts values to other types if specified.
                     * @param message VenueMap
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: es.onebox.venue.venuetemplates.VenueMap, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this VenueMap to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };
                }

                /** Properties of a SectorMap. */
                interface ISectorMap {

                    /** SectorMap id */
                    id?: (number|null);

                    /** SectorMap venuetemplate */
                    venuetemplate?: (number|null);

                    /** SectorMap code */
                    code?: (string|null);

                    /** SectorMap description */
                    description?: (string|null);

                    /** SectorMap type */
                    type?: (number|null);

                    /** SectorMap default */
                    "default"?: (boolean|null);

                    /** SectorMap order */
                    order?: (number|null);

                    /** SectorMap saveSequence */
                    saveSequence?: (number|Long|null);

                    /** SectorMap rowMap */
                    rowMap?: (es.onebox.venue.venuetemplates.IRowMap[]|null);

                    /** SectorMap notNumberedZoneMap */
                    notNumberedZoneMap?: (es.onebox.venue.venuetemplates.INotNumberedZoneMap[]|null);
                }

                /** Represents a SectorMap. */
                class SectorMap implements ISectorMap {

                    /**
                     * Constructs a new SectorMap.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: es.onebox.venue.venuetemplates.ISectorMap);

                    /** SectorMap id. */
                    public id: number;

                    /** SectorMap venuetemplate. */
                    public venuetemplate: number;

                    /** SectorMap code. */
                    public code: string;

                    /** SectorMap description. */
                    public description: string;

                    /** SectorMap type. */
                    public type: number;

                    /** SectorMap default. */
                    public default: boolean;

                    /** SectorMap order. */
                    public order: number;

                    /** SectorMap saveSequence. */
                    public saveSequence: (number|Long);

                    /** SectorMap rowMap. */
                    public rowMap: es.onebox.venue.venuetemplates.IRowMap[];

                    /** SectorMap notNumberedZoneMap. */
                    public notNumberedZoneMap: es.onebox.venue.venuetemplates.INotNumberedZoneMap[];

                    /**
                     * Creates a new SectorMap instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns SectorMap instance
                     */
                    public static create(properties?: es.onebox.venue.venuetemplates.ISectorMap): es.onebox.venue.venuetemplates.SectorMap;

                    /**
                     * Encodes the specified SectorMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.SectorMap.verify|verify} messages.
                     * @param message SectorMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: es.onebox.venue.venuetemplates.ISectorMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified SectorMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.SectorMap.verify|verify} messages.
                     * @param message SectorMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: es.onebox.venue.venuetemplates.ISectorMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a SectorMap message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns SectorMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): es.onebox.venue.venuetemplates.SectorMap;

                    /**
                     * Decodes a SectorMap message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns SectorMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): es.onebox.venue.venuetemplates.SectorMap;

                    /**
                     * Verifies a SectorMap message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a SectorMap message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns SectorMap
                     */
                    public static fromObject(object: { [k: string]: any }): es.onebox.venue.venuetemplates.SectorMap;

                    /**
                     * Creates a plain object from a SectorMap message. Also converts values to other types if specified.
                     * @param message SectorMap
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: es.onebox.venue.venuetemplates.SectorMap, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this SectorMap to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };
                }

                /** Properties of a RowMap. */
                interface IRowMap {

                    /** RowMap id */
                    id?: (number|null);

                    /** RowMap name */
                    name?: (string|null);

                    /** RowMap sectorId */
                    sectorId?: (number|null);

                    /** RowMap order */
                    order?: (number|null);

                    /** RowMap saveSequence */
                    saveSequence?: (number|Long|null);

                    /** RowMap seatMap */
                    seatMap?: (es.onebox.venue.venuetemplates.ISeatMap[]|null);
                }

                /** Represents a RowMap. */
                class RowMap implements IRowMap {

                    /**
                     * Constructs a new RowMap.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: es.onebox.venue.venuetemplates.IRowMap);

                    /** RowMap id. */
                    public id: number;

                    /** RowMap name. */
                    public name: string;

                    /** RowMap sectorId. */
                    public sectorId: number;

                    /** RowMap order. */
                    public order: number;

                    /** RowMap saveSequence. */
                    public saveSequence: (number|Long);

                    /** RowMap seatMap. */
                    public seatMap: es.onebox.venue.venuetemplates.ISeatMap[];

                    /**
                     * Creates a new RowMap instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns RowMap instance
                     */
                    public static create(properties?: es.onebox.venue.venuetemplates.IRowMap): es.onebox.venue.venuetemplates.RowMap;

                    /**
                     * Encodes the specified RowMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.RowMap.verify|verify} messages.
                     * @param message RowMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: es.onebox.venue.venuetemplates.IRowMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified RowMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.RowMap.verify|verify} messages.
                     * @param message RowMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: es.onebox.venue.venuetemplates.IRowMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a RowMap message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns RowMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): es.onebox.venue.venuetemplates.RowMap;

                    /**
                     * Decodes a RowMap message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns RowMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): es.onebox.venue.venuetemplates.RowMap;

                    /**
                     * Verifies a RowMap message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a RowMap message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns RowMap
                     */
                    public static fromObject(object: { [k: string]: any }): es.onebox.venue.venuetemplates.RowMap;

                    /**
                     * Creates a plain object from a RowMap message. Also converts values to other types if specified.
                     * @param message RowMap
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: es.onebox.venue.venuetemplates.RowMap, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this RowMap to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };
                }

                /** Properties of an Enums. */
                interface IEnums {
                }

                /** Represents an Enums. */
                class Enums implements IEnums {

                    /**
                     * Constructs a new Enums.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: es.onebox.venue.venuetemplates.IEnums);

                    /**
                     * Creates a new Enums instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns Enums instance
                     */
                    public static create(properties?: es.onebox.venue.venuetemplates.IEnums): es.onebox.venue.venuetemplates.Enums;

                    /**
                     * Encodes the specified Enums message. Does not implicitly {@link es.onebox.venue.venuetemplates.Enums.verify|verify} messages.
                     * @param message Enums message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: es.onebox.venue.venuetemplates.IEnums, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified Enums message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.Enums.verify|verify} messages.
                     * @param message Enums message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: es.onebox.venue.venuetemplates.IEnums, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes an Enums message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns Enums
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): es.onebox.venue.venuetemplates.Enums;

                    /**
                     * Decodes an Enums message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns Enums
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): es.onebox.venue.venuetemplates.Enums;

                    /**
                     * Verifies an Enums message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates an Enums message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns Enums
                     */
                    public static fromObject(object: { [k: string]: any }): es.onebox.venue.venuetemplates.Enums;

                    /**
                     * Creates a plain object from an Enums message. Also converts values to other types if specified.
                     * @param message Enums
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: es.onebox.venue.venuetemplates.Enums, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this Enums to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };
                }

                namespace Enums {

                    /** SeatStatus enum. */
                    enum SeatStatus {
                        UNKNOWN = 0,
                        FREE = 1,
                        SOLD = 2,
                        PROMOTOR_LOCKED = 3,
                        SYSTEM_LOCKED = 4,
                        BOOKED = 5,
                        KILL = 6,
                        EMITTED = 7,
                        VALIDATED = 8,
                        IN_REFUND = 9,
                        CANCELLED = 10,
                        PRESOLD_LOCKED = 11,
                        SOLD_LOCKED = 12,
                        GIFT = 13,
                        SEASON_LOCKED = 14,
                        EXTERNAL_LOCKED = 15,
                        EXTERNAL_DELETE = 16
                    }

                    /** Visibility enum. */
                    enum Visibility {
                        VIS_UNKNOWN = 0,
                        FULL = 1,
                        PARTIAL = 2,
                        NONE = 3,
                        SIDE = 4
                    }

                    /** Accessibility enum. */
                    enum Accessibility {
                        ACC_UNKNOWN = 0,
                        NORMAL = 1,
                        REDUCED_MOBILITY = 2
                    }
                }

                /** Properties of a SeatMap. */
                interface ISeatMap {

                    /** SeatMap id */
                    id?: (number|Long|null);

                    /** SeatMap name */
                    name?: (string|null);

                    /** SeatMap status */
                    status?: (es.onebox.venue.venuetemplates.Enums.SeatStatus|null);

                    /** SeatMap view */
                    view?: (number|null);

                    /** SeatMap rowBlock */
                    rowBlock?: (string|null);

                    /** SeatMap quota */
                    quota?: (number|null);

                    /** SeatMap priceType */
                    priceType?: (number|null);

                    /** SeatMap blockingReason */
                    blockingReason?: (number|null);

                    /** SeatMap visibility */
                    visibility?: (es.onebox.venue.venuetemplates.Enums.Visibility|null);

                    /** SeatMap accessibility */
                    accessibility?: (es.onebox.venue.venuetemplates.Enums.Accessibility|null);

                    /** SeatMap gate */
                    gate?: (number|Long|null);

                    /** SeatMap dynamicTag1 */
                    dynamicTag1?: (number|Long|null);

                    /** SeatMap dynamicTag2 */
                    dynamicTag2?: (number|Long|null);

                    /** SeatMap external */
                    external?: (number|Long|null);

                    /** SeatMap order */
                    order?: (number|null);

                    /** SeatMap weight */
                    weight?: (number|null);

                    /** SeatMap posX */
                    posX?: (number|null);

                    /** SeatMap posY */
                    posY?: (number|null);

                    /** SeatMap saveSequence */
                    saveSequence?: (number|Long|null);

                    /** SeatMap ticketId */
                    ticketId?: (number|Long|null);

                    /** SeatMap sessionPack */
                    sessionPack?: (number|Long|null);

                    /** SeatMap linkable */
                    linkable?: (boolean|null);

                    /** SeatMap linked */
                    linked?: (boolean|null);
                }

                /** Represents a SeatMap. */
                class SeatMap implements ISeatMap {

                    /**
                     * Constructs a new SeatMap.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: es.onebox.venue.venuetemplates.ISeatMap);

                    /** SeatMap id. */
                    public id: (number|Long);

                    /** SeatMap name. */
                    public name: string;

                    /** SeatMap status. */
                    public status: es.onebox.venue.venuetemplates.Enums.SeatStatus;

                    /** SeatMap view. */
                    public view: number;

                    /** SeatMap rowBlock. */
                    public rowBlock: string;

                    /** SeatMap quota. */
                    public quota: number;

                    /** SeatMap priceType. */
                    public priceType: number;

                    /** SeatMap blockingReason. */
                    public blockingReason: number;

                    /** SeatMap visibility. */
                    public visibility: es.onebox.venue.venuetemplates.Enums.Visibility;

                    /** SeatMap accessibility. */
                    public accessibility: es.onebox.venue.venuetemplates.Enums.Accessibility;

                    /** SeatMap gate. */
                    public gate: (number|Long);

                    /** SeatMap dynamicTag1. */
                    public dynamicTag1: (number|Long);

                    /** SeatMap dynamicTag2. */
                    public dynamicTag2: (number|Long);

                    /** SeatMap external. */
                    public external: (number|Long);

                    /** SeatMap order. */
                    public order: number;

                    /** SeatMap weight. */
                    public weight: number;

                    /** SeatMap posX. */
                    public posX: number;

                    /** SeatMap posY. */
                    public posY: number;

                    /** SeatMap saveSequence. */
                    public saveSequence: (number|Long);

                    /** SeatMap ticketId. */
                    public ticketId: (number|Long);

                    /** SeatMap sessionPack. */
                    public sessionPack: (number|Long);

                    /** SeatMap linkable. */
                    public linkable: boolean;

                    /** SeatMap linked. */
                    public linked: boolean;

                    /**
                     * Creates a new SeatMap instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns SeatMap instance
                     */
                    public static create(properties?: es.onebox.venue.venuetemplates.ISeatMap): es.onebox.venue.venuetemplates.SeatMap;

                    /**
                     * Encodes the specified SeatMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.SeatMap.verify|verify} messages.
                     * @param message SeatMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: es.onebox.venue.venuetemplates.ISeatMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified SeatMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.SeatMap.verify|verify} messages.
                     * @param message SeatMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: es.onebox.venue.venuetemplates.ISeatMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a SeatMap message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns SeatMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): es.onebox.venue.venuetemplates.SeatMap;

                    /**
                     * Decodes a SeatMap message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns SeatMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): es.onebox.venue.venuetemplates.SeatMap;

                    /**
                     * Verifies a SeatMap message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a SeatMap message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns SeatMap
                     */
                    public static fromObject(object: { [k: string]: any }): es.onebox.venue.venuetemplates.SeatMap;

                    /**
                     * Creates a plain object from a SeatMap message. Also converts values to other types if specified.
                     * @param message SeatMap
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: es.onebox.venue.venuetemplates.SeatMap, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this SeatMap to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };
                }

                /** Properties of a NotNumberedZoneMap. */
                interface INotNumberedZoneMap {

                    /** NotNumberedZoneMap id */
                    id?: (number|Long|null);

                    /** NotNumberedZoneMap name */
                    name?: (string|null);

                    /** NotNumberedZoneMap sector */
                    sector?: (number|null);

                    /** NotNumberedZoneMap priceType */
                    priceType?: (number|null);

                    /** NotNumberedZoneMap view */
                    view?: (number|null);

                    /** NotNumberedZoneMap visibility */
                    visibility?: (es.onebox.venue.venuetemplates.Enums.Visibility|null);

                    /** NotNumberedZoneMap accessibility */
                    accessibility?: (es.onebox.venue.venuetemplates.Enums.Accessibility|null);

                    /** NotNumberedZoneMap gate */
                    gate?: (number|Long|null);

                    /** NotNumberedZoneMap dynamicTag1 */
                    dynamicTag1?: (number|Long|null);

                    /** NotNumberedZoneMap dynamicTag2 */
                    dynamicTag2?: (number|Long|null);

                    /** NotNumberedZoneMap capacity */
                    capacity?: (number|null);

                    /** NotNumberedZoneMap oldCapacity */
                    oldCapacity?: (number|null);

                    /** NotNumberedZoneMap saveSequence */
                    saveSequence?: (number|Long|null);

                    /** NotNumberedZoneMap linkableSeats */
                    linkableSeats?: (number|null);

                    /** NotNumberedZoneMap statusCounters */
                    statusCounters?: (es.onebox.venue.venuetemplates.IStatusCounterMap[]|null);

                    /** NotNumberedZoneMap blockingReasonCounters */
                    blockingReasonCounters?: (es.onebox.venue.venuetemplates.IBlockingReasonsCounterMap[]|null);

                    /** NotNumberedZoneMap sessionPackCounters */
                    sessionPackCounters?: (es.onebox.venue.venuetemplates.ISessionPackCounterMap[]|null);

                    /** NotNumberedZoneMap quotaCounters */
                    quotaCounters?: (es.onebox.venue.venuetemplates.IQuotaCountersMap[]|null);
                }

                /** Represents a NotNumberedZoneMap. */
                class NotNumberedZoneMap implements INotNumberedZoneMap {

                    /**
                     * Constructs a new NotNumberedZoneMap.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: es.onebox.venue.venuetemplates.INotNumberedZoneMap);

                    /** NotNumberedZoneMap id. */
                    public id: (number|Long);

                    /** NotNumberedZoneMap name. */
                    public name: string;

                    /** NotNumberedZoneMap sector. */
                    public sector: number;

                    /** NotNumberedZoneMap priceType. */
                    public priceType: number;

                    /** NotNumberedZoneMap view. */
                    public view: number;

                    /** NotNumberedZoneMap visibility. */
                    public visibility: es.onebox.venue.venuetemplates.Enums.Visibility;

                    /** NotNumberedZoneMap accessibility. */
                    public accessibility: es.onebox.venue.venuetemplates.Enums.Accessibility;

                    /** NotNumberedZoneMap gate. */
                    public gate: (number|Long);

                    /** NotNumberedZoneMap dynamicTag1. */
                    public dynamicTag1: (number|Long);

                    /** NotNumberedZoneMap dynamicTag2. */
                    public dynamicTag2: (number|Long);

                    /** NotNumberedZoneMap capacity. */
                    public capacity: number;

                    /** NotNumberedZoneMap oldCapacity. */
                    public oldCapacity: number;

                    /** NotNumberedZoneMap saveSequence. */
                    public saveSequence: (number|Long);

                    /** NotNumberedZoneMap linkableSeats. */
                    public linkableSeats: number;

                    /** NotNumberedZoneMap statusCounters. */
                    public statusCounters: es.onebox.venue.venuetemplates.IStatusCounterMap[];

                    /** NotNumberedZoneMap blockingReasonCounters. */
                    public blockingReasonCounters: es.onebox.venue.venuetemplates.IBlockingReasonsCounterMap[];

                    /** NotNumberedZoneMap sessionPackCounters. */
                    public sessionPackCounters: es.onebox.venue.venuetemplates.ISessionPackCounterMap[];

                    /** NotNumberedZoneMap quotaCounters. */
                    public quotaCounters: es.onebox.venue.venuetemplates.IQuotaCountersMap[];

                    /**
                     * Creates a new NotNumberedZoneMap instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns NotNumberedZoneMap instance
                     */
                    public static create(properties?: es.onebox.venue.venuetemplates.INotNumberedZoneMap): es.onebox.venue.venuetemplates.NotNumberedZoneMap;

                    /**
                     * Encodes the specified NotNumberedZoneMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.NotNumberedZoneMap.verify|verify} messages.
                     * @param message NotNumberedZoneMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: es.onebox.venue.venuetemplates.INotNumberedZoneMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified NotNumberedZoneMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.NotNumberedZoneMap.verify|verify} messages.
                     * @param message NotNumberedZoneMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: es.onebox.venue.venuetemplates.INotNumberedZoneMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a NotNumberedZoneMap message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns NotNumberedZoneMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): es.onebox.venue.venuetemplates.NotNumberedZoneMap;

                    /**
                     * Decodes a NotNumberedZoneMap message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns NotNumberedZoneMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): es.onebox.venue.venuetemplates.NotNumberedZoneMap;

                    /**
                     * Verifies a NotNumberedZoneMap message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a NotNumberedZoneMap message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns NotNumberedZoneMap
                     */
                    public static fromObject(object: { [k: string]: any }): es.onebox.venue.venuetemplates.NotNumberedZoneMap;

                    /**
                     * Creates a plain object from a NotNumberedZoneMap message. Also converts values to other types if specified.
                     * @param message NotNumberedZoneMap
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: es.onebox.venue.venuetemplates.NotNumberedZoneMap, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this NotNumberedZoneMap to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };
                }

                /** Properties of a StatusCounterMap. */
                interface IStatusCounterMap {

                    /** StatusCounterMap status */
                    status?: (es.onebox.venue.venuetemplates.Enums.SeatStatus|null);

                    /** StatusCounterMap count */
                    count?: (number|null);

                    /** StatusCounterMap linked */
                    linked?: (boolean|null);
                }

                /** Represents a StatusCounterMap. */
                class StatusCounterMap implements IStatusCounterMap {

                    /**
                     * Constructs a new StatusCounterMap.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: es.onebox.venue.venuetemplates.IStatusCounterMap);

                    /** StatusCounterMap status. */
                    public status: es.onebox.venue.venuetemplates.Enums.SeatStatus;

                    /** StatusCounterMap count. */
                    public count: number;

                    /** StatusCounterMap linked. */
                    public linked: boolean;

                    /**
                     * Creates a new StatusCounterMap instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns StatusCounterMap instance
                     */
                    public static create(properties?: es.onebox.venue.venuetemplates.IStatusCounterMap): es.onebox.venue.venuetemplates.StatusCounterMap;

                    /**
                     * Encodes the specified StatusCounterMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.StatusCounterMap.verify|verify} messages.
                     * @param message StatusCounterMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: es.onebox.venue.venuetemplates.IStatusCounterMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified StatusCounterMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.StatusCounterMap.verify|verify} messages.
                     * @param message StatusCounterMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: es.onebox.venue.venuetemplates.IStatusCounterMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a StatusCounterMap message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns StatusCounterMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): es.onebox.venue.venuetemplates.StatusCounterMap;

                    /**
                     * Decodes a StatusCounterMap message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns StatusCounterMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): es.onebox.venue.venuetemplates.StatusCounterMap;

                    /**
                     * Verifies a StatusCounterMap message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a StatusCounterMap message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns StatusCounterMap
                     */
                    public static fromObject(object: { [k: string]: any }): es.onebox.venue.venuetemplates.StatusCounterMap;

                    /**
                     * Creates a plain object from a StatusCounterMap message. Also converts values to other types if specified.
                     * @param message StatusCounterMap
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: es.onebox.venue.venuetemplates.StatusCounterMap, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this StatusCounterMap to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };
                }

                /** Properties of a BlockingReasonsCounterMap. */
                interface IBlockingReasonsCounterMap {

                    /** BlockingReasonsCounterMap blockingReason */
                    blockingReason?: (number|null);

                    /** BlockingReasonsCounterMap count */
                    count?: (number|null);
                }

                /** Represents a BlockingReasonsCounterMap. */
                class BlockingReasonsCounterMap implements IBlockingReasonsCounterMap {

                    /**
                     * Constructs a new BlockingReasonsCounterMap.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: es.onebox.venue.venuetemplates.IBlockingReasonsCounterMap);

                    /** BlockingReasonsCounterMap blockingReason. */
                    public blockingReason: number;

                    /** BlockingReasonsCounterMap count. */
                    public count: number;

                    /**
                     * Creates a new BlockingReasonsCounterMap instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns BlockingReasonsCounterMap instance
                     */
                    public static create(properties?: es.onebox.venue.venuetemplates.IBlockingReasonsCounterMap): es.onebox.venue.venuetemplates.BlockingReasonsCounterMap;

                    /**
                     * Encodes the specified BlockingReasonsCounterMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.BlockingReasonsCounterMap.verify|verify} messages.
                     * @param message BlockingReasonsCounterMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: es.onebox.venue.venuetemplates.IBlockingReasonsCounterMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified BlockingReasonsCounterMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.BlockingReasonsCounterMap.verify|verify} messages.
                     * @param message BlockingReasonsCounterMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: es.onebox.venue.venuetemplates.IBlockingReasonsCounterMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a BlockingReasonsCounterMap message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns BlockingReasonsCounterMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): es.onebox.venue.venuetemplates.BlockingReasonsCounterMap;

                    /**
                     * Decodes a BlockingReasonsCounterMap message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns BlockingReasonsCounterMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): es.onebox.venue.venuetemplates.BlockingReasonsCounterMap;

                    /**
                     * Verifies a BlockingReasonsCounterMap message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a BlockingReasonsCounterMap message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns BlockingReasonsCounterMap
                     */
                    public static fromObject(object: { [k: string]: any }): es.onebox.venue.venuetemplates.BlockingReasonsCounterMap;

                    /**
                     * Creates a plain object from a BlockingReasonsCounterMap message. Also converts values to other types if specified.
                     * @param message BlockingReasonsCounterMap
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: es.onebox.venue.venuetemplates.BlockingReasonsCounterMap, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this BlockingReasonsCounterMap to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };
                }

                /** Properties of a SessionPackCounterMap. */
                interface ISessionPackCounterMap {

                    /** SessionPackCounterMap sessionPack */
                    sessionPack?: (number|Long|null);

                    /** SessionPackCounterMap count */
                    count?: (number|null);
                }

                /** Represents a SessionPackCounterMap. */
                class SessionPackCounterMap implements ISessionPackCounterMap {

                    /**
                     * Constructs a new SessionPackCounterMap.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: es.onebox.venue.venuetemplates.ISessionPackCounterMap);

                    /** SessionPackCounterMap sessionPack. */
                    public sessionPack: (number|Long);

                    /** SessionPackCounterMap count. */
                    public count: number;

                    /**
                     * Creates a new SessionPackCounterMap instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns SessionPackCounterMap instance
                     */
                    public static create(properties?: es.onebox.venue.venuetemplates.ISessionPackCounterMap): es.onebox.venue.venuetemplates.SessionPackCounterMap;

                    /**
                     * Encodes the specified SessionPackCounterMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.SessionPackCounterMap.verify|verify} messages.
                     * @param message SessionPackCounterMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: es.onebox.venue.venuetemplates.ISessionPackCounterMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified SessionPackCounterMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.SessionPackCounterMap.verify|verify} messages.
                     * @param message SessionPackCounterMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: es.onebox.venue.venuetemplates.ISessionPackCounterMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a SessionPackCounterMap message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns SessionPackCounterMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): es.onebox.venue.venuetemplates.SessionPackCounterMap;

                    /**
                     * Decodes a SessionPackCounterMap message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns SessionPackCounterMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): es.onebox.venue.venuetemplates.SessionPackCounterMap;

                    /**
                     * Verifies a SessionPackCounterMap message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a SessionPackCounterMap message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns SessionPackCounterMap
                     */
                    public static fromObject(object: { [k: string]: any }): es.onebox.venue.venuetemplates.SessionPackCounterMap;

                    /**
                     * Creates a plain object from a SessionPackCounterMap message. Also converts values to other types if specified.
                     * @param message SessionPackCounterMap
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: es.onebox.venue.venuetemplates.SessionPackCounterMap, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this SessionPackCounterMap to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };
                }

                /** Properties of a QuotaCountersMap. */
                interface IQuotaCountersMap {

                    /** QuotaCountersMap quota */
                    quota?: (number|null);

                    /** QuotaCountersMap count */
                    count?: (number|null);

                    /** QuotaCountersMap available */
                    available?: (number|null);
                }

                /** Represents a QuotaCountersMap. */
                class QuotaCountersMap implements IQuotaCountersMap {

                    /**
                     * Constructs a new QuotaCountersMap.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: es.onebox.venue.venuetemplates.IQuotaCountersMap);

                    /** QuotaCountersMap quota. */
                    public quota: number;

                    /** QuotaCountersMap count. */
                    public count: number;

                    /** QuotaCountersMap available. */
                    public available: number;

                    /**
                     * Creates a new QuotaCountersMap instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns QuotaCountersMap instance
                     */
                    public static create(properties?: es.onebox.venue.venuetemplates.IQuotaCountersMap): es.onebox.venue.venuetemplates.QuotaCountersMap;

                    /**
                     * Encodes the specified QuotaCountersMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.QuotaCountersMap.verify|verify} messages.
                     * @param message QuotaCountersMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: es.onebox.venue.venuetemplates.IQuotaCountersMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified QuotaCountersMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.QuotaCountersMap.verify|verify} messages.
                     * @param message QuotaCountersMap message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: es.onebox.venue.venuetemplates.IQuotaCountersMap, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a QuotaCountersMap message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns QuotaCountersMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): es.onebox.venue.venuetemplates.QuotaCountersMap;

                    /**
                     * Decodes a QuotaCountersMap message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns QuotaCountersMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): es.onebox.venue.venuetemplates.QuotaCountersMap;

                    /**
                     * Verifies a QuotaCountersMap message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a QuotaCountersMap message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns QuotaCountersMap
                     */
                    public static fromObject(object: { [k: string]: any }): es.onebox.venue.venuetemplates.QuotaCountersMap;

                    /**
                     * Creates a plain object from a QuotaCountersMap message. Also converts values to other types if specified.
                     * @param message QuotaCountersMap
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: es.onebox.venue.venuetemplates.QuotaCountersMap, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this QuotaCountersMap to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };
                }
            }
        }
    }
}
/* eslint-enable */

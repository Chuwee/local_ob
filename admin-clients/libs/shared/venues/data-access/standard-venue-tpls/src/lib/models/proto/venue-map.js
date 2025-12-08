/*eslint-disable block-scoped-var, id-length, no-control-regex, no-magic-numbers, no-prototype-builtins, no-redeclare, no-shadow, no-var, sort-vars*/
"use strict";

import * as $protobuf from "protobufjs/minimal";

// Common aliases
var $Reader = $protobuf.Reader, $Writer = $protobuf.Writer, $util = $protobuf.util;

// Exported root namespace
var $root = $protobuf.roots["default"] || ($protobuf.roots["default"] = {});

$root.es = (function () {

    /**
     * Namespace es.
     * @exports es
     * @namespace
     */
    var es = {};

    es.onebox = (function () {

        /**
         * Namespace onebox.
         * @memberof es
         * @namespace
         */
        var onebox = {};

        onebox.venue = (function () {

            /**
             * Namespace venue.
             * @memberof es.onebox
             * @namespace
             */
            var venue = {};

            venue.venuetemplates = (function () {

                /**
                 * Namespace venuetemplates.
                 * @memberof es.onebox.venue
                 * @namespace
                 */
                var venuetemplates = {};

                venuetemplates.VenueMap = (function () {

                    /**
                     * Properties of a VenueMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @interface IVenueMap
                     * @property {Array.<es.onebox.venue.venuetemplates.ISectorMap>|null} [sectorMap] VenueMap sectorMap
                     */

                    /**
                     * Constructs a new VenueMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @classdesc Represents a VenueMap.
                     * @implements IVenueMap
                     * @constructor
                     * @param {es.onebox.venue.venuetemplates.IVenueMap=} [properties] Properties to set
                     */
                    function VenueMap(properties) {
                        this.sectorMap = [];
                        if (properties)
                            for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * VenueMap sectorMap.
                     * @member {Array.<es.onebox.venue.venuetemplates.ISectorMap>} sectorMap
                     * @memberof es.onebox.venue.venuetemplates.VenueMap
                     * @instance
                     */
                    VenueMap.prototype.sectorMap = $util.emptyArray;

                    /**
                     * Creates a new VenueMap instance using the specified properties.
                     * @function create
                     * @memberof es.onebox.venue.venuetemplates.VenueMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IVenueMap=} [properties] Properties to set
                     * @returns {es.onebox.venue.venuetemplates.VenueMap} VenueMap instance
                     */
                    VenueMap.create = function create(properties) {
                        return new VenueMap(properties);
                    };

                    /**
                     * Encodes the specified VenueMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.VenueMap.verify|verify} messages.
                     * @function encode
                     * @memberof es.onebox.venue.venuetemplates.VenueMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IVenueMap} message VenueMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    VenueMap.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.sectorMap != null && message.sectorMap.length)
                            for (var i = 0; i < message.sectorMap.length; ++i)
                                $root.es.onebox.venue.venuetemplates.SectorMap.encode(message.sectorMap[i], writer.uint32(/* id 1, wireType 2 =*/10).fork()).ldelim();
                        return writer;
                    };

                    /**
                     * Encodes the specified VenueMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.VenueMap.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.VenueMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IVenueMap} message VenueMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    VenueMap.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a VenueMap message from the specified reader or buffer.
                     * @function decode
                     * @memberof es.onebox.venue.venuetemplates.VenueMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {es.onebox.venue.venuetemplates.VenueMap} VenueMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    VenueMap.decode = function decode(reader, length) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        var end = length === undefined ? reader.len : reader.pos + length, message = new $root.es.onebox.venue.venuetemplates.VenueMap();
                        while (reader.pos < end) {
                            var tag = reader.uint32();
                            switch (tag >>> 3) {
                                case 1:
                                    if (!(message.sectorMap && message.sectorMap.length))
                                        message.sectorMap = [];
                                    message.sectorMap.push($root.es.onebox.venue.venuetemplates.SectorMap.decode(reader, reader.uint32()));
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a VenueMap message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.VenueMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {es.onebox.venue.venuetemplates.VenueMap} VenueMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    VenueMap.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a VenueMap message.
                     * @function verify
                     * @memberof es.onebox.venue.venuetemplates.VenueMap
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    VenueMap.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.sectorMap != null && message.hasOwnProperty("sectorMap")) {
                            if (!Array.isArray(message.sectorMap))
                                return "sectorMap: array expected";
                            for (var i = 0; i < message.sectorMap.length; ++i) {
                                var error = $root.es.onebox.venue.venuetemplates.SectorMap.verify(message.sectorMap[i]);
                                if (error)
                                    return "sectorMap." + error;
                            }
                        }
                        return null;
                    };

                    /**
                     * Creates a VenueMap message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof es.onebox.venue.venuetemplates.VenueMap
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {es.onebox.venue.venuetemplates.VenueMap} VenueMap
                     */
                    VenueMap.fromObject = function fromObject(object) {
                        if (object instanceof $root.es.onebox.venue.venuetemplates.VenueMap)
                            return object;
                        var message = new $root.es.onebox.venue.venuetemplates.VenueMap();
                        if (object.sectorMap) {
                            if (!Array.isArray(object.sectorMap))
                                throw TypeError(".es.onebox.venue.venuetemplates.VenueMap.sectorMap: array expected");
                            message.sectorMap = [];
                            for (var i = 0; i < object.sectorMap.length; ++i) {
                                if (typeof object.sectorMap[i] !== "object")
                                    throw TypeError(".es.onebox.venue.venuetemplates.VenueMap.sectorMap: object expected");
                                message.sectorMap[i] = $root.es.onebox.venue.venuetemplates.SectorMap.fromObject(object.sectorMap[i]);
                            }
                        }
                        return message;
                    };

                    /**
                     * Creates a plain object from a VenueMap message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof es.onebox.venue.venuetemplates.VenueMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.VenueMap} message VenueMap
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    VenueMap.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        var object = {};
                        if (options.arrays || options.defaults)
                            object.sectorMap = [];
                        if (message.sectorMap && message.sectorMap.length) {
                            object.sectorMap = [];
                            for (var j = 0; j < message.sectorMap.length; ++j)
                                object.sectorMap[j] = $root.es.onebox.venue.venuetemplates.SectorMap.toObject(message.sectorMap[j], options);
                        }
                        return object;
                    };

                    /**
                     * Converts this VenueMap to JSON.
                     * @function toJSON
                     * @memberof es.onebox.venue.venuetemplates.VenueMap
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    VenueMap.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    return VenueMap;
                })();

                venuetemplates.SectorMap = (function () {

                    /**
                     * Properties of a SectorMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @interface ISectorMap
                     * @property {number|null} [id] SectorMap id
                     * @property {number|null} [venuetemplate] SectorMap venuetemplate
                     * @property {string|null} [code] SectorMap code
                     * @property {string|null} [description] SectorMap description
                     * @property {number|null} [type] SectorMap type
                     * @property {boolean|null} ["default"] SectorMap default
                     * @property {number|null} [order] SectorMap order
                     * @property {number|Long|null} [saveSequence] SectorMap saveSequence
                     * @property {Array.<es.onebox.venue.venuetemplates.IRowMap>|null} [rowMap] SectorMap rowMap
                     * @property {Array.<es.onebox.venue.venuetemplates.INotNumberedZoneMap>|null} [notNumberedZoneMap] SectorMap notNumberedZoneMap
                     */

                    /**
                     * Constructs a new SectorMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @classdesc Represents a SectorMap.
                     * @implements ISectorMap
                     * @constructor
                     * @param {es.onebox.venue.venuetemplates.ISectorMap=} [properties] Properties to set
                     */
                    function SectorMap(properties) {
                        this.rowMap = [];
                        this.notNumberedZoneMap = [];
                        if (properties)
                            for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * SectorMap id.
                     * @member {number} id
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @instance
                     */
                    SectorMap.prototype.id = 0;

                    /**
                     * SectorMap venuetemplate.
                     * @member {number} venuetemplate
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @instance
                     */
                    SectorMap.prototype.venuetemplate = 0;

                    /**
                     * SectorMap code.
                     * @member {string} code
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @instance
                     */
                    SectorMap.prototype.code = "";

                    /**
                     * SectorMap description.
                     * @member {string} description
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @instance
                     */
                    SectorMap.prototype.description = "";

                    /**
                     * SectorMap type.
                     * @member {number} type
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @instance
                     */
                    SectorMap.prototype.type = 0;

                    /**
                     * SectorMap default.
                     * @member {boolean} default
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @instance
                     */
                    SectorMap.prototype["default"] = false;

                    /**
                     * SectorMap order.
                     * @member {number} order
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @instance
                     */
                    SectorMap.prototype.order = 0;

                    /**
                     * SectorMap saveSequence.
                     * @member {number|Long} saveSequence
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @instance
                     */
                    SectorMap.prototype.saveSequence = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * SectorMap rowMap.
                     * @member {Array.<es.onebox.venue.venuetemplates.IRowMap>} rowMap
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @instance
                     */
                    SectorMap.prototype.rowMap = $util.emptyArray;

                    /**
                     * SectorMap notNumberedZoneMap.
                     * @member {Array.<es.onebox.venue.venuetemplates.INotNumberedZoneMap>} notNumberedZoneMap
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @instance
                     */
                    SectorMap.prototype.notNumberedZoneMap = $util.emptyArray;

                    /**
                     * Creates a new SectorMap instance using the specified properties.
                     * @function create
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.ISectorMap=} [properties] Properties to set
                     * @returns {es.onebox.venue.venuetemplates.SectorMap} SectorMap instance
                     */
                    SectorMap.create = function create(properties) {
                        return new SectorMap(properties);
                    };

                    /**
                     * Encodes the specified SectorMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.SectorMap.verify|verify} messages.
                     * @function encode
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.ISectorMap} message SectorMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    SectorMap.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.id != null && Object.hasOwnProperty.call(message, "id"))
                            writer.uint32(/* id 1, wireType 0 =*/8).int32(message.id);
                        if (message.venuetemplate != null && Object.hasOwnProperty.call(message, "venuetemplate"))
                            writer.uint32(/* id 2, wireType 0 =*/16).int32(message.venuetemplate);
                        if (message.code != null && Object.hasOwnProperty.call(message, "code"))
                            writer.uint32(/* id 3, wireType 2 =*/26).string(message.code);
                        if (message.description != null && Object.hasOwnProperty.call(message, "description"))
                            writer.uint32(/* id 4, wireType 2 =*/34).string(message.description);
                        if (message.type != null && Object.hasOwnProperty.call(message, "type"))
                            writer.uint32(/* id 5, wireType 0 =*/40).int32(message.type);
                        if (message["default"] != null && Object.hasOwnProperty.call(message, "default"))
                            writer.uint32(/* id 6, wireType 0 =*/48).bool(message["default"]);
                        if (message.order != null && Object.hasOwnProperty.call(message, "order"))
                            writer.uint32(/* id 7, wireType 0 =*/56).int32(message.order);
                        if (message.saveSequence != null && Object.hasOwnProperty.call(message, "saveSequence"))
                            writer.uint32(/* id 8, wireType 0 =*/64).int64(message.saveSequence);
                        if (message.rowMap != null && message.rowMap.length)
                            for (var i = 0; i < message.rowMap.length; ++i)
                                $root.es.onebox.venue.venuetemplates.RowMap.encode(message.rowMap[i], writer.uint32(/* id 9, wireType 2 =*/74).fork()).ldelim();
                        if (message.notNumberedZoneMap != null && message.notNumberedZoneMap.length)
                            for (var i = 0; i < message.notNumberedZoneMap.length; ++i)
                                $root.es.onebox.venue.venuetemplates.NotNumberedZoneMap.encode(message.notNumberedZoneMap[i], writer.uint32(/* id 10, wireType 2 =*/82).fork()).ldelim();
                        return writer;
                    };

                    /**
                     * Encodes the specified SectorMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.SectorMap.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.ISectorMap} message SectorMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    SectorMap.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a SectorMap message from the specified reader or buffer.
                     * @function decode
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {es.onebox.venue.venuetemplates.SectorMap} SectorMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    SectorMap.decode = function decode(reader, length) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        var end = length === undefined ? reader.len : reader.pos + length, message = new $root.es.onebox.venue.venuetemplates.SectorMap();
                        while (reader.pos < end) {
                            var tag = reader.uint32();
                            switch (tag >>> 3) {
                                case 1:
                                    message.id = reader.int32();
                                    break;
                                case 2:
                                    message.venuetemplate = reader.int32();
                                    break;
                                case 3:
                                    message.code = reader.string();
                                    break;
                                case 4:
                                    message.description = reader.string();
                                    break;
                                case 5:
                                    message.type = reader.int32();
                                    break;
                                case 6:
                                    message["default"] = reader.bool();
                                    break;
                                case 7:
                                    message.order = reader.int32();
                                    break;
                                case 8:
                                    message.saveSequence = reader.int64();
                                    break;
                                case 9:
                                    if (!(message.rowMap && message.rowMap.length))
                                        message.rowMap = [];
                                    message.rowMap.push($root.es.onebox.venue.venuetemplates.RowMap.decode(reader, reader.uint32()));
                                    break;
                                case 10:
                                    if (!(message.notNumberedZoneMap && message.notNumberedZoneMap.length))
                                        message.notNumberedZoneMap = [];
                                    message.notNumberedZoneMap.push($root.es.onebox.venue.venuetemplates.NotNumberedZoneMap.decode(reader, reader.uint32()));
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a SectorMap message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {es.onebox.venue.venuetemplates.SectorMap} SectorMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    SectorMap.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a SectorMap message.
                     * @function verify
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    SectorMap.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.id != null && message.hasOwnProperty("id"))
                            if (!$util.isInteger(message.id))
                                return "id: integer expected";
                        if (message.venuetemplate != null && message.hasOwnProperty("venuetemplate"))
                            if (!$util.isInteger(message.venuetemplate))
                                return "venuetemplate: integer expected";
                        if (message.code != null && message.hasOwnProperty("code"))
                            if (!$util.isString(message.code))
                                return "code: string expected";
                        if (message.description != null && message.hasOwnProperty("description"))
                            if (!$util.isString(message.description))
                                return "description: string expected";
                        if (message.type != null && message.hasOwnProperty("type"))
                            if (!$util.isInteger(message.type))
                                return "type: integer expected";
                        if (message["default"] != null && message.hasOwnProperty("default"))
                            if (typeof message["default"] !== "boolean")
                                return "default: boolean expected";
                        if (message.order != null && message.hasOwnProperty("order"))
                            if (!$util.isInteger(message.order))
                                return "order: integer expected";
                        if (message.saveSequence != null && message.hasOwnProperty("saveSequence"))
                            if (!$util.isInteger(message.saveSequence) && !(message.saveSequence && $util.isInteger(message.saveSequence.low) && $util.isInteger(message.saveSequence.high)))
                                return "saveSequence: integer|Long expected";
                        if (message.rowMap != null && message.hasOwnProperty("rowMap")) {
                            if (!Array.isArray(message.rowMap))
                                return "rowMap: array expected";
                            for (var i = 0; i < message.rowMap.length; ++i) {
                                var error = $root.es.onebox.venue.venuetemplates.RowMap.verify(message.rowMap[i]);
                                if (error)
                                    return "rowMap." + error;
                            }
                        }
                        if (message.notNumberedZoneMap != null && message.hasOwnProperty("notNumberedZoneMap")) {
                            if (!Array.isArray(message.notNumberedZoneMap))
                                return "notNumberedZoneMap: array expected";
                            for (var i = 0; i < message.notNumberedZoneMap.length; ++i) {
                                var error = $root.es.onebox.venue.venuetemplates.NotNumberedZoneMap.verify(message.notNumberedZoneMap[i]);
                                if (error)
                                    return "notNumberedZoneMap." + error;
                            }
                        }
                        return null;
                    };

                    /**
                     * Creates a SectorMap message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {es.onebox.venue.venuetemplates.SectorMap} SectorMap
                     */
                    SectorMap.fromObject = function fromObject(object) {
                        if (object instanceof $root.es.onebox.venue.venuetemplates.SectorMap)
                            return object;
                        var message = new $root.es.onebox.venue.venuetemplates.SectorMap();
                        if (object.id != null)
                            message.id = object.id | 0;
                        if (object.venuetemplate != null)
                            message.venuetemplate = object.venuetemplate | 0;
                        if (object.code != null)
                            message.code = String(object.code);
                        if (object.description != null)
                            message.description = String(object.description);
                        if (object.type != null)
                            message.type = object.type | 0;
                        if (object["default"] != null)
                            message["default"] = Boolean(object["default"]);
                        if (object.order != null)
                            message.order = object.order | 0;
                        if (object.saveSequence != null)
                            if ($util.Long)
                                (message.saveSequence = $util.Long.fromValue(object.saveSequence)).unsigned = false;
                            else if (typeof object.saveSequence === "string")
                                message.saveSequence = parseInt(object.saveSequence, 10);
                            else if (typeof object.saveSequence === "number")
                                message.saveSequence = object.saveSequence;
                            else if (typeof object.saveSequence === "object")
                                message.saveSequence = new $util.LongBits(object.saveSequence.low >>> 0, object.saveSequence.high >>> 0).toNumber();
                        if (object.rowMap) {
                            if (!Array.isArray(object.rowMap))
                                throw TypeError(".es.onebox.venue.venuetemplates.SectorMap.rowMap: array expected");
                            message.rowMap = [];
                            for (var i = 0; i < object.rowMap.length; ++i) {
                                if (typeof object.rowMap[i] !== "object")
                                    throw TypeError(".es.onebox.venue.venuetemplates.SectorMap.rowMap: object expected");
                                message.rowMap[i] = $root.es.onebox.venue.venuetemplates.RowMap.fromObject(object.rowMap[i]);
                            }
                        }
                        if (object.notNumberedZoneMap) {
                            if (!Array.isArray(object.notNumberedZoneMap))
                                throw TypeError(".es.onebox.venue.venuetemplates.SectorMap.notNumberedZoneMap: array expected");
                            message.notNumberedZoneMap = [];
                            for (var i = 0; i < object.notNumberedZoneMap.length; ++i) {
                                if (typeof object.notNumberedZoneMap[i] !== "object")
                                    throw TypeError(".es.onebox.venue.venuetemplates.SectorMap.notNumberedZoneMap: object expected");
                                message.notNumberedZoneMap[i] = $root.es.onebox.venue.venuetemplates.NotNumberedZoneMap.fromObject(object.notNumberedZoneMap[i]);
                            }
                        }
                        return message;
                    };

                    /**
                     * Creates a plain object from a SectorMap message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.SectorMap} message SectorMap
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    SectorMap.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        var object = {};
                        if (options.arrays || options.defaults) {
                            object.rowMap = [];
                            object.notNumberedZoneMap = [];
                        }
                        if (options.defaults) {
                            object.id = 0;
                            object.venuetemplate = 0;
                            object.code = "";
                            object.description = "";
                            object.type = 0;
                            object["default"] = false;
                            object.order = 0;
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.saveSequence = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.saveSequence = options.longs === String ? "0" : 0;
                        }
                        if (message.id != null && message.hasOwnProperty("id"))
                            object.id = message.id;
                        if (message.venuetemplate != null && message.hasOwnProperty("venuetemplate"))
                            object.venuetemplate = message.venuetemplate;
                        if (message.code != null && message.hasOwnProperty("code"))
                            object.code = message.code;
                        if (message.description != null && message.hasOwnProperty("description"))
                            object.description = message.description;
                        if (message.type != null && message.hasOwnProperty("type"))
                            object.type = message.type;
                        if (message["default"] != null && message.hasOwnProperty("default"))
                            object["default"] = message["default"];
                        if (message.order != null && message.hasOwnProperty("order"))
                            object.order = message.order;
                        if (message.saveSequence != null && message.hasOwnProperty("saveSequence"))
                            if (typeof message.saveSequence === "number")
                                object.saveSequence = options.longs === String ? String(message.saveSequence) : message.saveSequence;
                            else
                                object.saveSequence = options.longs === String ? $util.Long.prototype.toString.call(message.saveSequence) : options.longs === Number ? new $util.LongBits(message.saveSequence.low >>> 0, message.saveSequence.high >>> 0).toNumber() : message.saveSequence;
                        if (message.rowMap && message.rowMap.length) {
                            object.rowMap = [];
                            for (var j = 0; j < message.rowMap.length; ++j)
                                object.rowMap[j] = $root.es.onebox.venue.venuetemplates.RowMap.toObject(message.rowMap[j], options);
                        }
                        if (message.notNumberedZoneMap && message.notNumberedZoneMap.length) {
                            object.notNumberedZoneMap = [];
                            for (var j = 0; j < message.notNumberedZoneMap.length; ++j)
                                object.notNumberedZoneMap[j] = $root.es.onebox.venue.venuetemplates.NotNumberedZoneMap.toObject(message.notNumberedZoneMap[j], options);
                        }
                        return object;
                    };

                    /**
                     * Converts this SectorMap to JSON.
                     * @function toJSON
                     * @memberof es.onebox.venue.venuetemplates.SectorMap
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    SectorMap.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    return SectorMap;
                })();

                venuetemplates.RowMap = (function () {

                    /**
                     * Properties of a RowMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @interface IRowMap
                     * @property {number|null} [id] RowMap id
                     * @property {string|null} [name] RowMap name
                     * @property {number|null} [sectorId] RowMap sectorId
                     * @property {number|null} [order] RowMap order
                     * @property {number|Long|null} [saveSequence] RowMap saveSequence
                     * @property {Array.<es.onebox.venue.venuetemplates.ISeatMap>|null} [seatMap] RowMap seatMap
                     */

                    /**
                     * Constructs a new RowMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @classdesc Represents a RowMap.
                     * @implements IRowMap
                     * @constructor
                     * @param {es.onebox.venue.venuetemplates.IRowMap=} [properties] Properties to set
                     */
                    function RowMap(properties) {
                        this.seatMap = [];
                        if (properties)
                            for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * RowMap id.
                     * @member {number} id
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @instance
                     */
                    RowMap.prototype.id = 0;

                    /**
                     * RowMap name.
                     * @member {string} name
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @instance
                     */
                    RowMap.prototype.name = "";

                    /**
                     * RowMap sectorId.
                     * @member {number} sectorId
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @instance
                     */
                    RowMap.prototype.sectorId = 0;

                    /**
                     * RowMap order.
                     * @member {number} order
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @instance
                     */
                    RowMap.prototype.order = 0;

                    /**
                     * RowMap saveSequence.
                     * @member {number|Long} saveSequence
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @instance
                     */
                    RowMap.prototype.saveSequence = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * RowMap seatMap.
                     * @member {Array.<es.onebox.venue.venuetemplates.ISeatMap>} seatMap
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @instance
                     */
                    RowMap.prototype.seatMap = $util.emptyArray;

                    /**
                     * Creates a new RowMap instance using the specified properties.
                     * @function create
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IRowMap=} [properties] Properties to set
                     * @returns {es.onebox.venue.venuetemplates.RowMap} RowMap instance
                     */
                    RowMap.create = function create(properties) {
                        return new RowMap(properties);
                    };

                    /**
                     * Encodes the specified RowMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.RowMap.verify|verify} messages.
                     * @function encode
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IRowMap} message RowMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    RowMap.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.id != null && Object.hasOwnProperty.call(message, "id"))
                            writer.uint32(/* id 1, wireType 0 =*/8).int32(message.id);
                        if (message.name != null && Object.hasOwnProperty.call(message, "name"))
                            writer.uint32(/* id 2, wireType 2 =*/18).string(message.name);
                        if (message.sectorId != null && Object.hasOwnProperty.call(message, "sectorId"))
                            writer.uint32(/* id 3, wireType 0 =*/24).int32(message.sectorId);
                        if (message.order != null && Object.hasOwnProperty.call(message, "order"))
                            writer.uint32(/* id 4, wireType 0 =*/32).int32(message.order);
                        if (message.saveSequence != null && Object.hasOwnProperty.call(message, "saveSequence"))
                            writer.uint32(/* id 5, wireType 0 =*/40).int64(message.saveSequence);
                        if (message.seatMap != null && message.seatMap.length)
                            for (var i = 0; i < message.seatMap.length; ++i)
                                $root.es.onebox.venue.venuetemplates.SeatMap.encode(message.seatMap[i], writer.uint32(/* id 6, wireType 2 =*/50).fork()).ldelim();
                        return writer;
                    };

                    /**
                     * Encodes the specified RowMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.RowMap.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IRowMap} message RowMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    RowMap.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a RowMap message from the specified reader or buffer.
                     * @function decode
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {es.onebox.venue.venuetemplates.RowMap} RowMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    RowMap.decode = function decode(reader, length) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        var end = length === undefined ? reader.len : reader.pos + length, message = new $root.es.onebox.venue.venuetemplates.RowMap();
                        while (reader.pos < end) {
                            var tag = reader.uint32();
                            switch (tag >>> 3) {
                                case 1:
                                    message.id = reader.int32();
                                    break;
                                case 2:
                                    message.name = reader.string();
                                    break;
                                case 3:
                                    message.sectorId = reader.int32();
                                    break;
                                case 4:
                                    message.order = reader.int32();
                                    break;
                                case 5:
                                    message.saveSequence = reader.int64();
                                    break;
                                case 6:
                                    if (!(message.seatMap && message.seatMap.length))
                                        message.seatMap = [];
                                    message.seatMap.push($root.es.onebox.venue.venuetemplates.SeatMap.decode(reader, reader.uint32()));
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a RowMap message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {es.onebox.venue.venuetemplates.RowMap} RowMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    RowMap.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a RowMap message.
                     * @function verify
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    RowMap.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.id != null && message.hasOwnProperty("id"))
                            if (!$util.isInteger(message.id))
                                return "id: integer expected";
                        if (message.name != null && message.hasOwnProperty("name"))
                            if (!$util.isString(message.name))
                                return "name: string expected";
                        if (message.sectorId != null && message.hasOwnProperty("sectorId"))
                            if (!$util.isInteger(message.sectorId))
                                return "sectorId: integer expected";
                        if (message.order != null && message.hasOwnProperty("order"))
                            if (!$util.isInteger(message.order))
                                return "order: integer expected";
                        if (message.saveSequence != null && message.hasOwnProperty("saveSequence"))
                            if (!$util.isInteger(message.saveSequence) && !(message.saveSequence && $util.isInteger(message.saveSequence.low) && $util.isInteger(message.saveSequence.high)))
                                return "saveSequence: integer|Long expected";
                        if (message.seatMap != null && message.hasOwnProperty("seatMap")) {
                            if (!Array.isArray(message.seatMap))
                                return "seatMap: array expected";
                            for (var i = 0; i < message.seatMap.length; ++i) {
                                var error = $root.es.onebox.venue.venuetemplates.SeatMap.verify(message.seatMap[i]);
                                if (error)
                                    return "seatMap." + error;
                            }
                        }
                        return null;
                    };

                    /**
                     * Creates a RowMap message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {es.onebox.venue.venuetemplates.RowMap} RowMap
                     */
                    RowMap.fromObject = function fromObject(object) {
                        if (object instanceof $root.es.onebox.venue.venuetemplates.RowMap)
                            return object;
                        var message = new $root.es.onebox.venue.venuetemplates.RowMap();
                        if (object.id != null)
                            message.id = object.id | 0;
                        if (object.name != null)
                            message.name = String(object.name);
                        if (object.sectorId != null)
                            message.sectorId = object.sectorId | 0;
                        if (object.order != null)
                            message.order = object.order | 0;
                        if (object.saveSequence != null)
                            if ($util.Long)
                                (message.saveSequence = $util.Long.fromValue(object.saveSequence)).unsigned = false;
                            else if (typeof object.saveSequence === "string")
                                message.saveSequence = parseInt(object.saveSequence, 10);
                            else if (typeof object.saveSequence === "number")
                                message.saveSequence = object.saveSequence;
                            else if (typeof object.saveSequence === "object")
                                message.saveSequence = new $util.LongBits(object.saveSequence.low >>> 0, object.saveSequence.high >>> 0).toNumber();
                        if (object.seatMap) {
                            if (!Array.isArray(object.seatMap))
                                throw TypeError(".es.onebox.venue.venuetemplates.RowMap.seatMap: array expected");
                            message.seatMap = [];
                            for (var i = 0; i < object.seatMap.length; ++i) {
                                if (typeof object.seatMap[i] !== "object")
                                    throw TypeError(".es.onebox.venue.venuetemplates.RowMap.seatMap: object expected");
                                message.seatMap[i] = $root.es.onebox.venue.venuetemplates.SeatMap.fromObject(object.seatMap[i]);
                            }
                        }
                        return message;
                    };

                    /**
                     * Creates a plain object from a RowMap message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.RowMap} message RowMap
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    RowMap.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        var object = {};
                        if (options.arrays || options.defaults)
                            object.seatMap = [];
                        if (options.defaults) {
                            object.id = 0;
                            object.name = "";
                            object.sectorId = 0;
                            object.order = 0;
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.saveSequence = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.saveSequence = options.longs === String ? "0" : 0;
                        }
                        if (message.id != null && message.hasOwnProperty("id"))
                            object.id = message.id;
                        if (message.name != null && message.hasOwnProperty("name"))
                            object.name = message.name;
                        if (message.sectorId != null && message.hasOwnProperty("sectorId"))
                            object.sectorId = message.sectorId;
                        if (message.order != null && message.hasOwnProperty("order"))
                            object.order = message.order;
                        if (message.saveSequence != null && message.hasOwnProperty("saveSequence"))
                            if (typeof message.saveSequence === "number")
                                object.saveSequence = options.longs === String ? String(message.saveSequence) : message.saveSequence;
                            else
                                object.saveSequence = options.longs === String ? $util.Long.prototype.toString.call(message.saveSequence) : options.longs === Number ? new $util.LongBits(message.saveSequence.low >>> 0, message.saveSequence.high >>> 0).toNumber() : message.saveSequence;
                        if (message.seatMap && message.seatMap.length) {
                            object.seatMap = [];
                            for (var j = 0; j < message.seatMap.length; ++j)
                                object.seatMap[j] = $root.es.onebox.venue.venuetemplates.SeatMap.toObject(message.seatMap[j], options);
                        }
                        return object;
                    };

                    /**
                     * Converts this RowMap to JSON.
                     * @function toJSON
                     * @memberof es.onebox.venue.venuetemplates.RowMap
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    RowMap.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    return RowMap;
                })();

                venuetemplates.Enums = (function () {

                    /**
                     * Properties of an Enums.
                     * @memberof es.onebox.venue.venuetemplates
                     * @interface IEnums
                     */

                    /**
                     * Constructs a new Enums.
                     * @memberof es.onebox.venue.venuetemplates
                     * @classdesc Represents an Enums.
                     * @implements IEnums
                     * @constructor
                     * @param {es.onebox.venue.venuetemplates.IEnums=} [properties] Properties to set
                     */
                    function Enums(properties) {
                        if (properties)
                            for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * Creates a new Enums instance using the specified properties.
                     * @function create
                     * @memberof es.onebox.venue.venuetemplates.Enums
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IEnums=} [properties] Properties to set
                     * @returns {es.onebox.venue.venuetemplates.Enums} Enums instance
                     */
                    Enums.create = function create(properties) {
                        return new Enums(properties);
                    };

                    /**
                     * Encodes the specified Enums message. Does not implicitly {@link es.onebox.venue.venuetemplates.Enums.verify|verify} messages.
                     * @function encode
                     * @memberof es.onebox.venue.venuetemplates.Enums
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IEnums} message Enums message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    Enums.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        return writer;
                    };

                    /**
                     * Encodes the specified Enums message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.Enums.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.Enums
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IEnums} message Enums message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    Enums.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes an Enums message from the specified reader or buffer.
                     * @function decode
                     * @memberof es.onebox.venue.venuetemplates.Enums
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {es.onebox.venue.venuetemplates.Enums} Enums
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    Enums.decode = function decode(reader, length) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        var end = length === undefined ? reader.len : reader.pos + length, message = new $root.es.onebox.venue.venuetemplates.Enums();
                        while (reader.pos < end) {
                            var tag = reader.uint32();
                            switch (tag >>> 3) {
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes an Enums message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.Enums
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {es.onebox.venue.venuetemplates.Enums} Enums
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    Enums.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies an Enums message.
                     * @function verify
                     * @memberof es.onebox.venue.venuetemplates.Enums
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    Enums.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        return null;
                    };

                    /**
                     * Creates an Enums message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof es.onebox.venue.venuetemplates.Enums
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {es.onebox.venue.venuetemplates.Enums} Enums
                     */
                    Enums.fromObject = function fromObject(object) {
                        if (object instanceof $root.es.onebox.venue.venuetemplates.Enums)
                            return object;
                        return new $root.es.onebox.venue.venuetemplates.Enums();
                    };

                    /**
                     * Creates a plain object from an Enums message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof es.onebox.venue.venuetemplates.Enums
                     * @static
                     * @param {es.onebox.venue.venuetemplates.Enums} message Enums
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    Enums.toObject = function toObject() {
                        return {};
                    };

                    /**
                     * Converts this Enums to JSON.
                     * @function toJSON
                     * @memberof es.onebox.venue.venuetemplates.Enums
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    Enums.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    /**
                     * SeatStatus enum.
                     * @name es.onebox.venue.venuetemplates.Enums.SeatStatus
                     * @enum {number}
                     * @property {number} UNKNOWN=0 UNKNOWN value
                     * @property {number} FREE=1 FREE value
                     * @property {number} SOLD=2 SOLD value
                     * @property {number} PROMOTOR_LOCKED=3 PROMOTOR_LOCKED value
                     * @property {number} SYSTEM_LOCKED=4 SYSTEM_LOCKED value
                     * @property {number} BOOKED=5 BOOKED value
                     * @property {number} KILL=6 KILL value
                     * @property {number} EMITTED=7 EMITTED value
                     * @property {number} VALIDATED=8 VALIDATED value
                     * @property {number} IN_REFUND=9 IN_REFUND value
                     * @property {number} CANCELLED=10 CANCELLED value
                     * @property {number} PRESOLD_LOCKED=11 PRESOLD_LOCKED value
                     * @property {number} SOLD_LOCKED=12 SOLD_LOCKED value
                     * @property {number} GIFT=13 GIFT value
                     * @property {number} SEASON_LOCKED=14 SEASON_LOCKED value
                     * @property {number} EXTERNAL_LOCKED=15 EXTERNAL_LOCKED value
                     * @property {number} EXTERNAL_DELETE=16 EXTERNAL_DELETE value
                     */
                    Enums.SeatStatus = (function () {
                        var valuesById = {}, values = Object.create(valuesById);
                        values[valuesById[0] = "UNKNOWN"] = 0;
                        values[valuesById[1] = "FREE"] = 1;
                        values[valuesById[2] = "SOLD"] = 2;
                        values[valuesById[3] = "PROMOTOR_LOCKED"] = 3;
                        values[valuesById[4] = "SYSTEM_LOCKED"] = 4;
                        values[valuesById[5] = "BOOKED"] = 5;
                        values[valuesById[6] = "KILL"] = 6;
                        values[valuesById[7] = "EMITTED"] = 7;
                        values[valuesById[8] = "VALIDATED"] = 8;
                        values[valuesById[9] = "IN_REFUND"] = 9;
                        values[valuesById[10] = "CANCELLED"] = 10;
                        values[valuesById[11] = "PRESOLD_LOCKED"] = 11;
                        values[valuesById[12] = "SOLD_LOCKED"] = 12;
                        values[valuesById[13] = "GIFT"] = 13;
                        values[valuesById[14] = "SEASON_LOCKED"] = 14;
                        values[valuesById[15] = "EXTERNAL_LOCKED"] = 15;
                        values[valuesById[16] = "EXTERNAL_DELETE"] = 16;
                        return values;
                    })();

                    /**
                     * Visibility enum.
                     * @name es.onebox.venue.venuetemplates.Enums.Visibility
                     * @enum {number}
                     * @property {number} VIS_UNKNOWN=0 VIS_UNKNOWN value
                     * @property {number} FULL=1 FULL value
                     * @property {number} PARTIAL=2 PARTIAL value
                     * @property {number} NONE=3 NONE value
                     * @property {number} SIDE=4 SIDE value
                     */
                    Enums.Visibility = (function () {
                        var valuesById = {}, values = Object.create(valuesById);
                        values[valuesById[0] = "VIS_UNKNOWN"] = 0;
                        values[valuesById[1] = "FULL"] = 1;
                        values[valuesById[2] = "PARTIAL"] = 2;
                        values[valuesById[3] = "NONE"] = 3;
                        values[valuesById[4] = "SIDE"] = 4;
                        return values;
                    })();

                    /**
                     * Accessibility enum.
                     * @name es.onebox.venue.venuetemplates.Enums.Accessibility
                     * @enum {number}
                     * @property {number} ACC_UNKNOWN=0 ACC_UNKNOWN value
                     * @property {number} NORMAL=1 NORMAL value
                     * @property {number} REDUCED_MOBILITY=2 REDUCED_MOBILITY value
                     */
                    Enums.Accessibility = (function () {
                        var valuesById = {}, values = Object.create(valuesById);
                        values[valuesById[0] = "ACC_UNKNOWN"] = 0;
                        values[valuesById[1] = "NORMAL"] = 1;
                        values[valuesById[2] = "REDUCED_MOBILITY"] = 2;
                        return values;
                    })();

                    return Enums;
                })();

                venuetemplates.SeatMap = (function () {

                    /**
                     * Properties of a SeatMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @interface ISeatMap
                     * @property {number|Long|null} [id] SeatMap id
                     * @property {string|null} [name] SeatMap name
                     * @property {es.onebox.venue.venuetemplates.Enums.SeatStatus|null} [status] SeatMap status
                     * @property {number|null} [view] SeatMap view
                     * @property {string|null} [rowBlock] SeatMap rowBlock
                     * @property {number|null} [quota] SeatMap quota
                     * @property {number|null} [priceType] SeatMap priceType
                     * @property {number|null} [blockingReason] SeatMap blockingReason
                     * @property {es.onebox.venue.venuetemplates.Enums.Visibility|null} [visibility] SeatMap visibility
                     * @property {es.onebox.venue.venuetemplates.Enums.Accessibility|null} [accessibility] SeatMap accessibility
                     * @property {number|Long|null} [gate] SeatMap gate
                     * @property {number|Long|null} [dynamicTag1] SeatMap dynamicTag1
                     * @property {number|Long|null} [dynamicTag2] SeatMap dynamicTag2
                     * @property {number|Long|null} [external] SeatMap external
                     * @property {number|null} [order] SeatMap order
                     * @property {number|null} [weight] SeatMap weight
                     * @property {number|null} [posX] SeatMap posX
                     * @property {number|null} [posY] SeatMap posY
                     * @property {number|Long|null} [saveSequence] SeatMap saveSequence
                     * @property {number|Long|null} [ticketId] SeatMap ticketId
                     * @property {number|Long|null} [sessionPack] SeatMap sessionPack
                     * @property {boolean|null} [linkable] SeatMap linkable
                     * @property {boolean|null} [linked] SeatMap linked
                     */

                    /**
                     * Constructs a new SeatMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @classdesc Represents a SeatMap.
                     * @implements ISeatMap
                     * @constructor
                     * @param {es.onebox.venue.venuetemplates.ISeatMap=} [properties] Properties to set
                     */
                    function SeatMap(properties) {
                        if (properties)
                            for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * SeatMap id.
                     * @member {number|Long} id
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.id = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * SeatMap name.
                     * @member {string} name
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.name = "";

                    /**
                     * SeatMap status.
                     * @member {es.onebox.venue.venuetemplates.Enums.SeatStatus} status
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.status = 0;

                    /**
                     * SeatMap view.
                     * @member {number} view
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.view = 0;

                    /**
                     * SeatMap rowBlock.
                     * @member {string} rowBlock
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.rowBlock = "";

                    /**
                     * SeatMap quota.
                     * @member {number} quota
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.quota = 0;

                    /**
                     * SeatMap priceType.
                     * @member {number} priceType
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.priceType = 0;

                    /**
                     * SeatMap blockingReason.
                     * @member {number} blockingReason
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.blockingReason = 0;

                    /**
                     * SeatMap visibility.
                     * @member {es.onebox.venue.venuetemplates.Enums.Visibility} visibility
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.visibility = 0;

                    /**
                     * SeatMap accessibility.
                     * @member {es.onebox.venue.venuetemplates.Enums.Accessibility} accessibility
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.accessibility = 0;

                    /**
                     * SeatMap gate.
                     * @member {number|Long} gate
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.gate = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * SeatMap dynamicTag1.
                     * @member {number|Long} dynamicTag1
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.dynamicTag1 = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * SeatMap dynamicTag2.
                     * @member {number|Long} dynamicTag2
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.dynamicTag2 = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * SeatMap external.
                     * @member {number|Long} external
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.external = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * SeatMap order.
                     * @member {number} order
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.order = 0;

                    /**
                     * SeatMap weight.
                     * @member {number} weight
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.weight = 0;

                    /**
                     * SeatMap posX.
                     * @member {number} posX
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.posX = 0;

                    /**
                     * SeatMap posY.
                     * @member {number} posY
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.posY = 0;

                    /**
                     * SeatMap saveSequence.
                     * @member {number|Long} saveSequence
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.saveSequence = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * SeatMap ticketId.
                     * @member {number|Long} ticketId
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.ticketId = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * SeatMap sessionPack.
                     * @member {number|Long} sessionPack
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.sessionPack = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * SeatMap linkable.
                     * @member {boolean} linkable
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.linkable = false;

                    /**
                     * SeatMap linked.
                     * @member {boolean} linked
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     */
                    SeatMap.prototype.linked = false;

                    /**
                     * Creates a new SeatMap instance using the specified properties.
                     * @function create
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.ISeatMap=} [properties] Properties to set
                     * @returns {es.onebox.venue.venuetemplates.SeatMap} SeatMap instance
                     */
                    SeatMap.create = function create(properties) {
                        return new SeatMap(properties);
                    };

                    /**
                     * Encodes the specified SeatMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.SeatMap.verify|verify} messages.
                     * @function encode
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.ISeatMap} message SeatMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    SeatMap.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.id != null && Object.hasOwnProperty.call(message, "id"))
                            writer.uint32(/* id 1, wireType 0 =*/8).int64(message.id);
                        if (message.name != null && Object.hasOwnProperty.call(message, "name"))
                            writer.uint32(/* id 2, wireType 2 =*/18).string(message.name);
                        if (message.status != null && Object.hasOwnProperty.call(message, "status"))
                            writer.uint32(/* id 3, wireType 0 =*/24).int32(message.status);
                        if (message.view != null && Object.hasOwnProperty.call(message, "view"))
                            writer.uint32(/* id 4, wireType 0 =*/32).int32(message.view);
                        if (message.rowBlock != null && Object.hasOwnProperty.call(message, "rowBlock"))
                            writer.uint32(/* id 5, wireType 2 =*/42).string(message.rowBlock);
                        if (message.quota != null && Object.hasOwnProperty.call(message, "quota"))
                            writer.uint32(/* id 6, wireType 0 =*/48).int32(message.quota);
                        if (message.priceType != null && Object.hasOwnProperty.call(message, "priceType"))
                            writer.uint32(/* id 7, wireType 0 =*/56).int32(message.priceType);
                        if (message.blockingReason != null && Object.hasOwnProperty.call(message, "blockingReason"))
                            writer.uint32(/* id 8, wireType 0 =*/64).int32(message.blockingReason);
                        if (message.visibility != null && Object.hasOwnProperty.call(message, "visibility"))
                            writer.uint32(/* id 9, wireType 0 =*/72).int32(message.visibility);
                        if (message.accessibility != null && Object.hasOwnProperty.call(message, "accessibility"))
                            writer.uint32(/* id 10, wireType 0 =*/80).int32(message.accessibility);
                        if (message.gate != null && Object.hasOwnProperty.call(message, "gate"))
                            writer.uint32(/* id 11, wireType 0 =*/88).int64(message.gate);
                        if (message.dynamicTag1 != null && Object.hasOwnProperty.call(message, "dynamicTag1"))
                            writer.uint32(/* id 12, wireType 0 =*/96).int64(message.dynamicTag1);
                        if (message.dynamicTag2 != null && Object.hasOwnProperty.call(message, "dynamicTag2"))
                            writer.uint32(/* id 13, wireType 0 =*/104).int64(message.dynamicTag2);
                        if (message.external != null && Object.hasOwnProperty.call(message, "external"))
                            writer.uint32(/* id 14, wireType 0 =*/112).int64(message.external);
                        if (message.order != null && Object.hasOwnProperty.call(message, "order"))
                            writer.uint32(/* id 15, wireType 0 =*/120).int32(message.order);
                        if (message.weight != null && Object.hasOwnProperty.call(message, "weight"))
                            writer.uint32(/* id 16, wireType 0 =*/128).int32(message.weight);
                        if (message.posX != null && Object.hasOwnProperty.call(message, "posX"))
                            writer.uint32(/* id 17, wireType 0 =*/136).int32(message.posX);
                        if (message.posY != null && Object.hasOwnProperty.call(message, "posY"))
                            writer.uint32(/* id 18, wireType 0 =*/144).int32(message.posY);
                        if (message.saveSequence != null && Object.hasOwnProperty.call(message, "saveSequence"))
                            writer.uint32(/* id 19, wireType 0 =*/152).int64(message.saveSequence);
                        if (message.ticketId != null && Object.hasOwnProperty.call(message, "ticketId"))
                            writer.uint32(/* id 20, wireType 0 =*/160).int64(message.ticketId);
                        if (message.sessionPack != null && Object.hasOwnProperty.call(message, "sessionPack"))
                            writer.uint32(/* id 21, wireType 0 =*/168).int64(message.sessionPack);
                        if (message.linkable != null && Object.hasOwnProperty.call(message, "linkable"))
                            writer.uint32(/* id 22, wireType 0 =*/176).bool(message.linkable);
                        if (message.linked != null && Object.hasOwnProperty.call(message, "linked"))
                            writer.uint32(/* id 23, wireType 0 =*/184).bool(message.linked);
                        return writer;
                    };

                    /**
                     * Encodes the specified SeatMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.SeatMap.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.ISeatMap} message SeatMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    SeatMap.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a SeatMap message from the specified reader or buffer.
                     * @function decode
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {es.onebox.venue.venuetemplates.SeatMap} SeatMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    SeatMap.decode = function decode(reader, length) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        var end = length === undefined ? reader.len : reader.pos + length, message = new $root.es.onebox.venue.venuetemplates.SeatMap();
                        while (reader.pos < end) {
                            var tag = reader.uint32();
                            switch (tag >>> 3) {
                                case 1:
                                    message.id = reader.int64();
                                    break;
                                case 2:
                                    message.name = reader.string();
                                    break;
                                case 3:
                                    message.status = reader.int32();
                                    break;
                                case 4:
                                    message.view = reader.int32();
                                    break;
                                case 5:
                                    message.rowBlock = reader.string();
                                    break;
                                case 6:
                                    message.quota = reader.int32();
                                    break;
                                case 7:
                                    message.priceType = reader.int32();
                                    break;
                                case 8:
                                    message.blockingReason = reader.int32();
                                    break;
                                case 9:
                                    message.visibility = reader.int32();
                                    break;
                                case 10:
                                    message.accessibility = reader.int32();
                                    break;
                                case 11:
                                    message.gate = reader.int64();
                                    break;
                                case 12:
                                    message.dynamicTag1 = reader.int64();
                                    break;
                                case 13:
                                    message.dynamicTag2 = reader.int64();
                                    break;
                                case 14:
                                    message.external = reader.int64();
                                    break;
                                case 15:
                                    message.order = reader.int32();
                                    break;
                                case 16:
                                    message.weight = reader.int32();
                                    break;
                                case 17:
                                    message.posX = reader.int32();
                                    break;
                                case 18:
                                    message.posY = reader.int32();
                                    break;
                                case 19:
                                    message.saveSequence = reader.int64();
                                    break;
                                case 20:
                                    message.ticketId = reader.int64();
                                    break;
                                case 21:
                                    message.sessionPack = reader.int64();
                                    break;
                                case 22:
                                    message.linkable = reader.bool();
                                    break;
                                case 23:
                                    message.linked = reader.bool();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a SeatMap message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {es.onebox.venue.venuetemplates.SeatMap} SeatMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    SeatMap.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a SeatMap message.
                     * @function verify
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    SeatMap.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.id != null && message.hasOwnProperty("id"))
                            if (!$util.isInteger(message.id) && !(message.id && $util.isInteger(message.id.low) && $util.isInteger(message.id.high)))
                                return "id: integer|Long expected";
                        if (message.name != null && message.hasOwnProperty("name"))
                            if (!$util.isString(message.name))
                                return "name: string expected";
                        if (message.status != null && message.hasOwnProperty("status"))
                            switch (message.status) {
                                default:
                                    return "status: enum value expected";
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                case 7:
                                case 8:
                                case 9:
                                case 10:
                                case 11:
                                case 12:
                                case 13:
                                case 14:
                                case 15:
                                case 16:
                                    break;
                            }
                        if (message.view != null && message.hasOwnProperty("view"))
                            if (!$util.isInteger(message.view))
                                return "view: integer expected";
                        if (message.rowBlock != null && message.hasOwnProperty("rowBlock"))
                            if (!$util.isString(message.rowBlock))
                                return "rowBlock: string expected";
                        if (message.quota != null && message.hasOwnProperty("quota"))
                            if (!$util.isInteger(message.quota))
                                return "quota: integer expected";
                        if (message.priceType != null && message.hasOwnProperty("priceType"))
                            if (!$util.isInteger(message.priceType))
                                return "priceType: integer expected";
                        if (message.blockingReason != null && message.hasOwnProperty("blockingReason"))
                            if (!$util.isInteger(message.blockingReason))
                                return "blockingReason: integer expected";
                        if (message.visibility != null && message.hasOwnProperty("visibility"))
                            switch (message.visibility) {
                                default:
                                    return "visibility: enum value expected";
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                    break;
                            }
                        if (message.accessibility != null && message.hasOwnProperty("accessibility"))
                            switch (message.accessibility) {
                                default:
                                    return "accessibility: enum value expected";
                                case 0:
                                case 1:
                                case 2:
                                    break;
                            }
                        if (message.gate != null && message.hasOwnProperty("gate"))
                            if (!$util.isInteger(message.gate) && !(message.gate && $util.isInteger(message.gate.low) && $util.isInteger(message.gate.high)))
                                return "gate: integer|Long expected";
                        if (message.dynamicTag1 != null && message.hasOwnProperty("dynamicTag1"))
                            if (!$util.isInteger(message.dynamicTag1) && !(message.dynamicTag1 && $util.isInteger(message.dynamicTag1.low) && $util.isInteger(message.dynamicTag1.high)))
                                return "dynamicTag1: integer|Long expected";
                        if (message.dynamicTag2 != null && message.hasOwnProperty("dynamicTag2"))
                            if (!$util.isInteger(message.dynamicTag2) && !(message.dynamicTag2 && $util.isInteger(message.dynamicTag2.low) && $util.isInteger(message.dynamicTag2.high)))
                                return "dynamicTag2: integer|Long expected";
                        if (message.external != null && message.hasOwnProperty("external"))
                            if (!$util.isInteger(message.external) && !(message.external && $util.isInteger(message.external.low) && $util.isInteger(message.external.high)))
                                return "external: integer|Long expected";
                        if (message.order != null && message.hasOwnProperty("order"))
                            if (!$util.isInteger(message.order))
                                return "order: integer expected";
                        if (message.weight != null && message.hasOwnProperty("weight"))
                            if (!$util.isInteger(message.weight))
                                return "weight: integer expected";
                        if (message.posX != null && message.hasOwnProperty("posX"))
                            if (!$util.isInteger(message.posX))
                                return "posX: integer expected";
                        if (message.posY != null && message.hasOwnProperty("posY"))
                            if (!$util.isInteger(message.posY))
                                return "posY: integer expected";
                        if (message.saveSequence != null && message.hasOwnProperty("saveSequence"))
                            if (!$util.isInteger(message.saveSequence) && !(message.saveSequence && $util.isInteger(message.saveSequence.low) && $util.isInteger(message.saveSequence.high)))
                                return "saveSequence: integer|Long expected";
                        if (message.ticketId != null && message.hasOwnProperty("ticketId"))
                            if (!$util.isInteger(message.ticketId) && !(message.ticketId && $util.isInteger(message.ticketId.low) && $util.isInteger(message.ticketId.high)))
                                return "ticketId: integer|Long expected";
                        if (message.sessionPack != null && message.hasOwnProperty("sessionPack"))
                            if (!$util.isInteger(message.sessionPack) && !(message.sessionPack && $util.isInteger(message.sessionPack.low) && $util.isInteger(message.sessionPack.high)))
                                return "sessionPack: integer|Long expected";
                        if (message.linkable != null && message.hasOwnProperty("linkable"))
                            if (typeof message.linkable !== "boolean")
                                return "linkable: boolean expected";
                        if (message.linked != null && message.hasOwnProperty("linked"))
                            if (typeof message.linked !== "boolean")
                                return "linked: boolean expected";
                        return null;
                    };

                    /**
                     * Creates a SeatMap message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {es.onebox.venue.venuetemplates.SeatMap} SeatMap
                     */
                    SeatMap.fromObject = function fromObject(object) {
                        if (object instanceof $root.es.onebox.venue.venuetemplates.SeatMap)
                            return object;
                        var message = new $root.es.onebox.venue.venuetemplates.SeatMap();
                        if (object.id != null)
                            if ($util.Long)
                                (message.id = $util.Long.fromValue(object.id)).unsigned = false;
                            else if (typeof object.id === "string")
                                message.id = parseInt(object.id, 10);
                            else if (typeof object.id === "number")
                                message.id = object.id;
                            else if (typeof object.id === "object")
                                message.id = new $util.LongBits(object.id.low >>> 0, object.id.high >>> 0).toNumber();
                        if (object.name != null)
                            message.name = String(object.name);
                        switch (object.status) {
                            case "UNKNOWN":
                            case 0:
                                message.status = 0;
                                break;
                            case "FREE":
                            case 1:
                                message.status = 1;
                                break;
                            case "SOLD":
                            case 2:
                                message.status = 2;
                                break;
                            case "PROMOTOR_LOCKED":
                            case 3:
                                message.status = 3;
                                break;
                            case "SYSTEM_LOCKED":
                            case 4:
                                message.status = 4;
                                break;
                            case "BOOKED":
                            case 5:
                                message.status = 5;
                                break;
                            case "KILL":
                            case 6:
                                message.status = 6;
                                break;
                            case "EMITTED":
                            case 7:
                                message.status = 7;
                                break;
                            case "VALIDATED":
                            case 8:
                                message.status = 8;
                                break;
                            case "IN_REFUND":
                            case 9:
                                message.status = 9;
                                break;
                            case "CANCELLED":
                            case 10:
                                message.status = 10;
                                break;
                            case "PRESOLD_LOCKED":
                            case 11:
                                message.status = 11;
                                break;
                            case "SOLD_LOCKED":
                            case 12:
                                message.status = 12;
                                break;
                            case "GIFT":
                            case 13:
                                message.status = 13;
                                break;
                            case "SEASON_LOCKED":
                            case 14:
                                message.status = 14;
                                break;
                            case "EXTERNAL_LOCKED":
                            case 15:
                                message.status = 15;
                                break;
                            case "EXTERNAL_DELETE":
                            case 16:
                                message.status = 16;
                                break;
                        }
                        if (object.view != null)
                            message.view = object.view | 0;
                        if (object.rowBlock != null)
                            message.rowBlock = String(object.rowBlock);
                        if (object.quota != null)
                            message.quota = object.quota | 0;
                        if (object.priceType != null)
                            message.priceType = object.priceType | 0;
                        if (object.blockingReason != null)
                            message.blockingReason = object.blockingReason | 0;
                        switch (object.visibility) {
                            case "VIS_UNKNOWN":
                            case 0:
                                message.visibility = 0;
                                break;
                            case "FULL":
                            case 1:
                                message.visibility = 1;
                                break;
                            case "PARTIAL":
                            case 2:
                                message.visibility = 2;
                                break;
                            case "NONE":
                            case 3:
                                message.visibility = 3;
                                break;
                            case "SIDE":
                            case 4:
                                message.visibility = 4;
                                break;
                        }
                        switch (object.accessibility) {
                            case "ACC_UNKNOWN":
                            case 0:
                                message.accessibility = 0;
                                break;
                            case "NORMAL":
                            case 1:
                                message.accessibility = 1;
                                break;
                            case "REDUCED_MOBILITY":
                            case 2:
                                message.accessibility = 2;
                                break;
                        }
                        if (object.gate != null)
                            if ($util.Long)
                                (message.gate = $util.Long.fromValue(object.gate)).unsigned = false;
                            else if (typeof object.gate === "string")
                                message.gate = parseInt(object.gate, 10);
                            else if (typeof object.gate === "number")
                                message.gate = object.gate;
                            else if (typeof object.gate === "object")
                                message.gate = new $util.LongBits(object.gate.low >>> 0, object.gate.high >>> 0).toNumber();
                        if (object.dynamicTag1 != null)
                            if ($util.Long)
                                (message.dynamicTag1 = $util.Long.fromValue(object.dynamicTag1)).unsigned = false;
                            else if (typeof object.dynamicTag1 === "string")
                                message.dynamicTag1 = parseInt(object.dynamicTag1, 10);
                            else if (typeof object.dynamicTag1 === "number")
                                message.dynamicTag1 = object.dynamicTag1;
                            else if (typeof object.dynamicTag1 === "object")
                                message.dynamicTag1 = new $util.LongBits(object.dynamicTag1.low >>> 0, object.dynamicTag1.high >>> 0).toNumber();
                        if (object.dynamicTag2 != null)
                            if ($util.Long)
                                (message.dynamicTag2 = $util.Long.fromValue(object.dynamicTag2)).unsigned = false;
                            else if (typeof object.dynamicTag2 === "string")
                                message.dynamicTag2 = parseInt(object.dynamicTag2, 10);
                            else if (typeof object.dynamicTag2 === "number")
                                message.dynamicTag2 = object.dynamicTag2;
                            else if (typeof object.dynamicTag2 === "object")
                                message.dynamicTag2 = new $util.LongBits(object.dynamicTag2.low >>> 0, object.dynamicTag2.high >>> 0).toNumber();
                        if (object.external != null)
                            if ($util.Long)
                                (message.external = $util.Long.fromValue(object.external)).unsigned = false;
                            else if (typeof object.external === "string")
                                message.external = parseInt(object.external, 10);
                            else if (typeof object.external === "number")
                                message.external = object.external;
                            else if (typeof object.external === "object")
                                message.external = new $util.LongBits(object.external.low >>> 0, object.external.high >>> 0).toNumber();
                        if (object.order != null)
                            message.order = object.order | 0;
                        if (object.weight != null)
                            message.weight = object.weight | 0;
                        if (object.posX != null)
                            message.posX = object.posX | 0;
                        if (object.posY != null)
                            message.posY = object.posY | 0;
                        if (object.saveSequence != null)
                            if ($util.Long)
                                (message.saveSequence = $util.Long.fromValue(object.saveSequence)).unsigned = false;
                            else if (typeof object.saveSequence === "string")
                                message.saveSequence = parseInt(object.saveSequence, 10);
                            else if (typeof object.saveSequence === "number")
                                message.saveSequence = object.saveSequence;
                            else if (typeof object.saveSequence === "object")
                                message.saveSequence = new $util.LongBits(object.saveSequence.low >>> 0, object.saveSequence.high >>> 0).toNumber();
                        if (object.ticketId != null)
                            if ($util.Long)
                                (message.ticketId = $util.Long.fromValue(object.ticketId)).unsigned = false;
                            else if (typeof object.ticketId === "string")
                                message.ticketId = parseInt(object.ticketId, 10);
                            else if (typeof object.ticketId === "number")
                                message.ticketId = object.ticketId;
                            else if (typeof object.ticketId === "object")
                                message.ticketId = new $util.LongBits(object.ticketId.low >>> 0, object.ticketId.high >>> 0).toNumber();
                        if (object.sessionPack != null)
                            if ($util.Long)
                                (message.sessionPack = $util.Long.fromValue(object.sessionPack)).unsigned = false;
                            else if (typeof object.sessionPack === "string")
                                message.sessionPack = parseInt(object.sessionPack, 10);
                            else if (typeof object.sessionPack === "number")
                                message.sessionPack = object.sessionPack;
                            else if (typeof object.sessionPack === "object")
                                message.sessionPack = new $util.LongBits(object.sessionPack.low >>> 0, object.sessionPack.high >>> 0).toNumber();
                        if (object.linkable != null)
                            message.linkable = Boolean(object.linkable);
                        if (object.linked != null)
                            message.linked = Boolean(object.linked);
                        return message;
                    };

                    /**
                     * Creates a plain object from a SeatMap message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.SeatMap} message SeatMap
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    SeatMap.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        var object = {};
                        if (options.defaults) {
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.id = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.id = options.longs === String ? "0" : 0;
                            object.name = "";
                            object.status = options.enums === String ? "UNKNOWN" : 0;
                            object.view = 0;
                            object.rowBlock = "";
                            object.quota = 0;
                            object.priceType = 0;
                            object.blockingReason = 0;
                            object.visibility = options.enums === String ? "VIS_UNKNOWN" : 0;
                            object.accessibility = options.enums === String ? "ACC_UNKNOWN" : 0;
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.gate = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.gate = options.longs === String ? "0" : 0;
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.dynamicTag1 = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.dynamicTag1 = options.longs === String ? "0" : 0;
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.dynamicTag2 = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.dynamicTag2 = options.longs === String ? "0" : 0;
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.external = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.external = options.longs === String ? "0" : 0;
                            object.order = 0;
                            object.weight = 0;
                            object.posX = 0;
                            object.posY = 0;
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.saveSequence = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.saveSequence = options.longs === String ? "0" : 0;
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.ticketId = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.ticketId = options.longs === String ? "0" : 0;
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.sessionPack = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.sessionPack = options.longs === String ? "0" : 0;
                            object.linkable = false;
                            object.linked = false;
                        }
                        if (message.id != null && message.hasOwnProperty("id"))
                            if (typeof message.id === "number")
                                object.id = options.longs === String ? String(message.id) : message.id;
                            else
                                object.id = options.longs === String ? $util.Long.prototype.toString.call(message.id) : options.longs === Number ? new $util.LongBits(message.id.low >>> 0, message.id.high >>> 0).toNumber() : message.id;
                        if (message.name != null && message.hasOwnProperty("name"))
                            object.name = message.name;
                        if (message.status != null && message.hasOwnProperty("status"))
                            object.status = options.enums === String ? $root.es.onebox.venue.venuetemplates.Enums.SeatStatus[message.status] : message.status;
                        if (message.view != null && message.hasOwnProperty("view"))
                            object.view = message.view;
                        if (message.rowBlock != null && message.hasOwnProperty("rowBlock"))
                            object.rowBlock = message.rowBlock;
                        if (message.quota != null && message.hasOwnProperty("quota"))
                            object.quota = message.quota;
                        if (message.priceType != null && message.hasOwnProperty("priceType"))
                            object.priceType = message.priceType;
                        if (message.blockingReason != null && message.hasOwnProperty("blockingReason"))
                            object.blockingReason = message.blockingReason;
                        if (message.visibility != null && message.hasOwnProperty("visibility"))
                            object.visibility = options.enums === String ? $root.es.onebox.venue.venuetemplates.Enums.Visibility[message.visibility] : message.visibility;
                        if (message.accessibility != null && message.hasOwnProperty("accessibility"))
                            object.accessibility = options.enums === String ? $root.es.onebox.venue.venuetemplates.Enums.Accessibility[message.accessibility] : message.accessibility;
                        if (message.gate != null && message.hasOwnProperty("gate"))
                            if (typeof message.gate === "number")
                                object.gate = options.longs === String ? String(message.gate) : message.gate;
                            else
                                object.gate = options.longs === String ? $util.Long.prototype.toString.call(message.gate) : options.longs === Number ? new $util.LongBits(message.gate.low >>> 0, message.gate.high >>> 0).toNumber() : message.gate;
                        if (message.dynamicTag1 != null && message.hasOwnProperty("dynamicTag1"))
                            if (typeof message.dynamicTag1 === "number")
                                object.dynamicTag1 = options.longs === String ? String(message.dynamicTag1) : message.dynamicTag1;
                            else
                                object.dynamicTag1 = options.longs === String ? $util.Long.prototype.toString.call(message.dynamicTag1) : options.longs === Number ? new $util.LongBits(message.dynamicTag1.low >>> 0, message.dynamicTag1.high >>> 0).toNumber() : message.dynamicTag1;
                        if (message.dynamicTag2 != null && message.hasOwnProperty("dynamicTag2"))
                            if (typeof message.dynamicTag2 === "number")
                                object.dynamicTag2 = options.longs === String ? String(message.dynamicTag2) : message.dynamicTag2;
                            else
                                object.dynamicTag2 = options.longs === String ? $util.Long.prototype.toString.call(message.dynamicTag2) : options.longs === Number ? new $util.LongBits(message.dynamicTag2.low >>> 0, message.dynamicTag2.high >>> 0).toNumber() : message.dynamicTag2;
                        if (message.external != null && message.hasOwnProperty("external"))
                            if (typeof message.external === "number")
                                object.external = options.longs === String ? String(message.external) : message.external;
                            else
                                object.external = options.longs === String ? $util.Long.prototype.toString.call(message.external) : options.longs === Number ? new $util.LongBits(message.external.low >>> 0, message.external.high >>> 0).toNumber() : message.external;
                        if (message.order != null && message.hasOwnProperty("order"))
                            object.order = message.order;
                        if (message.weight != null && message.hasOwnProperty("weight"))
                            object.weight = message.weight;
                        if (message.posX != null && message.hasOwnProperty("posX"))
                            object.posX = message.posX;
                        if (message.posY != null && message.hasOwnProperty("posY"))
                            object.posY = message.posY;
                        if (message.saveSequence != null && message.hasOwnProperty("saveSequence"))
                            if (typeof message.saveSequence === "number")
                                object.saveSequence = options.longs === String ? String(message.saveSequence) : message.saveSequence;
                            else
                                object.saveSequence = options.longs === String ? $util.Long.prototype.toString.call(message.saveSequence) : options.longs === Number ? new $util.LongBits(message.saveSequence.low >>> 0, message.saveSequence.high >>> 0).toNumber() : message.saveSequence;
                        if (message.ticketId != null && message.hasOwnProperty("ticketId"))
                            if (typeof message.ticketId === "number")
                                object.ticketId = options.longs === String ? String(message.ticketId) : message.ticketId;
                            else
                                object.ticketId = options.longs === String ? $util.Long.prototype.toString.call(message.ticketId) : options.longs === Number ? new $util.LongBits(message.ticketId.low >>> 0, message.ticketId.high >>> 0).toNumber() : message.ticketId;
                        if (message.sessionPack != null && message.hasOwnProperty("sessionPack"))
                            if (typeof message.sessionPack === "number")
                                object.sessionPack = options.longs === String ? String(message.sessionPack) : message.sessionPack;
                            else
                                object.sessionPack = options.longs === String ? $util.Long.prototype.toString.call(message.sessionPack) : options.longs === Number ? new $util.LongBits(message.sessionPack.low >>> 0, message.sessionPack.high >>> 0).toNumber() : message.sessionPack;
                        if (message.linkable != null && message.hasOwnProperty("linkable"))
                            object.linkable = message.linkable;
                        if (message.linked != null && message.hasOwnProperty("linked"))
                            object.linked = message.linked;
                        return object;
                    };

                    /**
                     * Converts this SeatMap to JSON.
                     * @function toJSON
                     * @memberof es.onebox.venue.venuetemplates.SeatMap
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    SeatMap.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    return SeatMap;
                })();

                venuetemplates.NotNumberedZoneMap = (function () {

                    /**
                     * Properties of a NotNumberedZoneMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @interface INotNumberedZoneMap
                     * @property {number|Long|null} [id] NotNumberedZoneMap id
                     * @property {string|null} [name] NotNumberedZoneMap name
                     * @property {number|null} [sector] NotNumberedZoneMap sector
                     * @property {number|null} [priceType] NotNumberedZoneMap priceType
                     * @property {number|null} [view] NotNumberedZoneMap view
                     * @property {es.onebox.venue.venuetemplates.Enums.Visibility|null} [visibility] NotNumberedZoneMap visibility
                     * @property {es.onebox.venue.venuetemplates.Enums.Accessibility|null} [accessibility] NotNumberedZoneMap accessibility
                     * @property {number|Long|null} [gate] NotNumberedZoneMap gate
                     * @property {number|Long|null} [dynamicTag1] NotNumberedZoneMap dynamicTag1
                     * @property {number|Long|null} [dynamicTag2] NotNumberedZoneMap dynamicTag2
                     * @property {number|null} [capacity] NotNumberedZoneMap capacity
                     * @property {number|null} [oldCapacity] NotNumberedZoneMap oldCapacity
                     * @property {number|Long|null} [saveSequence] NotNumberedZoneMap saveSequence
                     * @property {number|null} [linkableSeats] NotNumberedZoneMap linkableSeats
                     * @property {Array.<es.onebox.venue.venuetemplates.IStatusCounterMap>|null} [statusCounters] NotNumberedZoneMap statusCounters
                     * @property {Array.<es.onebox.venue.venuetemplates.IBlockingReasonsCounterMap>|null} [blockingReasonCounters] NotNumberedZoneMap blockingReasonCounters
                     * @property {Array.<es.onebox.venue.venuetemplates.ISessionPackCounterMap>|null} [sessionPackCounters] NotNumberedZoneMap sessionPackCounters
                     * @property {Array.<es.onebox.venue.venuetemplates.IQuotaCountersMap>|null} [quotaCounters] NotNumberedZoneMap quotaCounters
                     */

                    /**
                     * Constructs a new NotNumberedZoneMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @classdesc Represents a NotNumberedZoneMap.
                     * @implements INotNumberedZoneMap
                     * @constructor
                     * @param {es.onebox.venue.venuetemplates.INotNumberedZoneMap=} [properties] Properties to set
                     */
                    function NotNumberedZoneMap(properties) {
                        this.statusCounters = [];
                        this.blockingReasonCounters = [];
                        this.sessionPackCounters = [];
                        this.quotaCounters = [];
                        if (properties)
                            for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * NotNumberedZoneMap id.
                     * @member {number|Long} id
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.id = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * NotNumberedZoneMap name.
                     * @member {string} name
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.name = "";

                    /**
                     * NotNumberedZoneMap sector.
                     * @member {number} sector
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.sector = 0;

                    /**
                     * NotNumberedZoneMap priceType.
                     * @member {number} priceType
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.priceType = 0;

                    /**
                     * NotNumberedZoneMap view.
                     * @member {number} view
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.view = 0;

                    /**
                     * NotNumberedZoneMap visibility.
                     * @member {es.onebox.venue.venuetemplates.Enums.Visibility} visibility
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.visibility = 0;

                    /**
                     * NotNumberedZoneMap accessibility.
                     * @member {es.onebox.venue.venuetemplates.Enums.Accessibility} accessibility
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.accessibility = 0;

                    /**
                     * NotNumberedZoneMap gate.
                     * @member {number|Long} gate
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.gate = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * NotNumberedZoneMap dynamicTag1.
                     * @member {number|Long} dynamicTag1
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.dynamicTag1 = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * NotNumberedZoneMap dynamicTag2.
                     * @member {number|Long} dynamicTag2
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.dynamicTag2 = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * NotNumberedZoneMap capacity.
                     * @member {number} capacity
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.capacity = 0;

                    /**
                     * NotNumberedZoneMap oldCapacity.
                     * @member {number} oldCapacity
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.oldCapacity = 0;

                    /**
                     * NotNumberedZoneMap saveSequence.
                     * @member {number|Long} saveSequence
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.saveSequence = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * NotNumberedZoneMap linkableSeats.
                     * @member {number} linkableSeats
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.linkableSeats = 0;

                    /**
                     * NotNumberedZoneMap statusCounters.
                     * @member {Array.<es.onebox.venue.venuetemplates.IStatusCounterMap>} statusCounters
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.statusCounters = $util.emptyArray;

                    /**
                     * NotNumberedZoneMap blockingReasonCounters.
                     * @member {Array.<es.onebox.venue.venuetemplates.IBlockingReasonsCounterMap>} blockingReasonCounters
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.blockingReasonCounters = $util.emptyArray;

                    /**
                     * NotNumberedZoneMap sessionPackCounters.
                     * @member {Array.<es.onebox.venue.venuetemplates.ISessionPackCounterMap>} sessionPackCounters
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.sessionPackCounters = $util.emptyArray;

                    /**
                     * NotNumberedZoneMap quotaCounters.
                     * @member {Array.<es.onebox.venue.venuetemplates.IQuotaCountersMap>} quotaCounters
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     */
                    NotNumberedZoneMap.prototype.quotaCounters = $util.emptyArray;

                    /**
                     * Creates a new NotNumberedZoneMap instance using the specified properties.
                     * @function create
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.INotNumberedZoneMap=} [properties] Properties to set
                     * @returns {es.onebox.venue.venuetemplates.NotNumberedZoneMap} NotNumberedZoneMap instance
                     */
                    NotNumberedZoneMap.create = function create(properties) {
                        return new NotNumberedZoneMap(properties);
                    };

                    /**
                     * Encodes the specified NotNumberedZoneMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.NotNumberedZoneMap.verify|verify} messages.
                     * @function encode
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.INotNumberedZoneMap} message NotNumberedZoneMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    NotNumberedZoneMap.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.id != null && Object.hasOwnProperty.call(message, "id"))
                            writer.uint32(/* id 1, wireType 0 =*/8).int64(message.id);
                        if (message.name != null && Object.hasOwnProperty.call(message, "name"))
                            writer.uint32(/* id 2, wireType 2 =*/18).string(message.name);
                        if (message.sector != null && Object.hasOwnProperty.call(message, "sector"))
                            writer.uint32(/* id 3, wireType 0 =*/24).int32(message.sector);
                        if (message.priceType != null && Object.hasOwnProperty.call(message, "priceType"))
                            writer.uint32(/* id 4, wireType 0 =*/32).int32(message.priceType);
                        if (message.view != null && Object.hasOwnProperty.call(message, "view"))
                            writer.uint32(/* id 5, wireType 0 =*/40).int32(message.view);
                        if (message.visibility != null && Object.hasOwnProperty.call(message, "visibility"))
                            writer.uint32(/* id 6, wireType 0 =*/48).int32(message.visibility);
                        if (message.accessibility != null && Object.hasOwnProperty.call(message, "accessibility"))
                            writer.uint32(/* id 7, wireType 0 =*/56).int32(message.accessibility);
                        if (message.gate != null && Object.hasOwnProperty.call(message, "gate"))
                            writer.uint32(/* id 8, wireType 0 =*/64).int64(message.gate);
                        if (message.dynamicTag1 != null && Object.hasOwnProperty.call(message, "dynamicTag1"))
                            writer.uint32(/* id 9, wireType 0 =*/72).int64(message.dynamicTag1);
                        if (message.dynamicTag2 != null && Object.hasOwnProperty.call(message, "dynamicTag2"))
                            writer.uint32(/* id 10, wireType 0 =*/80).int64(message.dynamicTag2);
                        if (message.capacity != null && Object.hasOwnProperty.call(message, "capacity"))
                            writer.uint32(/* id 11, wireType 0 =*/88).int32(message.capacity);
                        if (message.oldCapacity != null && Object.hasOwnProperty.call(message, "oldCapacity"))
                            writer.uint32(/* id 12, wireType 0 =*/96).int32(message.oldCapacity);
                        if (message.saveSequence != null && Object.hasOwnProperty.call(message, "saveSequence"))
                            writer.uint32(/* id 13, wireType 0 =*/104).int64(message.saveSequence);
                        if (message.linkableSeats != null && Object.hasOwnProperty.call(message, "linkableSeats"))
                            writer.uint32(/* id 14, wireType 0 =*/112).int32(message.linkableSeats);
                        if (message.statusCounters != null && message.statusCounters.length)
                            for (var i = 0; i < message.statusCounters.length; ++i)
                                $root.es.onebox.venue.venuetemplates.StatusCounterMap.encode(message.statusCounters[i], writer.uint32(/* id 15, wireType 2 =*/122).fork()).ldelim();
                        if (message.blockingReasonCounters != null && message.blockingReasonCounters.length)
                            for (var i = 0; i < message.blockingReasonCounters.length; ++i)
                                $root.es.onebox.venue.venuetemplates.BlockingReasonsCounterMap.encode(message.blockingReasonCounters[i], writer.uint32(/* id 16, wireType 2 =*/130).fork()).ldelim();
                        if (message.sessionPackCounters != null && message.sessionPackCounters.length)
                            for (var i = 0; i < message.sessionPackCounters.length; ++i)
                                $root.es.onebox.venue.venuetemplates.SessionPackCounterMap.encode(message.sessionPackCounters[i], writer.uint32(/* id 17, wireType 2 =*/138).fork()).ldelim();
                        if (message.quotaCounters != null && message.quotaCounters.length)
                            for (var i = 0; i < message.quotaCounters.length; ++i)
                                $root.es.onebox.venue.venuetemplates.QuotaCountersMap.encode(message.quotaCounters[i], writer.uint32(/* id 18, wireType 2 =*/146).fork()).ldelim();
                        return writer;
                    };

                    /**
                     * Encodes the specified NotNumberedZoneMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.NotNumberedZoneMap.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.INotNumberedZoneMap} message NotNumberedZoneMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    NotNumberedZoneMap.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a NotNumberedZoneMap message from the specified reader or buffer.
                     * @function decode
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {es.onebox.venue.venuetemplates.NotNumberedZoneMap} NotNumberedZoneMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    NotNumberedZoneMap.decode = function decode(reader, length) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        var end = length === undefined ? reader.len : reader.pos + length, message = new $root.es.onebox.venue.venuetemplates.NotNumberedZoneMap();
                        while (reader.pos < end) {
                            var tag = reader.uint32();
                            switch (tag >>> 3) {
                                case 1:
                                    message.id = reader.int64();
                                    break;
                                case 2:
                                    message.name = reader.string();
                                    break;
                                case 3:
                                    message.sector = reader.int32();
                                    break;
                                case 4:
                                    message.priceType = reader.int32();
                                    break;
                                case 5:
                                    message.view = reader.int32();
                                    break;
                                case 6:
                                    message.visibility = reader.int32();
                                    break;
                                case 7:
                                    message.accessibility = reader.int32();
                                    break;
                                case 8:
                                    message.gate = reader.int64();
                                    break;
                                case 9:
                                    message.dynamicTag1 = reader.int64();
                                    break;
                                case 10:
                                    message.dynamicTag2 = reader.int64();
                                    break;
                                case 11:
                                    message.capacity = reader.int32();
                                    break;
                                case 12:
                                    message.oldCapacity = reader.int32();
                                    break;
                                case 13:
                                    message.saveSequence = reader.int64();
                                    break;
                                case 14:
                                    message.linkableSeats = reader.int32();
                                    break;
                                case 15:
                                    if (!(message.statusCounters && message.statusCounters.length))
                                        message.statusCounters = [];
                                    message.statusCounters.push($root.es.onebox.venue.venuetemplates.StatusCounterMap.decode(reader, reader.uint32()));
                                    break;
                                case 16:
                                    if (!(message.blockingReasonCounters && message.blockingReasonCounters.length))
                                        message.blockingReasonCounters = [];
                                    message.blockingReasonCounters.push($root.es.onebox.venue.venuetemplates.BlockingReasonsCounterMap.decode(reader, reader.uint32()));
                                    break;
                                case 17:
                                    if (!(message.sessionPackCounters && message.sessionPackCounters.length))
                                        message.sessionPackCounters = [];
                                    message.sessionPackCounters.push($root.es.onebox.venue.venuetemplates.SessionPackCounterMap.decode(reader, reader.uint32()));
                                    break;
                                case 18:
                                    if (!(message.quotaCounters && message.quotaCounters.length))
                                        message.quotaCounters = [];
                                    message.quotaCounters.push($root.es.onebox.venue.venuetemplates.QuotaCountersMap.decode(reader, reader.uint32()));
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a NotNumberedZoneMap message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {es.onebox.venue.venuetemplates.NotNumberedZoneMap} NotNumberedZoneMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    NotNumberedZoneMap.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a NotNumberedZoneMap message.
                     * @function verify
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    NotNumberedZoneMap.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.id != null && message.hasOwnProperty("id"))
                            if (!$util.isInteger(message.id) && !(message.id && $util.isInteger(message.id.low) && $util.isInteger(message.id.high)))
                                return "id: integer|Long expected";
                        if (message.name != null && message.hasOwnProperty("name"))
                            if (!$util.isString(message.name))
                                return "name: string expected";
                        if (message.sector != null && message.hasOwnProperty("sector"))
                            if (!$util.isInteger(message.sector))
                                return "sector: integer expected";
                        if (message.priceType != null && message.hasOwnProperty("priceType"))
                            if (!$util.isInteger(message.priceType))
                                return "priceType: integer expected";
                        if (message.view != null && message.hasOwnProperty("view"))
                            if (!$util.isInteger(message.view))
                                return "view: integer expected";
                        if (message.visibility != null && message.hasOwnProperty("visibility"))
                            switch (message.visibility) {
                                default:
                                    return "visibility: enum value expected";
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                    break;
                            }
                        if (message.accessibility != null && message.hasOwnProperty("accessibility"))
                            switch (message.accessibility) {
                                default:
                                    return "accessibility: enum value expected";
                                case 0:
                                case 1:
                                case 2:
                                    break;
                            }
                        if (message.gate != null && message.hasOwnProperty("gate"))
                            if (!$util.isInteger(message.gate) && !(message.gate && $util.isInteger(message.gate.low) && $util.isInteger(message.gate.high)))
                                return "gate: integer|Long expected";
                        if (message.dynamicTag1 != null && message.hasOwnProperty("dynamicTag1"))
                            if (!$util.isInteger(message.dynamicTag1) && !(message.dynamicTag1 && $util.isInteger(message.dynamicTag1.low) && $util.isInteger(message.dynamicTag1.high)))
                                return "dynamicTag1: integer|Long expected";
                        if (message.dynamicTag2 != null && message.hasOwnProperty("dynamicTag2"))
                            if (!$util.isInteger(message.dynamicTag2) && !(message.dynamicTag2 && $util.isInteger(message.dynamicTag2.low) && $util.isInteger(message.dynamicTag2.high)))
                                return "dynamicTag2: integer|Long expected";
                        if (message.capacity != null && message.hasOwnProperty("capacity"))
                            if (!$util.isInteger(message.capacity))
                                return "capacity: integer expected";
                        if (message.oldCapacity != null && message.hasOwnProperty("oldCapacity"))
                            if (!$util.isInteger(message.oldCapacity))
                                return "oldCapacity: integer expected";
                        if (message.saveSequence != null && message.hasOwnProperty("saveSequence"))
                            if (!$util.isInteger(message.saveSequence) && !(message.saveSequence && $util.isInteger(message.saveSequence.low) && $util.isInteger(message.saveSequence.high)))
                                return "saveSequence: integer|Long expected";
                        if (message.linkableSeats != null && message.hasOwnProperty("linkableSeats"))
                            if (!$util.isInteger(message.linkableSeats))
                                return "linkableSeats: integer expected";
                        if (message.statusCounters != null && message.hasOwnProperty("statusCounters")) {
                            if (!Array.isArray(message.statusCounters))
                                return "statusCounters: array expected";
                            for (var i = 0; i < message.statusCounters.length; ++i) {
                                var error = $root.es.onebox.venue.venuetemplates.StatusCounterMap.verify(message.statusCounters[i]);
                                if (error)
                                    return "statusCounters." + error;
                            }
                        }
                        if (message.blockingReasonCounters != null && message.hasOwnProperty("blockingReasonCounters")) {
                            if (!Array.isArray(message.blockingReasonCounters))
                                return "blockingReasonCounters: array expected";
                            for (var i = 0; i < message.blockingReasonCounters.length; ++i) {
                                var error = $root.es.onebox.venue.venuetemplates.BlockingReasonsCounterMap.verify(message.blockingReasonCounters[i]);
                                if (error)
                                    return "blockingReasonCounters." + error;
                            }
                        }
                        if (message.sessionPackCounters != null && message.hasOwnProperty("sessionPackCounters")) {
                            if (!Array.isArray(message.sessionPackCounters))
                                return "sessionPackCounters: array expected";
                            for (var i = 0; i < message.sessionPackCounters.length; ++i) {
                                var error = $root.es.onebox.venue.venuetemplates.SessionPackCounterMap.verify(message.sessionPackCounters[i]);
                                if (error)
                                    return "sessionPackCounters." + error;
                            }
                        }
                        if (message.quotaCounters != null && message.hasOwnProperty("quotaCounters")) {
                            if (!Array.isArray(message.quotaCounters))
                                return "quotaCounters: array expected";
                            for (var i = 0; i < message.quotaCounters.length; ++i) {
                                var error = $root.es.onebox.venue.venuetemplates.QuotaCountersMap.verify(message.quotaCounters[i]);
                                if (error)
                                    return "quotaCounters." + error;
                            }
                        }
                        return null;
                    };

                    /**
                     * Creates a NotNumberedZoneMap message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {es.onebox.venue.venuetemplates.NotNumberedZoneMap} NotNumberedZoneMap
                     */
                    NotNumberedZoneMap.fromObject = function fromObject(object) {
                        if (object instanceof $root.es.onebox.venue.venuetemplates.NotNumberedZoneMap)
                            return object;
                        var message = new $root.es.onebox.venue.venuetemplates.NotNumberedZoneMap();
                        if (object.id != null)
                            if ($util.Long)
                                (message.id = $util.Long.fromValue(object.id)).unsigned = false;
                            else if (typeof object.id === "string")
                                message.id = parseInt(object.id, 10);
                            else if (typeof object.id === "number")
                                message.id = object.id;
                            else if (typeof object.id === "object")
                                message.id = new $util.LongBits(object.id.low >>> 0, object.id.high >>> 0).toNumber();
                        if (object.name != null)
                            message.name = String(object.name);
                        if (object.sector != null)
                            message.sector = object.sector | 0;
                        if (object.priceType != null)
                            message.priceType = object.priceType | 0;
                        if (object.view != null)
                            message.view = object.view | 0;
                        switch (object.visibility) {
                            case "VIS_UNKNOWN":
                            case 0:
                                message.visibility = 0;
                                break;
                            case "FULL":
                            case 1:
                                message.visibility = 1;
                                break;
                            case "PARTIAL":
                            case 2:
                                message.visibility = 2;
                                break;
                            case "NONE":
                            case 3:
                                message.visibility = 3;
                                break;
                            case "SIDE":
                            case 4:
                                message.visibility = 4;
                                break;
                        }
                        switch (object.accessibility) {
                            case "ACC_UNKNOWN":
                            case 0:
                                message.accessibility = 0;
                                break;
                            case "NORMAL":
                            case 1:
                                message.accessibility = 1;
                                break;
                            case "REDUCED_MOBILITY":
                            case 2:
                                message.accessibility = 2;
                                break;
                        }
                        if (object.gate != null)
                            if ($util.Long)
                                (message.gate = $util.Long.fromValue(object.gate)).unsigned = false;
                            else if (typeof object.gate === "string")
                                message.gate = parseInt(object.gate, 10);
                            else if (typeof object.gate === "number")
                                message.gate = object.gate;
                            else if (typeof object.gate === "object")
                                message.gate = new $util.LongBits(object.gate.low >>> 0, object.gate.high >>> 0).toNumber();
                        if (object.dynamicTag1 != null)
                            if ($util.Long)
                                (message.dynamicTag1 = $util.Long.fromValue(object.dynamicTag1)).unsigned = false;
                            else if (typeof object.dynamicTag1 === "string")
                                message.dynamicTag1 = parseInt(object.dynamicTag1, 10);
                            else if (typeof object.dynamicTag1 === "number")
                                message.dynamicTag1 = object.dynamicTag1;
                            else if (typeof object.dynamicTag1 === "object")
                                message.dynamicTag1 = new $util.LongBits(object.dynamicTag1.low >>> 0, object.dynamicTag1.high >>> 0).toNumber();
                        if (object.dynamicTag2 != null)
                            if ($util.Long)
                                (message.dynamicTag2 = $util.Long.fromValue(object.dynamicTag2)).unsigned = false;
                            else if (typeof object.dynamicTag2 === "string")
                                message.dynamicTag2 = parseInt(object.dynamicTag2, 10);
                            else if (typeof object.dynamicTag2 === "number")
                                message.dynamicTag2 = object.dynamicTag2;
                            else if (typeof object.dynamicTag2 === "object")
                                message.dynamicTag2 = new $util.LongBits(object.dynamicTag2.low >>> 0, object.dynamicTag2.high >>> 0).toNumber();
                        if (object.capacity != null)
                            message.capacity = object.capacity | 0;
                        if (object.oldCapacity != null)
                            message.oldCapacity = object.oldCapacity | 0;
                        if (object.saveSequence != null)
                            if ($util.Long)
                                (message.saveSequence = $util.Long.fromValue(object.saveSequence)).unsigned = false;
                            else if (typeof object.saveSequence === "string")
                                message.saveSequence = parseInt(object.saveSequence, 10);
                            else if (typeof object.saveSequence === "number")
                                message.saveSequence = object.saveSequence;
                            else if (typeof object.saveSequence === "object")
                                message.saveSequence = new $util.LongBits(object.saveSequence.low >>> 0, object.saveSequence.high >>> 0).toNumber();
                        if (object.linkableSeats != null)
                            message.linkableSeats = object.linkableSeats | 0;
                        if (object.statusCounters) {
                            if (!Array.isArray(object.statusCounters))
                                throw TypeError(".es.onebox.venue.venuetemplates.NotNumberedZoneMap.statusCounters: array expected");
                            message.statusCounters = [];
                            for (var i = 0; i < object.statusCounters.length; ++i) {
                                if (typeof object.statusCounters[i] !== "object")
                                    throw TypeError(".es.onebox.venue.venuetemplates.NotNumberedZoneMap.statusCounters: object expected");
                                message.statusCounters[i] = $root.es.onebox.venue.venuetemplates.StatusCounterMap.fromObject(object.statusCounters[i]);
                            }
                        }
                        if (object.blockingReasonCounters) {
                            if (!Array.isArray(object.blockingReasonCounters))
                                throw TypeError(".es.onebox.venue.venuetemplates.NotNumberedZoneMap.blockingReasonCounters: array expected");
                            message.blockingReasonCounters = [];
                            for (var i = 0; i < object.blockingReasonCounters.length; ++i) {
                                if (typeof object.blockingReasonCounters[i] !== "object")
                                    throw TypeError(".es.onebox.venue.venuetemplates.NotNumberedZoneMap.blockingReasonCounters: object expected");
                                message.blockingReasonCounters[i] = $root.es.onebox.venue.venuetemplates.BlockingReasonsCounterMap.fromObject(object.blockingReasonCounters[i]);
                            }
                        }
                        if (object.sessionPackCounters) {
                            if (!Array.isArray(object.sessionPackCounters))
                                throw TypeError(".es.onebox.venue.venuetemplates.NotNumberedZoneMap.sessionPackCounters: array expected");
                            message.sessionPackCounters = [];
                            for (var i = 0; i < object.sessionPackCounters.length; ++i) {
                                if (typeof object.sessionPackCounters[i] !== "object")
                                    throw TypeError(".es.onebox.venue.venuetemplates.NotNumberedZoneMap.sessionPackCounters: object expected");
                                message.sessionPackCounters[i] = $root.es.onebox.venue.venuetemplates.SessionPackCounterMap.fromObject(object.sessionPackCounters[i]);
                            }
                        }
                        if (object.quotaCounters) {
                            if (!Array.isArray(object.quotaCounters))
                                throw TypeError(".es.onebox.venue.venuetemplates.NotNumberedZoneMap.quotaCounters: array expected");
                            message.quotaCounters = [];
                            for (var i = 0; i < object.quotaCounters.length; ++i) {
                                if (typeof object.quotaCounters[i] !== "object")
                                    throw TypeError(".es.onebox.venue.venuetemplates.NotNumberedZoneMap.quotaCounters: object expected");
                                message.quotaCounters[i] = $root.es.onebox.venue.venuetemplates.QuotaCountersMap.fromObject(object.quotaCounters[i]);
                            }
                        }
                        return message;
                    };

                    /**
                     * Creates a plain object from a NotNumberedZoneMap message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.NotNumberedZoneMap} message NotNumberedZoneMap
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    NotNumberedZoneMap.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        var object = {};
                        if (options.arrays || options.defaults) {
                            object.statusCounters = [];
                            object.blockingReasonCounters = [];
                            object.sessionPackCounters = [];
                            object.quotaCounters = [];
                        }
                        if (options.defaults) {
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.id = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.id = options.longs === String ? "0" : 0;
                            object.name = "";
                            object.sector = 0;
                            object.priceType = 0;
                            object.view = 0;
                            object.visibility = options.enums === String ? "VIS_UNKNOWN" : 0;
                            object.accessibility = options.enums === String ? "ACC_UNKNOWN" : 0;
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.gate = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.gate = options.longs === String ? "0" : 0;
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.dynamicTag1 = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.dynamicTag1 = options.longs === String ? "0" : 0;
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.dynamicTag2 = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.dynamicTag2 = options.longs === String ? "0" : 0;
                            object.capacity = 0;
                            object.oldCapacity = 0;
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.saveSequence = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.saveSequence = options.longs === String ? "0" : 0;
                            object.linkableSeats = 0;
                        }
                        if (message.id != null && message.hasOwnProperty("id"))
                            if (typeof message.id === "number")
                                object.id = options.longs === String ? String(message.id) : message.id;
                            else
                                object.id = options.longs === String ? $util.Long.prototype.toString.call(message.id) : options.longs === Number ? new $util.LongBits(message.id.low >>> 0, message.id.high >>> 0).toNumber() : message.id;
                        if (message.name != null && message.hasOwnProperty("name"))
                            object.name = message.name;
                        if (message.sector != null && message.hasOwnProperty("sector"))
                            object.sector = message.sector;
                        if (message.priceType != null && message.hasOwnProperty("priceType"))
                            object.priceType = message.priceType;
                        if (message.view != null && message.hasOwnProperty("view"))
                            object.view = message.view;
                        if (message.visibility != null && message.hasOwnProperty("visibility"))
                            object.visibility = options.enums === String ? $root.es.onebox.venue.venuetemplates.Enums.Visibility[message.visibility] : message.visibility;
                        if (message.accessibility != null && message.hasOwnProperty("accessibility"))
                            object.accessibility = options.enums === String ? $root.es.onebox.venue.venuetemplates.Enums.Accessibility[message.accessibility] : message.accessibility;
                        if (message.gate != null && message.hasOwnProperty("gate"))
                            if (typeof message.gate === "number")
                                object.gate = options.longs === String ? String(message.gate) : message.gate;
                            else
                                object.gate = options.longs === String ? $util.Long.prototype.toString.call(message.gate) : options.longs === Number ? new $util.LongBits(message.gate.low >>> 0, message.gate.high >>> 0).toNumber() : message.gate;
                        if (message.dynamicTag1 != null && message.hasOwnProperty("dynamicTag1"))
                            if (typeof message.dynamicTag1 === "number")
                                object.dynamicTag1 = options.longs === String ? String(message.dynamicTag1) : message.dynamicTag1;
                            else
                                object.dynamicTag1 = options.longs === String ? $util.Long.prototype.toString.call(message.dynamicTag1) : options.longs === Number ? new $util.LongBits(message.dynamicTag1.low >>> 0, message.dynamicTag1.high >>> 0).toNumber() : message.dynamicTag1;
                        if (message.dynamicTag2 != null && message.hasOwnProperty("dynamicTag2"))
                            if (typeof message.dynamicTag2 === "number")
                                object.dynamicTag2 = options.longs === String ? String(message.dynamicTag2) : message.dynamicTag2;
                            else
                                object.dynamicTag2 = options.longs === String ? $util.Long.prototype.toString.call(message.dynamicTag2) : options.longs === Number ? new $util.LongBits(message.dynamicTag2.low >>> 0, message.dynamicTag2.high >>> 0).toNumber() : message.dynamicTag2;
                        if (message.capacity != null && message.hasOwnProperty("capacity"))
                            object.capacity = message.capacity;
                        if (message.oldCapacity != null && message.hasOwnProperty("oldCapacity"))
                            object.oldCapacity = message.oldCapacity;
                        if (message.saveSequence != null && message.hasOwnProperty("saveSequence"))
                            if (typeof message.saveSequence === "number")
                                object.saveSequence = options.longs === String ? String(message.saveSequence) : message.saveSequence;
                            else
                                object.saveSequence = options.longs === String ? $util.Long.prototype.toString.call(message.saveSequence) : options.longs === Number ? new $util.LongBits(message.saveSequence.low >>> 0, message.saveSequence.high >>> 0).toNumber() : message.saveSequence;
                        if (message.linkableSeats != null && message.hasOwnProperty("linkableSeats"))
                            object.linkableSeats = message.linkableSeats;
                        if (message.statusCounters && message.statusCounters.length) {
                            object.statusCounters = [];
                            for (var j = 0; j < message.statusCounters.length; ++j)
                                object.statusCounters[j] = $root.es.onebox.venue.venuetemplates.StatusCounterMap.toObject(message.statusCounters[j], options);
                        }
                        if (message.blockingReasonCounters && message.blockingReasonCounters.length) {
                            object.blockingReasonCounters = [];
                            for (var j = 0; j < message.blockingReasonCounters.length; ++j)
                                object.blockingReasonCounters[j] = $root.es.onebox.venue.venuetemplates.BlockingReasonsCounterMap.toObject(message.blockingReasonCounters[j], options);
                        }
                        if (message.sessionPackCounters && message.sessionPackCounters.length) {
                            object.sessionPackCounters = [];
                            for (var j = 0; j < message.sessionPackCounters.length; ++j)
                                object.sessionPackCounters[j] = $root.es.onebox.venue.venuetemplates.SessionPackCounterMap.toObject(message.sessionPackCounters[j], options);
                        }
                        if (message.quotaCounters && message.quotaCounters.length) {
                            object.quotaCounters = [];
                            for (var j = 0; j < message.quotaCounters.length; ++j)
                                object.quotaCounters[j] = $root.es.onebox.venue.venuetemplates.QuotaCountersMap.toObject(message.quotaCounters[j], options);
                        }
                        return object;
                    };

                    /**
                     * Converts this NotNumberedZoneMap to JSON.
                     * @function toJSON
                     * @memberof es.onebox.venue.venuetemplates.NotNumberedZoneMap
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    NotNumberedZoneMap.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    return NotNumberedZoneMap;
                })();

                venuetemplates.StatusCounterMap = (function () {

                    /**
                     * Properties of a StatusCounterMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @interface IStatusCounterMap
                     * @property {es.onebox.venue.venuetemplates.Enums.SeatStatus|null} [status] StatusCounterMap status
                     * @property {number|null} [count] StatusCounterMap count
                     * @property {boolean|null} [linked] StatusCounterMap linked
                     */

                    /**
                     * Constructs a new StatusCounterMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @classdesc Represents a StatusCounterMap.
                     * @implements IStatusCounterMap
                     * @constructor
                     * @param {es.onebox.venue.venuetemplates.IStatusCounterMap=} [properties] Properties to set
                     */
                    function StatusCounterMap(properties) {
                        if (properties)
                            for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * StatusCounterMap status.
                     * @member {es.onebox.venue.venuetemplates.Enums.SeatStatus} status
                     * @memberof es.onebox.venue.venuetemplates.StatusCounterMap
                     * @instance
                     */
                    StatusCounterMap.prototype.status = 0;

                    /**
                     * StatusCounterMap count.
                     * @member {number} count
                     * @memberof es.onebox.venue.venuetemplates.StatusCounterMap
                     * @instance
                     */
                    StatusCounterMap.prototype.count = 0;

                    /**
                     * StatusCounterMap linked.
                     * @member {boolean} linked
                     * @memberof es.onebox.venue.venuetemplates.StatusCounterMap
                     * @instance
                     */
                    StatusCounterMap.prototype.linked = false;

                    /**
                     * Creates a new StatusCounterMap instance using the specified properties.
                     * @function create
                     * @memberof es.onebox.venue.venuetemplates.StatusCounterMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IStatusCounterMap=} [properties] Properties to set
                     * @returns {es.onebox.venue.venuetemplates.StatusCounterMap} StatusCounterMap instance
                     */
                    StatusCounterMap.create = function create(properties) {
                        return new StatusCounterMap(properties);
                    };

                    /**
                     * Encodes the specified StatusCounterMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.StatusCounterMap.verify|verify} messages.
                     * @function encode
                     * @memberof es.onebox.venue.venuetemplates.StatusCounterMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IStatusCounterMap} message StatusCounterMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    StatusCounterMap.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.status != null && Object.hasOwnProperty.call(message, "status"))
                            writer.uint32(/* id 1, wireType 0 =*/8).int32(message.status);
                        if (message.count != null && Object.hasOwnProperty.call(message, "count"))
                            writer.uint32(/* id 2, wireType 0 =*/16).int32(message.count);
                        if (message.linked != null && Object.hasOwnProperty.call(message, "linked"))
                            writer.uint32(/* id 3, wireType 0 =*/24).bool(message.linked);
                        return writer;
                    };

                    /**
                     * Encodes the specified StatusCounterMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.StatusCounterMap.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.StatusCounterMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IStatusCounterMap} message StatusCounterMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    StatusCounterMap.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a StatusCounterMap message from the specified reader or buffer.
                     * @function decode
                     * @memberof es.onebox.venue.venuetemplates.StatusCounterMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {es.onebox.venue.venuetemplates.StatusCounterMap} StatusCounterMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    StatusCounterMap.decode = function decode(reader, length) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        var end = length === undefined ? reader.len : reader.pos + length, message = new $root.es.onebox.venue.venuetemplates.StatusCounterMap();
                        while (reader.pos < end) {
                            var tag = reader.uint32();
                            switch (tag >>> 3) {
                                case 1:
                                    message.status = reader.int32();
                                    break;
                                case 2:
                                    message.count = reader.int32();
                                    break;
                                case 3:
                                    message.linked = reader.bool();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a StatusCounterMap message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.StatusCounterMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {es.onebox.venue.venuetemplates.StatusCounterMap} StatusCounterMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    StatusCounterMap.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a StatusCounterMap message.
                     * @function verify
                     * @memberof es.onebox.venue.venuetemplates.StatusCounterMap
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    StatusCounterMap.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.status != null && message.hasOwnProperty("status"))
                            switch (message.status) {
                                default:
                                    return "status: enum value expected";
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                case 7:
                                case 8:
                                case 9:
                                case 10:
                                case 11:
                                case 12:
                                case 13:
                                case 14:
                                case 15:
                                case 16:
                                    break;
                            }
                        if (message.count != null && message.hasOwnProperty("count"))
                            if (!$util.isInteger(message.count))
                                return "count: integer expected";
                        if (message.linked != null && message.hasOwnProperty("linked"))
                            if (typeof message.linked !== "boolean")
                                return "linked: boolean expected";
                        return null;
                    };

                    /**
                     * Creates a StatusCounterMap message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof es.onebox.venue.venuetemplates.StatusCounterMap
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {es.onebox.venue.venuetemplates.StatusCounterMap} StatusCounterMap
                     */
                    StatusCounterMap.fromObject = function fromObject(object) {
                        if (object instanceof $root.es.onebox.venue.venuetemplates.StatusCounterMap)
                            return object;
                        var message = new $root.es.onebox.venue.venuetemplates.StatusCounterMap();
                        switch (object.status) {
                            case "UNKNOWN":
                            case 0:
                                message.status = 0;
                                break;
                            case "FREE":
                            case 1:
                                message.status = 1;
                                break;
                            case "SOLD":
                            case 2:
                                message.status = 2;
                                break;
                            case "PROMOTOR_LOCKED":
                            case 3:
                                message.status = 3;
                                break;
                            case "SYSTEM_LOCKED":
                            case 4:
                                message.status = 4;
                                break;
                            case "BOOKED":
                            case 5:
                                message.status = 5;
                                break;
                            case "KILL":
                            case 6:
                                message.status = 6;
                                break;
                            case "EMITTED":
                            case 7:
                                message.status = 7;
                                break;
                            case "VALIDATED":
                            case 8:
                                message.status = 8;
                                break;
                            case "IN_REFUND":
                            case 9:
                                message.status = 9;
                                break;
                            case "CANCELLED":
                            case 10:
                                message.status = 10;
                                break;
                            case "PRESOLD_LOCKED":
                            case 11:
                                message.status = 11;
                                break;
                            case "SOLD_LOCKED":
                            case 12:
                                message.status = 12;
                                break;
                            case "GIFT":
                            case 13:
                                message.status = 13;
                                break;
                            case "SEASON_LOCKED":
                            case 14:
                                message.status = 14;
                                break;
                            case "EXTERNAL_LOCKED":
                            case 15:
                                message.status = 15;
                                break;
                            case "EXTERNAL_DELETE":
                            case 16:
                                message.status = 16;
                                break;
                        }
                        if (object.count != null)
                            message.count = object.count | 0;
                        if (object.linked != null)
                            message.linked = Boolean(object.linked);
                        return message;
                    };

                    /**
                     * Creates a plain object from a StatusCounterMap message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof es.onebox.venue.venuetemplates.StatusCounterMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.StatusCounterMap} message StatusCounterMap
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    StatusCounterMap.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        var object = {};
                        if (options.defaults) {
                            object.status = options.enums === String ? "UNKNOWN" : 0;
                            object.count = 0;
                            object.linked = false;
                        }
                        if (message.status != null && message.hasOwnProperty("status"))
                            object.status = options.enums === String ? $root.es.onebox.venue.venuetemplates.Enums.SeatStatus[message.status] : message.status;
                        if (message.count != null && message.hasOwnProperty("count"))
                            object.count = message.count;
                        if (message.linked != null && message.hasOwnProperty("linked"))
                            object.linked = message.linked;
                        return object;
                    };

                    /**
                     * Converts this StatusCounterMap to JSON.
                     * @function toJSON
                     * @memberof es.onebox.venue.venuetemplates.StatusCounterMap
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    StatusCounterMap.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    return StatusCounterMap;
                })();

                venuetemplates.BlockingReasonsCounterMap = (function () {

                    /**
                     * Properties of a BlockingReasonsCounterMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @interface IBlockingReasonsCounterMap
                     * @property {number|null} [blockingReason] BlockingReasonsCounterMap blockingReason
                     * @property {number|null} [count] BlockingReasonsCounterMap count
                     */

                    /**
                     * Constructs a new BlockingReasonsCounterMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @classdesc Represents a BlockingReasonsCounterMap.
                     * @implements IBlockingReasonsCounterMap
                     * @constructor
                     * @param {es.onebox.venue.venuetemplates.IBlockingReasonsCounterMap=} [properties] Properties to set
                     */
                    function BlockingReasonsCounterMap(properties) {
                        if (properties)
                            for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * BlockingReasonsCounterMap blockingReason.
                     * @member {number} blockingReason
                     * @memberof es.onebox.venue.venuetemplates.BlockingReasonsCounterMap
                     * @instance
                     */
                    BlockingReasonsCounterMap.prototype.blockingReason = 0;

                    /**
                     * BlockingReasonsCounterMap count.
                     * @member {number} count
                     * @memberof es.onebox.venue.venuetemplates.BlockingReasonsCounterMap
                     * @instance
                     */
                    BlockingReasonsCounterMap.prototype.count = 0;

                    /**
                     * Creates a new BlockingReasonsCounterMap instance using the specified properties.
                     * @function create
                     * @memberof es.onebox.venue.venuetemplates.BlockingReasonsCounterMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IBlockingReasonsCounterMap=} [properties] Properties to set
                     * @returns {es.onebox.venue.venuetemplates.BlockingReasonsCounterMap} BlockingReasonsCounterMap instance
                     */
                    BlockingReasonsCounterMap.create = function create(properties) {
                        return new BlockingReasonsCounterMap(properties);
                    };

                    /**
                     * Encodes the specified BlockingReasonsCounterMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.BlockingReasonsCounterMap.verify|verify} messages.
                     * @function encode
                     * @memberof es.onebox.venue.venuetemplates.BlockingReasonsCounterMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IBlockingReasonsCounterMap} message BlockingReasonsCounterMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    BlockingReasonsCounterMap.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.blockingReason != null && Object.hasOwnProperty.call(message, "blockingReason"))
                            writer.uint32(/* id 1, wireType 0 =*/8).int32(message.blockingReason);
                        if (message.count != null && Object.hasOwnProperty.call(message, "count"))
                            writer.uint32(/* id 2, wireType 0 =*/16).int32(message.count);
                        return writer;
                    };

                    /**
                     * Encodes the specified BlockingReasonsCounterMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.BlockingReasonsCounterMap.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.BlockingReasonsCounterMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IBlockingReasonsCounterMap} message BlockingReasonsCounterMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    BlockingReasonsCounterMap.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a BlockingReasonsCounterMap message from the specified reader or buffer.
                     * @function decode
                     * @memberof es.onebox.venue.venuetemplates.BlockingReasonsCounterMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {es.onebox.venue.venuetemplates.BlockingReasonsCounterMap} BlockingReasonsCounterMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    BlockingReasonsCounterMap.decode = function decode(reader, length) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        var end = length === undefined ? reader.len : reader.pos + length, message = new $root.es.onebox.venue.venuetemplates.BlockingReasonsCounterMap();
                        while (reader.pos < end) {
                            var tag = reader.uint32();
                            switch (tag >>> 3) {
                                case 1:
                                    message.blockingReason = reader.int32();
                                    break;
                                case 2:
                                    message.count = reader.int32();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a BlockingReasonsCounterMap message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.BlockingReasonsCounterMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {es.onebox.venue.venuetemplates.BlockingReasonsCounterMap} BlockingReasonsCounterMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    BlockingReasonsCounterMap.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a BlockingReasonsCounterMap message.
                     * @function verify
                     * @memberof es.onebox.venue.venuetemplates.BlockingReasonsCounterMap
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    BlockingReasonsCounterMap.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.blockingReason != null && message.hasOwnProperty("blockingReason"))
                            if (!$util.isInteger(message.blockingReason))
                                return "blockingReason: integer expected";
                        if (message.count != null && message.hasOwnProperty("count"))
                            if (!$util.isInteger(message.count))
                                return "count: integer expected";
                        return null;
                    };

                    /**
                     * Creates a BlockingReasonsCounterMap message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof es.onebox.venue.venuetemplates.BlockingReasonsCounterMap
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {es.onebox.venue.venuetemplates.BlockingReasonsCounterMap} BlockingReasonsCounterMap
                     */
                    BlockingReasonsCounterMap.fromObject = function fromObject(object) {
                        if (object instanceof $root.es.onebox.venue.venuetemplates.BlockingReasonsCounterMap)
                            return object;
                        var message = new $root.es.onebox.venue.venuetemplates.BlockingReasonsCounterMap();
                        if (object.blockingReason != null)
                            message.blockingReason = object.blockingReason | 0;
                        if (object.count != null)
                            message.count = object.count | 0;
                        return message;
                    };

                    /**
                     * Creates a plain object from a BlockingReasonsCounterMap message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof es.onebox.venue.venuetemplates.BlockingReasonsCounterMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.BlockingReasonsCounterMap} message BlockingReasonsCounterMap
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    BlockingReasonsCounterMap.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        var object = {};
                        if (options.defaults) {
                            object.blockingReason = 0;
                            object.count = 0;
                        }
                        if (message.blockingReason != null && message.hasOwnProperty("blockingReason"))
                            object.blockingReason = message.blockingReason;
                        if (message.count != null && message.hasOwnProperty("count"))
                            object.count = message.count;
                        return object;
                    };

                    /**
                     * Converts this BlockingReasonsCounterMap to JSON.
                     * @function toJSON
                     * @memberof es.onebox.venue.venuetemplates.BlockingReasonsCounterMap
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    BlockingReasonsCounterMap.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    return BlockingReasonsCounterMap;
                })();

                venuetemplates.SessionPackCounterMap = (function () {

                    /**
                     * Properties of a SessionPackCounterMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @interface ISessionPackCounterMap
                     * @property {number|Long|null} [sessionPack] SessionPackCounterMap sessionPack
                     * @property {number|null} [count] SessionPackCounterMap count
                     */

                    /**
                     * Constructs a new SessionPackCounterMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @classdesc Represents a SessionPackCounterMap.
                     * @implements ISessionPackCounterMap
                     * @constructor
                     * @param {es.onebox.venue.venuetemplates.ISessionPackCounterMap=} [properties] Properties to set
                     */
                    function SessionPackCounterMap(properties) {
                        if (properties)
                            for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * SessionPackCounterMap sessionPack.
                     * @member {number|Long} sessionPack
                     * @memberof es.onebox.venue.venuetemplates.SessionPackCounterMap
                     * @instance
                     */
                    SessionPackCounterMap.prototype.sessionPack = $util.Long ? $util.Long.fromBits(0, 0, false) : 0;

                    /**
                     * SessionPackCounterMap count.
                     * @member {number} count
                     * @memberof es.onebox.venue.venuetemplates.SessionPackCounterMap
                     * @instance
                     */
                    SessionPackCounterMap.prototype.count = 0;

                    /**
                     * Creates a new SessionPackCounterMap instance using the specified properties.
                     * @function create
                     * @memberof es.onebox.venue.venuetemplates.SessionPackCounterMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.ISessionPackCounterMap=} [properties] Properties to set
                     * @returns {es.onebox.venue.venuetemplates.SessionPackCounterMap} SessionPackCounterMap instance
                     */
                    SessionPackCounterMap.create = function create(properties) {
                        return new SessionPackCounterMap(properties);
                    };

                    /**
                     * Encodes the specified SessionPackCounterMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.SessionPackCounterMap.verify|verify} messages.
                     * @function encode
                     * @memberof es.onebox.venue.venuetemplates.SessionPackCounterMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.ISessionPackCounterMap} message SessionPackCounterMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    SessionPackCounterMap.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.sessionPack != null && Object.hasOwnProperty.call(message, "sessionPack"))
                            writer.uint32(/* id 1, wireType 0 =*/8).int64(message.sessionPack);
                        if (message.count != null && Object.hasOwnProperty.call(message, "count"))
                            writer.uint32(/* id 2, wireType 0 =*/16).int32(message.count);
                        return writer;
                    };

                    /**
                     * Encodes the specified SessionPackCounterMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.SessionPackCounterMap.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.SessionPackCounterMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.ISessionPackCounterMap} message SessionPackCounterMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    SessionPackCounterMap.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a SessionPackCounterMap message from the specified reader or buffer.
                     * @function decode
                     * @memberof es.onebox.venue.venuetemplates.SessionPackCounterMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {es.onebox.venue.venuetemplates.SessionPackCounterMap} SessionPackCounterMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    SessionPackCounterMap.decode = function decode(reader, length) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        var end = length === undefined ? reader.len : reader.pos + length, message = new $root.es.onebox.venue.venuetemplates.SessionPackCounterMap();
                        while (reader.pos < end) {
                            var tag = reader.uint32();
                            switch (tag >>> 3) {
                                case 1:
                                    message.sessionPack = reader.int64();
                                    break;
                                case 2:
                                    message.count = reader.int32();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a SessionPackCounterMap message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.SessionPackCounterMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {es.onebox.venue.venuetemplates.SessionPackCounterMap} SessionPackCounterMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    SessionPackCounterMap.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a SessionPackCounterMap message.
                     * @function verify
                     * @memberof es.onebox.venue.venuetemplates.SessionPackCounterMap
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    SessionPackCounterMap.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.sessionPack != null && message.hasOwnProperty("sessionPack"))
                            if (!$util.isInteger(message.sessionPack) && !(message.sessionPack && $util.isInteger(message.sessionPack.low) && $util.isInteger(message.sessionPack.high)))
                                return "sessionPack: integer|Long expected";
                        if (message.count != null && message.hasOwnProperty("count"))
                            if (!$util.isInteger(message.count))
                                return "count: integer expected";
                        return null;
                    };

                    /**
                     * Creates a SessionPackCounterMap message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof es.onebox.venue.venuetemplates.SessionPackCounterMap
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {es.onebox.venue.venuetemplates.SessionPackCounterMap} SessionPackCounterMap
                     */
                    SessionPackCounterMap.fromObject = function fromObject(object) {
                        if (object instanceof $root.es.onebox.venue.venuetemplates.SessionPackCounterMap)
                            return object;
                        var message = new $root.es.onebox.venue.venuetemplates.SessionPackCounterMap();
                        if (object.sessionPack != null)
                            if ($util.Long)
                                (message.sessionPack = $util.Long.fromValue(object.sessionPack)).unsigned = false;
                            else if (typeof object.sessionPack === "string")
                                message.sessionPack = parseInt(object.sessionPack, 10);
                            else if (typeof object.sessionPack === "number")
                                message.sessionPack = object.sessionPack;
                            else if (typeof object.sessionPack === "object")
                                message.sessionPack = new $util.LongBits(object.sessionPack.low >>> 0, object.sessionPack.high >>> 0).toNumber();
                        if (object.count != null)
                            message.count = object.count | 0;
                        return message;
                    };

                    /**
                     * Creates a plain object from a SessionPackCounterMap message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof es.onebox.venue.venuetemplates.SessionPackCounterMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.SessionPackCounterMap} message SessionPackCounterMap
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    SessionPackCounterMap.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        var object = {};
                        if (options.defaults) {
                            if ($util.Long) {
                                var long = new $util.Long(0, 0, false);
                                object.sessionPack = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.sessionPack = options.longs === String ? "0" : 0;
                            object.count = 0;
                        }
                        if (message.sessionPack != null && message.hasOwnProperty("sessionPack"))
                            if (typeof message.sessionPack === "number")
                                object.sessionPack = options.longs === String ? String(message.sessionPack) : message.sessionPack;
                            else
                                object.sessionPack = options.longs === String ? $util.Long.prototype.toString.call(message.sessionPack) : options.longs === Number ? new $util.LongBits(message.sessionPack.low >>> 0, message.sessionPack.high >>> 0).toNumber() : message.sessionPack;
                        if (message.count != null && message.hasOwnProperty("count"))
                            object.count = message.count;
                        return object;
                    };

                    /**
                     * Converts this SessionPackCounterMap to JSON.
                     * @function toJSON
                     * @memberof es.onebox.venue.venuetemplates.SessionPackCounterMap
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    SessionPackCounterMap.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    return SessionPackCounterMap;
                })();

                venuetemplates.QuotaCountersMap = (function () {

                    /**
                     * Properties of a QuotaCountersMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @interface IQuotaCountersMap
                     * @property {number|null} [quota] QuotaCountersMap quota
                     * @property {number|null} [count] QuotaCountersMap count
                     * @property {number|null} [available] QuotaCountersMap available
                     */

                    /**
                     * Constructs a new QuotaCountersMap.
                     * @memberof es.onebox.venue.venuetemplates
                     * @classdesc Represents a QuotaCountersMap.
                     * @implements IQuotaCountersMap
                     * @constructor
                     * @param {es.onebox.venue.venuetemplates.IQuotaCountersMap=} [properties] Properties to set
                     */
                    function QuotaCountersMap(properties) {
                        if (properties)
                            for (var keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * QuotaCountersMap quota.
                     * @member {number} quota
                     * @memberof es.onebox.venue.venuetemplates.QuotaCountersMap
                     * @instance
                     */
                    QuotaCountersMap.prototype.quota = 0;

                    /**
                     * QuotaCountersMap count.
                     * @member {number} count
                     * @memberof es.onebox.venue.venuetemplates.QuotaCountersMap
                     * @instance
                     */
                    QuotaCountersMap.prototype.count = 0;

                    /**
                     * QuotaCountersMap available.
                     * @member {number} available
                     * @memberof es.onebox.venue.venuetemplates.QuotaCountersMap
                     * @instance
                     */
                    QuotaCountersMap.prototype.available = 0;

                    /**
                     * Creates a new QuotaCountersMap instance using the specified properties.
                     * @function create
                     * @memberof es.onebox.venue.venuetemplates.QuotaCountersMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IQuotaCountersMap=} [properties] Properties to set
                     * @returns {es.onebox.venue.venuetemplates.QuotaCountersMap} QuotaCountersMap instance
                     */
                    QuotaCountersMap.create = function create(properties) {
                        return new QuotaCountersMap(properties);
                    };

                    /**
                     * Encodes the specified QuotaCountersMap message. Does not implicitly {@link es.onebox.venue.venuetemplates.QuotaCountersMap.verify|verify} messages.
                     * @function encode
                     * @memberof es.onebox.venue.venuetemplates.QuotaCountersMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IQuotaCountersMap} message QuotaCountersMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    QuotaCountersMap.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.quota != null && Object.hasOwnProperty.call(message, "quota"))
                            writer.uint32(/* id 1, wireType 0 =*/8).int32(message.quota);
                        if (message.count != null && Object.hasOwnProperty.call(message, "count"))
                            writer.uint32(/* id 2, wireType 0 =*/16).int32(message.count);
                        if (message.available != null && Object.hasOwnProperty.call(message, "available"))
                            writer.uint32(/* id 3, wireType 0 =*/24).int32(message.available);
                        return writer;
                    };

                    /**
                     * Encodes the specified QuotaCountersMap message, length delimited. Does not implicitly {@link es.onebox.venue.venuetemplates.QuotaCountersMap.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.QuotaCountersMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.IQuotaCountersMap} message QuotaCountersMap message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    QuotaCountersMap.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a QuotaCountersMap message from the specified reader or buffer.
                     * @function decode
                     * @memberof es.onebox.venue.venuetemplates.QuotaCountersMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {es.onebox.venue.venuetemplates.QuotaCountersMap} QuotaCountersMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    QuotaCountersMap.decode = function decode(reader, length) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        var end = length === undefined ? reader.len : reader.pos + length, message = new $root.es.onebox.venue.venuetemplates.QuotaCountersMap();
                        while (reader.pos < end) {
                            var tag = reader.uint32();
                            switch (tag >>> 3) {
                                case 1:
                                    message.quota = reader.int32();
                                    break;
                                case 2:
                                    message.count = reader.int32();
                                    break;
                                case 3:
                                    message.available = reader.int32();
                                    break;
                                default:
                                    reader.skipType(tag & 7);
                                    break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a QuotaCountersMap message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof es.onebox.venue.venuetemplates.QuotaCountersMap
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {es.onebox.venue.venuetemplates.QuotaCountersMap} QuotaCountersMap
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    QuotaCountersMap.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a QuotaCountersMap message.
                     * @function verify
                     * @memberof es.onebox.venue.venuetemplates.QuotaCountersMap
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    QuotaCountersMap.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.quota != null && message.hasOwnProperty("quota"))
                            if (!$util.isInteger(message.quota))
                                return "quota: integer expected";
                        if (message.count != null && message.hasOwnProperty("count"))
                            if (!$util.isInteger(message.count))
                                return "count: integer expected";
                        if (message.available != null && message.hasOwnProperty("available"))
                            if (!$util.isInteger(message.available))
                                return "available: integer expected";
                        return null;
                    };

                    /**
                     * Creates a QuotaCountersMap message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof es.onebox.venue.venuetemplates.QuotaCountersMap
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {es.onebox.venue.venuetemplates.QuotaCountersMap} QuotaCountersMap
                     */
                    QuotaCountersMap.fromObject = function fromObject(object) {
                        if (object instanceof $root.es.onebox.venue.venuetemplates.QuotaCountersMap)
                            return object;
                        var message = new $root.es.onebox.venue.venuetemplates.QuotaCountersMap();
                        if (object.quota != null)
                            message.quota = object.quota | 0;
                        if (object.count != null)
                            message.count = object.count | 0;
                        if (object.available != null)
                            message.available = object.available | 0;
                        return message;
                    };

                    /**
                     * Creates a plain object from a QuotaCountersMap message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof es.onebox.venue.venuetemplates.QuotaCountersMap
                     * @static
                     * @param {es.onebox.venue.venuetemplates.QuotaCountersMap} message QuotaCountersMap
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    QuotaCountersMap.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        var object = {};
                        if (options.defaults) {
                            object.quota = 0;
                            object.count = 0;
                            object.available = 0;
                        }
                        if (message.quota != null && message.hasOwnProperty("quota"))
                            object.quota = message.quota;
                        if (message.count != null && message.hasOwnProperty("count"))
                            object.count = message.count;
                        if (message.available != null && message.hasOwnProperty("available"))
                            object.available = message.available;
                        return object;
                    };

                    /**
                     * Converts this QuotaCountersMap to JSON.
                     * @function toJSON
                     * @memberof es.onebox.venue.venuetemplates.QuotaCountersMap
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    QuotaCountersMap.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    return QuotaCountersMap;
                })();

                return venuetemplates;
            })();

            return venue;
        })();

        return onebox;
    })();

    return es;
})();

module.exports = $root;

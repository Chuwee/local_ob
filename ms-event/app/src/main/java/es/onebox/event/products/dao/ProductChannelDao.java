package es.onebox.event.products.dao;

import es.onebox.event.priceengine.request.ChannelStatus;
import es.onebox.event.products.domain.ProductChannelRecord;
import es.onebox.event.products.dto.ChannelSessionProductDTO;
import es.onebox.event.products.enums.ProductEventStatus;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.SelectionType;
import es.onebox.jooq.cpanel.tables.CpanelCanal;
import es.onebox.jooq.cpanel.tables.CpanelChannelProduct;
import es.onebox.jooq.cpanel.tables.CpanelEntidad;
import es.onebox.jooq.cpanel.tables.CpanelProduct;
import es.onebox.jooq.cpanel.tables.CpanelProductChannel;
import es.onebox.jooq.cpanel.tables.CpanelProductEvent;
import es.onebox.jooq.cpanel.tables.CpanelProductSession;
import es.onebox.jooq.cpanel.tables.CpanelSesion;
import es.onebox.jooq.cpanel.tables.records.CpanelChannelProductRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductChannelRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CANAL;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_CHANNEL;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_EVENT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_SESSION;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SESION;
import static org.jooq.impl.DSL.concat;
import static org.jooq.impl.DSL.val;

@Repository
public class ProductChannelDao extends DaoImpl<CpanelProductChannelRecord, Integer> {

    protected ProductChannelDao() {
        super(CPANEL_PRODUCT_CHANNEL);
    }

    private static final CpanelProductChannel productChannel = CpanelProductChannel.CPANEL_PRODUCT_CHANNEL;
    private static final CpanelChannelProduct channelProduct = CpanelChannelProduct.CPANEL_CHANNEL_PRODUCT;
    private static final CpanelEntidad entity = CpanelEntidad.CPANEL_ENTIDAD;
    private static final CpanelCanal channel = CPANEL_CANAL;
    private static final CpanelProduct product = CPANEL_PRODUCT;
    private static final CpanelSesion session = CPANEL_SESION.as("session");
    private static final CpanelProductEvent productEvent = CPANEL_PRODUCT_EVENT.as("productEvent");
    private static final CpanelProductSession productSession = CPANEL_PRODUCT_SESSION.as("productSession");
    private static final Field<String> JOIN_CHANNEL_NAME = channel.NOMBRECANAL.as("channelName");
    private static final Field<Integer> JOIN_CHANNEL_SUBTYPE_ID = channel.IDSUBTIPOCANAL.as("channelSubtypeId");
    private static final Field<String> JOIN_PRODUCT_NAME = product.NAME.as("productName");
    private static final Field<Integer> JOIN_ENTITY_ID = entity.IDENTIDAD.as("entityId");
    private static final Field<String> JOIN_ENTITY_NAME = entity.NOMBRE.as("entityName");
    private static final Field<Integer> JOIN_ENTITY_OPERATOR_ID = entity.IDOPERADORA.as("entityOperatorId");
    private static final Field<String> JOIN_ENTITY_LOGO = entity.PATHIMAGEN.as("entityLogoPath");
    private static final Field<Integer> JOIN_PRODUCT_SALE_REQUEST_STATUS_ID = channelProduct.STATUS.as("productSaleRequestsStatusId");
    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_CHANNEL_NAME,
            JOIN_PRODUCT_NAME,
            JOIN_PRODUCT_SALE_REQUEST_STATUS_ID,
            JOIN_ENTITY_ID,
            JOIN_ENTITY_NAME,
            JOIN_ENTITY_LOGO,
            JOIN_ENTITY_OPERATOR_ID
    };
    private static final SelectFieldOrAsterisk[] JOIN_GET_FIELDS = ArrayUtils.add(JOIN_FIELDS, JOIN_CHANNEL_SUBTYPE_ID);
    private static final Integer ACCEPTED_SALE_REQUEST = 2;

    public List<ProductChannelRecord> findByProductId(Long productId) {
        List<ProductChannelRecord> result = new ArrayList<>();
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(productChannel.fields(), JOIN_FIELDS);

        SelectJoinStep query = dsl
                .select(fields)
                .from(productChannel)
                .innerJoin(channel).on(productChannel.CHANNELID.eq(channel.IDCANAL))
                .innerJoin(product).on(productChannel.PRODUCTID.eq(product.PRODUCTID))
                .innerJoin(entity).on(channel.IDENTIDAD.eq(entity.IDENTIDAD))
                .leftJoin(channelProduct).on(productChannel.PRODUCTID.eq(channelProduct.PRODUCTID))
                .and(productChannel.CHANNELID.eq(channelProduct.CHANNELID));

        Condition conditions = DSL.trueCondition();
        conditions = conditions.and(productChannel.PRODUCTID.eq(productId.intValue()));
        conditions = conditions.and(channel.ESTADO.ne(ChannelStatus.DELETED.getId()));
        query.where(conditions);

        List<Record> records = query.fetch();
        for (Record record : records) {
            ProductChannelRecord productChannelRecord = buildProductChannelRecord(record, fields.length, false);
            result.add(productChannelRecord);
        }
        return result;
    }

    public ProductChannelRecord findByProductIdAndChannelId(Long productId, Long channelId) {
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(productChannel.fields(), JOIN_GET_FIELDS);

        Record productChannelRecord = dsl.select(fields)
                .from(productChannel)
                .innerJoin(channel).on(productChannel.CHANNELID.eq(channel.IDCANAL))
                .innerJoin(product).on(productChannel.PRODUCTID.eq(product.PRODUCTID))
                .leftJoin(channelProduct).on(productChannel.PRODUCTID.eq(channelProduct.PRODUCTID))
                .and(productChannel.CHANNELID.eq(channelProduct.CHANNELID))
                .innerJoin(entity).on(channel.IDENTIDAD.eq(entity.IDENTIDAD))
                .where(productChannel.PRODUCTID.eq(productId.intValue()))
                .and(productChannel.CHANNELID.eq(channelId.intValue()))
                .fetchOne();

        return productChannelRecord != null ? buildProductChannelRecord(productChannelRecord, fields.length, true) : null;
    }

    public Long countByProductIdAndChannelIds(Long productId, List<Integer> channelIds) {
        return dsl.selectCount()
                .from(productChannel)
                .where(productChannel.PRODUCTID.eq(productId.intValue()))
                .and(productChannel.CHANNELID.in(channelIds))
                .fetchOne()
                .into(Long.class);
    }

    public Map<String, List<Long>> findChannelSessionsProducts(List<Long> channelIds) {
        List<ChannelSessionProductDTO> result = new ArrayList<>();
        try {
            List<ChannelSessionProductDTO> channelSessionProducts1 = dsl.select(concat(productChannel.CHANNELID, val("_"), session.IDSESION).as("key"), CPANEL_PRODUCT_CHANNEL.PRODUCTID.as("productId"))
                    .from(productChannel)
                    .innerJoin(productEvent)
                    .on(productEvent.PRODUCTID.eq(productChannel.PRODUCTID))
                    .and(productEvent.STATUS.eq(ProductEventStatus.ACTIVE.getId()))
                    .and(productEvent.SESSIONSSELECTIONTYPE.eq(SelectionType.ALL.getId()))
                    .innerJoin(session)
                    .on(session.IDEVENTO.eq(productEvent.EVENTID))
                    .innerJoin(product).on(product.PRODUCTID.eq(productEvent.PRODUCTID)).and(product.STATE.eq(ProductState.ACTIVE.getId()))
                    .where(productChannel.CHANNELID.in(channelIds))
                    .fetchInto(ChannelSessionProductDTO.class);

            List<ChannelSessionProductDTO> channelSessionProducts2 = dsl.select(concat(productChannel.CHANNELID, val("_"), productSession.SESSIONID).as("key"), productEvent.PRODUCTID.as("productId"))
                    .from(productChannel)
                    .innerJoin(productEvent)
                    .on(productEvent.PRODUCTID.eq(productChannel.PRODUCTID))
                    .and(productEvent.STATUS.eq(ProductEventStatus.ACTIVE.getId()))
                    .and(productEvent.SESSIONSSELECTIONTYPE.eq(SelectionType.RESTRICTED.getId()))
                    .innerJoin(productSession)
                    .on(productSession.PRODUCTEVENTID.eq(productEvent.PRODUCTEVENTID))
                    .innerJoin(product).on(product.PRODUCTID.eq(productEvent.PRODUCTID)).and(product.STATE.eq(ProductState.ACTIVE.getId()))
                    .where(productChannel.CHANNELID.in(channelIds))
                    .fetchInto(ChannelSessionProductDTO.class);

            result.addAll(channelSessionProducts1);
            result.addAll(channelSessionProducts2);

            return result.stream().collect(Collectors.groupingBy(ChannelSessionProductDTO::getKey, Collectors.mapping(ChannelSessionProductDTO::getProductId, Collectors.toList())));
        } catch (Exception e) {
            return null;
        }
    }

    public List<CpanelChannelProductRecord> findAcceptedProductSaleRequestByChannelId(List<Integer> channelIds) {
        return dsl.select(channelProduct.fields())
                .from(channelProduct)
                .where(channelProduct.STATUS.eq(ACCEPTED_SALE_REQUEST))
                .and(channelProduct.CHANNELID.in(channelIds))
                .fetchInto(channelProduct);
    }

    private static ProductChannelRecord buildProductChannelRecord(Record pcRecord, int fields, boolean includeSubtype) {
        ProductChannelRecord productChannelRecord = pcRecord.into(ProductChannelRecord.class);
        if (fields > productChannelRecord.fields().length) {
            productChannelRecord.setProductName(pcRecord.getValue(JOIN_PRODUCT_NAME));
            productChannelRecord.setChannelName(pcRecord.getValue(JOIN_CHANNEL_NAME));
            productChannelRecord.setProductSaleRequestsStatusId(pcRecord.getValue(JOIN_PRODUCT_SALE_REQUEST_STATUS_ID));
            productChannelRecord.setEntityId(pcRecord.getValue(JOIN_ENTITY_ID));
            productChannelRecord.setEntityName(pcRecord.getValue(JOIN_ENTITY_NAME));
            productChannelRecord.setEntityLogoPath(pcRecord.getValue(JOIN_ENTITY_LOGO));
            productChannelRecord.setEntityOperatorId(pcRecord.getValue(JOIN_ENTITY_OPERATOR_ID));

            if (includeSubtype) {
                productChannelRecord.setChannelSubtypeId(pcRecord.getValue(JOIN_CHANNEL_SUBTYPE_ID));
            }
        }
        return productChannelRecord;
    }
}

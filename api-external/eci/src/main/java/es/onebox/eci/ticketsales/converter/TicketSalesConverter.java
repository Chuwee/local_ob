package es.onebox.eci.ticketsales.converter;

import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.orders.dto.OrderPayment;
import es.onebox.common.datasources.orders.enums.PaymentType;
import es.onebox.eci.ticketsales.dto.Brand;
import es.onebox.common.datasources.common.enums.OrderType;
import es.onebox.common.datasources.orders.dto.OrderDetail;
import es.onebox.common.datasources.orders.dto.OrderDetailItem;
import es.onebox.common.datasources.orders.dto.OrderPaymentDetailExtended;
import es.onebox.common.datasources.orders.dto.OrderPaymentRefund;
import es.onebox.common.datasources.orders.dto.PGPItem;
import es.onebox.common.datasources.orders.dto.PGPPayment;
import es.onebox.common.datasources.orders.enums.OrderPaymentRefundStatus;
import es.onebox.eci.ticketsales.dto.BrandType;
import es.onebox.eci.ticketsales.dto.Customer;
import es.onebox.eci.ticketsales.dto.Discount;
import es.onebox.eci.ticketsales.dto.Fee;
import es.onebox.eci.ticketsales.dto.Item;
import es.onebox.eci.ticketsales.dto.Order;
import es.onebox.eci.ticketsales.dto.OrderedItem;
import es.onebox.eci.ticketsales.dto.OrderedItemType;
import es.onebox.eci.ticketsales.dto.Payment;
import es.onebox.eci.ticketsales.dto.Price;
import es.onebox.eci.ticketsales.dto.Promotion;
import es.onebox.eci.ticketsales.dto.PromotionalAction;
import es.onebox.eci.ticketsales.dto.Provider;
import es.onebox.eci.ticketsales.dto.RelatedReservation;
import es.onebox.eci.ticketsales.dto.RelatedSaleOperation;
import es.onebox.eci.ticketsales.dto.Reservation;
import es.onebox.eci.ticketsales.dto.ReservationFor;
import es.onebox.eci.ticketsales.dto.ReservedTicket;
import es.onebox.eci.ticketsales.dto.SaleOperation;
import es.onebox.eci.ticketsales.dto.SaleType;
import es.onebox.eci.ticketsales.dto.Seat;
import es.onebox.eci.ticketsales.dto.Session;
import es.onebox.eci.ticketsales.dto.Tax;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class TicketSalesConverter {

    private static final String EXTERNAL_CLIENT_ID = "external_client_id";
    public static final String UNECO796 = "001079610300042000";

    private TicketSalesConverter() {
    }

    public static List<Order> convert(List<OrderDetail> orders, Function<Long, ChannelDTO> channelGetter) {
        List<Order> result = new ArrayList<>();

        for (OrderDetail orderDetail : orders) {
            Order order = new Order();
            order.setOrderDate(orderDetail.getDate());
            BrandType brandType = BrandType.findByValue(channelGetter.apply(orderDetail.getChannel().getId()).getExternalReference());
            if (brandType != null && StringUtils.isNotEmpty(brandType.getIdentifier())) {
                order.setChannelPlatform(brandType.getIdentifier());
            } else {
                order.setChannelPlatform(String.valueOf(orderDetail.getChannel().getId()));
            }
            order.setProvider(getProvider(orderDetail));
            order.setCustomer(getCustomer(orderDetail));
            order.setReservation(getReservation(orderDetail));
            order.setSaleOperation(getSaleOperation(orderDetail, orders));
            order.setOrderedItems(getOrderItems(orderDetail.getItems(), orderDetail.getPaymentDetail()));
            order.setPayments(getPayments(orderDetail));
            result.add(order);
        }

        return result;
    }

    private static Provider getProvider(OrderDetail detail) {
        Provider provider = new Provider();
        provider.setIdentifier(String.format("P%s", detail.getChannel().getEntity().getId().toString()));
        Brand brand = new Brand();
        brand.setIdentifier(detail.getChannel().getId().toString());
        provider.setBrand(brand);
        return provider;
    }

    private static Customer getCustomer(OrderDetail detail) {
        Customer customer = new Customer();

        if (detail.getBuyerData() != null && !detail.getBuyerData().isEmpty()) {
            Map.Entry<String, Object> cucData = detail.getBuyerData()
                    .entrySet()
                    .stream()
                    .filter(stringObjectEntry -> stringObjectEntry.getKey().equals(EXTERNAL_CLIENT_ID))
                    .findFirst()
                    .orElse(null);
            if (cucData != null) {
                customer.setIdentifier(String.valueOf(cucData.getValue()));
                return customer;
            }
        }
        return null;
    }

    private static Reservation getReservation(OrderDetail detail) {
        Reservation reservation = new Reservation();
        reservation.setIdentifier(detail.getCode());
        if (detail.getType().equals(OrderType.REFUND)) {
            reservation.setRelatedReservation(getRelatedReservation(detail));
        }

        return reservation;
    }

    private static RelatedReservation getRelatedReservation(OrderDetail detail) {
        RelatedReservation relatedReservation = new RelatedReservation();
        Optional<OrderDetailItem> item = detail.getItems().stream().findFirst();
        relatedReservation.setIdentifier(item.isPresent() ? item.get().getPreviousOrder().getCode() : null);
        return relatedReservation;
    }

    private static SaleOperation getSaleOperation(OrderDetail detail, List<OrderDetail> orders) {
        SaleOperation saleOperation = new SaleOperation();
        OrderDetail originalOrder;

        saleOperation.setSaleType(getSaleType(detail.getType()));

        if (detail.getType().equals(OrderType.PURCHASE)) {
            saleOperation.setIdentifier(getTransactionId(detail));
        } else if (detail.getType().equals(OrderType.REFUND)) {
            originalOrder = getOriginalOrder(orders, detail);
            saleOperation.setIdentifier(getRefundTransactionId(originalOrder, detail));
            saleOperation.setRelatedSaleOperation(getRelatedSaleOperation(originalOrder));
        }

        return saleOperation;
    }

    private static OrderDetail getOriginalOrder(List<OrderDetail> orders, OrderDetail orderDetail) {
        OrderDetail originalOrder = null;
        String originalCode = null;

        if (orderDetail.getType().equals(OrderType.REFUND)) {
            OrderDetailItem detailItem = orderDetail.getItems().stream().findFirst().orElse(null);
            if (detailItem != null) {
                originalCode = detailItem.getPreviousOrder().getCode();
            }
            String finalOriginalCode = originalCode;
            originalOrder = orders.stream()
                    .filter(detail -> detail.getCode().equals(finalOriginalCode))
                    .findFirst()
                    .orElse(null);
        }

        return originalOrder;
    }

    private static String getTransactionId(OrderDetail orderDetail) {
        String transactionID = "";
        if (orderDetail.getPaymentDetail() != null && orderDetail.getPaymentDetail().getCustomInfo() != null) {
            transactionID = orderDetail.getPaymentDetail().getCustomInfo().getPgpSaleTransactionId();
        } else {
            OrderPayment payment = orderDetail.getPaymentsData().stream().findFirst().orElse(null);
            if(payment != null &&  payment.getReference() != null && BooleanUtils.isFalse(PaymentType.CASH.equals(payment.getType()))){
                transactionID = payment.getReference();
            }
        }
        return transactionID;
    }

    private static String getRefundTransactionId(OrderDetail originalOrder, OrderDetail detail) {
        if (originalOrder.getPaymentDetail() != null && originalOrder.getPaymentDetail().getReimbursements() != null) {
            for (OrderPaymentRefund reimbursement : originalOrder.getPaymentDetail().getReimbursements()) {
                if (detail.getItems().stream().anyMatch(item -> reimbursement.getStatus().equals(OrderPaymentRefundStatus.OK) &&
                        reimbursement.getItemIds().contains(item.getId()))) {
                    return reimbursement.getCustomInfo().getPgpRefundTransactionId();
                }
            }
        }
        return null;
    }

    private static RelatedSaleOperation getRelatedSaleOperation(OrderDetail originalOrder) {
        RelatedSaleOperation relatedSaleOperation = new RelatedSaleOperation();
        relatedSaleOperation.setIdentifier(getTransactionId(originalOrder));
        return relatedSaleOperation;
    }

    private static String getSaleType(OrderType type) {
        if (type.equals(OrderType.REFUND)) {
            return SaleType.REFUND.getValue();
        }
        return SaleType.SALE.getValue();
    }

    private static List<Payment> getPayments(OrderDetail detail) {
        List<Payment> payments = new ArrayList<>();

        List<PGPPayment> pgpPayments = getPGPPayments(detail.getPaymentDetail());

        for (PGPPayment pgpPayment : pgpPayments) {
            Payment payment = new Payment();
            payment.setMethodId(String.valueOf(pgpPayment.getId()));
            payment.setMethod(pgpPayment.getDescription());
            payment.setGateway(detail.getPaymentDetail().getGateway());
            payment.setAmount(pgpPayment.getAmount().movePointLeft(2));
            payments.add(payment);
        }

        return payments;
    }

    private static List<PGPPayment> getPGPPayments(OrderPaymentDetailExtended paymentDetail) {
        List<PGPPayment> pgpPayments = new ArrayList<>();
        if (paymentDetail != null && paymentDetail.getCustomInfo() != null && paymentDetail.getCustomInfo().getPgpPayments() != null) {
            pgpPayments = paymentDetail.getCustomInfo().getPgpPayments();
        }
        return pgpPayments;
    }

    private static List<OrderedItem> getOrderItems(List<OrderDetailItem> orderDetailItems, OrderPaymentDetailExtended orderPaymentDetail) {
        List<OrderedItem> orderedItems = new ArrayList<>();
        for (OrderDetailItem orderDetailItem : orderDetailItems) {
            OrderedItem orderedItem = new OrderedItem();
            orderedItem.setType(OrderedItemType.TICKET.getValue());
            orderedItem.setQuantity(1);
            orderedItem.setItem(getItem(orderDetailItem, orderPaymentDetail));
            orderedItem.setPrice(getPrice(orderDetailItem));
            orderedItem.setDiscount(getDiscounts(orderDetailItem));
            if (orderPaymentDetail != null && orderPaymentDetail.getCustomInfo() != null && orderPaymentDetail.getCustomInfo().getPgpItems() != null) {
                PGPItem pgpItem = orderPaymentDetail.getCustomInfo().getPgpItems().stream().filter(item ->
                        orderDetailItem.getId().toString().equals(item.getExternalReference())).findFirst().orElse(null);
                if (pgpItem != null) {
                    orderedItem.setLineNumber(pgpItem.getLineNumber().toString());
                }
            }

            orderedItems.add(orderedItem);
        }
        return orderedItems;
    }

    private static List<Discount> getDiscounts(OrderDetailItem orderDetailItem) {
        List<Discount> discounts = new ArrayList<>();
        if (orderDetailItem.getTicket().getSales() != null) {
            if (orderDetailItem.getTicket().getSales().getAutomatic() != null) {
                Discount automatic = new Discount();
                BigDecimal priceSaleAutomatic = BigDecimal.ZERO;
                if (Objects.nonNull(orderDetailItem.getPrice().getSales()) &&
                        Objects.nonNull(orderDetailItem.getPrice().getSales().getAutomatic())) {
                    priceSaleAutomatic = orderDetailItem.getPrice().getSales().getAutomatic();
                }

                automatic.setPromotionalAction(getPromotionalAction(
                        orderDetailItem.getTicket().getSales().getAutomatic().getId(), priceSaleAutomatic));
                discounts.add(automatic);
            }
            if (orderDetailItem.getTicket().getSales().getPromotion() != null) {
                Discount promotion = new Discount();
                BigDecimal priceSalePromotion = BigDecimal.ZERO;
                if (Objects.nonNull(orderDetailItem.getPrice().getSales()) &&
                        Objects.nonNull(orderDetailItem.getPrice().getSales().getPromotion())) {
                    priceSalePromotion = orderDetailItem.getPrice().getSales().getPromotion();
                }
                promotion.setPromotionalAction(getPromotionalAction(
                        orderDetailItem.getTicket().getSales().getPromotion().getId(), priceSalePromotion));
                discounts.add(promotion);
            }
            if (orderDetailItem.getTicket().getSales().getDiscount() != null) {
                Discount discount = new Discount();
                BigDecimal priceSaleDiscount = BigDecimal.ZERO;
                if (Objects.nonNull(orderDetailItem.getPrice().getSales()) &&
                        Objects.nonNull(orderDetailItem.getPrice().getSales().getDiscount())) {
                    priceSaleDiscount = orderDetailItem.getPrice().getSales().getDiscount();
                }
                discount.setPromotionalAction(getPromotionalAction(
                        orderDetailItem.getTicket().getSales().getDiscount().getId(), priceSaleDiscount));
                discounts.add(discount);
            }
        }
        return discounts;
    }

    private static PromotionalAction getPromotionalAction(Long id, BigDecimal amount) {
        PromotionalAction promotionalAction = new PromotionalAction();
        promotionalAction.setAmount(amount);
        Promotion promotion = new Promotion();
        promotion.setIdentifier(String.valueOf(id));
        promotionalAction.setPromotion(promotion);
        return promotionalAction;
    }

    private static Price getPrice(OrderDetailItem orderDetailItem) {
        Price price = new Price();
        if (orderDetailItem.getTicket().getAllocation() != null) {
            price.setLevel(orderDetailItem.getTicket().getAllocation().getPriceType().getName());
        }
        price.setFeeAndCommisions(orderDetailItem.getTicket().getRate().getName());
        price.setAmount(orderDetailItem.getPrice().getFinalAmount());
        price.setGross(orderDetailItem.getPrice().getBase());
        price.setNet(getNetPrice(orderDetailItem));
        price.setCurrency(orderDetailItem.getPrice().getCurrency());
        price.setTax(getTax(orderDetailItem.getPrice().getFinalAmount(), orderDetailItem.getPrice().getTax().getTicket()));
        if (orderDetailItem.getPrice().getCharges() != null) {
            price.setCustomerFee(getFee(orderDetailItem.getPrice().getCharges().getChannel(), orderDetailItem.getPrice().getTax().getCharges()));
            price.setProviderFee(getFee(orderDetailItem.getPrice().getCharges().getPromoter(), orderDetailItem.getPrice().getTax().getCharges()));
        }

        return price;
    }

    private static Tax getTax(BigDecimal amount, BigDecimal percentage) {
        Tax tax = new Tax();
        tax.setAmount(getAmountFromGross(amount, percentage));
        tax.setPercentage(percentage);
        return tax;
    }

    private static Fee getFee(BigDecimal amount, BigDecimal percentage) {
        Fee customerFee = new Fee();
        customerFee.setAmount(amount);
        customerFee.setTax(getTax(amount, percentage));
        return customerFee;
    }

    private static BigDecimal getAmountFromGross(BigDecimal amount, BigDecimal percentage) {
        if (percentage == null || BigDecimal.ZERO.equals(percentage)) {
            return BigDecimal.ZERO;
        }
        BigDecimal taxFee = BigDecimal.valueOf(100).add(percentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return amount.divide(taxFee, 2, RoundingMode.HALF_UP).subtract(amount).negate();
    }

    private static BigDecimal getNetPrice(OrderDetailItem orderDetailItem) {
        BigDecimal base = orderDetailItem.getPrice().getBase();
        BigDecimal automatic = BigDecimal.ZERO;
        BigDecimal promotion = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        if (orderDetailItem.getPrice().getSales() != null) {
            if (orderDetailItem.getPrice().getSales().getAutomatic() != null) {
                automatic = orderDetailItem.getPrice().getSales().getAutomatic();
            }
            if (orderDetailItem.getPrice().getSales().getPromotion() != null) {
                promotion = orderDetailItem.getPrice().getSales().getPromotion();
            }
            if (orderDetailItem.getPrice().getSales().getDiscount() != null) {
                discount = orderDetailItem.getPrice().getSales().getDiscount();
            }
        }
        return base.subtract(automatic).subtract(promotion).subtract(discount);
    }

    private static Item getItem(OrderDetailItem orderDetailItem, OrderPaymentDetailExtended orderPaymentDetail) {
        Item item = new Item();
        item.setReservationFor(getReservationFor(orderDetailItem));
        item.setReservedTicket(getReservedTicket(orderDetailItem, orderPaymentDetail));
        return item;
    }

    private static ReservedTicket getReservedTicket(OrderDetailItem orderDetailItem, OrderPaymentDetailExtended orderPaymentDetail) {
        ReservedTicket reservedTicket = new ReservedTicket();
        reservedTicket.setSkuPlatform(String.valueOf(orderDetailItem.getId()));
        if (orderDetailItem.getTicket().getAllocation() != null) {
            reservedTicket.setTicketType(orderDetailItem.getTicket().getType().name() + " - " +
                    orderDetailItem.getTicket().getAllocation().getType().name());
            reservedTicket.setSeats(getSeat(orderDetailItem));
        }
        if (orderPaymentDetail != null && orderPaymentDetail.getCustomInfo() != null && orderPaymentDetail.getCustomInfo().getPgpItems() != null) {
            PGPItem pgpItem = orderPaymentDetail.getCustomInfo().getPgpItems().stream().filter(item ->
                    orderDetailItem.getId().toString().equals(item.getExternalReference())).findFirst().orElse(null);
            if (pgpItem != null) {
                reservedTicket.setSkuEci(pgpItem.getReference());
            }
        }else{
            reservedTicket.setSkuEci(UNECO796);
        }

        return reservedTicket;
    }

    private static List<Seat> getSeat(OrderDetailItem orderDetailItem) {
        List<Seat> seats = new ArrayList<>();

        Seat seat = new Seat();

        seat.setIdentifier(orderDetailItem.getTicket().getBarcode().getCode());
        if (orderDetailItem.getTicket().getAllocation().getSector() != null) {
            seat.setSection(orderDetailItem.getTicket().getAllocation().getSector().getName());
        } else if (orderDetailItem.getTicket().getAllocation().getNotNumberedArea() != null) {
            seat.setSection(orderDetailItem.getTicket().getAllocation().getNotNumberedArea().getName());
        }
        if (orderDetailItem.getTicket().getAllocation().getRow() != null) {
            seat.setRow(orderDetailItem.getTicket().getAllocation().getRow().getName());
        }

        if (orderDetailItem.getTicket().getAllocation().getSeat() != null) {
            seat.setNumber(orderDetailItem.getTicket().getAllocation().getSeat().getName());
        }

        seats.add(seat);
        return seats;
    }

    private static ReservationFor getReservationFor(OrderDetailItem orderDetailItem) {
        ReservationFor reservationFor = new ReservationFor();
        if (orderDetailItem.getTicket().getAllocation() != null) {
            reservationFor.setIdentifier(String.valueOf(orderDetailItem.getTicket().getAllocation().getEvent().getId()));
            reservationFor.setSession(getSession(orderDetailItem));
        }
        return reservationFor;
    }

    private static Session getSession(OrderDetailItem orderDetailItem) {
        Session session = new Session();
        session.setIdentifier(String.valueOf(orderDetailItem.getTicket().getAllocation().getSession().getId()));
        return session;
    }
}

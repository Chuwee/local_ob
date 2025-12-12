package es.onebox.internal.automaticsales.processsales.utils;

import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.OrderTicketDataDTO;

import java.util.Comparator;

public class ProductDTOPositionComparator implements Comparator<OrderProductDTO> {

    @Override
    public int compare(OrderProductDTO p1, OrderProductDTO p2) {
        OrderTicketDataDTO td1 = p1.getTicketData();
        OrderTicketDataDTO td2 = p2.getTicketData();
        int sessionIdComp = p1.getSessionId().compareTo(p2.getSessionId());

        if (sessionIdComp != 0) {
            return sessionIdComp;
        }

        //If is not numbered, check only notNumberedAreaId
        Integer checkNNResult = checkNotNumberedAreaId(td1, td2);
        if (checkNNResult != null) {
            return checkNNResult;
        }

        Integer rowIdComp = checkRowId(td1, td2);
        if (rowIdComp != null) {
            return rowIdComp;
        }

        Integer rowBlockComp = checkRowBlock(td1, td2);
        if (rowBlockComp != null) {
            return rowBlockComp;
        }

        if (hasRowOrder(td1, td2)) {
            return td1.getRowOrder().compareTo(td2.getRowOrder());
        }

        return 0;
    }

    private boolean hasRowOrder(OrderTicketDataDTO td1, OrderTicketDataDTO td2) {
        return td1 != null && td1.getRowOrder() != null && td2 != null && td2.getRowOrder() != null;
    }

    private Integer checkRowBlock(OrderTicketDataDTO td1, OrderTicketDataDTO td2) {
        if(td1 != null && td1.getRowBlock() != null && td2 != null && td2.getRowBlock() != null){
            int rowBlockComp = td1.getRowBlock().compareTo(td2.getRowBlock());
            if (rowBlockComp != 0) {
                return rowBlockComp;
            }
        }
        return null;
    }

    private Integer checkRowId(OrderTicketDataDTO td1, OrderTicketDataDTO td2) {
        if(td1 != null && td1.getRowId() != null && td2 != null && td2.getRowId() != null){
            int rowIdComp = td1.getRowId().compareTo(td2.getRowId());
            if (rowIdComp != 0) {
                return rowIdComp;
            }
        }
        return null;
    }

    private Integer checkNotNumberedAreaId(OrderTicketDataDTO td1, OrderTicketDataDTO td2) {
        if(td1 != null && td1.getNotNumberedAreaId() != null) {
            if(td2 != null && td2.getNotNumberedAreaId() != null) {
                return td1.getNotNumberedAreaId().compareTo(td2.getNotNumberedAreaId());
            } else {
                return -1;
            }
        } else if(td2 != null && td2.getNotNumberedAreaId() != null) {
            return 1;
        }
        return null;
    }
    
}

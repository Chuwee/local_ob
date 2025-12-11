package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import es.onebox.mgmt.datasources.common.dto.Surcharge;

public class MsSaleRequestSurchargesDTO extends Surcharge {

    private static final long serialVersionUID = -8713101679030073519L;

    private MsSaleRequestSurchargeLimitDTO limitProducer;

    public MsSaleRequestSurchargeLimitDTO getLimitProducer() {
        return limitProducer;
    }

    public void setLimitProducer(MsSaleRequestSurchargeLimitDTO limitProducer) {
        this.limitProducer = limitProducer;
    }
}

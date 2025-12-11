package es.onebox.mgmt.channels.gateways.converter;

/**
 * This enum maps every OneboxAccounting Gateway Config Field to a es.onebox.mgmt.datasources.ms.channel.dto.ChannelAccounting field.
 * Whenever a new field is added to this gateway configuration, it should be mapped.
 */
public enum OneboxAccountingGatewayConfigurationFields {
    OB_ACC_MERCHANT_CODE("getMerchantCode"),
    OB_ACC_PASSWORD("getSecretKey"),
    OB_ACC_PROVIDER_ID("getProviderId")
    ;

    private String channelAccountingBoundFieldGetter;

    OneboxAccountingGatewayConfigurationFields(String channelAccountingBoundFieldGetter) {
        this.channelAccountingBoundFieldGetter = channelAccountingBoundFieldGetter;
    }

    public String getChannelAccountingBoundFieldGetter(){
        return channelAccountingBoundFieldGetter;
    }
}

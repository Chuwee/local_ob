package es.onebox.common.amt;

public enum AMTCustomTag {

    ORDER_CODE("order.code");

    private String value;

    AMTCustomTag(String value){
        this.value = value;
    }

    public String value(){
        return this.value;
    }
}

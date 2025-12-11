package es.onebox.event.priceengine.request;

public enum ChannelSubtype {

    WEB(1),
    PORTAL_WEB(7),
    BOX_OFFICE_ONEBOX(8),
    BOX_OFFICE_WEB(10),
    PORTAL_COLLECTIVE(11),
    PORTAL_SUBSCRIBERS(12),
    PORTAL_B2B(13),
    AVET(14);

    private int idSubtipo;

    ChannelSubtype(int idSubtipo) {
        this.idSubtipo = idSubtipo;
    }

    public int getIdSubtipo() {
        return idSubtipo;
    }
}

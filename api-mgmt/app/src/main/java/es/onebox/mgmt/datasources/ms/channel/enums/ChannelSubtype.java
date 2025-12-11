package es.onebox.mgmt.datasources.ms.channel.enums;

import java.util.stream.Stream;

public enum ChannelSubtype {

    WEB(1, ChannelType.EXTERNAL), // EXTERNAL
    PORTAL_WEB(7, ChannelType.OB_PORTAL), // WEB
    BOX_OFFICE_ONEBOX(8, ChannelType.OB_BOX_OFFICE), // BOX_OFFICE
    BOX_OFFICE_WEB(10, ChannelType.OB_PORTAL), // WEB_BOX_OFFICE
    PORTAL_COLLECTIVE(11, ChannelType.OB_PORTAL), //
    PORTAL_SUBSCRIBERS(12, ChannelType.OB_PORTAL), // WEB_SUBSCRIBERS
    PORTAL_B2B(13, ChannelType.OB_PORTAL), // WEB_B2B
    AVET(14, ChannelType.MEMBER); // MEMBERS

    private int idSubtipo;
    private ChannelType tipo;

    ChannelSubtype(int idSubtipo, ChannelType tipo) {
        this.idSubtipo = idSubtipo;
        this.tipo = tipo;
    }

    public int getIdSubtipo() {
        return idSubtipo;
    }

    public ChannelType getTipo() {
        return tipo;
    }

    public static ChannelSubtype getById(int id) {
        return Stream.of(values())
                .filter(cs -> cs.getIdSubtipo() == id)
                .findAny()
                .orElse(null);
    }

}

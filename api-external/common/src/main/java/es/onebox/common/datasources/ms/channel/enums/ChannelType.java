package es.onebox.common.datasources.ms.channel.enums;

import java.util.stream.Stream;

public enum ChannelType {

    EXTERNAL(1),
    OB_PORTAL(2),
    OB_BOX_OFFICE(3),
    MEMBER(4);

    private int tipo;

    ChannelType(int tipo) {
        this.tipo = tipo;
    }

    public int getTipo() {
        return tipo;
    }

    public static ChannelType getById(Integer id) {
        if (id == null) {
            return null;
        }
        return Stream.of(values())
                .filter(ct -> ct.tipo == id)
                .findFirst()
                .orElse(null);
    }
}

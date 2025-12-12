package es.onebox.common.datasources.ms.channel.enums;

import java.util.Objects;
import java.util.stream.Stream;

public enum EmailServerSecurityType {
    NONE(0),
    STARTTLS(1),
    SSL_TLS(2);

    EmailServerSecurityType(Integer id){
        this.id = id;
    }

    private Integer id;

    public Integer getId(){
        return this.id;
    }

    public static EmailServerSecurityType toDto(Integer id) {
        if(Objects.isNull(id)){
            return null;
        }
        return Stream.of(EmailServerSecurityType.values())
                .filter(t -> t.getId().equals(id))
                .findFirst().orElse(null);
    }
}

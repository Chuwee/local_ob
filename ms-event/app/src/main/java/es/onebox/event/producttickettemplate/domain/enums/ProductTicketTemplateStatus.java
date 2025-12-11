package es.onebox.event.producttickettemplate.domain.enums;

import java.io.Serializable;
import java.util.stream.Stream;

public enum ProductTicketTemplateStatus implements Serializable {

	DELETED(0), ACTIVE(1);

	private final Integer id;

	ProductTicketTemplateStatus(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public static ProductTicketTemplateStatus fromId(Integer id) {
		return Stream.of(ProductTicketTemplateStatus.values())
				.filter(v -> v.getId().equals(id))
				.findFirst()
				.orElse(null);
	}

}

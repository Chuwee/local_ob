package es.onebox.event.producttickettemplate.dao;

import org.jooq.Field;
import org.jooq.SelectFieldOrAsterisk;

import es.onebox.jooq.cpanel.tables.CpanelEntidad;
import es.onebox.jooq.cpanel.tables.CpanelProductTicketModel;
import es.onebox.jooq.cpanel.tables.CpanelProductTicketTemplate;
import es.onebox.jooq.cpanel.tables.CpanelProductTicketTemplateLanguage;

public class TableConstants {

	private TableConstants() {
	}

	static final CpanelProductTicketTemplate PRODUCT_TEMPLATE = CpanelProductTicketTemplate.CPANEL_PRODUCT_TICKET_TEMPLATE
			.as("productTemplate");
	static final CpanelProductTicketModel PRODUCT_MODEL = CpanelProductTicketModel.CPANEL_PRODUCT_TICKET_MODEL
			.as("productModel");
	static final CpanelEntidad ENTITY = CpanelEntidad.CPANEL_ENTIDAD.as("entity");
	static final CpanelProductTicketTemplateLanguage LANGUAGE = CpanelProductTicketTemplateLanguage.CPANEL_PRODUCT_TICKET_TEMPLATE_LANGUAGE
			.as("language");
	static final Field<String> JOIN_ENTITY_NAME = ENTITY.NOMBRE.as("entityName");
	static final Field<Integer> JOIN_ENTITY_ID = ENTITY.IDENTIDAD.as("entityId");
	static final Field<Integer> JOIN_PRODUCT_ID = PRODUCT_MODEL.MODELID.as("productModelId");
	static final Field<String> JOIN_PRODUCT_NAME = PRODUCT_MODEL.NAME.as("productModelName");
	static final Field<String> JOIN_PRODUCT_DESCRIPTION = PRODUCT_MODEL.DESCRIPTION.as("description");
	static final Field<Integer> JOIN_PRODUCT_MODEL_TYPE = PRODUCT_MODEL.MODELTYPE.as("modelType");
	static final Field<Integer> JOIN_PRODUCT_TARGET_TYPE = PRODUCT_MODEL.TARGETTYPE.as("targetType");
	static final Field<String> JOIN_PRODUCT_FILE_NAME = PRODUCT_MODEL.FILENAME.as("fileName");

	static final SelectFieldOrAsterisk[] COMMON_FIELDS = { PRODUCT_TEMPLATE.TEMPLATEID.as("id"),
			PRODUCT_TEMPLATE.NAME.as("name") };
	static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
			JOIN_ENTITY_ID, JOIN_ENTITY_NAME, JOIN_PRODUCT_ID, JOIN_PRODUCT_NAME, JOIN_PRODUCT_DESCRIPTION,
			JOIN_PRODUCT_MODEL_TYPE, JOIN_PRODUCT_TARGET_TYPE, JOIN_PRODUCT_FILE_NAME
	};
}

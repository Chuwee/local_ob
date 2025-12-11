package es.onebox.event.config;

public final class CouchbaseKeys {

    public static final String BUCKET_ONEBOX_OPERATIVE = "onebox-operative";
    public static final String FORMS_SCOPE = "forms";
    public static final String MASTER_COLLECTION = "master";
    public static final String SEASON_TICKET_COLLECTION = "season-ticket";
    public static final String MASTER_FORMS = "masterFormFields";
    public static final String DEFAULT_FORM = "defaultForm";
    public static final String FORM = "form";
    public static final String PRODUCTS = "products";
    public static final String PRODUCT_TICKET_LITERALS_COLLECTION = "ticket-literals";
    public static final String PRODUCT_TICKET_LITERALS_PREFIX = "productTicketLiterals";


    private CouchbaseKeys() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }
} 
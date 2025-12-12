package es.onebox.fifaqatar.config.translation;

import es.onebox.couchbase.annotations.CouchDocument;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@CouchDocument
public class FifaQatarTranslation extends HashMap<String, Map<String, String>> implements Serializable {

}

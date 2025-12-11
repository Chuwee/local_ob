package es.onebox.event.forms.domain;

import es.onebox.couchbase.annotations.CouchDocument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@CouchDocument
public class Form extends ArrayList<List<FormField>> implements Serializable {

    private static final long serialVersionUID = 1L;

} 
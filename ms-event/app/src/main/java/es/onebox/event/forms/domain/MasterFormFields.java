package es.onebox.event.forms.domain;

import es.onebox.couchbase.annotations.CouchDocument;

import java.io.Serializable;
import java.util.ArrayList;

@CouchDocument
public class MasterFormFields extends ArrayList<MasterFormField> implements Serializable {

    private static final long serialVersionUID = 1L;

} 
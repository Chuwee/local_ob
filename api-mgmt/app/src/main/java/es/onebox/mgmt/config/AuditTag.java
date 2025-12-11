package es.onebox.mgmt.config;

public class AuditTag {

    public static final String AUDIT_SERVICE = "MGMT";
    public static final String AUDIT_ACTION_GET = "GET";
    public static final String AUDIT_ACTION_SEARCH = "SEARCH";
    public static final String AUDIT_ACTION_CREATE = "CREATE";
    public static final String AUDIT_ACTION_CLONE = "CLONE";
    public static final String AUDIT_ACTION_UPDATE = "UPDATE";
    public static final String AUDIT_ACTION_DELETE = "DELETE";
    public static final String AUDIT_ACTION_EXPORT = "EXPORT";
    public static final String AUDIT_ACTION_ADD = "ADD";
    public static final String AUDIT_ACTION_GET_AVAILABLE = "GET_AVAILABLE";
    public static final String AUDIT_ACTION_FORGOT = "FORGOT";
    public static final String AUDIT_ACTION_STATUS = "STATUS";
    public static final String AUDIT_ACTION_LINK = "LINK";
    public static final String AUDIT_ACTION_UNLINK = "UNLINK";
    public static final String AUDIT_ACTION_REFRESH = "REFRESH";
    public static final String AUDIT_ACTION_IMPORT = "IMPORT";

    private AuditTag() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }
}

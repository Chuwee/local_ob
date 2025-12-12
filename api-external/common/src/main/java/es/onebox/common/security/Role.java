package es.onebox.common.security;

public class Role {
    public static final String OPERATOR_MANAGER = "ROLE_OPR_MGR";
    public static final String OPERATOR_ANALYST = "ROLE_OPR_ANS";
    public static final String OPERATOR_CALL_CENTER = "ROLE_OPR_CALL";
    public static final String ENTITY_ANALYST = "ROLE_ENT_ANS";
    public static final String ENTITY_MANAGER = "ROLE_ENT_MGR";
    public static final String EVENT_MANAGER = "ROLE_EVN_MGR";
    public static final String CHANNEL_MANAGER = "ROLE_CNL_MGR";
    public static final String CHANNEL_INTEGRATION = "ROLE_CNL_INT";
    public static final String CHANNEL_CALL_CENTER = "ROLE_CNL_SAC";
    public static final String ROLE_FV_REPORTING = "ROLE_FV_REPORTING";


    private Role(){
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }
}

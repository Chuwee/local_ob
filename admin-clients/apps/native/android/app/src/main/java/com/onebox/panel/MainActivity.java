package com.onebox.panel;

import com.getcapacitor.BridgeActivity;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class MainActivity extends BridgeActivity {
        protected void onCreate() {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        }
}

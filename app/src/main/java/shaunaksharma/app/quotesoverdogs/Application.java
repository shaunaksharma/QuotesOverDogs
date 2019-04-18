package shaunaksharma.app.quotesoverdogs;

import android.content.Context;
import org.acra.*;
import org.acra.annotation.*;

@AcraMailSender(mailTo = "shaunaksharma5797@gmail.com", reportAsFile = true, resSubject = R.string.bug_report_subject, resBody = R.string.bug_report_body)

@AcraCore(buildConfigClass = BuildConfig.class)
public class Application extends android.app.Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}

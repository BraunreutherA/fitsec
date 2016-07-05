package secureapps.com.fitsec;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;

import java.util.List;

import secureapps.com.fitsec.data.RealmApp;

/**
 * Created by Alex on 05.07.16.
 */
public abstract class BaseRealmAppAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    protected final SecureReportService secureReportService;
    protected final AppService appService;

    protected final Context context;
    protected List<RealmApp> realmApps;

    protected ControlOpenApp controlOpenApp;

    public BaseRealmAppAdapter(Context context, List<RealmApp> realmApps) {
        this.context = context;
        this.realmApps = realmApps;
        secureReportService = new SecureReportService();
        appService = new AppService();
        controlOpenApp = new ControlOpenApp(context);
    }

    @Override
    public int getItemCount() {
        return realmApps.size();
    }

    public void setRealmApps(List<RealmApp> realmApps) {
        this.realmApps = realmApps;
    }

    protected abstract class OnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        protected int position;

        public OnCheckedChangeListener(int position) {
            this.position = position;
        }
    }
}
package secureapps.com.fitsec;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import secureapps.com.fitsec.data.App;
import secureapps.com.fitsec.data.RealmApp;
import secureapps.com.fitsec.data.SecureReport;
import timber.log.Timber;

/**
 * Created by Alex on 27.06.16.
 */
public class RealmAppAdapter extends RecyclerView.Adapter<RealmAppAdapter.SecureAppViewHolder> {
    private final SecureReportService secureReportService;
    private final AppService appService;

    private final Context context;
    private List<RealmApp> realmApps;

    private ControlOpenApp controlOpenApp;

    public RealmAppAdapter(Context context, List<RealmApp> realmApps) {
        this.context = context;
        this.realmApps = realmApps;
        secureReportService = new SecureReportService();
        appService = new AppService();
        controlOpenApp = new ControlOpenApp(context);
    }


    @Override
    public SecureAppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.d("creating viewholder...");

        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.app_view_holder, parent, false);

        return new SecureAppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SecureAppViewHolder holder, int position) {
        final RealmApp app = realmApps.get(position);

        final Bitmap appIcon = BitmapFactory.decodeByteArray(app.getAppIcon(), 0, app.getAppIcon().length);
        holder.logo.setImageBitmap(appIcon);
        holder.name.setText(app.getName());

        float percentage = (float) Math.max(0, app.getSecureCount())  / (float) app.getInstallations();

        holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));

        if (percentage < 0.2f) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.veryLightColorPrimary));
        }
        if (percentage > 0.6f) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.veryLightColorAccent));
        }

        holder.secureCount.setText(Integer.toString((int) (percentage * 100)) + "% der Nutzer sichern diese App.");

        holder.toggle.setOnCheckedChangeListener(null);
        holder.toggle.setChecked(app.isSecured());
        holder.toggle.setTag(app);
        holder.toggle.setOnCheckedChangeListener(new OnCheckedChangeListener(position) {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(controlOpenApp.getUsageStatsList().isEmpty()){
                    new AlertDialog.Builder(RealmAppAdapter.this.context)
                            .setTitle("Additional Settings")
                            .setMessage("There are settings missing to run this app properly. Please go to the settings menu and activate them.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Nothing happens
                                }
                            })
                            .show();
                }
                RealmApp app = (RealmApp) buttonView.getTag();
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                app.setSecured(isChecked);
                realm.commitTransaction();
            }
        });
    }
    @Override
    public int getItemCount() {
        return realmApps.size();
    }

    public void setRealmApps(List<RealmApp> realmApps) {
        this.realmApps = realmApps;
    }

    private abstract class OnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        protected int position;

        public OnCheckedChangeListener(int position) {
            this.position = position;
        }
    }

    public final class SecureAppViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.app_logo)
        ImageView logo;

        @BindView(R.id.app_name)
        TextView name;

        @BindView(R.id.app_secure_count)
        TextView secureCount;

        @BindView(R.id.app_secure_toggle)
        Switch toggle;

        public SecureAppViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}


package secureapps.com.fitsec;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import secureapps.com.fitsec.data.App;
import timber.log.Timber;

/**
 * @author Alexander Braunreuther
 */
public class SecureAppAdapter extends RecyclerView.Adapter<SecureAppAdapter.SecureAppViewHolder> {
    private List<App> appList;
    private final Context context;

    public SecureAppAdapter(List<App> appList, Context context) {
        this.appList = appList;
        this.context = context;
    }

    @Override
    public SecureAppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.d("creating viewholder...");

        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.app_view_holder, parent, false);

        return new SecureAppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SecureAppViewHolder holder, final int position) {
        final App app = appList.get(position);
        Picasso.with(context)
                .load(app.getAppImageUrl())
                .into(holder.logo);

        holder.name.setText(app.getAppName());

        float percentage = (float) app.getFakeSecureCount() / (float) app.getUserCount();
        holder.secureCount.setText(Integer.toString((int) (percentage * 100)) + "%");

        holder.toggle.setChecked(app.isChecked());
        holder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Timber.d("increase the secure count");
                    app.incrementSecuredCount();
                    appList.set(position, app);
                } else {
                    Timber.d("decrease the secure count");
                    app.decrementSecureCount();
                    appList.set(position, app);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public static final class SecureAppViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.app_logo)
        ImageView logo;

        @BindView(R.id.app_name)
        TextView name;

        @BindView(R.id.app_secure_count)
        TextView secureCount;

        @BindView(R.id.app_secure_toggle)
        ToggleButton toggle;

        public SecureAppViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

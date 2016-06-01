package secureapps.com.fitsec;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import secureapps.com.fitsec.base.BaseActivity;
import secureapps.com.fitsec.data.App;
import timber.log.Timber;

public class MainActivity extends BaseActivity {
    @BindView(R.id.app_list)
    RecyclerView appList;

    @BindView(R.id.btn_save)
    Button saveBtn;

    private List<App> apps;
    private SecureAppAdapter secureAppAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apps = new ArrayList<>();

        secureAppAdapter = new SecureAppAdapter(apps, this);
        appList.setLayoutManager(new LinearLayoutManager(this));
        appList.setItemAnimator(new DefaultItemAnimator());
        appList.setAdapter(secureAppAdapter);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.saveAllInBackground(apps, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast toast = Toast.makeText(MainActivity.this, "error on save... " + e.getMessage(), Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(MainActivity.this, "saved...", Toast.LENGTH_LONG);
                            toast.show();
                            fetchSecureAppdata();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSecureAppdata();
    }

    private void fetchSecureAppdata() {
        ParseQuery<App> query = new ParseQuery<>(App.class);
        query.findInBackground(new FindCallback<App>() {
            @Override
            public void done(List<App> apps, ParseException e) {
                Timber.d(apps.toString());
                MainActivity.this.apps.clear();
                MainActivity.this.apps.addAll(apps);
                MainActivity.this.secureAppAdapter.notifyDataSetChanged();
            }
        });
    }
}

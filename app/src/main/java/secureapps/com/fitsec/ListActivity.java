package secureapps.com.fitsec;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import secureapps.com.fitsec.base.BaseActivity;

public class ListActivity extends BaseActivity {
    @BindView(R.id.app_list)
    RecyclerView appList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        AppService appService = new AppService(this);

        RealmAppAdapter realmAppAdapter = new RealmAppAdapter(this, appService.getInstalledApps());
        appList.setLayoutManager(new LinearLayoutManager(this));
        appList.setItemAnimator(new DefaultItemAnimator());
        appList.setAdapter(realmAppAdapter);
    }
}
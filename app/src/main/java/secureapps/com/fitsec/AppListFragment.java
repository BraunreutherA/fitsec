package secureapps.com.fitsec;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import secureapps.com.fitsec.base.BaseFragment;

/**
 * Created by Alex on 30.06.16.
 */
public class AppListFragment extends BaseFragment {
    @BindView(R.id.app_list)
    RecyclerView appList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppService appService = new AppService(getContext());

        RealmAppAdapter realmAppAdapter = new RealmAppAdapter(getContext(), appService.getInstalledApps());
        appList.setLayoutManager(new LinearLayoutManager(getContext()));
        appList.setItemAnimator(new DefaultItemAnimator());
        appList.setAdapter(realmAppAdapter);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_list;
    }
}

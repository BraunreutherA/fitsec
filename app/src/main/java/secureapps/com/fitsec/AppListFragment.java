package secureapps.com.fitsec;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import secureapps.com.fitsec.base.BaseFragment;
import secureapps.com.fitsec.data.RealmApp;
import timber.log.Timber;

/**
 * Created by Alex on 30.06.16.
 */
public class AppListFragment extends BaseFragment {
    @BindView(R.id.app_list)
    RecyclerView appList;

    private RealmAppAdapter realmAppAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppService appService = new AppService();
        realmAppAdapter = new RealmAppAdapter(getContext(), new ArrayList<RealmApp>());

        appService.getInstalledApps()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<RealmApp>>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("fetching complete...");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e.getMessage());
                    }

                    @Override
                    public void onNext(List<RealmApp> realmApps) {
                        newData(realmApps);
                    }
                });

        appList.setLayoutManager(new LinearLayoutManager(getContext()));
        appList.setItemAnimator(new DefaultItemAnimator());
        appList.setAdapter(realmAppAdapter);
    }

    private void newData(List<RealmApp> realmApps) {
        realmAppAdapter.setRealmApps(realmApps);
        realmAppAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_list;
    }
}

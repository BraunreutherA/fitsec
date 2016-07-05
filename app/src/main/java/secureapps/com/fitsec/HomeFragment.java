package secureapps.com.fitsec;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import secureapps.com.fitsec.base.BaseFragment;
import secureapps.com.fitsec.data.RealmApp;
import timber.log.Timber;

/**
 * Created by Sarah on 02.07.2016.
 */
public class HomeFragment extends BaseFragment {
    @BindView(R.id.app_suggestions)
    RecyclerView appSuggestions;

    private SeekBar thresholdSlider;
    private int currentProgress;

    private AppService appService;
    private RecommendedRealmAppAdapter realmAppAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActionBar().setTitle(getString(R.string.action_home));

        appService = new AppService();

        realmAppAdapter = new RecommendedRealmAppAdapter(getContext(), new ArrayList<RealmApp>());
        appSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));
        appSuggestions.setItemAnimator(new DefaultItemAnimator());
        appSuggestions.setAdapter(realmAppAdapter);

        thresholdSlider = (SeekBar)view.findViewById(R.id.treshold_slider);
        thresholdSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("Slider", "Slider progress " + progress);
                currentProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //save to secure apps by treshold
                Log.e("Slider", "Stopped tracking, current progress " + currentProgress);
                appService.setAppSecured(currentProgress);

                //TODO maybe update view

            }
        });

        float treshold = 0.0f;

        appService.getUnsecured(treshold)
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
                        for(RealmApp app : realmApps){
                            Log.e("LISTE", app.getName());
                        }
                        newData(realmApps);
                    }
                });


    }

    private void newData(List<RealmApp> realmApps) {
        realmAppAdapter.setRealmApps(realmApps);
        realmAppAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.home_fragment;
    }
}

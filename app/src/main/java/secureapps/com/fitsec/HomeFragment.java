package secureapps.com.fitsec;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import secureapps.com.fitsec.base.BaseFragment;

/**
 * Created by Sarah on 02.07.2016.
 */
public class HomeFragment extends BaseFragment {
    private SeekBar tresholdSlider;
    private int currentProgress;

    private AppService appService;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActionBar().setTitle(getString(R.string.action_home));

        appService = new AppService();

        tresholdSlider = (SeekBar)view.findViewById(R.id.treshold_slider);
        tresholdSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.home_fragment;
    }
}

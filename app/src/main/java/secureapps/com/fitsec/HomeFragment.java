package secureapps.com.fitsec;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import secureapps.com.fitsec.base.BaseFragment;

/**
 * Created by Sarah on 02.07.2016.
 */
public class HomeFragment extends BaseFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActionBar().setTitle(getString(R.string.action_home));

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.home_fragment;
    }
}

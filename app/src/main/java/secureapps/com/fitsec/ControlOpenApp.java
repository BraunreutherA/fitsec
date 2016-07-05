package secureapps.com.fitsec;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

/**
 * Created by TaiwanFelix on 27.06.2016.
 */
public class ControlOpenApp {
    private static final String TAG = ControlOpenApp.class.getSimpleName();
    private static final String KEY_USAGE_STATS_MANAGER = "usagestats";

    private OnAppOpenListener onAppOpenListener;
    private final Context context;

    public ControlOpenApp(Context context) {
        this.context = context;
    }

    private List<UsageStats> storeBefore;

    public void setOnAppOpenListener(OnAppOpenListener onAppOpenListener) {
        this.onAppOpenListener = onAppOpenListener;
    }

    public List<UsageStats> getUsageStatsList() {
        UsageStatsManager usm = getUsageStatsManager();
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        return usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
    }


    /**
     * Detect which application user starts
     */
    private void printUsageStats(List<UsageStats> usageStatsList) {
        boolean secondCall = fillSecondListByFirstCall(usageStatsList);
        if (secondCall == true) {
            if (storeBefore.size() != usageStatsList.size()) {
                boolean foundSamePackageName = false;
                for (int arraySize1 = 0; arraySize1 < storeBefore.size(); arraySize1++) {
                    UsageStats firstArray = usageStatsList.get(arraySize1);
                    UsageStats secondArray = storeBefore.get(arraySize1);
                    if (foundSamePackageName == false) {
                        foundSamePackageName = checkForUnequelPackage(firstArray, secondArray);
                    }
                }
                if (foundSamePackageName == false) {
                    appNeverOpendBeforeInList(storeBefore, usageStatsList);
                }
                storeBefore = usageStatsList;
            } else {
                checkForAppChange(usageStatsList);
                storeBefore = usageStatsList;
            }
        }
    }

    public void printCurrentUsageStatus() {
        if(!getUsageStatsList().isEmpty()) {
            printUsageStats(getUsageStatsList());
        }
    }

    @SuppressWarnings("ResourceType")
    private UsageStatsManager getUsageStatsManager() {
        return  (UsageStatsManager) context.getSystemService(KEY_USAGE_STATS_MANAGER);
    }

    /**
     * @param storeBefore    the list with all the package names
     * @param usageStatsList the list with all the package names plus a new package name which wasnt in their before
     */
    private void appNeverOpendBeforeInList(List<UsageStats> storeBefore, List<UsageStats> usageStatsList) {
        int notInSize = storeBefore.size();
        UsageStats firstArray = usageStatsList.get(notInSize);
        String appPackageName = firstArray.getPackageName();
        Log.e(TAG, "Package Name: " + firstArray.getPackageName());

        if (AppService.isAppSecured(appPackageName)) {
            Log.e(TAG, "App is in the secured list ");
        }
    }

    /**
     * If application is called the first time storeBefore list gets filled with package names for later use
     *
     * @return if storeBefore List has already been filled
     * @Input: List of all the Package names which are now stored in the system
     */
    private boolean fillSecondListByFirstCall(List<UsageStats> usageStatsList) {
        boolean firstCall = true;
        if (storeBefore == null) {
            storeBefore = usageStatsList;
            firstCall = false;
        }
        return firstCall;
    }

    /**
     * Check the two lists for the last time their time changed
     *
     * @param usageStatsList List with all the package names
     */
    private void checkForAppChange(List<UsageStats> usageStatsList) {
        for (int arraySize = 0; arraySize < usageStatsList.size(); arraySize++) {
            UsageStats firstArray = usageStatsList.get(arraySize);
            UsageStats secondArray = storeBefore.get(arraySize);

            if (firstArray.getLastTimeUsed() != secondArray.getLastTimeUsed()) {
                if (firstArray.getTotalTimeInForeground() == secondArray.getTotalTimeInForeground()) {
                    Log.e(TAG, "Package Name:  " + firstArray.getPackageName());

                    String appPackageName = firstArray.getPackageName();
                    if (AppService.isAppSecured(appPackageName)) {

                        onAppOpenListener.openedApp(appPackageName);
                        Log.e(TAG, "App is in the secured list ");

                    }
                }
            }
        }
    }

    /**
     * @param firstArray  List with all the package names plus the new one which wasnt in there before
     * @param secondArray List of all the package names before user click
     * @return boolean value if unequal package name found
     */
    private boolean checkForUnequelPackage(UsageStats firstArray, UsageStats secondArray) {
        boolean foundSamePackageName = false;
        if (!firstArray.getPackageName().equals(secondArray.getPackageName())) {
            Log.e(TAG, "Package Name: " + firstArray.getPackageName());

            String appPackageName = firstArray.getPackageName();
            if (AppService.isAppSecured(appPackageName)) {
                Log.e(TAG, "App is in the secured list ");
            }
            foundSamePackageName = true;
        }
        return foundSamePackageName;
    }

    interface OnAppOpenListener {
        void openedApp(String packageName);
    }
}

package secureapps.com.fitsec;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

/**
 * Created by TaiwanFelix on 27.06.2016.
 */
public class ControlOpenApp {

    public static final String TAG = ControlOpenApp.class.getSimpleName();
    private static List<UsageStats> storeBefore;
    public static Context contextMain;

    public static List<UsageStats> getUsageStatsList(Context context){
        contextMain = context;
        UsageStatsManager usm = getUsageStatsManager(context);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startTime,endTime);

        return usageStatsList;
    }


    /** Detect which application user starts */
    public static void printUsageStats(List<UsageStats> usageStatsList){
        boolean secondCall = fillSecondListByFirstCall(usageStatsList);
        if(secondCall == true) {
            if(storeBefore.size() != usageStatsList.size()){
                boolean foundSamePackageName = false;
                for(int arraySize1 = 0; arraySize1 < storeBefore.size(); arraySize1 ++) {
                    UsageStats firstArray = usageStatsList.get(arraySize1);
                    UsageStats secondArray = storeBefore.get(arraySize1);
                    if(foundSamePackageName == false) {
                        foundSamePackageName = checkForUnequelPackage(firstArray,secondArray);
                    }
                }
                if(foundSamePackageName == false){
                    appNeverOpendBeforeInList(storeBefore, usageStatsList);
                }
                storeBefore = usageStatsList;
            }
            else {
                checkForAppChange(usageStatsList);
                storeBefore = usageStatsList;
            }
        }
    }

    public static void printCurrentUsageStatus(Context context){
        printUsageStats(getUsageStatsList(context));
    }

    @SuppressWarnings("ResourceType")
    private static UsageStatsManager getUsageStatsManager(Context context){
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        return usm;
    }

    /**
     *
     * @param storeBefore the list with all the package names
     * @param usageStatsList the list with all the package names plus a new package name which wasnt in their before
     */
    private static void appNeverOpendBeforeInList(List<UsageStats> storeBefore, List<UsageStats> usageStatsList){
        int notInSize = storeBefore.size();
        UsageStats firstArray = usageStatsList.get(notInSize);
        String appPackageName = firstArray.getPackageName();
        Log.e(TAG, "Package Name: " + firstArray.getPackageName());
        if(AppService.isAppSecured(appPackageName) == true){
            Log.e(TAG, "App is in the secured list ");
            openLockScreen();
        }
    }

    /**If application is called the first time storeBefore list gets filled with package names for later use
     * @Input: List of all the Package names which are now stored in the system
     * @return if storeBefore List has already been filled
     */
    private static boolean fillSecondListByFirstCall(List<UsageStats> usageStatsList){
        boolean firstCall = true;
        if(storeBefore == null) {
            storeBefore = usageStatsList;
            firstCall = false;
        }
        return firstCall;
    }

    /**
     * Check the two lists for the last time their time changed
     * @param usageStatsList List with all the package names
     *
     */
    private static void checkForAppChange(List<UsageStats> usageStatsList){
        for(int arraySize = 0; arraySize < usageStatsList.size(); arraySize ++){
            UsageStats firstArray = usageStatsList.get(arraySize);
            UsageStats secondArray = storeBefore.get(arraySize);

            if(firstArray.getLastTimeUsed() != secondArray.getLastTimeUsed()){
                if(firstArray.getTotalTimeInForeground() == secondArray.getTotalTimeInForeground()) {
                    Log.e(TAG, "Package Name:  " + firstArray.getPackageName());
                    String appPackageName = firstArray.getPackageName();
                    if(AppService.isAppSecured(appPackageName) == true){
                        Log.e(TAG, "App is in the secured list ");
                        openLockScreen();
                    }
                }
            }
        }
    }

    /**
     *
     * @param firstArray List with all the package names plus the new one which wasnt in there before
     * @param secondArray List of all the package names before user click
     * @return boolean value if unequal package name found
     */
    private static boolean checkForUnequelPackage(UsageStats firstArray, UsageStats secondArray){
        boolean foundSamePackageName = false;
        if (!firstArray.getPackageName().equals(secondArray.getPackageName())) {
            Log.e(TAG, "Package Name: " + firstArray.getPackageName());
            String appPackageName = firstArray.getPackageName();
            if(AppService.isAppSecured(appPackageName) == true){
                Log.e(TAG, "App is in the secured list ");
                openLockScreen();
            }
            foundSamePackageName = true;
        }
        return foundSamePackageName;
    }

    private static void openLockScreen() {
        LockScreenActivity lockScreen = new LockScreenActivity();
        lockScreen.openLockScreen();
    }
}

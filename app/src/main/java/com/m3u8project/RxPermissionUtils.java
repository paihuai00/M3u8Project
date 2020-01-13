package com.m3u8project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.functions.Consumer;

/**
 * Date: 2019/10/23
 * create by cuishuxiang
 * description: RxPermission 工具类
 */
public class RxPermissionUtils {
    private static final String TAG = "RxPermissionUtils";

    public static final int SetInstallRequestCode = 0x555;

    /**
     * 请求权限,内部有检测权限是否同意
     *
     * 注意：使用 requestEach ，可以根据返回，判断用户是否选中了"不再询问"
     *
     * @param activityOrFragment 传入的必须是 Activity or Fragment
     * @param callBack 同意or拒绝回调
     * @param permissions 需要请求的权限
     */
    public static void requestPermission(Object activityOrFragment,
            final OnRxPermissionCallBack callBack, String... permissions) {
        if (!(activityOrFragment instanceof Activity)
                && !(activityOrFragment instanceof Fragment)) {
            throw new IllegalArgumentException(
                    "RxPermissionUtils Exception : Object Must extend Activity Or Fragment ");
        }

        if (activityOrFragment instanceof Activity) {
            RxPermissions rxPermissions1 = new RxPermissions((FragmentActivity) activityOrFragment);
            /**
             * requestEachCombined: 该方法会在所有的权限都同意后，只回调一次
             *
             * requestEach: 每个权限都会回调
             * */
            rxPermissions1.requestEachCombined(permissions).subscribe(new Consumer<Permission>() {
                @Override
                public void accept(Permission permission) throws Exception {
                    if (permission.granted) {
                        // 用户已经同意该权限
                        Log.d(TAG , "  用户已经同意该权限 : " + permission.name);
                        if(callBack!=null) callBack.onGrant();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        Log.d(TAG , "  用户拒绝了该权限，没有选中『不再询问』 : " + permission.name);
                        if(callBack!=null) callBack.onRefuse(false);

                    } else {
                        // 用户拒绝了该权限，并且选中『不再询问』
                        Log.d(TAG ,"  用户拒绝了该权限，并且选中『不再询问』 : " + permission.name);
                        if(callBack!=null) callBack.onRefuse(true);
                    }
                }
            });

        } else if (activityOrFragment instanceof Fragment) {
            RxPermissions rxPermissions = new RxPermissions((Fragment) activityOrFragment);
            /**
             * requestEachCombined: 该方法会在所有的权限都同意后，只回调一次
             *
             * requestEach: 每个权限都会回调
             * */
            rxPermissions.requestEachCombined(permissions).subscribe(new Consumer<Permission>() {
                @Override
                public void accept(Permission permission) throws Exception {
                    if (permission.granted) {
                        // 用户已经同意该权限
                        Log.d(TAG , "  用户已经同意该权限 : " + permission.name);
                        if(callBack!=null) callBack.onGrant();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        Log.d(TAG , "  用户拒绝了该权限，没有选中『不再询问』 : " + permission.name);
                        if(callBack!=null) callBack.onRefuse(false);

                    } else {
                        // 用户拒绝了该权限，并且选中『不再询问』
                        Log.d(TAG ,"  用户拒绝了该权限，并且选中『不再询问』 : " + permission.name);
                        if(callBack!=null) callBack.onRefuse(true);
                    }
                }
            });

        }
    }

    /**
     * 是否允许 安装app
     * @param context
     * @return
     *
     *  注意需要声明该权限
     *   <!--8.0 安装权限-->
     *   <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
     */
    public static boolean isAgreeInstallPackage(Context context) {
        boolean isAgree = true;
        //8.0以上 需要判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            isAgree = context.getPackageManager().canRequestPackageInstalls();
        }

        return isAgree;
    }

    /**
     * 前往设置，开启 安装apk权限
     * @param context
     */
    public static void openInstallSetting(Activity context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        context.startActivityForResult(intent,SetInstallRequestCode);
    }

    public interface OnRxPermissionCallBack {
        void onGrant();//同意

        void onRefuse(boolean isNeverAskAgain);//拒绝
    }
}

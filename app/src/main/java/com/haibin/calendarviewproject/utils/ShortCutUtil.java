package com.haibin.calendarviewproject.utils;

import static android.app.PendingIntent.FLAG_MUTABLE;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.widget.Toast;
import com.haibin.calendarviewproject.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 2021/4/6
 * 作者:Tiaw.
 */
public class ShortCutUtil {


    public static final String SHORTCUT_TAB_INDEX = "shortcut_tab_index";
    public ShortCutUtil() {
    }

    private volatile static  ShortCutUtil mShortCutUtil;

    public static  ShortCutUtil getInstance() {
        if (mShortCutUtil == null) {
            synchronized ( ShortCutUtil.class) {
                if (mShortCutUtil == null) {
                    mShortCutUtil = new  ShortCutUtil();
                }
            }
        }
        return mShortCutUtil;
    }


    private static int[] icons = {R.mipmap.ic_launcher};


    private ShortcutManager sm;

    public  ShortCutUtil init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            sm = context.getSystemService(ShortcutManager.class);
        }
        return mShortCutUtil;
    }



    /**
     * @param context       当前content
     * @param targetClass   快捷图标打开的界面
     * @param backClass     打开后按返回键返回的界面
     * @param shortCutId    shortCut 唯一id
     * @param shortCutIcon  桌面上显示的图标
     */
    public void AddShortCut(Context context, Class targetClass, Class backClass, int shortCutId, int shortCutIcon) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager shortcutManager = (ShortcutManager) context.getSystemService(Context.SHORTCUT_SERVICE);
            if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported()) {
                Intent shortcutInfoIntent = new Intent(context, targetClass);
                shortcutInfoIntent.putExtra("scheduleId", shortCutId + "");
                shortcutInfoIntent.setAction(Intent.ACTION_VIEW);

                ShortcutInfo info = new ShortcutInfo.Builder(context, "id" + shortCutId)
                        .setIcon(Icon.createWithResource(context, shortCutIcon)).
                        setShortLabel("重要日").setIntent(shortcutInfoIntent).build();

                PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, backClass), FLAG_MUTABLE);
                shortcutManager.requestPinShortcut(info, shortcutCallbackIntent.getIntentSender());
            }
        } else {
            Toast.makeText(context, "设备不支持在桌面创建快捷图标！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * @param context       上下文
     * @param cls           要跳转的页面
     * @param shortCutId    shortCut 唯一id
     * @param shortCutIcon  桌面上显示的图标
     * @param shortCutLabel 桌面图标下方显示的文字
     */
    public void updItem(Context context, Class<?> cls, int shortCutId, int shortCutIcon, String shortCutLabel) {
        Intent intent = new Intent(context, cls);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("scheduleId", shortCutId+"");

        ShortcutInfo info = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            info = new ShortcutInfo.Builder(context, "id" + shortCutId)
                    .setIcon(Icon.createWithResource(context, shortCutIcon))
                    .setShortLabel(shortCutLabel)
                    .setIntent(intent)
                    .build();
            sm.updateShortcuts(Arrays.asList(info));
        }
    }

    /**
     * 禁止使用快捷方式
     *
     * @param index 禁止使用下标
     */
    public void remove(int index) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            sm.removeAllDynamicShortcuts();
            List<String> list = new ArrayList<String>();
            list.add("id" + index);
            sm.disableShortcuts(list);
        }
    }

    public List<ShortcutInfo> allDate() {
        List<ShortcutInfo> dynamicShortcuts = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            dynamicShortcuts = sm.getDynamicShortcuts();
        }

        return dynamicShortcuts;
    }
}

package xyz.zzyitj.keledge.downloader.xposed;

import android.content.Context;
import android.widget.Toast;
import com.google.gson.Gson;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import xyz.zzyitj.keledge.downloader.bean.Details;
import xyz.zzyitj.keledge.downloader.util.KeledgeFileUtils;

import java.io.Serializable;
import java.security.PrivateKey;

import static xyz.zzyitj.keledge.downloader.util.ConstUtils.*;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/18 8:55 上午
 * @email zzy.main@gmail.com
 */
public class XposedInit implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (KELEDGE_PACKAGE.equals(lpparam.packageName)) {
            XposedHelpers.findAndHookMethod(FUCK_PACKAGE, lpparam.classLoader,
                    FUCK_METHOD, Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            //获取到360的Context对象，通过这个对象来获取classloader
                            Context context = (Context) param.args[0];
                            //获取360的classloader，之后hook加固后的代码就使用这个classloader
                            ClassLoader classLoader = context.getClassLoader();
                            //替换classloader,hook加固后的真正代码
                            hookKeledge(context, classLoader);
                        }
                    });
        }
    }

    private void hookKeledge(Context context, ClassLoader classLoader) {
        final long[] fileId = {-1};
        final String[] fileType = {null};
        final String[] title = {null};
        Class<?> StringCallback = XposedHelpers.findClass(KELEDGE_STRING_CALLBACK_PACKAGE, classLoader);
        XposedHelpers.findAndHookMethod(KELEDGE_DETAILS_PACKAGE, classLoader,
                KELEDGE_DETAILS_METHOD, Object.class, long.class, long.class, String.class, StringCallback, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Object result = param.getResult();
                        String json = new Gson().toJson(result);
                        Details details = new Gson().fromJson(json, Details.class);
                        fileId[0] = details.getData().getDefaultFileId();
                        fileType[0] = details.getData().getDefaultFileExtension();
                        title[0] = details.getData().getTitle();
                        KeledgeFileUtils.saveData(fileId[0] + fileType[0] + "." + DETAIL_SUFFIX, json);
                    }
                });
        XposedHelpers.findAndHookMethod(KELEDGE_AUTHORIZE_PACKAGE, classLoader,
                KELEDGE_AUTHORIZE_METHOD, String.class, Object.class, String.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Serializable result = (Serializable) param.getResult();
                        String json = new Gson().toJson(result);
                        KeledgeFileUtils.saveData(fileId[0] + fileType[0] + "." + JSON_SUFFIX, json);
                    }
                });
        XposedHelpers.findAndHookMethod(KELEDGE_RSA_PACKAGE, classLoader,
                KELEDGE_RSA_METHOD, String.class, PrivateKey.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (fileId[0] != -1) {
                            String result = (String) param.getResult();
                            XposedBridge.log(TAG + "id: " + fileId[0]
                                    + ", type: " + fileType[0] + ", title: " + title[0] + ", result: " + result);
                            KeledgeFileUtils.saveData(fileId[0] + fileType[0] + "." + KEY_SUFFIX, result);
                            Toast.makeText(context, "可知下载, 获取到书籍 " + title[0] + " 已保存数据", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

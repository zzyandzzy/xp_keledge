package xyz.zzyitj.keledge.xposed;

import android.content.Context;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.security.PrivateKey;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/17 11:18 上午
 * @email zzy.main@gmail.com
 */
public class XposedInit implements IXposedHookLoadPackage {
    private static final String TAG = "可知: ";

    private static final String KELEDGE_PACKAGE = "com.kingchannels.kezhiphone";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (KELEDGE_PACKAGE.equals(lpparam.packageName)) {
            XposedBridge.log(TAG + lpparam.packageName);

            XposedHelpers.findAndHookMethod("com.stub.StubApp", lpparam.classLoader,
                    "attachBaseContext", Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            //获取到360的Context对象，通过这个对象来获取classloader
                            Context context = (Context) param.args[0];
                            //获取360的classloader，之后hook加固后的代码就使用这个classloader
                            ClassLoader classLoader = context.getClassLoader();
                            //替换classloader,hook加固后的真正代码
                            hookKey(classLoader);
                        }
                    });
        }
    }

    private void hookKey(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.lzy.okserver.utils.RsaUtils", classLoader,
                "decryptDataStr", String.class, PrivateKey.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String result = (String) param.getResult();
                        XposedBridge.log(TAG + "result: " + result);
                    }
                });
    }
}

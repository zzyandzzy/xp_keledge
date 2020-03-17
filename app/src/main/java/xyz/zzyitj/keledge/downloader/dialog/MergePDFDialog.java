package xyz.zzyitj.keledge.downloader.dialog;

import android.content.Context;
import androidx.annotation.NonNull;
import xyz.zzyitj.keledge.downloader.R;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/18 5:13 下午
 * @email zzy.main@gmail.com
 */
public class MergePDFDialog extends android.app.Dialog {
    public MergePDFDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.merge_pdf_dialog);
        // 按空白处不能取消动画
//        setCanceledOnTouchOutside(false);
    }
}

package xyz.zzyitj.keledge.downloader.util;

import android.os.Environment;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static xyz.zzyitj.keledge.downloader.util.ConstUtils.*;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/18 9:24 上午
 * @email zzy.main@gmail.com
 */
public class KeledgeFileUtils {
    public static final String DATA_PATH = KeledgeFileUtils.getPath() + APP_DATA_PATH;
    public static final String PDF_PATH = KeledgeFileUtils.getPath() + PDF_DATA_PATH;
    public static final String BOOK_PATH = KeledgeFileUtils.getPath() + BOOK_DATA_PATH;


    public static void savePdf(String filename, byte[] data) throws IOException {
        String path = getPath() + PDF_DATA_PATH + "/" + filename;
        FileUtils.writeByteArrayToFile(new File(path), data);
    }

    public static void saveData(String filename, String data) throws IOException {
        String path = getPath() + APP_DATA_PATH + "/" + filename;
        FileUtils.writeStringToFile(new File(path), data, StandardCharsets.UTF_8);
    }

    public static String getPath() {
        File file = Environment.getExternalStorageDirectory();
        if (file == null) {
            throw new NullPointerException("File cannot be null.");
        }
        return file.getAbsolutePath() + "/" + APP_STORAGE_PATH + "/";
    }
}

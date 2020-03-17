package xyz.zzyitj.keledge.downloader;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;
import org.apache.commons.io.FileUtils;
import xyz.zzyitj.keledge.downloader.adapter.BookDetailAdapter;
import xyz.zzyitj.keledge.downloader.bean.Details;
import xyz.zzyitj.keledge.downloader.bean.DetailsData;
import xyz.zzyitj.keledge.downloader.util.KeledgeFileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;

import static xyz.zzyitj.keledge.downloader.util.ConstUtils.*;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private File parentFile;
    private LinkedList<File> fileLinkedList;
    private ArrayList<DetailsData> detailsDataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BookDetailAdapter bookDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDatas();
        initViews();
        initAdapter();
    }

    private void initAdapter() {
        detailsDataList.clear();
        for (File file : fileLinkedList) {
            String fileId = file.getName().replace(PDF_SUFFIX + "." + KEY_SUFFIX, "");
            File detailFile = new File(parentFile.getAbsoluteFile() + File.separator + fileId + PDF_SUFFIX + "." + DETAIL_SUFFIX);
            if (detailFile.exists()) {
                try {
                    String json = FileUtils.readFileToString(detailFile, StandardCharsets.UTF_8);
                    Details details = new Gson().fromJson(json, Details.class);
                    detailsDataList.add(details.getData());
//                    Log.i(TAG, "initAdapter: " + details.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        bookDetailAdapter.setDetailsDataList(detailsDataList);
        bookDetailAdapter.notifyDataSetChanged();
    }

    private void initViews() {
        MyApplication myApplication = MyApplication.getInstance();
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        bookDetailAdapter = new BookDetailAdapter(detailsDataList, this);
        recyclerView.setAdapter(bookDetailAdapter);
    }

    private void initDatas() {
        PDFBoxResourceLoader.init(getApplicationContext());
        parentFile = new File(KeledgeFileUtils.DATA_PATH);
        try {
            FileUtils.forceMkdir(parentFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileLinkedList = (LinkedList<File>) FileUtils.listFiles(
                parentFile, new String[]{PDF_SUFFIX + "." + KEY_SUFFIX}, false);
    }
}

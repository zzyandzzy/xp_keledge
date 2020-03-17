package xyz.zzyitj.keledge.downloader.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import xyz.zzyitj.keledge.downloader.MyApplication;
import xyz.zzyitj.keledge.downloader.R;
import xyz.zzyitj.keledge.downloader.bean.AuthorizeCertData;
import xyz.zzyitj.keledge.downloader.bean.Details;
import xyz.zzyitj.keledge.downloader.bean.DetailsData;
import xyz.zzyitj.keledge.downloader.dialog.MergePDFDialog;
import xyz.zzyitj.keledge.downloader.net.DownloadUtils;
import xyz.zzyitj.keledge.downloader.net.ImageUtils;
import xyz.zzyitj.keledge.downloader.util.AESUtils;
import xyz.zzyitj.keledge.downloader.util.KeledgeFileUtils;
import xyz.zzyitj.keledge.downloader.util.OpenFileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static xyz.zzyitj.keledge.downloader.util.ConstUtils.*;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/18 11:33 上午
 * @email zzy.main@gmail.com
 */
public class BookDetailAdapter extends RecyclerView.Adapter<BookDetailAdapter.ViewHolder> {
    private static final String TAG = BookDetailAdapter.class.getSimpleName();

    private ArrayList<DetailsData> detailsDataList;

    private Context context;

    public ArrayList<DetailsData> getDetailsDataList() {
        return detailsDataList;
    }

    public void setDetailsDataList(ArrayList<DetailsData> detailsDataList) {
        this.detailsDataList = detailsDataList;
    }

    public BookDetailAdapter(ArrayList<DetailsData> detailsDataList, Context context) {
        this.detailsDataList = detailsDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_detail_list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final BookDetailAdapter.ViewHolder holder, int position) {
        DetailsData detailsData = detailsDataList.get(position);
        if (StringUtils.isNotBlank(detailsData.getCoverUrl())) {
            Disposable disposable = ImageUtils.getImg(detailsData.getCoverUrl())
                    .subscribe(responseBody -> {
                        byte[] bytes = responseBody.bytes();
                        holder.cover.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }, throwable -> {
                    });
        }
        holder.title.setText(detailsData.getTitle());
        holder.author.setText(detailsData.getAuthor());
        String savePath = KeledgeFileUtils.BOOK_PATH + File.separator + detailsData.getTitle() + "." + PDF_SUFFIX;
        File pdfFile = new File(savePath);
        AtomicBoolean isCanOpen = new AtomicBoolean(pdfFile.exists());
        holder.downloadButton.setVisibility(View.VISIBLE);
        holder.progressBar.setVisibility(View.GONE);
        holder.progressText.setVisibility(View.GONE);
        if (isCanOpen.get()) {
            holder.downloadButton.setText("打开");
        } else {
            holder.downloadButton.setText("下载");
        }
        holder.deleteButton.setOnClickListener(v -> {
            deleteAppData(detailsData.getDefaultFileId(), position);
        });
        holder.downloadButton.setOnClickListener(v -> {
            if (isCanOpen.get()) {
                Intent intent = OpenFileUtils.getPdfFileIntent(this.context, pdfFile);
                context.startActivity(intent);
            } else {
                String keyPath = KeledgeFileUtils.DATA_PATH + File.separator + detailsData.getDefaultFileId() + "." + PDF_SUFFIX + "." + KEY_SUFFIX;
                File keyFile = new File(keyPath);
                String jsonDataPath = KeledgeFileUtils.DATA_PATH + File.separator + detailsData.getDefaultFileId() + "." + PDF_SUFFIX + "." + JSON_SUFFIX;
                File jsonDataFile = new File(jsonDataPath);
                if (jsonDataFile.exists() && keyFile.exists()) {
                    try {
                        String key = FileUtils.readFileToString(keyFile, StandardCharsets.UTF_8);
                        String jsonData = FileUtils.readFileToString(jsonDataFile, StandardCharsets.UTF_8);
                        AuthorizeCertData authorizeCertData = new Gson().fromJson(jsonData, AuthorizeCertData.class);
                        ArrayList<String> urls = authorizeCertData.getSplitFileUrls();
                        // 设置进度条
                        holder.downloadButton.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.VISIBLE);
                        holder.progressBar.setMax(urls.size());
                        holder.progressText.setVisibility(View.VISIBLE);
                        // 已下载检测数据完整性
                        int progress = checkData(detailsData.getDefaultFileId(), urls.size());
                        holder.progressBar.setProgress(progress);
                        holder.progressText.setText(progress + " / " + urls.size());
                        if (progress == urls.size()) {
                            mergePDF(holder, isCanOpen, detailsData, urls.size(), savePath, position);
                        } else {
                            MyApplication.getInstance().setDownloadProgress(detailsData.getDefaultFileId(), progress);
                            Log.i(TAG, detailsData.getTitle() + "开始从 " + progress + " 下载, 共: " + urls.size());
                            Toast.makeText(context, detailsData.getTitle() + "开始从 " + progress + " 下载, 共: " + urls.size(), Toast.LENGTH_SHORT).show();
//                            int maxProgress = urls.size() < 50 ? urls.size() : progress + 50;
                            for (int i = progress; i < urls.size(); i++) {
                                String filename = detailsData.getDefaultFileId() + File.separator + i + "." + PDF_SUFFIX;
                                // 如果文件不存在就下载
                                if (!new File(KeledgeFileUtils.PDF_PATH + File.separator + filename).exists()) {
//                                    Log.i(TAG, "onBindViewHolder: " + filename);
                                    Disposable disposable = DownloadUtils.download(urls.get(i))
                                            .subscribe(responseBody -> {
                                                byte[] content = responseBody.bytes();
                                                try {
                                                    Details details = new Gson().fromJson(new String(content), Details.class);
                                                    Log.e(TAG, details.Description);
                                                    setDownloadFail(holder, isCanOpen);
                                                } catch (JsonSyntaxException e) {
                                                    KeledgeFileUtils.savePdf(filename, AESUtils.decode(content, key));
                                                    int fileCount = FileUtils.listFiles(
                                                            new File(KeledgeFileUtils.PDF_PATH + File.separator + detailsData.getDefaultFileId()),
                                                            new String[]{PDF_SUFFIX}, false).size();
                                                    if (fileCount == urls.size()) {
                                                        mergePDF(holder, isCanOpen, detailsData, urls.size(), savePath, position);
                                                    } else {
                                                        setProgressBar(holder, detailsData.getDefaultFileId(), urls.size());
                                                    }
                                                }
                                            }, throwable -> {
                                                Log.e(TAG, detailsData.getTitle() + ", 下载错误，message: " + throwable.getMessage());
//                                                Toast.makeText(context, detailsData.getTitle() + ", 下载错误，请重新打开可知获取最新下载链接!", Toast.LENGTH_SHORT).show();
//                                                setDownloadFail(holder, isCanOpen);
                                            });
                                } else {
                                    setProgressBar(holder, detailsData.getDefaultFileId(), urls.size());
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setProgressBar(ViewHolder holder, long id, int size) {
        MyApplication.getInstance().addDownloadProgress(id, 1);
        int p = MyApplication.getInstance().getDownloadProgress(id);
        holder.progressBar.setProgress(p);
        holder.progressText.setText(p + " / " + size);
    }

    private void setDownloadFail(ViewHolder holder, AtomicBoolean isCanOpen) {
        holder.progressBar.setVisibility(View.GONE);
        holder.progressText.setVisibility(View.GONE);
        holder.downloadButton.setVisibility(View.VISIBLE);
        holder.downloadButton.setText("下载");
        isCanOpen.set(false);
    }

    private void mergePDF(ViewHolder holder, AtomicBoolean isCanOpen,
                          DetailsData detailsData, int size, String savePath, int pos) {
        MergePDFDialog mergePDFDialog = new MergePDFDialog(context);
        mergePDFDialog.show();
        Log.i(TAG, detailsData.getTitle() + ", 下载完成，正在合并...");
        Toast.makeText(context, detailsData.getTitle() + ", 下载完成，正在合并pdf", Toast.LENGTH_LONG).show();
        try {
            FileUtils.forceMkdir(new File(KeledgeFileUtils.BOOK_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 解密合并
        Disposable disposable = Observable
                .create((ObservableOnSubscribe<Integer>) emitter -> {
                    // 先解密
                    PDFMergerUtility mergePdf = new PDFMergerUtility();
                    for (int j = 0; j < size; j++) {
                        File file = new File(
                                KeledgeFileUtils.PDF_PATH + File.separator
                                        + detailsData.getDefaultFileId() + File.separator + j + "." + PDF_SUFFIX);
                        mergePdf.addSource(file);
                    }
                    mergePdf.setDestinationFileName(savePath);
                    mergePdf.mergeDocuments(false);
                    emitter.onNext(1);
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    Toast.makeText(context, detailsData.getTitle() + ", 合并完成", Toast.LENGTH_LONG).show();
                    mergePDFDialog.dismiss();
                    Intent intent = OpenFileUtils.getPdfFileIntent(this.context, new File(savePath));
                    context.startActivity(intent);
                    // 删除pdf文件夹
                    FileUtils.deleteDirectory(new File(KeledgeFileUtils.PDF_PATH + File.separator + detailsData.getDefaultFileId()));
                    // 删除json数据
                    deleteAppData(detailsData.getDefaultFileId(), pos);
                    isCanOpen.set(true);
                    holder.progressBar.setVisibility(View.GONE);
                    holder.progressText.setVisibility(View.GONE);
                    holder.downloadButton.setVisibility(View.VISIBLE);
                    holder.downloadButton.setText("打开");
                }, throwable -> {
                    Log.e(TAG, detailsData.getTitle() + ", 合并错误: " + throwable.getMessage(), throwable);
                    Toast.makeText(context, detailsData.getTitle() + ", 合并错误: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    mergePDFDialog.dismiss();
                    setDownloadFail(holder, isCanOpen);
                });
    }

    private void deleteAppData(long id, int pos) {
        FileUtils.deleteQuietly(new File(KeledgeFileUtils.DATA_PATH + File.separator + id + "." + PDF_SUFFIX + "." + KEY_SUFFIX));
        FileUtils.deleteQuietly(new File(KeledgeFileUtils.DATA_PATH + File.separator + id + "." + PDF_SUFFIX + "." + JSON_SUFFIX));
        FileUtils.deleteQuietly(new File(KeledgeFileUtils.DATA_PATH + File.separator + id + "." + PDF_SUFFIX + "." + DETAIL_SUFFIX));
        detailsDataList.remove(pos);
        notifyItemRemoved(pos);
    }

    private int checkData(long id, int size) {
        if (size <= 0) return 0;
        for (int i = 0; i < size; i++) {
            String filePath = KeledgeFileUtils.PDF_PATH + File.separator
                    + id + File.separator + i + "." + PDF_SUFFIX;
            if (!new File(filePath).exists()) {
                return i;
            }
        }
        return size;
    }

    @Override
    public int getItemCount() {
        return detailsDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView cover;
        private TextView title;
        private TextView author;
        private Button downloadButton;
        private Button deleteButton;
        private ProgressBar progressBar;
        private TextView progressText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            downloadButton = itemView.findViewById(R.id.download_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
            progressBar = itemView.findViewById(R.id.progress_bar);
            progressText = itemView.findViewById(R.id.progress_text);
        }
    }
}

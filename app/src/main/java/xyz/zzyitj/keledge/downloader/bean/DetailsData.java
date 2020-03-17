package xyz.zzyitj.keledge.downloader.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/18 12:31 下午
 * @email zzy.main@gmail.com
 */
@Getter
@Setter
@ToString
public class DetailsData {
    private String Abstracts;
    private String Author;
    private String CoverUrl;
    private String CreateTime;
    private String UpdateTime;
    private String DefaultFileExtension;
    private long DefaultFileId;
    private String Publisher;
    private int ReadCount;
    private String Title;
}

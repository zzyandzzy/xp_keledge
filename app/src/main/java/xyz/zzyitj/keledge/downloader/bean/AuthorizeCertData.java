package xyz.zzyitj.keledge.downloader.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/18 9:46 上午
 * @email zzy.main@gmail.com
 */
@Getter
@Setter
@ToString
public class AuthorizeCertData {
    private String FileFormat;
    private String Key;
    private long NumberOfPages;
    private ArrayList<String> SplitFileUrls;
}

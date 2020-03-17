package xyz.zzyitj.keledge.downloader.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/18 11:56 上午
 * @email zzy.main@gmail.com
 */
@Getter
@Setter
@ToString
public class Details {
    public int Code;
    public DetailsData Data;
    public String Description;
    public boolean Success;
}

# xp_keledge
基于Xposed的可知pdf下载
因为目前可知改变了加密方式，为了获取加密后的密匙，就使用了Xposed这个神器

# 免责声明
请自觉遵守法律法规，本脚本仅供学习参考，所有下载的**PDF**请在**24小时**内删除，请勿传播，一切法律责任由用户自己承担，与本人无关

# 使用方法
第一步：安装可知APP，安装[Xposed](https://github.com/rovo89/Xposed) / [Ed Xposed](https://github.com/ElderDrivers/EdXposed) / [太极](https://taichi.cool/zh/)，太极最好激活**太极阳**模式（因为**太极阴**模式我没测试），下载[可知下载](https://github.com/zzyandzzy/xp_keledge/releases)，激活**可知下载**（太极如果不能激活请授权[无极码](https://taichi.cool/zh/doc/how-to-debug.html)）

第二步：打开可知APP，点击PDF阅读（仅支持PDF下载）会弹出Toast提示已下载数据（json数据）至本地，然后打开可知下载就能看到书籍了

一些细节：

1.
请允许可知下载APP读写手机存储的权限

2.
可知会封IP，每5分钟只能下载100页，如果是超过100页的PDF点击下载会瞬间发送大于100个的HTTP请求，进度条会一闪而过，又恢复到可下载状态，
这种情况其实是正在下载，不过发送了大于100个HTTP请求被可知拒绝了，所以APP里面的状态又为可下载.

3.
下载目录在：外置存储设备/keledge_download里面，

data目录是APP数据目录，也就是从可知抓取的数据

pdf目录是正在下载的书籍目录，里面是已解密但未合成的pdf

book目录是已下载完成并合成好的pdf

4.
pdf太大可能会导致合成失败（100MB左右，小米9）

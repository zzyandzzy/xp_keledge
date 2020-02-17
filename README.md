# xp_keledge
基于Xposed的可知pdf下载
因为目前可知改变了加密方式，为了获取加密后的密匙，就使用了Xposed这个神器

# 免责声明
请自觉遵守法律法规，本脚本仅供学习参考，所有下载的**PDF**请在**24小时**内删除，请勿传播，一切法律责任由用户自己承担，与本人无关

# 使用方法
第一步：安装可知APP，安装[Xposed](https://github.com/rovo89/Xposed) / [Ed Xposed](https://github.com/ElderDrivers/EdXposed) / [太极](https://taichi.cool/zh/)，太极最好激活**太极阳**模式（因为**太极阴**模式我没测试），下载[可知密匙](https://github.com/zzyandzzy/xp_keledge/raw/master/app/release/app-release.apk)，激活**可知密匙**（太极如果不能激活请授权[无极码](https://taichi.cool/zh/doc/how-to-debug.html)），安装一个顺手的抓包工具，比如：[HttpCanary](https://github.com/MegatronKing/HttpCanary)

第二步：手机连接电脑，输入命令：

```
adb logcat | grep "可知: result"
```

开始抓包，打开可知APP，点在线阅读（pdf版），等书籍打开后会看到控制台输出了类似下图的加密代码：

![cmd](https://github.com/zzyandzzy/xp_keledge/raw/master/images/1.png)

复制这串加密代码，停止抓包，打开抓包工具会有一个请求Url结尾为：`/transfer/aqr/AuthorizeForApp`的包，保存这个包的响应。

写一个简单的Python代码得到Key

```python
hex_ascii_key = ''
for i in '你得到的加密代码':
    hex_ascii_key += f'{ord(i):x}'
print(hex_ascii_key)
```

复制结果。

第三步，先测试下Key能不能用，在原来抓包的响应中返回的是json数据，在root->Data->SplitFileUrls里面随便复制一个Url，到浏览器下载并重命名为encode.pdf（这是加密的pdf）
输入：

```
openssl enc -d -aes-128-ecb -K 你的Key -in encode.pdf -out decode.pdf
```

看看decode.pdf能不能打开，如果能打开说明解密成功，继续下一步，下载整个pdf。

第四步，复制

```python
requests==2.22.0
progressbar33==2.4
PyPDF2==1.26.0
```

并把文件重命名为`requirements.txt`

复制下面的Python代码

```python
import os
import json
import uuid
import argparse
import requests
import progressbar
from PyPDF2 import PdfFileMerger, PdfFileReader, PdfFileWriter

parser = argparse.ArgumentParser()
parser.add_argument('-k', '--key', dest='key', help="input the key")
parser.add_argument('-n', '--name', dest='name', help="input books's name")
args = parser.parse_args()

if args.key == None or args.name == None:
    parser.print_help()
    os._exit(0)

try:
    with open('res.json', 'r') as f:
        data = json.load(f)
except Exception as e:
    print(e)

bookurls = data['Data']['SplitFileUrls']
uuid_str = uuid.uuid4()
tmp_dir = 'download_%s' % uuid_str
os.system(f'mkdir {tmp_dir}')

p = progressbar.ProgressBar()
for url in p(bookurls):
    page = bookurls.index(url) + 1
    filename = f"x-{page}"
    r = requests.get(url=url, stream=True)
    with open(tmp_dir + '/' + filename + '.aes', 'wb') as f:
        f.write(r.content)
    os.system(
        f'openssl enc -d -aes-128-ecb -K {args.key} -in {tmp_dir}/{filename}.aes -out {tmp_dir}/{filename}.pdf')
    os.system(f'rm {tmp_dir}/*.aes')

print("Synthesizing PDF file.........")
for root, dirs, files in os.walk(tmp_dir):
    if '.DS_Store' in files:
        files.remove('.DS_Store')
    files.sort(key=lambda x: int(x[x.rfind('-') + 1:][:-4]))
    file_list = [tmp_dir + '/' + file for file in files]
    merger = PdfFileMerger(strict=False)
    for pdf in file_list:
        merger.append(pdf)
    path = args.name + '.pdf'
    merger.write(path)
print("Success !!!!")
os.system(f'rm -rf {tmp_dir}')
```

重命名文件名为`download.py`，把第二步抓取的响应包重命名为`res.json`复制到`download.py`同一目录下，
运行

```
pip install
```

安装好环境后再运行：

```
python download.py -k 你的Key -n 文件名
```

等待下载并合成完毕。。。

至于为什么那么麻烦，因为我懒。。。

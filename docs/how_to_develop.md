# 如何进行二次开发

## 下载源码
我们可以开发的源码分两部分，这个我在cts专辑中也讲过，cts分两部分，一个是基础库，一个是运行库，所以我们的源码也分为两部分
### 运行库
同步CrashMonkey4Android源码:
` https://github.com/DoctorQ/CrashMonkey4Android.git`
或者下载zip包，解压.然后导入到eclipse中.

### 基础库
同步CrashMonkey4Android_tradefederation源码:
` git clone https://github.com/DoctorQ/CrashMonkey4Android_tradefederation.git`
或者下载zip包，解压.然后导入到eclipse中.

导入后，eclipse含有如下两个项目：

![](/Users/wuxian/Desktop/05F9DB8D-A7C6-4820-8C61-960019C138C7.png)


## eclipse配置

### 为cts-tradefed-host关联tradefederation

在cts-tradefed-host右键点击Build Path->Configure Build Path,切换到Projects一栏，然后添加tradefederation,添加后的效果如下:
![](/Users/wuxian/Desktop/6F4E7FC8-65EE-4D37-B491-43D509FB8CC1.png)

如果错误请自行排除.


### 配置运行参数

找到CtsConsole.java文件，位于`com.android.cts.tradefed.command`包下,右击Run As->Run Configurations,切换到Arguments一栏:

![](/Users/wuxian/Desktop/EB53358C-A7AF-4CBF-8EFC-9EEF277D098E.png)

其中VM arguments中填入`-DCTS_ROOT=/Users/wuxian/Documents`
后面的路径一定要指向你下载的可执行文件的根目录,比如我本地的可执行文件存放路径为`/Users/wuxian/Documents/android-cts`,所以我CTS_ROOT变量设置为android-cts的根目录`/Users/wuxian/Documents`。
然后在Program arguments一栏输入`run cts --plan Monkey`,点击Run 按钮就可以运行了。




## 友情提示

如遇问题，请转至[toubleshoot.md](toubleshoot.md)查找解决方法.










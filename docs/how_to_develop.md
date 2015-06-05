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

![这里写图片描述](http://img.blog.csdn.net/20150605111733336)


## eclipse配置

### 为cts-tradefed-host关联tradefederation

在cts-tradefed-host右键点击Build Path->Configure Build Path,切换到Projects一栏，然后添加tradefederation,添加后的效果如下:
![这里写图片描述](http://img.blog.csdn.net/20150605112113506)

然后切换到Order and Export保持上面的项目在tradefederation.jar之上：
![这里写图片描述](http://img.blog.csdn.net/20150605112153701)

这样你在tradefedertion项目中的修改就会应用到cts-tradefed-host项目中。

### 配置运行参数

找到CtsConsole.java文件，位于`com.android.cts.tradefed.command`包下,右击Run As->Run Configurations,切换到Arguments一栏:

![](/Users/wuxian/Desktop/EB53358C-A7AF-4CBF-8EFC-9EEF277D098E.png)

其中VM arguments中填入`-DCTS_ROOT=/Users/wuxian/Documents`
后面的路径一定要指向你下载的可执行文件的根目录,比如我本地的可执行文件存放路径为`/Users/wuxian/Documents/android-cts`,所以我CTS_ROOT变量设置为android-cts的根目录`/Users/wuxian/Documents`。
然后在Program arguments一栏输入`run cts --plan Monkey`,点击Run 按钮就可以运行了。


## 生成自己的执行文件

首先我们来看看执行文件tools目录下的文件：

![这里写图片描述](http://img.blog.csdn.net/20150605112353072)

其中cts-tradefed.jar就是cts-tradefed-host对应的jar包，tradefederation.jar就是tradefedertion项目对应的jar包，所以如果你开发完以后，想生成可执行文件，直接用你的项目替换这两个jar就行，下面说说如何导出成jar包。

### cts-tradefed.jar

在cts-tradefed-host项目右键，在弹出菜单中选择Export,选择Java项目下的jar file,点击Next:

![这里写图片描述](http://img.blog.csdn.net/20150605112929899)

在到处的资源文件选择中，选择src和res/report，不选择res/config的原因是我已经把该文件放到外面了，你可以在tools目录下看到，如果这里就不要选择了。然后在JAR file一栏点击Browse按钮，找到可执行文件路径下的cts-tradefed.jar，点击Finish就会将原来的替换掉。

# tradefederation.jar

在tradefederation项目选择同样找到上面的导出配置页面：

![这里写图片描述](http://img.blog.csdn.net/20150605113201228)

这里我们选择src和res文件，JAR file选择tools下的tradefederation.jar，就会替换掉之前的jar包。

## 友情提示

如遇问题，请转至[toubleshoot.md](toubleshoot.md)查找解决方法.











# Mac下配置

## 第一步
 下载可执行文件，去[release]( https://github.com/DoctorQ/CrashMonkey4Android/releases)页面中下载最新版本，点击android-cts.zip文件下载：

![这里写图片描述](http://img.blog.csdn.net/20150604152838451)

你也可以同步[CrashMonkey4Androd_bin]项目(https://github.com/DoctorQ/CrashMonkey4Androd_bin.git)来获取可执行文件，推荐使用这种方式，因为修复bug比较及时。

## 第二步

解压后，项目目录结构如下：

![这里写图片描述](http://img.blog.csdn.net/20150604153335077)

找到tools目录下的cts-tradefed文件,双击会出现终端窗口：

![这里写图片描述](http://img.blog.csdn.net/20150604153616125)

(可以看出来我们支持多设备)

## 第三步

在终端输入命令 ： `run cts --plan Monkey` 按`return` ,@Monkey开跑了。

![这里写图片描述](http://img.blog.csdn.net/20150604154251327)

## 第四步

参看报告，在tools的同级目录repository下有2个目录很重要
1. logs：保存测试过程中的截图和log信息
2. results: 保存测试报告

![这里写图片描述](http://img.blog.csdn.net/20150604154414734)

首先去results下打开报告，一个文件夹代表一次测试，我们刚才有2台设备，所以生成了2个报告，进入文件下找到index.html，打开：

![这里写图片描述](http://img.blog.csdn.net/20150604154900065)

**有crash版本：**

![这里写图片描述](http://img.blog.csdn.net/20150604160419624)
`index.html`为结果总结页面，上面显示了测试设备的硬件信息(Hardware)，被测应用信息(Application)，测试周期(Span)，结果(Results)。下方有一个表格中显示了测试所花时间(Duration),Monkey的事件数(20)。点击result一览的链接进入详细报告：
**无crash版本：**

![这里写图片描述](http://img.blog.csdn.net/20150604155242764)
**有crash版本**
![这里写图片描述](http://img.blog.csdn.net/20150604160526900)

详细页面中显示了最后50步的操作截图(少于50的全部列出)，截图上绘制了操作的类型，还可以点击图片查看该步操作的相关logcat信息。还包括3按钮(crash log按钮会在发生crash的时候显示)，点击`system log` 可以看到系统log：

![这里写图片描述](http://img.blog.csdn.net/20150604155706646)

点击`uiauto trace`按钮会显示所有步骤的信息：

![这里写图片描述](http://img.blog.csdn.net/20150604155805271)

如果有`crash log` 按钮，会显示crash的简短信息，目前只是简单的从logcat分析ANR和Java Crash信息，后续会详细研究一下crash知识:

![这里写图片描述](http://img.blog.csdn.net/20150604160101279)
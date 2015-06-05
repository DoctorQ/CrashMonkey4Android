# WINDOWS下配置

## 第一步
 

同步[CrashMonkey4Androd_bin](https://github.com/DoctorQ/CrashMonkey4Androd_bin.git)  项目来获取可执行文件，或者直接下载ZIP包，解压。

## 第二步

解压后，项目目录结构如下：

![这里写图片描述](http://img.blog.csdn.net/20150605155345923)

找到tools目录下的cts-tradefed.bat文件,双击会出现终端窗口：

![这里写图片描述](http://img.blog.csdn.net/20150605155436600)



## 第三步

在终端输入命令 ： `run cts --plan Monkey` 按`return` ,@Monkey开跑了。

![这里写图片描述](http://img.blog.csdn.net/20150605155636736)

## 第四步

参看报告，在tools的同级目录repository下有2个目录很重要
1. logs：保存测试过程中的截图和log信息
2. results: 保存测试报告

![这里写图片描述](http://img.blog.csdn.net/20150605155930153)

首先去results下打开报告，一个文件夹代表一次测试，我们刚才有1台设备，所以生成了1个报告，进入文件下找到index.html，打开：

![这里写图片描述](http://img.blog.csdn.net/20150605160151474)



其他信息和mac版一样.不再详细介绍.

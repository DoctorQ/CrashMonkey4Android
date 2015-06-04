# 简介

CrashMonkey4Android，是一个依靠Cts框架，对原生Monkey进行改造后的产物，拥有以下新增功能：

 1. 保存每一步的截图
 2. 保存logcat
 3. 保存每一个Monkey事件的信息
 4. 分析Crash
 5. Html报告
 6. 支持多设备


# 环境要求
 

 1. 安装JDK1.7并配置环境变量
 2. 安装SDK并配置环境变量



# 如何使用




  > 下面以Mac环境为例

## 第一步
 下载可执行文件，去[release]( https://github.com/DoctorQ/CrashMonkey4Android/releases)页面中下载最新版本，点击android-cts.zip文件下载：

![这里写图片描述](http://img.blog.csdn.net/20150604152838451)

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


# 参数配置

我们可以通过在命令行下输入`run cts --help-all` 获取所有的可设置参数：

```
test options:
    --p                  package of test app
    --a                  main activity of test app
    --v                  monkey event count Default: 20.
    --throttle           The delay time between the events Default: 300.
    --pct-tap            percentage of tap event Default: 25.0.
    --pct-motion         percentage of motion event Default: 0.0.
    --pct-nav            percentage of navigation event Default: 15.0.
    --pct-majornav       percentage of major navigation event Default: 15.0.
    --pct-syskeys        percentage of system key event Default: 15.0.
    --pct-drag           percentage of drag evnet Default: 30.0.
    --logcat-size        The max number of logcat data in bytes to capture when --logcat-on-failure is on. Should be an amount that can comfortably fit in memory. Default: 20480.
    --plan               the test plan to run.
    --[no-]reboot        Do not reboot device after running some amount of tests. Default behavior is to reboot. Default: false.
    --[no-]skip-device-info
                         flag to control whether to collect info from device. Providing this flag will speed up test execution for short test runs but will result in required data being omitted from the test report. Default: false.
    --[no-]device-unlock unlock device Default: false.
    --app-path           local app's path
    --wifiSsdk           wifi username
    --wifiPsk            wifi password
    --[no-]skip-uninstall-app
                         no uninstall test app Default: true.
    --monkey-log-size    monkey log size Default: 10485760.
    -b, --[no-]bugreport take a bugreport after each failed test. Warning: can potentially use a lot of disk space. Default: false.
    --[no-]tracefile     get trace file ,in /data/anr/trace.txt Default: false.

  'stdout' logger options:
    --log-level          minimum log level to display. Default: INFO. Valid values: [VERBOSE, DEBUG, INFO, WARN, ERROR, ASSERT]

  
```
主要关注以下属性：


 1. p ：测试app的包名.
 2. a ：测试app的主activity，如果正确设置上面两项，Monkey会针对上面-p指定的应用测试，一直保持在该应用界面.
 3. throttle：2个Monkey事件之间的间隔，默认为300毫秒.
 4. pct-tap：点击事件的百分比，默认为%25.
 5. pct-motion：多点滑动事件百分比，默认为%0，（暂时还没实现）.
 6. pct-nav: 导航事件的百分比(导航事件由方向输入设备的上下左右按键所触发的事件组成)，默认为%15.
 7. pct-majornav：主要导航事件的百分比。（这些导航事件通常会导致UI界面中的动作事件，如5-way键盘的中间键，回退按键、菜单按键）,默认为%15.
 8. pct-syskeys：系统事件百分比。（这些按键通常由系统保留使用，如Home、Back、Start Call、End Call、音量调节），默认为%15.
 9. pct-drag：拖拽事件的百分比，默认为%30.

目前只实现了5/6，但是上面的数字相加一定要为100%。

10. reboot : 重启机器，默认为false，不重启。如果想要重启的话，直接在命令行附上该参数，不用在后面加true，因为boolen类型的设置方式和其他不一样。
11. device-unlock：解锁手机，默认为false，如果收集重启的话，建议将该属性设置为true。解锁原理就是利用appium自带的apk来解锁的。
12. skip-device-info：是否跳过设备信息获取，默认为false。因为我们的报告中用到了设备信息，所以建议不要将该属性设置为true。
13. app-path:如果应用需要从本地安装，用该属性设置app路径，会自动安装app到收集端。
14. wifiSsdk： wifi的用户名
15. wifiPsk：wifi的密码

因为该工具支持自动连接wifi，所以你的app需要在wifi情况下工作，请设置这两个属性，它会自动检测断网并重连

16. skip-uninstall-app：是否跳过卸载app的阶段，因为如果使用本地app安装后，有时想卸载应用，可以设置该属性为false。默认是不卸载。
17. monkey-log-size：如果针对某一个应用测试，该工具为该app单独收集log，这里可以设置log可以最大到多少B。
18. bugreport：是否保存bugreport信息，默认为false。如果研发想要bugreport信息，将该属性设置为true。
19. tracefile：是否保存trace.txt文件，该文件位于/data/anr/trace.txt。一般发生crash的时候会用到该文件分析问题。

# 总结

目前CrashMonkey4Android还处于pre-release阶段，需要改善的地方还有很多，请大家多多提出建议。

目前我们已经推出了iOS和Android两个平台的Monkey，归并到一个组织[58Automation](https://github.com/58Automation)中,
欢迎大家fork，有问题请在github上提。





 





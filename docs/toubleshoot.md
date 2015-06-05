## TroubleShoot

配置过程中如遇问题,请查看下面的问题总结:

### 1. Unable to locate adb

```

06-04 19:34:10 W/DeviceManager: Fastboot is not available.
06-04 19:34:10 E/adb: Unable to locate adb.
Please use SDK Manager and check if Android SDK platform-tools are installed.
Android CTS 4.4_r0
Non-interactive mode: Running initial command then exiting.
Using commandline arguments as starting command: [run, cts, --plan, Monkey, -v, 10]
06-04 19:34:10 I/ConfigurationFactory: Loading configuration 'cts'
06-04 19:34:11 I/CommandScheduler: Waiting for invocation threads to complete
06-04 19:34:11 I/LogRegistry: Saved log to /var/folders/3j/s3hfvmy572vcn3h02c_rxcbm0000gn/T/tradefed_global_log_5366589741908633505.txt
06-04 19:34:11 I/CommandScheduler: All done

```

在Mac环境下会遇到这种问题，搜索后发现是Mac版的IDE的bug，所以如果想解决这个问题，需要将tradefederation项目中的DeviceManager.java项目中adb修改为你的sdk全目录就可以了.

![](/Users/wuxian/Desktop/CF3ABF70-C15A-4AFB-AB5E-EFE76B48041C.png)

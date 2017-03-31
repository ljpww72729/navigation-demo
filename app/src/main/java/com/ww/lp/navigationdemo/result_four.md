## 测试三  
### 测试目的
Navigate up with a new back stack

### 测试输入 
```
String uri = "navigationdemo://";
try {
    Intent intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
} catch (URISyntaxException e) {
    e.printStackTrace();
}
```

### 测试输出  
### app未处于后台  
* 第三方app通过uri scheme (添加NEW_TASK)唤起app的一个详情页面，处于不同的栈中  
```
$ adb shell dumpsys activity activities | sed -En -e '/Running activities/,/Run #0/p' 
    Running activities (most recent first):
      TaskRecord{154551d #177 A=com.ww.lp.navigationdemo U=0 StackId=1 sz=1}
        Run #1: ActivityRecord{28466df u0 com.ww.lp.navigationdemo/.OneActivity t177}
      TaskRecord{1007163 #176 A=com.ww.lp.thirdapp U=0 StackId=1 sz=1}
        Run #0: ActivityRecord{e9580f6 u0 com.ww.lp.thirdapp/.MainActivity t176}
    Running activities (most recent first):
      TaskRecord{7cd361e #149 I=com.android.launcher3/.Launcher U=0 StackId=0 sz=1}
        Run #1: ActivityRecord{ed1a0b u0 com.android.launcher3/.Launcher t149}
      TaskRecord{49154cc #167 A=com.android.systemui U=0 StackId=0 sz=1}
        Run #0: ActivityRecord{aed6d1b u0 com.android.systemui/.recents.RecentsActivity t167}

```
* 点击 R.id.home 按钮，详情页finish掉了，并未跳转到首页  
```  
 方法调用
I/lp_OneActivity: onCreate: 
I/lp_OneActivity: onOptionsItemSelected: 
I/lp_OneActivity: onOptionsItemSelected: shouldUpRecreateTask = false 
  
 栈信息
$ adb shell dumpsys activity activities | sed -En -e '/Running activities/,/Run #0/p' 
    Running activities (most recent first):
      TaskRecord{1007163 #176 A=com.ww.lp.thirdapp U=0 StackId=1 sz=1}
        Run #0: ActivityRecord{e9580f6 u0 com.ww.lp.thirdapp/.MainActivity t176}
    Running activities (most recent first):
      TaskRecord{7cd361e #149 I=com.android.launcher3/.Launcher U=0 StackId=0 sz=1}
        Run #1: ActivityRecord{ed1a0b u0 com.android.launcher3/.Launcher t149}
      TaskRecord{49154cc #167 A=com.android.systemui U=0 StackId=0 sz=1}
        Run #0: ActivityRecord{aed6d1b u0 com.android.systemui/.recents.RecentsActivity t167}

```
  
* 点击返回按钮，同上，因为是在不同的栈中，所以shouldUpRecreateTask()返回了false 
#### app处于后台  
* 如果自身app已经处于后台，不会新开一个栈，而是在原有的栈里添加一个activity，栈号是相同的，不要被迷惑了 
```  
 栈信息
    Running activities (most recent first):
      TaskRecord{b43ad4f #178 A=com.ww.lp.navigationdemo U=0 StackId=1 sz=2}
        Run #2: ActivityRecord{19a9b98 u0 com.ww.lp.navigationdemo/.OneActivity t178}
      TaskRecord{b6b4ba #179 A=com.ww.lp.thirdapp U=0 StackId=1 sz=1}
        Run #1: ActivityRecord{7eba07c u0 com.ww.lp.thirdapp/.MainActivity t179}
      TaskRecord{b43ad4f #178 A=com.ww.lp.navigationdemo U=0 StackId=1 sz=2}
        Run #0: ActivityRecord{7ed7fc6 u0 com.ww.lp.navigationdemo/.MainActivity t178}

```
* app处于后台，点击R.id.home，会展示之前打开的activity
```  
 日志信息  
   I/lp_MainActivity: onCreate: 
   I/lp_OneActivity: onCreate: 
   I/lp_OneActivity: onOptionsItemSelected: 
   I/lp_OneActivity: onOptionsItemSelected: shouldUpRecreateTask = false
   
   //此处是走onNewIntent还是onCreate()方法，主要看MainActivity的launchmode,具体参考测试一及测试二
   I/lp_MainActivity: onNewIntent: 
   
 栈信息
   Running activities (most recent first):
     TaskRecord{b43ad4f #178 A=com.ww.lp.navigationdemo U=0 StackId=1 sz=1}
       Run #1: ActivityRecord{7ed7fc6 u0 com.ww.lp.navigationdemo/.MainActivity t178}
     TaskRecord{b6b4ba #179 A=com.ww.lp.thirdapp U=0 StackId=1 sz=1}
       Run #0: ActivityRecord{7eba07c u0 com.ww.lp.thirdapp/.MainActivity t179}

```
* app处于后台，点击返回键，跳转到首页，但是不调用首页的onCreate()及onNewIntent()方法  
  为什么？因为他们处于同一个栈中，所以只是简单的finish()掉了
  
```
 日志信息  
    I/lp_MainActivity: onCreate: 
    I/lp_OneActivity: onCreate: 
    I/lp_OneActivity: onBackPressed: 
 
 
```

### 总结  
如果第三方app在跳转到自身app的详情页时，设置了NEW_TASK，详情页是否在新栈中打开，根据app的情况而定：  
1. 若app处于后台，则在原来的栈中压入一个activity；  
2. 若app未处于后台，则会创建一个新栈。  
至于返回的时候父activity是否调用onCreate()，onNewIntent()还是均不调用，主要看父activity的launchmode及NavUtils    
若使用NavUtils则根据父activity的launchmode是否是singleTop来调用onCreate或者onNewIntent;  
若未使用，则只是finish()掉，不会调用任何方法。    
`
如果你的app需要唤起第三方app的某个页面，最好的方式是唤起时添加NEW_TASK的flags，这样才不会被唤起的activity遮住自己的页面。   
`  
参见：git tag t1.0.4  

## 测试NavUtils及其表现形式
## 参考：https://developer.android.com/training/implementing-navigation/ancestral.html?hl=zh-cn
## 测试一  
### 测试目的
If the parent activity has launch mode <standard>, and the up intent does not contain FLAG_ACTIVITY_CLEAR_TOP, 
the parent activity is popped off the stack, and a new instance of that activity is created on top of the stack to receive the intent.

NavUtils.navigateUpFromSameTask(this);  

### 测试输入 
MainActivity: launchmode=standard   
OneActivity: launchmode=standard  
Intent:  
  Intent intent = new Intent(MainActivity.this, OneActivity.class);
  startActivity(intent);  

### 测试输出     
* 点击 R.id.home 按钮  
I/lp_MainActivity: onCreate:   
I/lp_MainActivity: bntOne onClick:   
I/lp_OneActivity: onCreate:   
I/lp_OneActivity: onOptionsItemSelected:   
I/lp_MainActivity: onCreate:      
* 点击返回按钮    
I/lp_MainActivity: onCreate:       
I/lp_MainActivity: bntOne onClick:   
I/lp_OneActivity: onCreate:   
I/lp_OneActivity: onBackPressed:   

### 总结  
NavUtils.navigateUpFromSameTask(this);  
该方法使用并不是简单的将父activity带到前台。   
参见：git tag t1.0.1  

## 测试二  
### 测试目的
If the parent activity has launch mode <singleTop>, or the up intent contains FLAG_ACTIVITY_CLEAR_TOP,
 the parent activity is brought to the top of the stack, and receives the intent through its onNewIntent() method.    
NavUtils.navigateUpFromSameTask(this);  

### 测试输入 
MainActivity: launchmode=sigleTop     
OneActivity: launchmode=standard  
Intent:  
  Intent intent = new Intent(MainActivity.this, OneActivity.class);
  startActivity(intent);  

### 测试输出     
* 点击 R.id.home 按钮  
I/lp_MainActivity: onCreate: 
I/lp_MainActivity: bntOne onClick: 
I/lp_OneActivity: onCreate: 
I/lp_OneActivity: onOptionsItemSelected: 
I/lp_MainActivity: onNewIntent: 
  
* 点击返回按钮    
I/lp_MainActivity: onCreate:       
I/lp_MainActivity: bntOne onClick:   
I/lp_OneActivity: onCreate:   
I/lp_OneActivity: onBackPressed:   

### 总结  
NavUtils.navigateUpFromSameTask(this);  
验证了测试目的    
参见：git tag t1.0.2  

## 测试三  
### 测试目的
Navigate up with a new back stack

### 测试输入 
```
String uri = "navigationdemo://";
try {
    Intent intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
    startActivity(intent);
} catch (URISyntaxException e) {
    e.printStackTrace();
}
```

### 测试输出  
* 第三方app通过uri scheme 唤起app的一个详情页面，可以看出属于同一个栈中  
```
   $ adb shell dumpsys activity activities | sed -En -e '/Running activities/,/Run #0/p'    
       Running activities (most recent first):   
         TaskRecord{ba7fb94 #171 A=com.ww.lp.thirdapp U=0 StackId=1 sz=2}   
           Run #1: ActivityRecord{8cdb783 u0 com.ww.lp.navigationdemo/.OneActivity t171}   
           Run #0: ActivityRecord{1e4f1b8 u0 com.ww.lp.thirdapp/.MainActivity t171}   
       Running activities (most recent first):   
         TaskRecord{7cd361e #149 I=com.android.launcher3/.Launcher U=0 StackId=0 sz=1}   
           Run #1: ActivityRecord{ed1a0b u0 com.android.launcher3/.Launcher t149}   
         TaskRecord{49154cc #167 A=com.android.systemui U=0 StackId=0 sz=1}   
           Run #0: ActivityRecord{aed6d1b u0 com.android.systemui/.recents.RecentsActivity t167}   
```
* 点击 R.id.home 按钮，详情页仍在第三方app的栈中，但是自身app的首页会在新的栈中打开  
```  
 方法调用
 I/lp_OneActivity: onCreate: 
 I/lp_OneActivity: onOptionsItemSelected: 
 I/lp_OneActivity: onOptionsItemSelected: shouldUpRecreateTask = true
 I/lp_MainActivity: onCreate:   
  
 栈信息
    Running activities (most recent first):
      TaskRecord{5de91bc #172 A=com.ww.lp.navigationdemo U=0 StackId=1 sz=1}
        Run #2: ActivityRecord{6abcd92 u0 com.ww.lp.navigationdemo/.MainActivity t172}
      TaskRecord{ba7fb94 #171 A=com.ww.lp.thirdapp U=0 StackId=1 sz=2}
        Run #1: ActivityRecord{8cdb783 u0 com.ww.lp.navigationdemo/.OneActivity t171}
        Run #0: ActivityRecord{1e4f1b8 u0 com.ww.lp.thirdapp/.MainActivity t171}
    Running activities (most recent first):
      TaskRecord{7cd361e #149 I=com.android.launcher3/.Launcher U=0 StackId=0 sz=1}
        Run #1: ActivityRecord{ed1a0b u0 com.android.launcher3/.Launcher t149}
      TaskRecord{49154cc #167 A=com.android.systemui U=0 StackId=0 sz=1}
        Run #0: ActivityRecord{aed6d1b u0 com.android.systemui/.recents.RecentsActivity t167}

```
  
* 点击返回按钮，正常finish掉自身栈中的activity      
```
$ adb shell dumpsys activity activities | sed -En -e '/Running activities/,/Run #0/p' 
    Running activities (most recent first):
      TaskRecord{c1a4584 #175 A=com.ww.lp.thirdapp U=0 StackId=1 sz=1}
        Run #0: ActivityRecord{5e32978 u0 com.ww.lp.thirdapp/.MainActivity t175}
    Running activities (most recent first):
      TaskRecord{7cd361e #149 I=com.android.launcher3/.Launcher U=0 StackId=0 sz=1}
        Run #1: ActivityRecord{ed1a0b u0 com.android.launcher3/.Launcher t149}
      TaskRecord{49154cc #167 A=com.android.systemui U=0 StackId=0 sz=1}
        Run #0: ActivityRecord{aed6d1b u0 com.android.systemui/.recents.RecentsActivity t167}

```

### 总结  
如果第三方app在跳转到自身app的详情页时，若未设置NEW_TASK，则会导致详情页在处于第三方app的栈中，这种体验对第三方
app来说体验不好，会造成用户使用混乱。解决办法参见下一个提交  
参见：git tag t1.0.3  




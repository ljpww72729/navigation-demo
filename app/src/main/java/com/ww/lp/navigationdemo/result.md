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


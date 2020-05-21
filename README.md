# Client-of-charging-system
电动汽车充电系统客户端(Android)  
管理端(web)实现地址: https://github.com/M-michael-zhang/Management-end-of-charging-system  
后台实现地址:  https://github.com/M-michael-zhang/Charging-systems-for-electric-vehicles  
# 技术栈  
Baidu Map Android SDK 实现地图定位，标志物和导航  
ZXing barcode library  实现二维码扫码
# 实现效果
***地图与导航***
![地图与导航](https://raw.githubusercontent.com/M-michael-zhang/Uav-charging-pile-system/master/show/map.gif)
***短信登录***
![短信登录](https://raw.githubusercontent.com/M-michael-zhang/Uav-charging-pile-system/master/show/sms_login.gif)
***聊天***
![聊天](https://raw.githubusercontent.com/M-michael-zhang/Uav-charging-pile-system/master/show/chat.gif)  
# 导入过程  
* 将项目导入AndroidStudio,配置key（这个有点坑，可能是我不熟练）
* 在百度地图开放平台中申请应用，将AK配置在AndroidManifest.xml中，如下：
```
<meta-data
            android:name="com.baidu.lbsapi.API_KEY"
            android:value="XXXXXXXXXXXXXXXXXXXXXX" />
```
* 在res/values/strings.xml中设置service_address为自己的ip地址和端口

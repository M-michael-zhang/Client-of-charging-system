# Client-of-charging-system
电动汽车充电系统客户端(Android),功能包括了地图定位，充电桩展示，充电桩预约，充电桩导航，充电桩扫码，历史订单信息，远程关闭充电，用户登录（密码登录和验证码登录），聊天通信，修改用户信息，修改密码  
管理端(web)实现地址: https://github.com/M-michael-zhang/Management-end-of-charging-system  
后台实现地址:  https://github.com/M-michael-zhang/Charging-systems-for-electric-vehicles  
# 下载体验
请使用在release中的安装包
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
# 联系作者
paypome@163.com

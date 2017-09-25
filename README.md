# aiui
科大讯飞aiui后处理Demo，包括服务端、android app

[博客文章](http://www.jianshu.com/p/b548671e0541)

# 准备工作
需要一台可以公网访问的服务器，并且安装nodejs，在AIUI控制台中配置后处理地址为该服务器地址

# 后处理服务端
```
npm install
node ./bin/www
```

# android 

与官方demo的区别：在语义理解demo中加入了语音合成，将aiui返回的内容语音播报出来

选择最后一个
![](https://raw.githubusercontent.com/jackgreentemp/aiui/master/images/1.jpeg)

进入录入界面，可语音或者文字录入
![](https://raw.githubusercontent.com/jackgreentemp/aiui/master/images/2.jpeg)

文字录入示例
![](https://raw.githubusercontent.com/jackgreentemp/aiui/master/images/3.jpeg)

语义理解结果
![](https://raw.githubusercontent.com/jackgreentemp/aiui/master/images/4.jpeg)

# ProjectCache

#### 项目介绍
我的测试大杂烩.

#### 1.定制ImageView使用说明【研究状态：Finish】

1.按比例设置图片的长宽
2.设置圆角图片
```java
 <mos.kos.cache.sakura.image.SakuraImage
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:scaleType="centerCrop"
        android:src="@drawable/image"
        sakura:radius="20"
        sakura:ratio="1"
        sakura:refer="高"/>
```
> `radius`为圆弧长度，圆弧长<=0的时候，弧度为0，大于0的时候图片展示有弧度
><br/> `ratio`为长宽的比例、`refer`为参考边

- 1.宽高比为1的时候
![圆角图片](https://github.com/KosmoSakura/ProjectCache/blob/master/show/circle_1.png)
- 2.这里是以宽为参考边
![圆角图片](https://github.com/KosmoSakura/ProjectCache/blob/master/show/circle_2.png)
- 3.这里是以高为参考边
![圆角图片](https://github.com/KosmoSakura/ProjectCache/blob/master/show/circle_3.png)

#### 2.桌面宠物【研究状态：暂停】
基于[sufushi/MyPet](https://github.com/sufushi/MyPet)二次开发

#### 3.粒子雨【研究状态：暂停】
粒子绘制的雨

#### 4.时光轴【研究状态：进行中】
类似于QQ空间的时光轴效果

#### 5.转转转【研究状态：进行中】
![转转转](https://github.com/KosmoSakura/ProjectCache/blob/master/show/Whorl.gif)

#### 6.下拉刷新【研究状态：进行中】
基于[XRecyclerView](https://github.com/XRecyclerView/XRecyclerView)的研究、测试心得和笔记snapseed
- 关于基类XHolder的封装
- 还有所有的适配器他爹XAdapter的封装

#### 7.讯飞语音转文字【研究状态：暂停】
集成讯飞语音输入框架


-------
License
-------

    Copyright 2015 jianghejie

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
# ProjectCache

#### 项目介绍
我的测试大杂烩.

#### 1.定制ImageView使用说明

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

![圆角图片](https://github.com/KosmoSakura/ProjectCache/blob/master/show/images.png)
#### 2.桌面宠物
#### 3.粒子雨
#### 4.时光轴
#### 5.转转转
![转转转](https://github.com/KosmoSakura/ProjectCache/blob/master/show/Whorl.gif)
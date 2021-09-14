# Simple Browser

Android APP

> 本仓库现设置为私密状态，在本学期大作业提交截止日期之前，请勿与他人共享本仓库任何内容。
> 
> @toulzx 编辑

项目成员： @toulzx  @GodWuzZ  @TangJoy0615

## Tips

### Version

Android Studio Arctic Fox (2021-03)

#### compileOptions

`sourceCompatibility JavaVersion.VERSION_11`

`targetCompatibility JavaVersion.VERSION_11`

#### distribution

`distributionUrl = https\://services.gradle.org/distributions/gradle-7.0.2-bin.zip`

#### dependencies

`com.android.tools.build:gradle:7.0.2`

## Requirements

### Mandatory

- **`输入链接访问`**
- **`历史记录`**：
  - _按时间排序_
  - _点击可访问_
- **`网页快照`**：_保存网页到本地_
- **`收藏夹`**：
  - _可分类存放_
  - _可编辑修改_
- **`浏览器界面设置`**：
  - `全屏模式`
  - `仅横/竖屏`

### Optional

- **`网址匹配`**
- **`浏览器界面设置`**
  - `屏幕模式自适应`
  - `无图模式`

### Considerable

- `扫描二维码`
- `内核`

## Plans

主要由 @toulzx 开发，计划 1 个月内完成。

目前开发计划主要以：

- 以 `$1`、`$10`、`$17` 为样例
- 以 `$2`、`$11` 为参考
- 以 `$6`、`$14`、`$22` 为补充
- 以 `$20` 探索内核

**优先满足基础功能的稳定性**！然后再考虑探索其它功能。

## Criteria

### Bonus

1. 基准分： 60
2. 搭建环境并进行开发与调试：5
3. 文档清晰、功能设计合理：10
4. 实现全部 `必选（Mandatory）` 功能：15
5. 代码整齐清晰：5
6. 可选功能：5
7. UI 设计优美、用户体验好：5

### Demerits

1. 无文档：-10
2. 缺少功能：-5/个
3. 无注释：-10
4. 功能异常、闪退：-5/个

## Reference

GitHub `Android browser` 关键字搜索，取 `Best match` 和 `Recent updated` 各自前 200 条记录查看。

1. **[mengkunsoft/MkBrowser](https://github.com/mengkunsoft/MkBrowser)**
  
   - 注释丰富、UI美观（支持深色模式）
   - 代码简单
   - 只有最基本功能

2. [ricky9090/EasyBrowser](https://github.com/ricky9090/EasyBrowser)

   - 注释丰富、UI一般
   - 代码复杂
   - 功能基本实现，但 体验上有较多待完善的 bugs
   - 可与`mengkunsoft/MkBrowser`一起修改

3. _~~[Ericsongyl/S-Browser](https://github.com/Ericsongyl/S-Browser)~~_

   - 无法打开
   - 复杂，少注释
   - 有内核，有 HTML

4. _~~[ByskyXie/GeekBrowser](https://github.com/ByskyXie/GeekBrowser)~~_

   - 可以打开，无法使用，UI一般
   - 代码复杂程度中等，注释丰富（可参考）
   - TargetSDK 26

5. _~~[NoorMohammedAnik/SimpleWebBrowser](https://github.com/NoorMohammedAnik/SimpleWebBrowser)~~_

   - 可以打开，UI 差，体验极差
   - 代码极其简单

6. [chdder/SimpleAndroidBrowser](https://github.com/chdder/SimpleAndroidBrowser)

   - 注释丰富、UI 一般
   - 代码复杂程度中等。体验上有待完善的 bugs
   - 有**技术文档**，可作为参考！！
   - TargetSDK 26（较为久远）

7. _~~[xSavior-of-God/FullScreenBrowser_Android](https://github.com/xSavior-of-God/FullScreenBrowser_Android)~~_

    - 无法使用，但**全屏模式**可参考

8. _~~[Faskyy/WebBrowser](https://github.com/Faskyy/WebBrowser)~~_

   - 可以打开，UI 差，体验差
   - 代码复杂度中等

9. _~~[Juliaaan2502/browserAndroid](https://github.com/Juliaaan2502/browserAndroid)~~_

   - 无法使用

10. **[seehin/Android_Browser](https://github.com/seehin/Android_Browser)**

    - 注释较少，代码规范很好，UI 中上
    - 代码复杂度较高，几乎没bug！！！
    - 功能丰富！
    - TargetSDK30!
    - 不亏字节跳动

11. [shensishijie/Android-browser](https://github.com/shensishijie/Android-browser)

     - 注释丰富，代码规范良好，UI 差
     - 代码复杂度中等，体验上有待完善的 bugs
     - Target 30
     - 可作为参考

12. _~~[Mohamedhazzali/Android-Browser-App](https://github.com/Mohamedhazzali/Android-Browser-App)~~_

      - 无法运行，文件不完整

13. _~~[mesameerkumar/Web-Browser](https://github.com/mesameerkumar/Web-Browser)~~_

      - 体验极差，几乎无法使用

14. [wdfgithub/AndroidBrowser](https://github.com/wdfgithub/AndroidBrowser)

    - 无注释，代码复杂度中上，代码规范良好，UI差
    - 基础体验一般，有明显、待完善 bugs
    - 有分享功能、下载功能（可参考）
    - TargetSDK 27

15. _~~[avipars/SimpleBrowse](https://github.com/avipars/SimpleBrowse)~~_

    - 无法打开
    - 注释少，代码复杂度低
    - TargetSDK 25

16. _~~[Bonkware/B-browser](https://github.com/Bonkware/B-browser)~~_

      - 可以打开，UI 差，体验差
      - 代码简单
      - 有开屏界面

17. **[HeXavi8/Orange-Browser](https://github.com/HeXavi8/Orange-Browser)**

    - 注释中等，代码规范好
    - 体验中上，有体验上的 bugs
    - 特色：注册登录（应该是本地的）、适配深色模式、有开屏界面
    - TargetSDK 30
    - 华南理工 × 字节跳动

18. _~~[VladThodo/behe-explorer](https://github.com/VladThodo/behe-explorer)~~_

    - TargetSDK 25 且 Gradle 版本较低，放弃

19. _~~[mogoweb/365browser](https://github.com/mogoweb/365browser)~~_

    - 无法打开，不深究
    - 有内核，代码复杂，是当初比较经典的 demo
    - TargetSDK 25 较久

20. [JackyAndroid/AndroidChromium](https://github.com/JackyAndroid/AndroidChromium)

    - 体积大
    - 谷歌浏览器源码
    - 注释不多。代码复杂度高
    - 有内核
    - 可作为研究用
    - TargetSDK 较久

21. _~~[xdevs23/Cornowser](https://github.com/xdevs23/Cornowser)~~_

     - 无法打开
     - 体积大

22. [jawaid-Ahmed/Leo-Browser](https://github.com/jawai  d-Ahmed/Leo-Browser)

    - 注释少，代码规范良好
    - 体验一般，有 bugs
    - 特色：**全屏模式**、有开屏界面、多任务
    - TargetSDK 30

23. _~~[CemalMertOzkan/SteelBat-Browser](https://github.com/CemalMertOzkan/SteelBat-Browser)~~_

      - 缺少文件，无法运行

### others

- [hikikomoriphoenix/Beedio](https://github.com/hikikomoriphoenix/Beedio)
- [Android Browser Switch](https://github.com/braintree/browser-switch-android)
- [Goverse/browser-android](https://github.com/Goverse/browser-android)


## 其它

[Android Studio 之 Gradle 和 Gradle 插件的区别](https://blog.csdn.net/LVXIANGAN/article/details/)
# 安心圈 FamilyGuard

面向家庭日常场景的 Android 安全守护 App：位置共享、电子围栏、异常告警、一键求助、健康提醒。

## 技术栈

- Kotlin + MVVM + 协程 / Flow
- Android SDK 34（minSdk 24）
- Room 本地数据库（KSP）
- Foreground Service 后台定位
- Material Components + ViewBinding
- Gradle 8.7 / AGP 8.5.2 / Kotlin 1.9.24

## 当前进度（MVP v0.1.0）

已实现：

- [x] 底部导航四大模块：首页 / 家庭圈 / 告警 / 我的
- [x] 家庭圈成员列表（Room 持久化 + Flow 实时刷新）
- [x] 首页 SOS 一键求助（写入紧急告警）
- [x] 告警中心（添加、处理、状态展示）
- [x] 后台定位前台服务（写入本地位置记录）
- [x] 定位权限申请与服务开关
- [x] 首次启动演示数据注入
- [x] 管理员控制台：身份校验、数据概览、成员/围栏管理、告警与轨迹清理
- [x] 液态玻璃 UI：彩色光晕背景、半透明玻璃卡片、高光描边、毛玻璃底部导航
- [x] iOS 27 导航栏：大标题随滚动折叠为小标题、内容从玻璃条下柔和淡出、右侧玻璃胶囊操作
- [x] iOS 风格设置页：分组列表 + 自绘 UISwitch（按下时滑块变为玻璃透镜）
- [x] Liquid Glass 磨砂浓度滑块：通透 ↔ 磨砂实时调节，偏好本地持久化
- [x] 切换动效：tab 图标弹性放大、页面缩放淡入、导航按钮 ripple
- [x] 统一间距系统：页面边距 16dp、卡片内边距 16dp、双列栅格严格等分

## 构建

依赖 JDK 21、Android SDK Platform 34、Build Tools 34。

```bash
# 本机 Gradle
/d/Gradle/gradle-8.7/bin/gradle assembleDebug

# 或使用 wrapper（生成后）
./gradlew assembleDebug
```

SDK 路径在 `local.properties` 中配置（已指向 `D:\AndroidSDK`，该文件不提交）。

## 目录结构

```
app/src/main/java/com/limithue/familyguard/
├── FamilyGuardApp.kt        # Application
├── MainActivity.kt          # 主容器 + 底部导航
├── data/
│   ├── db/                  # Room 数据库、Entity、DAO
│   ├── model/               # 数据模型
│   └── repository/          # 数据仓库
├── service/LocationService  # 后台定位服务
├── util/
│   ├── PermissionHelper     # 权限工具
│   ├── IosNavController     # 导航栏折叠 / 滚动边缘
│   ├── GlassTint            # 磨砂浓度 → 玻璃透明度
│   └── AppearancePrefs      # 外观偏好存储
└── ui/
    ├── widget/IosSwitch     # 自绘 iOS 开关
    └── ...                  # 首页/家庭圈/告警/控制台/我的 + Adapter
```

## 设计：iOS 27 液态玻璃

材质按 Liquid Glass 的分层结构实现（观感近似，非系统材质）：

- **外暗边**：向下偏移的冷色轮廓，制造玻璃厚度
- **上缘定向高光**：光源固定在左上，形成镜面亮带
- **底部内阴影**：下沉感与厚度
- **色散描边**：外层冷紫 + 内层白光，模拟边缘色散
- **磨砂滑块**：0 = 通透（只保留玻璃边缘轮廓），100 = 乳白磨砂

导航层（顶栏、底栏）与内容层卡片使用同一材质的不同浓度，避免玻璃叠玻璃。

> Android 无法像 iOS 一样按帧折射/模糊背后的内容，这里用多层渐变 + 高光 + 内阴影做静态近似；如需真模糊，可在 API 31+ 接入 RenderEffect 或 BlurView。

## 仓库

https://github.com/limithue/FamilyGuard

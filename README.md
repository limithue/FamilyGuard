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
├── util/PermissionHelper    # 权限工具
└── ui/                      # 首页/家庭圈/告警/我的 + Adapter
```

## 仓库

https://github.com/limithue/FamilyGuard

# KDungeon

* 支持异步生成遗迹 
* 支持随玩家移动和随区块创建两种生成判定点的模式 
* 支持多种条件，未来还会支持自定义生物群落
* 支持多种木牌处理方式，现有：生成原版怪，生成mm怪，运行命令3种。未来会开放API 
* 木牌懒处理模式：当玩家靠近遗迹时才会处理木牌并销毁遗迹对象 
* 发包隐藏木牌，让玩家无法在远处看到遗迹中的木牌，避免破坏“沉浸感” 
* 遗迹生成的平滑化处理：让遗迹的棱角不再突出，避免与周围显得格格不入 
* 支持配置文件自动重载 
* 支持游戏内菜单，显示遗迹的种类和显示，并且可以设置遗迹的条件参数 


- -----
# 构建方式

**Windows平台:**

```
gradlew.bat clean build
```

**macOS/Linux平台:**

```
./gradlew clean build
```

构建完成后jar包将在 `./build/libs` 文件夹
# MapView 室内地图图层控件

> 项目地址：[https://github.com/onlylemi/MapView](https://github.com/onlylemi/MapView)

这是我在做一个室内地磁导航应用时，所写的一个地图图层框架，后来把它抽出来单独写成了一个 `MapView` 控件。

## 图层

* MapLayer —— 地图图层
	* rotate —— 旋转（双指旋转）
	* scale —— 缩放（双指缩放）
	* translate —— 平移（单指平移）
* LocationLayer —— 定位图层（地位时，所需要的定位点显示）
	* Sensor —— 传感器设置（设置指示方向、指南针方向）
* BitmapLayer —— 图片图层（在地图中加入图片，同时可设置点击事件）
* MarkLayer —— 标记点图层（商店）
* RouteLayer —— 导航路线图层
	* ShortestPath —— 两点间最短路径（采用 `Floyd 算法`）
	* BestPath —— 多点间最优路径（采用 `遗传算法`）

## 效果图

![](https://raw.githubusercontent.com/onlylemi/res/master/android_mapview_1.gif)
![](https://raw.githubusercontent.com/onlylemi/res/master/android_mapview_2.gif)
![](https://raw.githubusercontent.com/onlylemi/res/master/android_mapview_3.gif)

## 使用

### MapLayer

* 首先需要在你的布局问价中加入 MapView 布局控件

```xml
	<com.onlylemi.mapview.library.MapView
        android:id="@+id/mapview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

* 在代码中，获取 `MapView` 对象，并且设置地图

```java
		MapView mapView = (MapView) findViewById(R.id.mapview);
    	Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open("map.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加载地图
        mapView.loadMap(bitmap);
        // 设置监听器
        mapView.setMapViewListener(new MapViewListener() {
            @Override
            public void onMapLoadSuccess() {
                Log.i(TAG, "onMapLoadSuccess");
                // 设置当前地图的旋转角度为 60
                mapView.setCurrentRotateDegrees(60);
            }

            @Override
            public void onMapLoadFail() {
                Log.i(TAG, "onMapLoadFail");
            }

        });
```

### BitmapLayer

在地图加载成功之后 `onMapLoadSuccess()` 方法中添加

```java
	Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
	// 新建一个 BitmapLayer 图层
    BitmapLayer bitmapLayer = new BitmapLayer(mapView, bmp);
    // 设置图片的位置
    bitmapLayer.setLocation(new PointF(400, 400));
    // 设置图片点击监听事件
    bitmapLayer.setOnBitmapClickListener(new BitmapLayer.OnBitmapClickListener() {
        @Override
        public void onBitmapClick(BitmapLayer layer) {
            Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_SHORT).show();
        }
    });
    // 添加图层到 mapview 中
    mapView.addLayer(bitmapLayer);
    // 刷新
    mapView.refresh();
```

### LocationLayer

```java
	// 初始化一个 LocationLayer 图层
	LocationLayer locationLayer = new LocationLayer(mapView, new PointF(400, 400));
	// 设置打开指南针
    locationLayer.setOpenCompass(true);
    // 设置指南针圆环旋转角度（指南针）
    locationLayer.setCompassIndicatorCircleRotateDegree(60);
    // 设置箭头指示角度（指示当前位置的方向角）
    locationLayer.setCompassIndicatorArrowRotateDegree(-30);
    // 添加图层
    mapView.addLayer(locationLayer);
    // 刷新
    mapView.refresh();
```

### MarkLayer 和 RouteLayer

这两个图层一般都是在一起使用的，`MarkLayer` 图层设置标记点（**标记点**、**转弯点**、**转弯点的邻接关系**），`RouteLayer` 图层，一般都是计算**定位点到某个标记点的路线**和***多个标记点间的最优路线*。

* 1. 初始化各个数据

```java
		// 地图中所有路线转折点 集合（计算路径、画路线中使用）
		List<PointF> nodes = TestData.getNodesList();
		// 地图中相邻两个转折点 集合（标号、构建邻接关系）
        List<PointF> nodesContract = TestData.getNodesContactList();
        // 地图中真正存在的标记点（商家）
        List<PointF> marks = TestData.getMarks();
        // 商店的名字
        List<String> marksName = TestData.getMarksName();
        // 初始化数据
        MapUtils.init(nodes.size(), nodesContract.size());
```

* 2. 加载地图，添加图层（建议计算路线可以放到线程中）

```java
		MapView mapView = (MapView) findViewById(R.id.mapview);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open("map.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapView.loadMap(bitmap);
        mapView.setMapViewListener(new MapViewListener() {
            @Override
            public void onMapLoadSuccess() {
            	// 添加路线图层
                RouteLayer routeLayer = new RouteLayer(mapView);
                mapView.addLayer(routeLayer);

                // 添加标记点图层
                MarkLayer markLayer = new MarkLayer(mapView, marks, marksName);
                mapView.addLayer(markLayer);
                // 设置标记点监听器
                markLayer.setMarkIsClickListener(new MarkLayer.MarkIsClickListener() {
                    @Override
                    public void markIsClick(int num) {
                    	// 得到被点击的点
                        PointF target = new PointF(marks.get(num).x, marks.get(num).y);
                        // 计算定位点到该点的最短路径（这里任意设置了一个点）
                        List<Integer> routeList = MapUtils.getShortestDistanceBetweenTwoPoints
                                (marks.get(39), target, nodes, nodesContract);
                        // 设置转折点集
                        routeLayer.setNodeList(nodes);
                        // 设置路线集
                        routeLayer.setRouteList(routeList);
                        // 刷新
                        mapView.refresh();
                    }
                });
                mapView.refresh();
            }

            @Override
            public void onMapLoadFail() {
            }

        });
```

## END

如果你在使用过程中，有任何问题，欢迎与我[联系](http://onlylemi.github.io/about/)~
# ListView 介绍

* java.lang.Object
	* android.view.View
		* android.view.ViewGroup
			* android.widget.AdapterView<android.widget.ListAdapter>
				* android.widget.AbsListView
					* android.widget.ListView


Adpater 在 ListView 和数据源之间起到了一个桥梁的作用，通过 Adapter 为 ListView 来填充数据。

## RecycleBin机制

对 listview 中的 item view 进行缓存重用。
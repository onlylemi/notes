# chapter19-组合模式

将对象组合成树形结构以表示“部分——整体”的层次结构，使得用户对单个对象和组合对象的使用具有一致性。

> Android 中 View、ViewGroup 就是这种结构

## UML

![](https://raw.githubusercontent.com/onlylemi/res/master/dp_composite_uml.png)

* Component（Dir） —— 抽象节点，为组中中的对象声明接口。
* Composite（Folder） —— 定义子节点的那些干节点的行为，储存子节点。
* Leaf（File） —— 在组合中表示叶子节点对象，叶子节点没有子节点。

## 示例

> 以目录文件夹结构为例，目录中下有文件夹和问价，文件夹下还有文件夹和文件

#### Dir 抽象目录

```java
public abstract class Dir {

    private String name;

    public Dir(String name) {
        this.name = name;
    }

    /**
     * 输出文件夹目录结构
     */
    public abstract void print();

    /**
     * 获取文件夹或文件名
     *
     * @return
     */
    public String getName() {
        return name;
    }
}
```

#### Folder 文件夹

```java
public class Folder extends Dir {

    protected List<Dir> dirs;

    public Folder(String name) {
        super(name);
        dirs = new ArrayList<Dir>();
    }

    /**
     * 添加文件夹或文件
     *
     * @param dir
     */
    public void addDir(Dir dir) {
        dirs.add(dir);
    }

    /**
     * 删除文件夹或文件
     *
     * @param dir
     */
    public void rmDir(Dir dir) {
        dirs.remove(dir);
    }

    /**
     * 清空目录
     */
    public void clear() {
        dirs.clear();
    }

    /**
     * 输出目录结构
     */
    public void print() {
        System.out.print(getName() + "{");
        Iterator<Dir> iterator = dirs.iterator();
        while (iterator.hasNext()) {
            Dir dir = iterator.next();
            dir.print();
            if (iterator.hasNext()) {
                System.out.print(", ");
            }
        }
        System.out.print("}");
    }

    public List<Dir> getDirs() {
        return dirs;
    }
}
```

#### File 文件

```java
public class File extends Dir {

    public File(String name) {
        super(name);
    }

    @Override
    public void print() {
        System.out.print(getName());
    }
}
```

#### 测试

```java
public class CompositeTest {

    public static void main(String[] args) {
        Folder folder1 = new Folder("folder1");
        Folder folder2 = new Folder("folder2");

        File file1 = new File("file1");
        File file2 = new File("file");
        File file3 = new File("file3");

        folder1.addDir(file1);
        folder1.addDir(folder2);
        folder2.addDir(file2);
        folder2.addDir(file3);

        folder1.print();
        // 输出：folder1{file1, folder2{file, file3}}
        System.out.println();
        folder2.print();
        // 输出：folder2{file, file3}
    }
}
```


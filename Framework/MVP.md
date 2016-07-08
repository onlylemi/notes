# MVP

![](https://raw.githubusercontent.com/onlylemi/res/master/mvp.png)

## 结构

* Presenter —— 交互中间人。主要沟通View和Model的桥梁，它从Model层检索数据之后，返回给View层，使得View和Model之间没有耦合，也讲业务逻辑从View角色上抽离出来。
* View —— 用户界面。View通常指Activity、Fragment或者某个View空间，它包含一个Presenter成员变量。通常View需要实现一个逻辑接口，将View上的操作交给Presenter进行实现，最后，Presenter调用View的逻辑接口将结果返回给View对象。
* Model —— 数据的存取。主要提供数据的存取功能，Presenter需要通过Model层存储、获取数据，model就像一个数据仓库。更直白的说，Model封装了数据库DAO或者网络获取数据的角色，或者两种数据获取方式的集合。

## 示例

Google官方提供的MVP示例为例分析：[https://github.com/googlesamples/android-architecture](https://github.com/googlesamples/android-architecture) 中的 [`todo-mvp`](https://github.com/googlesamples/android-architecture/tree/todo-mvp/)

### 项目结构

```
todo-mvp
	addedittask
		AddEditTaskActivity.java
		...Contract.java
		...Fragment.java---------------------- View层
		...Presenterjava---------------------- Presenter层
	data-------------------------------------- Model 层
		source
			local...
			remote...
		TasksDataSource.java
		TasksRepository.java
	statistics
		StatisticsActivity.java
		...Contract.java
		...Fragment.java
		...Presenterjava
	taskdetail...
	tasks...
	util...
	BasePresenter.java
	BaseView.java
```

没有列出所有类，只是部分做参考，可以看出项目结构是按功能模块进行划分（也可以使用activity、adapter、fragment、contract、presenter进行划分）。不过其中多了 Contract 一个类来管理View与Presenter，使得View与Presenter中的功能一目了然，同时维护起来也比较方便。

下面以 `addedittask` 功能分析，其他功能类似

#### 基类

**BaseView** 是View的基类，包含 `setPresenter()` 方法将Presenter实例传到View层，调用时机在Presenter的实现类的构造函数中调用，相当于在Activity中生成一个Presenter对象时，该方法就会被调用，设置给View层中的Presenter对象。

```java
public interface BaseView<T> {
    void setPresenter(T presenter);
}
```

**BasePresenter** 是Presenter的基类，包含 `start()` 方法，其作用是Presenter开始获取数据并调用View中的方法改变界面的显示，调用时机在View层实现类中也就是Fragment类的 `onResume()` 方法中。

```java
public interface BasePresenter {
    void start();
}
```

#### Contract类统一管理View与Presenter

```java
public interface AddEditTaskContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTaskError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void saveTask(String title, String description);

        void populateTask();
    }
}
```

#### Activity的作用

Activity 在项目中是一个全局控制着，负责创建View以及Presenter，并将两者联系起来

```java
public class TaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "TASK_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.taskdetail_act);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        // Get the requested task id
        String taskId = getIntent().getStringExtra(EXTRA_TASK_ID);

        // Fragment作为View层，在Activity中创建
        TaskDetailFragment taskDetailFragment = (TaskDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (taskDetailFragment == null) {
            taskDetailFragment = TaskDetailFragment.newInstance(taskId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    taskDetailFragment, R.id.contentFrame);
        }

        // Presenter对象
        new TaskDetailPresenter(
                taskId,
                Injection.provideTasksRepository(getApplicationContext()),
                taskDetailFragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }
}
```

在Activity中，创建后的Fragment实例作为Presenter构造函数的参数传入，这样Presenter中就会有View层的实例；在Presenter的构造函数中会将此对象设置给Fragment中的Presenter对象，这样View层就有Presenter实例了，就保证了相互间的交互。

#### Fragment（ViewC层）

实例中将fragment作为view层的实现类，为什么是fragment呢？有两个原因：
* 第一个原因是我们把activity作为一个全局控制类来创建对象，把fragment作为view，这样两者就能各司其职。
* 第二个原因是因为fragment比较灵活，能够方便的处理界面适配的问题。

```java
public class AddEditTaskFragment extends Fragment implements AddEditTaskContract.View {

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    // Presenter 实例
    private AddEditTaskContract.Presenter mPresenter;

    private TextView mTitle;

    private TextView mDescription;

    public static AddEditTaskFragment newInstance() {
        return new AddEditTaskFragment();
    }

    public AddEditTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull AddEditTaskContract.Presenter presenter) {
    	// 通过该方法，view获得了presenter得实例，从而可以调用presenter代码来处理业务逻辑。
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task_done);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveTask(mTitle.getText().toString(), mDescription.getText().toString());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.addtask_frag, container, false);
        mTitle = (TextView) root.findViewById(R.id.add_task_title);
        mDescription = (TextView) root.findViewById(R.id.add_task_description);

        setHasOptionsMenu(true);
        setRetainInstance(true);
        return root;
    }

    @Override
    public void showEmptyTaskError() {
        Snackbar.make(mTitle, getString(R.string.empty_task_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTasksList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setDescription(String description) {
        mDescription.setText(description);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}

```

#### AddEditTaskPresenter（Presenter层）

presenter构造函数中调用了view得setPresenter方法将自身实例传入，start方法中处理了数据加载与展示。如果需要界面做对应的变化，直接调用view层的方法即可，这样view层与presenter层就能够很好的被划分。

```java
public class AddEditTaskPresenter implements AddEditTaskContract.Presenter,
        TasksDataSource.GetTaskCallback {

    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private final AddEditTaskContract.View mAddTaskView;

    @Nullable
    private String mTaskId;

    public AddEditTaskPresenter(@Nullable String taskId, @NonNull TasksDataSource tasksRepository,
            @NonNull AddEditTaskContract.View addTaskView) {
        mTaskId = taskId;
        mTasksRepository = checkNotNull(tasksRepository);
        mAddTaskView = checkNotNull(addTaskView);

        // 设置View层中的Presenter对象
        mAddTaskView.setPresenter(this);
    }

    @Override
    public void start() {
    	// 此方法在View层中的onResume方法中调用
        if (!isNewTask()) {
            populateTask();
        }
    }

    @Override
    public void saveTask(String title, String description) {
        if (isNewTask()) {
            createTask(title, description);
        } else {
            updateTask(title, description);
        }
    }

    @Override
    public void populateTask() {
        if (isNewTask()) {
            throw new RuntimeException("populateTask() was called but task is new.");
        }
        mTasksRepository.getTask(mTaskId, this);
    }

    @Override
    public void onTaskLoaded(Task task) {
        // The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive()) {
            mAddTaskView.setTitle(task.getTitle());
            mAddTaskView.setDescription(task.getDescription());
        }
    }

    @Override
    public void onDataNotAvailable() {
        // The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive()) {
            mAddTaskView.showEmptyTaskError();
        }
    }

    private boolean isNewTask() {
        return mTaskId == null;
    }

    private void createTask(String title, String description) {
        Task newTask = new Task(title, description);
        if (newTask.isEmpty()) {
            mAddTaskView.showEmptyTaskError();
        } else {
            mTasksRepository.saveTask(newTask);
            mAddTaskView.showTasksList();
        }
    }

    private void updateTask(String title, String description) {
        if (isNewTask()) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        mTasksRepository.saveTask(new Task(title, description, mTaskId));
        mAddTaskView.showTasksList(); // After an edit, go back to the list.
    }
}
```

#### data（Model层）

data下的就都是model层了。项目中model层最大的特点是被赋予了数据获取的职责，与我们平常model层只定义实体对象截然不同，实例中，数据的获取、存储、数据状态变化都是model层的任务，presenter会根据需要调用该层的数据处理逻辑并在需要时将回调传入。这样model、presenter、view都只处理各自的任务，此种实现确实是单一职责最好的诠释。
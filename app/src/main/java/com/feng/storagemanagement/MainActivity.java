package com.feng.storagemanagement;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int METHOD_FF = 0;     // 首次适应算法
    private static final int METHOD_NF = 1;     // 循环首次适应算法
    private static final int METHOD_BF = 2;     // 最佳适应算法
    private static final int METHOD_WF = 3;     // 最差适应算法

    private Button mGenerateZoneBtn;
    private Button mRecycleZoneBtn;
    private RecyclerView mZoneRv;
    private Spinner mMethodSp;
    private EditText mTaskSizeEt;
    private Button mAddTaskBtn;
    private RecyclerView mTaskRv;

    private int mMethod = 0;
    private ZoneAdapter mZoneAdapter;
    private List<ZoneData> mZoneDataList = new ArrayList<>();
    private TaskAdapter mTaskAdapter;
    private List<TaskData> mTaskDataList = new ArrayList<>();
    private int mTaskId = 0;    // 作业号
    private int mLastZoneIndex = -1; // NF 算法中上一次找到的空闲分区号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);   //隐藏标题栏
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mGenerateZoneBtn = findViewById(R.id.btn_main_generate_free_zone);
        mGenerateZoneBtn.setOnClickListener(this);
        mRecycleZoneBtn = findViewById(R.id.btn_main_recycle_zone);
        mRecycleZoneBtn.setOnClickListener(this);

        mZoneRv = findViewById(R.id.rv_main_free_zone);
        mZoneRv.setLayoutManager(new LinearLayoutManager(this));

        mMethodSp = findViewById(R.id.sp_main_method);
        mMethodSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mMethod = METHOD_FF;
                        idOrder();
                        showZoneList();
                        break;
                    case 1:
                        mMethod = METHOD_NF;
                        idOrder();
                        showZoneList();
                        break;
                    case 2:
                        mMethod = METHOD_BF;
                        positiveOrder();
                        showZoneList();
                        break;
                    case 3:
                        mMethod = METHOD_WF;
                        negativeOrder();
                        showZoneList();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTaskSizeEt = findViewById(R.id.et_main_task_size);
        mAddTaskBtn = findViewById(R.id.btn_main_add_task);
        mAddTaskBtn.setOnClickListener(this);

        mTaskRv = findViewById(R.id.rv_main_task_distribution);
        mTaskRv.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_main_generate_free_zone:
                // 随机生成空闲分区
                generateZone();
                // 显示空闲分区列表
                showZoneList();
                break;
            case R.id.btn_main_recycle_zone:
                if (!checkRecycle()) {
                    break;
                }
                // 进行回收工作
                recycleZone();
                // 更新列表
                switch (mMethod) {
                    case METHOD_FF:
                        idOrder();
                        break;
                    case METHOD_NF:
                        idOrder();
                        break;
                    case METHOD_BF:
                        positiveOrder();
                        break;
                    case METHOD_WF:
                        negativeOrder();
                        break;
                    default:
                        break;
                }
                showZoneList();
                // 一些清除工作
                mTaskDataList.clear();
                showTaskList();
                mTaskId = 0;
                mLastZoneIndex = -1;
                break;
            case R.id.btn_main_add_task:
                if (!checkAddTask()) {
                    break;
                }
                // 根据分配算法为作业分配合适的空闲分区
                int taskSize = Integer.parseInt(mTaskSizeEt.getText().toString());
                int[] res = new int[2];
                switch (mMethod) {
                    case METHOD_FF:
                        res = doFF(taskSize);
                        break;
                    case METHOD_NF:
                        res = doNF(taskSize);
                        break;
                    case METHOD_BF:
                        res = doBF(taskSize);
                        positiveOrder();
                        break;
                    case METHOD_WF:
                        res = doWF(taskSize);
                        negativeOrder();
                        break;
                    default:
                        break;
                }
                int zoneId = res[0];
                int initialAddress = res[1];
                // 更新作业列表
                if (zoneId == -1) {
                    // 没有找到合适的分区
                    mTaskDataList.add(new TaskData(mTaskId++, taskSize, "无",
                            "无", "失败"));
                } else {
                    mTaskDataList.add(new TaskData(mTaskId++, taskSize, String.valueOf(zoneId),
                            String.valueOf(initialAddress), "成功"));
                }
                showTaskList();
                // 更新分区列表
                showZoneList();
                // 其他操作
                hideSoftKeyboard(MainActivity.this);
                mTaskSizeEt.setText("");
                break;
            default:
                break;
        }
    }

    private void showShortToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 随机生成空闲分区
     */
    private void generateZone() {
        mZoneDataList.clear();
        int n = 10;     // 生成的分区数
        int lastEndAddress = 0;
        int initialAddress;
        int size;
        for (int i = 0; i < n; i++) {
            size = new Random().nextInt(100);
            initialAddress = lastEndAddress + new Random().nextInt(50) + 1; // 加 1 保证各分区块不相连
            mZoneDataList.add(new ZoneData(i, size, String.valueOf(initialAddress)));
            lastEndAddress = initialAddress + size;
        }
    }

    /**
     * 显示空闲分区列表
     */
    private void showZoneList() {
        if (mZoneAdapter == null) {
            mZoneAdapter = new ZoneAdapter(MainActivity.this, mZoneDataList);
            mZoneRv.setAdapter(mZoneAdapter);
        } else {
            mZoneAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 按照分区号排列分区
     */
    private void idOrder() {
        Collections.sort(mZoneDataList, new Comparator<ZoneData>() {
            @Override
            public int compare(ZoneData o1, ZoneData o2) {
                return o1.getId() - o2.getId();
            }
        });
    }

    /**
     * 按照分区大小升序排列分区
     */
    private void positiveOrder() {
        Collections.sort(mZoneDataList, new Comparator<ZoneData>() {
            @Override
            public int compare(ZoneData o1, ZoneData o2) {
                return o1.getSize() - o2.getSize();
            }
        });
    }

    /**
     * 按照分区大小降序排列分区
     */
    private void negativeOrder() {
        Collections.sort(mZoneDataList, new Comparator<ZoneData>() {
            @Override
            public int compare(ZoneData o1, ZoneData o2) {
                return o2.getSize() - o1.getSize();
            }
        });
    }

    private boolean checkAddTask() {
        if (mZoneDataList.isEmpty()) {
            showShortToast("请先随机生成空闲分区");
            return false;
        }
        if (mTaskSizeEt.getText().toString().equals("")) {
            showShortToast("请先输入作业大小");
            return false;
        }
        if (Integer.parseInt(mTaskSizeEt.getText().toString()) == 0) {
            showShortToast("作业大小不能为 0");
            return false;
        }

        return true;
    }

    private boolean checkRecycle() {
        if (mZoneDataList.isEmpty()) {
            showShortToast("请先随机生成空闲分区");
            return false;
        }
        if (mTaskDataList.isEmpty()) {
            showShortToast("请先添加作业");
            return false;
        }

        return true;
    }

    /**
     * 显示作业列表
     */
    private void showTaskList() {
        if (mTaskAdapter == null) {
            mTaskAdapter = new TaskAdapter(MainActivity.this, mTaskDataList);
            mTaskRv.setAdapter(mTaskAdapter);
        } else {
            mTaskAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 执行首次适应算法，返回分配到的分区号和初始地址（按顺序，a[0] 为分区号，a[1] 为初始地址）
     * 分配失败返回 -1
     */
    private int[] doFF(int taskSize) {
        int zoneId = -1;
        int initialAddress = 0;
        for (int i = 0; i < mZoneDataList.size(); i++) {
            if (mZoneDataList.get(i).getSize() == taskSize) {
                // 一整块都给该作业
                zoneId = mZoneDataList.get(i).getId();
                initialAddress = Integer.parseInt(mZoneDataList.get(i).getInitialAddress());
                mZoneDataList.get(i).setSize(0);
                mZoneDataList.get(i).setInitialAddress("无");
                break;
            }
            if (mZoneDataList.get(i).getSize() > taskSize) {
                // 分出一份区域给作业
                zoneId = mZoneDataList.get(i).getId();
                initialAddress = Integer.parseInt(mZoneDataList.get(i).getInitialAddress());
                mZoneDataList.get(i).setSize(mZoneDataList.get(i).getSize() - taskSize);
                mZoneDataList.get(i).setInitialAddress(String.valueOf(
                        Integer.parseInt(mZoneDataList.get(i).getInitialAddress()) + taskSize));
                break;
            }
        }

        int[] res = new int[2];
        res[0] = zoneId;
        res[1] = initialAddress;
        return res;
    }

    /**
     * 执行循环首次适应算法，返回分配到的分区号和初始地址（按顺序，a[0] 为分区号，a[1] 为初始地址）
     * 分配失败返回 -1
     */
    private int[] doNF(int taskSize) {
        int zoneId = -1;
        int initialAddress = 0;
        for (int j = 1; j <= mZoneDataList.size(); j++) {
            int i = (mLastZoneIndex + j) % mZoneDataList.size();
            if (mZoneDataList.get(i).getSize() == taskSize) {
                // 一整块都给该作业
                zoneId = mZoneDataList.get(i).getId();
                initialAddress = Integer.parseInt(mZoneDataList.get(i).getInitialAddress());
                mZoneDataList.get(i).setSize(0);
                mZoneDataList.get(i).setInitialAddress("无");
                mLastZoneIndex = i;
                break;
            }
            if (mZoneDataList.get(i).getSize() > taskSize) {
                // 分出一份区域给作业
                zoneId = mZoneDataList.get(i).getId();
                initialAddress = Integer.parseInt(mZoneDataList.get(i).getInitialAddress());
                mZoneDataList.get(i).setSize(mZoneDataList.get(i).getSize() - taskSize);
                mZoneDataList.get(i).setInitialAddress(String.valueOf(
                        Integer.parseInt(mZoneDataList.get(i).getInitialAddress()) + taskSize));
                mLastZoneIndex = i;
                break;
            }
        }

        int[] res = new int[2];
        res[0] = zoneId;
        res[1] = initialAddress;
        return res;
    }

    /**
     * 执行最佳适应算法，返回分配到的分区号和初始地址（按顺序，a[0] 为分区号，a[1] 为初始地址）
     * 分配失败返回 -1
     */
    private int[] doBF(int taskSize) {
        int zoneId = -1;
        int initialAddress = 0;
        // 从前到后查找合适的
        for (int i = 0; i < mZoneDataList.size(); i++) {
            if (mZoneDataList.get(i).getSize() == taskSize) {
                // 一整块都给该作业
                zoneId = mZoneDataList.get(i).getId();
                initialAddress = Integer.parseInt(mZoneDataList.get(i).getInitialAddress());
                mZoneDataList.get(i).setSize(0);
                mZoneDataList.get(i).setInitialAddress("无");
                break;
            }
            if (mZoneDataList.get(i).getSize() > taskSize) {
                // 分出一份区域给作业
                zoneId = mZoneDataList.get(i).getId();
                initialAddress = Integer.parseInt(mZoneDataList.get(i).getInitialAddress());
                mZoneDataList.get(i).setSize(mZoneDataList.get(i).getSize() - taskSize);
                mZoneDataList.get(i).setInitialAddress(String.valueOf(
                        Integer.parseInt(mZoneDataList.get(i).getInitialAddress()) + taskSize));
                break;
            }
        }

        int[] res = new int[2];
        res[0] = zoneId;
        res[1] = initialAddress;
        return res;
    }

    /**
     * 执行最差适应算法，返回分配到的分区号和初始地址（按顺序，a[0] 为分区号，a[1] 为初始地址）
     * 分配失败返回 -1
     */
    private int[] doWF(int taskSize) {
        int zoneId = -1;
        int initialAddress = 0;
        // 看第一块是否合适
        int i = 0;
        if (mZoneDataList.get(i).getSize() == taskSize) {
            // 一整块都给该作业
            zoneId = mZoneDataList.get(i).getId();
            initialAddress = Integer.parseInt(mZoneDataList.get(i).getInitialAddress());
            mZoneDataList.get(i).setSize(0);
            mZoneDataList.get(i).setInitialAddress("无");
        }
        if (mZoneDataList.get(i).getSize() > taskSize) {
            // 分出一份区域给作业
            zoneId = mZoneDataList.get(i).getId();
            initialAddress = Integer.parseInt(mZoneDataList.get(i).getInitialAddress());
            mZoneDataList.get(i).setSize(mZoneDataList.get(i).getSize() - taskSize);
            mZoneDataList.get(i).setInitialAddress(String.valueOf(
                    Integer.parseInt(mZoneDataList.get(i).getInitialAddress()) + taskSize));
        }

        int[] res = new int[2];
        res[0] = zoneId;
        res[1] = initialAddress;
        return res;
    }

    /**
     * 回收所有资源
     */
    private void recycleZone() {
        for (int i = mTaskDataList.size()-1; i >= 0; i--) {
            TaskData taskData = mTaskDataList.get(i);
            if (taskData.getState().equals("失败")) {
                // 该作业分配失败，不用管它
                continue;
            }
            // 找到对应的分区号
            int zoneId = Integer.parseInt(taskData.getZoneId());
            for (int j = 0; j < mZoneDataList.size(); j++) {
                if (mZoneDataList.get(j).getId() == zoneId) {
                    // 更新该分区的始址和分区大小
                    mZoneDataList.get(j).setSize(mZoneDataList.get(j).getSize() + taskData.getSize());
                    mZoneDataList.get(j).setInitialAddress(taskData.getInitialAddress());
                    break;
                }
            }
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}

package com.feng.storagemanagement;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mGenerateZoneBtn;
    private Button mRecycleZoneBtn;
    private RecyclerView mZoneRv;
    private Spinner mMethodSp;
    private EditText mTaskSizeEt;
    private Button mAddTaskBtn;
    private RecyclerView mTaskRv;

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
                        showShortToast("首次适应算法");
                        break;
                    case 1:
                        showShortToast("循环首次适应算法");
                        break;
                    case 2:
                        showShortToast("最佳适应算法");
                        break;
                    case 3:
                        showShortToast("最差适应算法");
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

    }

    private void showShortToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}

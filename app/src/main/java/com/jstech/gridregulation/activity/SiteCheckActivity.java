package com.jstech.gridregulation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jstech.gridregulation.R;
import com.jstech.gridregulation.adapter.CheckResultAdapter;
import com.jstech.gridregulation.base.BaseActivity;
import com.jstech.gridregulation.base.BaseRecyclerAdapter;
import com.jstech.gridregulation.bean.CheckItemBean;
import com.jstech.gridregulation.utils.SystemUtil;
import com.jstech.gridregulation.widget.MyPopupWindow;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 选完检查项目后开始进行检查
 */
public class SiteCheckActivity extends BaseActivity implements CheckResultAdapter.MethodInterface {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar_subtitle)
    TextView tvNext;

    CheckResultAdapter mResultAdapter;
    ArrayList<CheckItemBean> mItemList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_site_check;
    }

    @Override
    public void initView() {
        initPopupWindow();

        tvNext.setText(R.string.next);
        tvNext.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        mItemList = (ArrayList<CheckItemBean>) bundle.getSerializable("list");
        if (null == mItemList) {
            mItemList = new ArrayList<>();
        }
        mResultAdapter = new CheckResultAdapter(mItemList, this, R.layout.item_site_check, this);
        recyclerView.setAdapter(mResultAdapter);

    }


    /**
     * 检查是否所有的项目都已经有结果
     */
    private boolean isAllChecked() {
        for (CheckItemBean b : mItemList) {
            if (null == b.getResult() || "".equals(b.getResult())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 查看检查方法
     *
     * @param id
     */
    @Override
    public void showMethod(String id) {
        Intent intent = new Intent(this, CheckMethodActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    /**
     * 选择检查结果
     *
     * @param data
     * @param viewHolder
     */
    @Override
    public void selectResult(int result, final CheckItemBean data, final BaseRecyclerAdapter.ViewHolder viewHolder) {
        data.setResult(result + "");
        if (result == 2) {
            //选择不合格
            final TextView tvReason = viewHolder.getView(R.id.tv_unqualified_reason);
            reasonWindow.showAtLocation(getLayoutId(), Gravity.CENTER, 0, 0);
            final String reason = data.getReason();
            if (null != reason && !"".equals(reason)) {
                edtReason.setText(reason);
            } else {
                edtReason.setText("");
            }
            reasonWindow.setPassButtonOnclickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = edtReason.getText().toString();
                    tvReason.setVisibility(View.VISIBLE);
                    if (null != s || !s.equals("")) {
                        tvReason.setText(getResources().getString(R.string.unqualified_reason) + s);
                        data.setReason(s);
                    } else {
                        tvReason.setText(getResources().getString(R.string.unqualified_reason));
                        data.setReason("");
                    }

                    reasonWindow.dismiss();
                }
            });
            reasonWindow.setUnPassButtonOnclickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvReason.setVisibility(View.VISIBLE);
                    data.setReason("");
                    tvReason.setText(getResources().getString(R.string.unqualified_reason));
                    reasonWindow.dismiss();
                }
            });
        }
        if (isAllChecked()) {
            tvNext.setVisibility(View.VISIBLE);
        }

    }

    MyPopupWindow reasonWindow;
    EditText edtReason;

    private void initPopupWindow() {
        reasonWindow = new MyPopupWindow.Builder().setContext(this).
                setContentView(R.layout.layout_unqualified_reason_input).setTitle("请输入原因")
                .setwidth(SystemUtil.getWith(this) * 2 / 3)
                .setheight(SystemUtil.getHeight(this) / 2)
                .setFouse(true)
                .setAnimationStyle(R.style.Animation_CustomPopup)
                .setPass(getString(R.string.confrim))
                .setUnpass(getString(R.string.cancel))
                .setOutSideCancel(false)
                .setIsUnpassVisiable(true)
                .builder();
        edtReason = reasonWindow.getContentFrameLayout().findViewById(R.id.edit);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (null != reasonWindow && reasonWindow.isShowing()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }
}

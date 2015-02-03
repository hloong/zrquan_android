package com.zrquan.mobile.ui.feed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zrquan.mobile.R;
import com.zrquan.mobile.ZrquanApplication;
import com.zrquan.mobile.ui.authentic.UserLoginActivity;
import com.zrquan.mobile.ui.authentic.UserRegisterActivity;
import com.zrquan.mobile.ui.common.CommonFragment;
import com.zrquan.mobile.ui.demo.DemoPopupWindow;
import com.zrquan.mobile.ui.search.SearchActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FeedFragment extends CommonFragment {

    private Context context;
    private View rootView;                          //当前Fragment持有的View实例
    private ArrayList<CommonFragment> fragmentList;
    private int currentIndex;                       //当前页卡编号

    @InjectView(R.id.iv_search)
    ImageView ivSearch;
    @InjectView(R.id.iv_arrange_setting)
    ImageView ivArrangeSetting;
    @InjectView(R.id.tv_discussion)
    TextView tvDiscussion;
    @InjectView(R.id.tv_question)
    TextView tvQuestion;
    @InjectView(R.id.tv_register)
    TextView tvRegister;
    @InjectView(R.id.tv_login)
    TextView tvLogin;
    @InjectView(R.id.vp_feed_content)
    ViewPager vpFeedContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        boolean isLogin = ((ZrquanApplication) getActivity().getApplicationContext()).getAccount().isLogin();

        //Avoid recreating same view when perform tab switching
        //http://stackoverflow.com/questions/10716571/avoid-recreating-same-view-when-perform-tab-switching
        if (rootView == null) {
            context = getActivity().getApplicationContext();
            rootView = inflater.inflate(R.layout.fragment_feed, container, false);
            ButterKnife.inject(this, rootView);
        } else {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        initNavBar(isLogin);
        initViewPager();
        setRetainInstance(true);
        return rootView;
    }

    /*
     * 初始化导航栏
	 */
    private void initNavBar(Boolean isLogin) {
        tvDiscussion.setOnClickListener(new TxListener(0));
        tvQuestion.setOnClickListener(new TxListener(1));
        if (isLogin) {
            tvLogin.setVisibility(View.GONE);
            tvRegister.setVisibility(View.GONE);
            ivSearch.setVisibility(View.VISIBLE);
            ivArrangeSetting.setVisibility(View.VISIBLE);
        } else {
            tvLogin.setVisibility(View.VISIBLE);
            tvRegister.setVisibility(View.VISIBLE);
            ivSearch.setVisibility(View.GONE);
            ivArrangeSetting.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.iv_arrange_setting)
    public void onArrangeSettingClick(View v) {
        View popupContent = View.inflate(context, R.layout.pop_up_window_more_demo, null);
        DemoPopupWindow demoPopupWindow = new DemoPopupWindow(context, popupContent, 150, 110);
        Rect location = locateView(v);
        demoPopupWindow.showAtLocation(v, Gravity.TOP|Gravity.LEFT, location.left, location.bottom);
    }

    @OnClick(R.id.tv_login)
    public void onLoginClick(View v) {
        Intent myIntent = new Intent(getActivity(), UserLoginActivity.class);
        getActivity().startActivity(myIntent);
        getActivity().overridePendingTransition(R.anim.right2left_enter, R.anim.right2left_exit);
    }

    @OnClick(R.id.tv_register)
    public void onRegisterClick(View v) {
        Intent myIntent = new Intent(getActivity(), UserRegisterActivity.class);
        getActivity().startActivity(myIntent);
        getActivity().overridePendingTransition(R.anim.right2left_enter, R.anim.right2left_exit);
    }

    @OnClick(R.id.iv_search)
    public void onSearchClick(View v) {
        Intent myIntent = new Intent(getActivity(), SearchActivity.class);
        getActivity().startActivity(myIntent);
        getActivity().overridePendingTransition(R.anim.left2right_enter, R.anim.left2right_exit);
    }

    public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try
        {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe)
        {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

    private class TxListener implements View.OnClickListener {
        private int index = 0;

        public TxListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            vpFeedContent.setCurrentItem(index);
        }
    }

    /*
     * 初始化ViewPager
     */
    private void initViewPager() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new DiscussionFragment());
        fragmentList.add(new QuestionFragment());

        //给ViewPager设置适配器
        vpFeedContent.setAdapter(new FeedPagerAdapter(getChildFragmentManager(), fragmentList));
        vpFeedContent.setCurrentItem(0);                                           //设置当前显示标签页为第一页
        vpFeedContent.setOnPageChangeListener(new MyOnPageChangeListener());       //页面变化时的监听器
        selectTabDiscussion();
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    selectTabDiscussion();
                    break;
                case 1:
                    selectTabQuestion();
                    break;
                default:
                    break;
            }
            Toast.makeText(getActivity(), "您选择了第" + (position + 1) + "个页卡", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectTabDiscussion() {
        tvDiscussion.setSelected(true);
        tvDiscussion.setBackgroundResource(R.drawable.bg_navigation_text_selected);
        tvQuestion.setSelected(false);
        tvQuestion.setBackgroundResource(Color.TRANSPARENT);
    }

    private void selectTabQuestion() {
        tvDiscussion.setSelected(false);
        tvDiscussion.setBackgroundResource(Color.TRANSPARENT);
        tvQuestion.setSelected(true);
        tvQuestion.setBackgroundResource(R.drawable.bg_navigation_text_selected);
    }
}

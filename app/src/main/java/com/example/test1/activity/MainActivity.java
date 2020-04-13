package com.example.test1.activity;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.example.test1.R;
import com.example.test1.fragment.ChatOperationFragment;
import com.example.test1.fragment.ContactOperateFragment;
import com.example.test1.fragment.FileListFragment;
import com.example.test1.fragment.WeatherAndLocationFragmment;
import com.example.test1.service.NoticeService;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int PERMISSION_CODE_CONTACT = 1;
    ViewPager vpContainer;
    Toolbar toolbar;
    TabLayout tlTabs;
    ServiceConnection serviceConnection;
    NoticeService noticeService;
    long firstPressTime = 0;
    String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE};
    List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "Begin to start!");
        setContentView(R.layout.activity_main);
        vpContainer = findViewById(R.id.vp_container);
        toolbar = findViewById(R.id.tb_main);
        tlTabs = findViewById(R.id.tl_tabs);

        fragments = new ArrayList<>();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragments.add(new ChatOperationFragment());
        fragments.add(new FileListFragment());
        fragments.add(new ContactOperateFragment());
        fragments.add(new WeatherAndLocationFragmment());
        vpContainer.setAdapter(new PagerAdapter(fragmentManager, fragments));
        tlTabs.addTab(tlTabs.newTab());
        tlTabs.addTab(tlTabs.newTab());
        tlTabs.addTab(tlTabs.newTab());
        tlTabs.addTab(tlTabs.newTab());
        tlTabs.setupWithViewPager(vpContainer);
        tlTabs.getTabAt(0).setText("聊天").setIcon(R.drawable.menu_chat);
        tlTabs.getTabAt(1).setText("文件").setIcon(R.drawable.menu_file_manager);
        tlTabs.getTabAt(2).setText("通讯").setIcon(R.drawable.menu_contact);
        tlTabs.getTabAt(3).setText("天气").setIcon(R.drawable.menu_weather);
        toolbar.inflateMenu(R.menu.toolbar_main_menu);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.grayblue));

        GetPermission();
        InitConnection();
        StartPushService();
        BindPushService();
    }

    private void InitConnection() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                NoticeService.SingleBinder binder = (NoticeService.SingleBinder) iBinder;
                noticeService = binder.GetService();
                noticeService.StopCurrentThread();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                noticeService.StartNewThread();
            }
        };
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        FragmentManager fm;
        List<Fragment> fragments;

        public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fm = fm;
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private void StartPushService() {
        Intent serviceIntent = new Intent(this, NoticeService.class);
//        ComponentName componentName=new ComponentName(ConstantAssemble.PACKAGE_NAME,ConstantAssemble.SERVICE_NAME);
//        serviceIntent.setComponent(componentName);
        startService(serviceIntent);
    }

    private void BindPushService() {
        Intent serviceIntent = new Intent(this, NoticeService.class);
        bindService(serviceIntent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    private void StopPushService() {
        Intent serviceIntent = new Intent(this, NoticeService.class);
        stopService(serviceIntent);
    }

    private void GetPermission() {
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            List<String> needGrantedPermissions = new ArrayList<>();
            for (int index = 0; index != permissions.length; index++) {
                if (ContextCompat.checkSelfPermission(this, permissions[index]) != PackageManager.PERMISSION_GRANTED) {
                    needGrantedPermissions.add(permissions[index]);
                }
            }
            if (!needGrantedPermissions.isEmpty()) {
                String[] grantedPermissions = needGrantedPermissions.toArray(new String[needGrantedPermissions.size()]);
                ActivityCompat.requestPermissions(MainActivity.this, grantedPermissions, PERMISSION_CODE_CONTACT);
            }
        }
    }

    @Override
    public void onBackPressed() {
        int position = vpContainer.getCurrentItem();
        if (position == 1) {
            FileListFragment fileListFragments = (FileListFragment) fragments.get(position);
            if (fileListFragments.BackToLastLevel()) {
                DoublePressExit();
            }
        } else {
            DoublePressExit();
        }
    }

    private void DoublePressExit() {
        long secondPressTime = System.currentTimeMillis();
        if (secondPressTime - firstPressTime < 2000) {
            System.exit(0);
        } else {
            firstPressTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();
        }
    }
}

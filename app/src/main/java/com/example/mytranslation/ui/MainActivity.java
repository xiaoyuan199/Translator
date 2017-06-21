package com.example.mytranslation.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.mytranslation.R;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private  NavigationView navigationiew;
    private Toolbar toolbar;

    private TranslateFragment translateFragment;
    private NoteBookFragment noteBookFragment;
    private DailyOneFragment  dailyOneFragment;
    private static final String ACTION_NOTEBOOK = "com.marktony.translator.notebook";
    private static final String ACTION_DAILY_ONE = "com.marktony.translator.dailyone";


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (translateFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "translateFragment", translateFragment);
        }
        if(noteBookFragment.isAdded()){
            getSupportFragmentManager().putFragment(outState,"noteBookFragment",noteBookFragment);
        }
        if(dailyOneFragment.isAdded()){
            getSupportFragmentManager().putFragment(outState,"dailyOneFragment",dailyOneFragment);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        if(savedInstanceState!=null){
            FragmentManager manager = getSupportFragmentManager();
            translateFragment=(TranslateFragment)manager.getFragment(savedInstanceState,"translateFragment");
            noteBookFragment=(NoteBookFragment) manager.getFragment(savedInstanceState,"noteBookFragment");
            dailyOneFragment= (DailyOneFragment) manager.getFragment(savedInstanceState,"dailyOneFragment");
        }else {
            translateFragment=new TranslateFragment();
            noteBookFragment=new NoteBookFragment();
            dailyOneFragment=new DailyOneFragment();
        }

        FragmentManager manager=getSupportFragmentManager();
        manager.beginTransaction()
                //此时得到FragmentTransaction类的实例
                // 使用Fragment时，可以通过用户交互来执行一些动作，比如增加、移除、替换等。
                //所有这些改变构成一个集合，这个集合被叫做一个transaction。
                //可以调用FragmentTransaction中的方法来处理这个transaction，
                // 并且可以将transaction存进由activity管理的back stack中，这样用户就可以进行fragment变化的回退操作。
                //用add(), remove(), replace()方法，把所有需要的变化加进去，然后调用commit()方法，将这些变化应用。
                .add(R.id.content_main,translateFragment,"translateFragment")
                //R.id.container_main是所要添加的容器的id
                .commit();//提交事物

        manager.beginTransaction()
                .add(R.id.content_main,noteBookFragment,"noteBookFragment")
                .commit();

        manager.beginTransaction()
                .add(R.id.content_main,dailyOneFragment,"dailyOneFragment")
                .commit();

        Intent intent=new Intent();
        if(intent.getAction()==ACTION_NOTEBOOK){
            showHideFragment(1);
        }else if(intent.getAction()==ACTION_DAILY_ONE){
            showHideFragment(2);
        }else {
            showHideFragment(0);
        }


    }

    private void initView() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout mDrawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle是滑动菜单的滑动时的监听事件
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationiew= (NavigationView) findViewById(R.id.nav_view);
        navigationiew.setNavigationItemSelectedListener(this);



    }

    //滑动猜单的按钮获取与处理事件
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();

        if(id==R.id.nav_translate){

            showHideFragment(0);

        }else if (id==R.id.nav_notebook){

            showHideFragment(1);

        }else if(id==R.id.nav_daily){
            showHideFragment(2);
        }else if(id==R.id.nav_setting){

          startActivity(new Intent(MainActivity.this,SettingActivity.class));

        }
          //关闭菜单
        DrawerLayout mdrawer= (DrawerLayout) findViewById(R.id.drawer_layout);
        mdrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showHideFragment(@IntRange(from=0,to=2)int position) {

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().hide(translateFragment).commit();
        manager.beginTransaction().hide(noteBookFragment).commit();
        manager.beginTransaction().hide(dailyOneFragment).commit();

        if(position==0){
            manager.beginTransaction().show(translateFragment).commit();
            toolbar.setTitle("简单翻译");
            navigationiew.setCheckedItem(R.id.nav_translate);
        }
        if(position==1){
            manager.beginTransaction().show(noteBookFragment).commit();
            toolbar.setTitle("笔记本");
            navigationiew.setCheckedItem(R.id.nav_notebook);
        }/////////////////////////这里////////////
        if(position==2){
            manager.beginTransaction().show(dailyOneFragment).commit();
            toolbar.setTitle("每日一句");
            navigationiew.setCheckedItem(R.id.nav_daily);
        }


    }
}

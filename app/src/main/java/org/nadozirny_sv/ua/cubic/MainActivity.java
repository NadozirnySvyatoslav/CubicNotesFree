package org.nadozirny_sv.ua.cubic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;



public final class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final String mainFolder="/Notes";

    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private ArrayList<NotesItem> feed;
    private NotesAdapter adapter;
    private AsyncTask<String, Void, Integer> dataloader;
    public ImageButton add_item;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private RelativeLayout mDrawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_list);
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state) && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            if (!(new File(Environment.getExternalStorageDirectory()+mainFolder)).exists()) {
                new File(Environment.getExternalStorageDirectory() + mainFolder).mkdir();
                startActivity(new Intent(this, AboutActivity.class));
            }
            DestDir.get().path=new File(Environment.getExternalStorageDirectory()+mainFolder).getAbsolutePath();
        }else{
            DestDir.get().path=getFilesDir().getAbsolutePath();
        }


        mRecyclerView=(RecyclerView)findViewById(R.id.notes_list);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        progressBar=(ProgressBar)findViewById(R.id.notes_progress);
        add_item= (ImageButton) findViewById(R.id.add_item);
        add_item.setOnClickListener(this);
        progressBar.setVisibility(View.VISIBLE);
        adapter=new NotesAdapter(this);
        progressBar.setVisibility(View.GONE);
        mRecyclerView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                            if (dX<0) {
                                View itemView = viewHolder.itemView;
                                Paint p = new Paint();
                                p.setARGB(255, 255, 0, 0);
                                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), itemView.getRight()-dX,
                                        (float) itemView.getBottom(), p);
                                Bitmap b = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_delete);
                                int posY=viewHolder.itemView.getTop()+(viewHolder.itemView.getHeight()/2-b.getHeight()/2);
                                int posX=viewHolder.itemView.getWidth()-b.getWidth();
                                c.drawBitmap(b, posX, posY, p);
                                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                            }
                        }
                    }
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {

                        switch(i){
                            case ItemTouchHelper.LEFT:
                                    adapter.deleteItem(viewHolder.getAdapterPosition());
                                    adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                return;

                        }

                    }
                };
        new ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(mRecyclerView);
        dataloader=new AsyncLoadtask().execute("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerView= (RelativeLayout) findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                new Toolbar(this), R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };


        }
    @Override
    public void onConfigurationChanged(Configuration cfg){
        super.onConfigurationChanged(cfg);
        if (Configuration.ORIENTATION_LANDSCAPE==cfg.orientation){
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }else{
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                loadByText(query);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_about:
                    startActivity(new Intent(this,AboutActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onClick(View v) {
        int[] color_id={R.id.white,R.id.orange_300,R.id.brown_300,R.id.yellow_300,
                R.id.red_300,R.id.blue_200,R.id.green_300, R.id.pink_200 };
        int[] colors={R.color.white,R.color.orange_300,R.color.brown_300,R.color.yellow_300,
                R.color.red_300,R.color.blue_400,R.color.green_300,R.color.pink_200};
        for(int i=0;i<color_id.length;i++) {
            if (v.getId()==color_id[i]){
                adapter.setItemsColor(getResources().getColor(colors[i]));
                mDrawerLayout.closeDrawer(mDrawerView);
                return;
            }
        }

        switch(v.getId()) {
            case R.id.add_item:
                    adapter.insertItem("Note_" + new SimpleDateFormat("yyMMdd").format(new Date()));
                    mRecyclerView.scrollToPosition(0);
                    adapter.editItem(0);
                    return;
        }
    }

    public void loadByText(String query) {
        if (dataloader!=null)
            if (dataloader.getStatus() == AsyncLoadtask.Status.RUNNING)
                dataloader.cancel(true);
        dataloader=new AsyncLoadtask().execute(query);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==1)
        {
            adapter.loaddata("");
            adapter.notifyDataSetChanged();
        }
    }
    public  class AsyncLoadtask extends AsyncTask<String,Void,Integer>{
        @Override
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result=0;
            adapter.loaddata(params[0]);
            return result;
        }

        @Override
        protected void onPostExecute(Integer result){
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    }
}

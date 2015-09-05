package org.nadozirny_sv.ua.cubic;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class NoteActivity extends AppCompatActivity {

    private NotesItem ni;
    private EditText title;
    private EditText desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Bundle extras = getIntent().getExtras();
        String filename = extras.getString("filename");

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        // add the custom view to the action bar
        actionBar.setCustomView(R.layout.edit_layout);
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM |
                ActionBar.DISPLAY_SHOW_HOME);
        title = (EditText) actionBar.getCustomView().findViewById(
                R.id.title);
        desc= (EditText) findViewById(R.id.desc);
        ni=new NotesItem(new File(filename).getName(),getApplicationContext());
        title.setText(ni.getTitle());
        desc.setText(ni.getDesc());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_activity_actions, menu);
        MenuItem saveItem = menu.findItem(R.id.action_save);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_save:
                if (title.getText().toString().length()==0) {
                    Toast.makeText(this,getResources().getString(R.string.check_filename),Toast.LENGTH_SHORT).show();
                    return false;
                }
                Intent intent=new Intent();
                if (!ni.getTitle().matches(title.getText().toString())){
                    if (new File(DestDir.get().path + '/' +title.getText()).exists()){
                        Toast.makeText(this, Html.fromHtml("<font color='red'><b>"+title.getText()+"</b></font> - "
                                +getResources().getString(R.string.check_filename_exists)),Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                ni.setTitle(title.getText().toString());
                ni.setDesc(desc.getText().toString());
                ni.save();
                setResult(1, intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    }

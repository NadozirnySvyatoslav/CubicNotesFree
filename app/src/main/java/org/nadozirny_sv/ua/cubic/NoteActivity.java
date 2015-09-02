package org.nadozirny_sv.ua.cubic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
        title= (EditText) findViewById(R.id.title);
        desc= (EditText) findViewById(R.id.desc);
        ni=new NotesItem(filename,getApplicationContext());
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

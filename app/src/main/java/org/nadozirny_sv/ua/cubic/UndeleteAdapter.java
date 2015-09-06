package org.nadozirny_sv.ua.cubic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Святослав on 06.09.2015.
 */
public class UndeleteAdapter extends ArrayAdapter<HashMap<String,Object>> {
    public RecoverNoteInterface recoverer;
    public UndeleteAdapter(Context context, int res_id) {
        super(context,res_id);
    }
    public UndeleteAdapter(Context context, int res_id,List<HashMap<String,Object>> items) {
        super(context,res_id,items);
    }
    @Override
    public View getView(int pos,View v,ViewGroup vg) {
        if (v == null) {
            LayoutInflater li=LayoutInflater.from(getContext());
            v=li.inflate(R.layout.deleted,null);
        }

        HashMap<String,Object> item=getItem(pos);
        if (item!=null){
            TextView name= (TextView) v.findViewById(R.id.name);
            ImageButton btn= (ImageButton) v.findViewById(R.id.delButton);
            name.setText((String)item.get("Name"));
            btn.setTag(pos);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos= (int) view.getTag();
                    HashMap<String,Object> item=getItem(pos);
                    recoverer.recoverNote((String) item.get("Name"), pos);
                }
            });
        }
        return v;
    }
}

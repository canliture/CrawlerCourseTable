package com.shengid.liture.coursetable.Helper;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shengid.liture.coursetable.Activity.MainActivity;
import com.shengid.liture.coursetable.R;

import java.util.List;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.ViewHolder> {

    private List<String> mWeekList;
    private static int x = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView weekText;

        public ViewHolder(View view) {
            super(view);
            this.weekText = view.findViewById(R.id.week);
            this.weekText.setTextColor(view.getContext().getResources().getColor(R.color.colorBlack));
            Log.e("ViewHolder Constructor", this.weekText.getText().toString() );
        }
    }

    public WeekAdapter(List<String> mWeekList){
        this.mWeekList = mWeekList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from( parent.getContext() ).inflate(R.layout.week_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        holder.weekText.setOnClickListener( (v)->{
            int pos = holder.getAdapterPosition();
            ((MainActivity)view.getContext()).refreshTable(pos+1);
            TextView showWeek = ((MainActivity)v.getContext()).findViewById(R.id.show_week);

            String showStr = null;
            int color = 0;
            if(pos+1 == DateUtil.weekThFromStart){
                showStr = "第" + (pos+1) + "周";
                color = showWeek.getContext().getResources().getColor(R.color.colorAccent);
            } else {
                showStr = "第" + (pos+1) + "周(返回本周)";
                color = showWeek.getContext().getResources().getColor(R.color.colorBlack);
            }
            showWeek.setText( showStr );
            showWeek.setTextColor( color );
        });

        Log.e("onCreateViewHolder", holder.weekText.getText().toString() );

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        String text = mWeekList.get(pos);
        holder.weekText.setText(text);

        Log.e("onBindViewHolder,pos", ""+pos );

        if(pos == (DateUtil.weekThFromStart-1) ) {
            holder.weekText.setTextColor(holder.weekText.getContext().getResources().getColor(R.color.colorAccent));
        } else {
            holder.weekText.setTextColor(holder.weekText.getContext().getResources().getColor(R.color.colorBlack));
        }
    }

    @Override
    public int getItemCount() {
        return mWeekList.size();
    }

}


package com.shengid.liture.coursetable.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shengid.liture.coursetable.Entity.Course;
import com.shengid.liture.coursetable.Entity.CourseInfoEntity;
import com.shengid.liture.coursetable.Entity.CourseTable;
import com.shengid.liture.coursetable.Helper.DateUtil;
import com.shengid.liture.coursetable.Helper.WeekAdapter;
import com.shengid.liture.coursetable.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CourseTable courseTable = new CourseTable();

    private LinearLayout showView;
    private RecyclerView weekChoose;
    private LinearLayout monthAndWeek;
    private TextView showWeek;

    private List<String> listWeek = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)  actionBar.hide();

        //get the parsed HTML body
        Intent intent = getIntent();
        String responseBody = intent.getStringExtra("responseBody");

        //get the components
        showView = findViewById(R.id.show_data);

        weekChoose = findViewById(R.id.week_choose);
        monthAndWeek = findViewById(R.id.month_and_week);
        showWeek = findViewById(R.id.show_week);

        showWeek.setText("第" + DateUtil.weekThFromStart + "周");
        showWeek.setTextColor( getBaseContext().getResources().getColor(R.color.colorAccent) );
        showWeek.setOnClickListener( (view)->{
            TextView tv = (TextView)view;
            String showStr = tv.getText().toString();
            if( showStr.charAt( showStr.length()-1 ) == ')' ){
                tv.setText( "第" + DateUtil.weekThFromStart + "周" );
                tv.setTextColor( showWeek.getContext().getResources().getColor(R.color.colorAccent) );
                refreshTable( (int)DateUtil.weekThFromStart );
            }
            weekChoose.scrollToPosition( (int)DateUtil.weekThFromStart - 1 );
        } );

        //initialize week data
        for(int i = 1; i <= 18 ; i++)  listWeek.add( "第" + i + "周" );

        //set recycler layout organization
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation( LinearLayout.HORIZONTAL );
        weekChoose.setLayoutManager( layoutManager );
        WeekAdapter adapter = new WeekAdapter(listWeek);
        weekChoose.setAdapter(adapter);
        weekChoose.scrollToPosition((int) DateUtil.weekThFromStart - 1);

        //handle the response the data of html body
        parseResponse(responseBody);
        refreshTable( (int)DateUtil.weekThFromStart );    //DateUtil.showData(this);
    }

    private LinearLayout getTr(){
        LinearLayout row = new LinearLayout(this);
        row.setOrientation( LinearLayout.HORIZONTAL );
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
        row.setLayoutParams( params );

        return row;
    }

    private TextView getTd(int height, float weight, int marginL, int MarginU, int marginR, int marginD){
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(0, height==0?ViewGroup.LayoutParams.WRAP_CONTENT :
                                                                                          getResources().getDisplayMetrics().widthPixels / 3
                                                                     );
        p2.weight = weight;
        p2.setMargins(15, 15, 15, 15);
        textView.setLayoutParams(p2);

        return textView;
    }

    public void refreshTable(int weekTh){
        monthAndWeek.removeAllViews();
        showView.removeAllViews();

        //set data and week infomation into layout
        for(int i = 0 ; i <= 7 ; i++) {
            TextView td = td = null;
            String[] yearMonthDay = DateUtil.firstDayOfSomeWeek( weekTh  ).split(",");
            if(i == 0){
                td = getTd(0, 0.5f, 0, 15, 0, 0);
                td.setTextColor( td.getContext().getResources().getColor( R.color.colorBlack ) );
                td.setText(yearMonthDay[1] + "\n月" );
            } else {
                td = getTd(0, 1, 15, 15, 15, 15);

                Calendar carlendar = Calendar.getInstance();
                carlendar.set( Integer.valueOf( yearMonthDay[0]), Integer.valueOf(yearMonthDay[1])-1, Integer.valueOf(yearMonthDay[2] ) );

                int daysOfMonth = carlendar.getActualMaximum( Calendar.DAY_OF_MONTH );
                int dayThInt = Integer.valueOf(yearMonthDay[2])+(i-1);
                String dayThStr = dayThInt + "日";

                if( dayThInt > daysOfMonth ){
                    if( ( dayThInt - daysOfMonth ) == 1){
                        carlendar.add( Calendar.MONTH, 1 );
                        dayThStr = ( carlendar.get( Calendar.MONTH )+1 ) + "月";
                    } else{
                        dayThStr = ( dayThInt - carlendar.getActualMaximum( Calendar.DATE ) ) + "日";
                    }
                }

                td.setText("星期" + (i == 7 ? "日" : i) + "\n" + dayThStr );
                if (( ( DateUtil.dayOfWeekNow == 0 && i == 7) || DateUtil.dayOfWeekNow == i ) &&  ( weekTh == DateUtil.weekThFromStart )  )
                    td.setTextColor(this.getBaseContext().getResources().getColor(R.color.colorAccent));
                else
                    td.setTextColor(this.getBaseContext().getResources().getColor(R.color.colorBlack));
            }
            monthAndWeek.setBackgroundColor( td.getContext().getResources().getColor( R.color.colorPrimaryDark )  );
            monthAndWeek.addView(td);
        }

        for(int i = 0 ; i < 5 ; i ++) {
            LinearLayout tr = getTr();
            ArrayList<CourseInfoEntity> trData = courseTable.getTable().get(i);

            TextView tdFirst = getTd(1, 0.5f, 0, 15, 0, 0);
            tdFirst.setText( "0" + (i*2+1) + (i==4?10:("\n0" + (i*2+2) ) ) );
            tr.addView(tdFirst);

            for(int j = 0 ; j < 7 ; j++) {
                TextView textView = getTd(1, 1, 15, 15,15, 15);

                CourseInfoEntity tdData = trData.get(j);
                List<Course> list = tdData.getCourse();

                if(list.size() == 0)  {/*System.out.print("NULL");*/ }          //table entity is empty
                for(Course c : list)   {
                    String weekStr = c.getWeek();
                    if( weekIsInWeekStr(weekTh, weekStr) ){
                        textView.setText(c.getCourseName() + " " + /*c.getTeacherName() + " " + c.getWeek() + " " +*/ c.getPlace());
                    }
                }
                tr.addView(textView);
            }
            showView.addView(tr);
        }
    }

    private boolean weekIsInWeekStr(int weekTh, String weekStr){           //1-5,7-8周   |  1-12周  |   2周
        int index = 0;
        while( index < weekStr.length() ){
            int l = 0, r = 0;
            while( Character.isDigit( weekStr.charAt(index) ) ) l = l*10 + (weekStr.charAt(index++)-'0');         //get left limit
            if(weekStr.charAt(index) == '-' ){
                index++;                         //here! notice to add "index++;"
                while( Character.isDigit( weekStr.charAt(index) ) ) r = r*10 + (weekStr.charAt(index++)-'0');     //get right limit
                if( l <= weekTh &&  weekTh <= r) return true;
            } else if( weekStr.charAt(index) == ',' || weekStr.charAt(index) == '周' ){
                if(weekTh == l)  return true;
            }
            index++;
        }
        return false;
    }

    private void parseResponse(String responseBody){

        Document doc = Jsoup.parse( responseBody );
        for(int courseNo = 1 ; courseNo <= 5 ; courseNo++) {

            ArrayList<CourseInfoEntity> infoList = new ArrayList<CourseInfoEntity>();

            for(int weekNo = 1; weekNo <= 7 ; weekNo++) {
                String text = doc.getElementById( (""+courseNo) + ("-" + weekNo) + ("-" + 2) )
                        .text();
                StringBuilder textInfo = new StringBuilder(text);

                infoList.add( new CourseInfoEntity(text) );
            }
            courseTable.add(infoList);
        }
    }

}

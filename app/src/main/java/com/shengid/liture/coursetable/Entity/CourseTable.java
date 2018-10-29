package com.shengid.liture.coursetable.Entity;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CourseTable {

	 //course 0102[0] 0304[1] 0506[3] 0708[4] 0910[5]
	private static ArrayList< ArrayList<CourseInfoEntity> > table = new ArrayList<>(5);
	
	public CourseTable(){}

	public ArrayList< ArrayList<CourseInfoEntity> > getTable(){  return this.table; }

	public void add(ArrayList<CourseInfoEntity> infoList) {
		table.add(infoList);
	}
	
	public void show() {
		for(int i = 0 ; i < 5 ; i ++) {

			System.out.println( "" + i);
			
			ArrayList<CourseInfoEntity> tr = table.get(i);
			
			for(int j = 0 ; j < 7 ; j++) {

				CourseInfoEntity td = tr.get(j);
				List<Course> list = td.getCourse();

				System.out.print(" ||-- ");
				
				if(list.size() == 0)  System.out.print("NULL");           //table entity is empty 
				
				for(Course c : list) {
					System.out.print(c.getCourseName() + " " + c.getTeacherName() + " " + c.getWeek() + " " + c.getPlace() );
				}
				System.out.print(" --|| ");
			}
			System.out.print("\n");
		}
	}
}

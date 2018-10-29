package com.shengid.liture.coursetable.Entity;
import java.util.ArrayList;
import java.util.List;


//a <td> entity string, we split into several courses infomation;
public class CourseInfoEntity {                       
	
	private List<Course> courseList = new ArrayList<>();
	
	
	public CourseInfoEntity(String info){              //形势与政策 王献志 2周 第五教学楼2-4 智能终端系统及应用开发 廖祝华 5-8周 逸夫楼418
		System.out.println(info);
		addListInfo(info);
	}
	
	public void add(String info) {
		addListInfo(info);
	}
	
	public List<Course> getCourse() {
		return this.courseList;
	}

	private void addListInfo(String info) {                       //形势与政策 王献志 2周 第五教学楼2-4 智能终端系统及应用开发 廖祝华 5-8周 逸夫楼418
																  //IT行业资格认证考试辅导A 廖璟 4周 第五教学楼2-1 IT行业资格认证考试辅导A 廖璟 7周 第五教学楼2-1
		int index = 0;
		
		while( index < info.length() ) {                          //scan

			int segNum = 1;
			StringBuilder infoSeg = new StringBuilder();
			
			Course course = new Course();
			
			while(segNum <= 4 && index <= info.length() ) {

				char ch = index==info.length() ? ' ' : info.charAt(index);

				index++;

				if( !Character.isWhitespace(ch) )  {   infoSeg.append(ch);  }
				else {
					String strSeg = infoSeg.toString();
					switch(segNum++) {
						case 1:
							//System.out.println("Course name: " + infoSeg);
							course.setCourseName( strSeg );
							break;
						case 2:
							//System.out.println("Teacher name: " + infoSeg);
							course.setTeacherName( strSeg );
							break;
						case 3:
							//System.out.println("Week Info: " + infoSeg);
							course.setWeek( strSeg );
							break;
						case 4:
							//System.out.println("Place Info: " + infoSeg);
							course.setPlace(strSeg);
							break;
						case 5:
							System.out.println("??error??");
					}
					infoSeg = new StringBuilder();
				}
			}
			//scan one courseInfo
			courseList.add(course);
		}
	}
}

package com.shengid.liture.coursetable.Entity;

public class Course {
	private String courseName;
	private String teacherName;                 // name(,name)*
	private String week;                        // number-number周(,number-number周)*    or  number周
	private String place;						
	
	public Course(){ 
		this.courseName = "";
		this.teacherName = "";
		this.week = "";
		this.place = "";
	}

	public Course(String courseName, String teacherName, String week, String place){
		this.courseName = courseName;
		this.teacherName = teacherName;
		this.week = week;
		this.place = place;
	}
	
	public String getCourseName() { return courseName; }
	public void setCourseName(String courseName) { this.courseName = courseName; }
	
	public String getTeacherName() { return teacherName; }
	public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

	public String getWeek() { return week; }
	public void setWeek(String week) { this.week = week; }
	
	public String getPlace() { return place; }
	public void setPlace(String place) {this.place = place; }
}

package org.itschoolhillel.dnepropetrovsk.pojo;

import org.itschoolhillel.dnepropetrovsk.entity.Lecture;
import org.itschoolhillel.dnepropetrovsk.entity.TimeTable;

import java.util.*;

/**
 * Created by stephenvolf on 14/12/16.
 */

public class TimeTablePOJO implements TimeTable {

    public List<Lecture> lectures;

    public TimeTablePOJO(List<Lecture> lectures) {
        this.lectures = lectures;
    }

    @Override
    public List<Lecture> allLectures() {
        return this.lectures;
    }

    @Override
    public List<Lecture> lecturesFrom(Date date) {
        List<Lecture> lecturesFromDate = new ArrayList<>();
        for (Lecture lecture : this.lectures) {
            if (lecture.startTime().after(date)) {
                lecturesFromDate.add(lecture);
            }
        }
        return lecturesFromDate;
    }

    @Override
    public List<Lecture> lecturesTo(Date date) {
        List<Lecture> lecturesToDate = new ArrayList<>();
        for (Lecture lecture : this.lectures) {
            if (lecture.startTime().before(date)) {
                lecturesToDate.add(lecture);
            }
        }
        return lecturesToDate;
    }

    @Override
    public List<Lecture> lecturesForNextWeek() {
        List<Lecture> lecturesForNextWeek = new ArrayList<>();
        Calendar present = new GregorianCalendar();
        Calendar inTheWeek = new GregorianCalendar();
        inTheWeek.add(Calendar.DAY_OF_WEEK, 7);
        for (Lecture lecture: this.lectures) {
            if (lecture.startTime().after(present.getTime()) && lecture.endTime().before(inTheWeek.getTime())) {
                lecturesForNextWeek.add(lecture);
            }
        }
        return lecturesForNextWeek;
    }
}



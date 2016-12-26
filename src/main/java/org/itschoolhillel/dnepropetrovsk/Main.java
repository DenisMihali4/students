package org.itschoolhillel.dnepropetrovsk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.itschoolhillel.dnepropetrovsk.entity.Course;
import org.itschoolhillel.dnepropetrovsk.entity.Lecture;
import org.itschoolhillel.dnepropetrovsk.entity.LectureRoom;
import org.itschoolhillel.dnepropetrovsk.pojo.CoursePOJO;
import org.itschoolhillel.dnepropetrovsk.pojo.LecturePOJO;
import org.itschoolhillel.dnepropetrovsk.pojo.TimeTablePOJO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Main {

    private final static Logger LL = LoggerFactory.getLogger(Main.class);
    private final static String COURSES_PATH_KEY = "courses.path";
    private final static String SUBSCRIPTIONS_PATH_KEY = "subscriptions.path";
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
                  LL.error("Please, enter ID of student");
                return;
             }

            System.out.println(new File("/app.properties").getAbsolutePath());

            Properties properties = new Properties();
            properties.load(new FileInputStream(new File("/app.properties")));
            String coursesName= "subscription";
            String coursesPath = properties.getProperty(COURSES_PATH_KEY,"src/main/resources/courses");
            Path coursesDirectoryPath = Paths.get(coursesPath,coursesName +".txt");

            Map<Integer, List<String>> subscriptions = readSubscriptions();
            if (!subscriptions.containsKey(args[0])) {
                LL.error("There is no student with the same ID");
                return;
            }
            List<String> courseNames = subscriptions.get(args[0]);


            List<Path> courseFiles = new ArrayList<>();
            for (String name : courseNames) {
                courseFiles.add(Paths.get(String.valueOf(coursesDirectoryPath), name + ".txt"));
            }

            //Делаю все в цикле по одной переменной для взаимосвязи между coursePathes и courseNames
            for (int i = 0; i < courseFiles.size(); i++) {
                String json = null;

                try {
                    json = new String(Files.readAllBytes(courseFiles.get(i)));
                } catch (IOException e) {
                    LL.error("Could not read file", e);
                    return;
                }

                List<Lecture> lectureList = null;

                try {
                    ObjectMapper mapper = new ObjectMapper();
                    DateFormat df = new SimpleDateFormat("dd.MM.yyyy-HH:mm");
                    mapper.setDateFormat(df);
                    Lecture[] lectures = mapper.readValue(json, LecturePOJO[].class);
                    lectureList = Arrays.asList(lectures);
                } catch (IOException e) {
                    LL.error("Could not deserialize json", e);
                    return;
                }

                Course course = new CoursePOJO(courseNames.get(i), new TimeTablePOJO(lectureList));
                printForNextWeek(course);
            }

        }

    private static void print(Course course) {
        System.out.println("Course: " + course.title());
        for (Lecture lecture : course.timeTable().allLectures()) {
            System.out.println("Lecture: " + lecture.title());
            System.out.println("start: " + lecture.startTime());
            System.out.println("end: " + lecture.endTime());
            System.out.println("description: " + lecture.description());
            LectureRoom room = lecture.lectureRoom();
            System.out.println("room: " + room.floor() + " floor, " + room.number() + ", " + room.description());
            System.out.println();
        }
        System.out.println("********************************");
    }

    private static void printToDate(Course course, Date date) {
        System.out.println("Course: " + course.title());
        for (Lecture lecture : course.timeTable().lecturesTo(date)) {
            System.out.println("Lecture: " + lecture.title());
            System.out.println("start: " + lecture.startTime());
            System.out.println("end: " + lecture.endTime());
            System.out.println("description: " + lecture.description());
            LectureRoom room = lecture.lectureRoom();
            System.out.println("room: " + room.floor() + " floor, " + room.number() + ", " + room.description());
            System.out.println();
        }
    }

    private static void printForNextWeek(Course course) {
        System.out.println("Course: " + course.title());
        for (Lecture lecture : course.timeTable().lecturesForNextWeek()) {
            System.out.println("Lecture: " + lecture.title());
            System.out.println("start: " + lecture.startTime());
            System.out.println("end: " + lecture.endTime());
            System.out.println("description: " + lecture.description());
            LectureRoom room = lecture.lectureRoom();
            System.out.println("room: " + room.floor() + " floor, " + room.number() + ", " + room.description());
            System.out.println();
        }
    }


    private static List<String> readCourses(String path) {
        List<String> filenames = new ArrayList<>();
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file : files) {
            filenames.add(file.getName().split(".t")[0]);
        }
        return filenames;
    }

    private static Map<Integer, List<String>> readSubscriptions() {
        Map<Integer, List<String>> result = new HashMap<>();
        Path subs = Paths.get("src/main/resources/subscriptions.txt");

        byte[] json;
        try {
            json = Files.readAllBytes(subs);
        } catch (IOException e) {
            LL.error("Failed to open file", e);
            return result;
        }

        try {
            result = new ObjectMapper().readValue(json, Map.class);
        } catch (IOException e) {
            LL.error("Failed to read file", e);
        }
        return result;
    }
}

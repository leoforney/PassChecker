package tk.leoforney.passchecker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SemesterUtil {

    private static SemesterUtil instance;

    private final int FIRSTSTART = 715; // 07/15
    private final int FIRSTEND = 1229; // 12/29
    private final int SECONDSTART = 102; // 01/02
    private final int SECONDEND = 610; // 06/10

    private SimpleDateFormat compareFormat, fullFormat;

    public SimpleDateFormat getCompareFormat() {
        return compareFormat;
    }

    public void setCompareFormat(SimpleDateFormat compareFormat) {
        this.compareFormat = compareFormat;
    }

    public SimpleDateFormat getFullFormat() {
        return fullFormat;
    }

    public void setFullFormat(SimpleDateFormat fullFormat) {
        this.fullFormat = fullFormat;
    }

    public static SemesterUtil getInstance() {
        if (instance == null) {
            instance = new SemesterUtil();
        }
        return instance;
    }

    private SemesterUtil() {
        compareFormat = new SimpleDateFormat("MMdd");
        fullFormat = new SimpleDateFormat("MMddyyyy");
    }

    public Semester getCurrentSemester() {
        return getSemester(new Date());
    }

    public Semester getSemester(String date) {
        return getSemester(date, fullFormat);
    }

    public Semester getSemester(String date, DateFormat format) {
        try {
            return getSemester(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return Semester.UNKNOWN;
        }
    }

    public Semester getSemester(Date date) {
        int compareDateInt = Integer.valueOf(compareFormat.format(date));
        if (FIRSTSTART <= compareDateInt && compareDateInt <= FIRSTEND) {
            return Semester.FIRST;
        } else if (SECONDSTART <= compareDateInt && compareDateInt <= SECONDEND) {
            return Semester.SECOND;
        } else {
            return Semester.UNKNOWN;
        }
    }

    public enum Semester {
        FIRST,
        SECOND,
        UNKNOWN
    }

}
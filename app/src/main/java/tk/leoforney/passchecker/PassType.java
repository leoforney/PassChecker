package tk.leoforney.passchecker;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PassType {

    private static final Logger logger = Logger.getLogger(PassType.class.getName());

    public List<String> getDayPasses() {
        if (dayPasses == null) {
            dayPasses = new ArrayList<>();
        }
        return dayPasses;
    }

    public void setDayPasses(List<String> dayPasses) {
        this.dayPasses = dayPasses;
    }

    public void addDayPass(Date date) {
        addDayPass(SemesterUtil.getInstance().getFullFormat().format(date));
    }

    public void addDayPass(LocalDate date) {
        // UNIMPLEMENTED
    }

    public void deleteDayPass(String date) {
        if (type != null && !type.equals(Type.FULLYEAR)) {
            String existingDate = null;
            for (String iteratedDays: getDayPasses()) {
                if (iteratedDays.contains(date)) {
                    existingDate = iteratedDays;
                }
            }
            if (existingDate != null) {
                dayPasses.remove(existingDate);
            }
        }
    }

    public void addDayPass(String day) {
        if (dayPasses == null) {
            dayPasses = new ArrayList<>();
        }
        if (type != null && !type.equals(Type.FULLYEAR)) {
            boolean exists = false;
            for (String pass: dayPasses) {
                if (pass.equals(day)) {
                    exists = true;
                }
                logger.log(Level.INFO, day + " - " + (exists ? "Exists" : "Does not exist"));
            }
            if (!exists) {
                dayPasses.add(day);
            }
        }
    }

    // TODO: Implement 5 day pass limit per semester

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private List<String> dayPasses;
    private Type type;

    public boolean isPassValid() {
        if (type.equals(Type.FULLYEAR)) {
            return true;
        }

        SemesterUtil util = SemesterUtil.getInstance();
        SemesterUtil.Semester currentSemester = util.getCurrentSemester();

        if (type.equals(Type.FIRSTSEMESTER)) {
            if (currentSemester.equals(SemesterUtil.Semester.FIRST)) {
                return true;
            }
        }

        if (type.equals(Type.SECONDSEMESTER)) {
            if (currentSemester.equals(SemesterUtil.Semester.SECOND)) {
                return true;
            }
        }

        // Check for current dayPass
        Date currentDate = new Date();
        for (String day: dayPasses) {
            if (util.getFullFormat().format(currentDate).equals(day)) {
                return true;
            }
        }

        return false;
    }

    public enum Type {
        FULLYEAR,
        FIRSTSEMESTER,
        SECONDSEMESTER,
        NONE
    }

}

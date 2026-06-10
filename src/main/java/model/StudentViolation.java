package model;

import com.jfinal.plugin.activerecord.Model;

public class StudentViolation extends Model<StudentViolation> {
    public static final StudentViolation dao = new StudentViolation().dao();
}

package model;

import com.jfinal.plugin.activerecord.Model;

public class College extends Model<College> {
    public static final College dao = new College().dao();
}

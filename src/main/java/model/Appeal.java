package model;

import com.jfinal.plugin.activerecord.Model;

public class Appeal extends Model<Appeal> {
    public static final Appeal dao = new Appeal().dao();
}

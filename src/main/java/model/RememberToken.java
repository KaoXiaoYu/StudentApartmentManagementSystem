package model;

import com.jfinal.plugin.activerecord.Model;

public class RememberToken extends Model<RememberToken> {
    public static final RememberToken dao = new RememberToken().dao();
}

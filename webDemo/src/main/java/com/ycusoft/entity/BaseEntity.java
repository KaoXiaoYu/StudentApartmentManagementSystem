package com.ycusoft.entity;

import java.io.Serializable;
import java.util.Date;

public class BaseEntity implements Serializable {
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;

    // getter/setter
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
}
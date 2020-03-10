package com.ly.sample.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;

/**
 * Description: ItemEntity对应的数据库表的实体
 * Company    : 重庆市了赢科技有限公司 http://www.liaoin.com/
 * Author     : 李大发
 * Date       : 2020/2/18 on 09:57
 *
 * @version 1.0
 */
@Entity         //这个实体类会在数据库中生成对应的表
public class ItemEntity {

    @Transient//不映射到数据库
    public static final int SEX_GIRL = 0;//女
    @Transient
    public static final int SEX_BOY = 1;//男
    @Transient
    public static final int SEX_UNKNOWN = 2;//未知
    @Transient
    public static final String[] sexs = {"Girl女", "Boy男", "Unknown未知"};

    @Id(autoincrement = true)
    private Long   id;      //表id(从0开始, 自增长. 如果自增长=true, 必须是Long)
    private String name;

    @NotNull                //该字段不可以为空
    @Unique                 //该字段唯一
    private String idCard;  //身份证

    private Date time;      //添加时间

    private int sex = SEX_UNKNOWN;//性别, 默认未知

    /**
     * 由于我们的这个表的id是自增长&自动生成的, 所以id必须传null.
     * 为了避免每次都手动传null, 所以增加了这个构造方法.
     * 如果有其它参数是固定的且懒得写, 也可以这样处理(比如: userId, ...)
     */
    public ItemEntity(String name, @NotNull String idCard, Date time, int sex) {
        this(null, name, idCard, time, sex);
    }

    //@Generated: 由'Build -> Make Project'生成
    @Generated(hash = 884245130)
    public ItemEntity(Long id, String name, @NotNull String idCard, Date time, int sex) {
        this.id = id;
        this.name = name;
        this.idCard = idCard;
        this.time = time;
        this.sex = sex;
    }

    //@Generated: 由'Build -> Make Project'生成
    @Generated(hash = 365170573)
    public ItemEntity() {
    }

    //get/set方法: 由'Build -> Make Project'生成
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return this.idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getSex() {
        return this.sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    /**
     * 自定义方法
     * @return 返回性别
     */
    public String getSexStr() {
        return sexs[getSex()];
    }
}

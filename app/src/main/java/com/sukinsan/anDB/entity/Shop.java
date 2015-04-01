package com.sukinsan.anDB.entity;

import com.sukinsan.anDB.anDB.abstracts.BaseEntity;
import com.sukinsan.anDB.anDB.annotations.Column;
import com.sukinsan.anDB.anDB.annotations.Table;

/**
 * Created by victor on 01.04.15.
 */
@Table(name="shop")
public class Shop extends BaseEntity{

    @Column(name = "name",type = "TEXT")
    public String name;

    @Override
    public void beforeDelete(BaseEntity baseEntity) {

    }
}

package com.sukinsan.anDB.anDB.abstracts;

import com.sukinsan.anDB.anDB.DBHandler;
import com.sukinsan.anDB.anDB.QueryManager;
import com.sukinsan.anDB.anDB.annotations.Column;
import com.sukinsan.anDB.anDB.annotations.Index;


/**
 * Created by victorPAul on 6/25/14.
 */
public abstract class BaseEntity {

	@Column(
        name= QueryManager.MAIN_ID,
        type="INTEGER",
        AUTOINCREMENT = true,
        PRIMARY_KEY = true,
        index = @Index(name="index_id",sortBy = "DESC", unique = true)
    )
	protected int id;

	public int getId() {
		return id;
	}

	abstract public void beforeDelete(BaseEntity baseEntity);
}
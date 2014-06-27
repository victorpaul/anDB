package com.sukinsan.anDB.anDB.schema;

/**
 * Created by viktor_2 on 6/27/14.
 */
public class SchemaColumn {
	private String name;
	private String type;
	private boolean notNull;
	private String dfltValue;
	private boolean primaryKey;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	public String getDfltValue() {
		return dfltValue;
	}

	public void setDfltValue(String dfltValue) {
		this.dfltValue = dfltValue;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	@Override
	public String toString() {
		return "SchemaColumn{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				", notNull=" + notNull +
				", dfltValue='" + dfltValue + '\'' +
				", primaryKey=" + primaryKey +
				'}';
	}
}

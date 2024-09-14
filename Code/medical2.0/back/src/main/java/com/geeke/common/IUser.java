package com.geeke.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface IUser {

	public String getName();
	public String getId();
	public String getLoginName();
	
	@JsonIgnore
	public String getCompanyId();
}

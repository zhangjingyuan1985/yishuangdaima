package com.sutpc.demo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Demo实体类")
public class DemoModel extends BaseModel {
	

	@ApiModelProperty(value = "邮箱")
	private String mail;
	@ApiModelProperty(value = "名称")
	private String name;
	
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}

}

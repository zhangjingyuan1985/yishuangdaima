package com.example.demo.model;

import lombok.Data;

/**
 * .
 *
 * @Author:Steven.Yi
 * @Date: 19:00$ 20200704$.
 * @Description
 * @Modified By:
 */
@Data
public class SqlApiEntity {

  private String title;

  private String endpoint;

  private String content;

  public String getTitle() {
    return title;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getContent() {
    return content;
  }
}

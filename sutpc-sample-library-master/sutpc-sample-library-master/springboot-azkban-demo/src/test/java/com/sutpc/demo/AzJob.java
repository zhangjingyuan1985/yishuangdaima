package com.sutpc.demo;

import java.util.List;
import lombok.Data;

/**
 * .
 *
 * @Description: .
 * @Author: HuYing
 * @Date: 2020/5/21 19:25
 * @Modified By:
 */
@Data
public class AzJob {
  private String name;
  private String type;
  private List<String> dependsOn;
  private AzJobConfig config;
}

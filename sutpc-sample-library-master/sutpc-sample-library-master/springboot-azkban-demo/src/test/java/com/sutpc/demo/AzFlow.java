package com.sutpc.demo;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * .
 *
 * @Description: .
 * @Author: HuYing
 * @Date: 2020/5/21 19:23
 * @Modified By:
 */
@Data
public class AzFlow {
  private Map<String,String> config;
  
  private AzJob[] nodes;
  
}

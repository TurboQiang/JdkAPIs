package com.javafields.thread.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author turboqiang
 * @description 仅仅是一个业务实体类,无特殊意义
 */
@Data
@AllArgsConstructor
public class BusinessQuotaInfoVO implements Serializable {
    private static final long serialVersionUID = 634783032498333579L;

    /**
     * 限额名称
     */
    private String name;
    /**
     * 限额来源:10企业，20部门，30个人，40场景，50单程
     *
     */
    private Integer quotaSource;
    /**
     * 限额
     */
    private BigDecimal quotaAmount;
    /**
     * 剩余额度
     */
    private BigDecimal quotaAvailable;
    /**
     * 过期时间
     */
    private String expireTime;
    /**
     * 限额类型（0:不限额；1：月度循环；2：短期限额；3：日限额）
     */
    private Integer quotaType;
    /**
     * 账户状态（1有效；0无效）
     */
    private Integer status;
    /**
     * 是否需要个人支付 1需要 非1不需要
     */
    private String remark;

    /**
     * 是否使用个人限额
     */
    private Boolean usePersonal;

}

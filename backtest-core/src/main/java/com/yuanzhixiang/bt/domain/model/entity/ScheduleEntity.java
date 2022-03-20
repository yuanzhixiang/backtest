package com.yuanzhixiang.bt.domain.model.entity;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

/**
 * @author yuanzhixiang
 */
@Data
public class ScheduleEntity {

    private String securitiesExchangeCode;

    private List<LocalDate> scheduleList;

}

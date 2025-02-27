package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        turnoverReportVO.setDateList(StringUtils.join(dateList, ","));

        List<Double> turnoverList = new ArrayList<>();

        for(LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList, ","));
        return turnoverReportVO;
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        UserReportVO userReportVO = new UserReportVO();
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        userReportVO.setDateList(StringUtils.join(dateList, ","));

        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        for(LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end",endTime);
            Integer totalUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);
            map.put("begin",beginTime);
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser);
        }

        userReportVO.setNewUserList(StringUtils.join(newUserList, ","));
        userReportVO.setTotalUserList(StringUtils.join(totalUserList, ","));
        return userReportVO;
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        OrderReportVO orderReportVO = new OrderReportVO();
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        orderReportVO.setDateList(StringUtils.join(dateList, ","));

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        Integer totalOrderCount = 0;
        Integer totalValidOrderCount = 0;


        for (LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            Integer orderCount = orderMapper.countByMap(map);
            orderCountList.add(orderCount);
            totalOrderCount += orderCount;
            map.put("status",Orders.COMPLETED);
            Integer validOrderCount = orderMapper.countByMap(map);
            validOrderCountList.add(validOrderCount);
            totalValidOrderCount += validOrderCount;
        }
        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 : Double.valueOf(totalValidOrderCount) / Double.valueOf(totalOrderCount);

        orderReportVO.setOrderCountList(StringUtils.join(orderCountList, ","));
        orderReportVO.setValidOrderCountList(StringUtils.join(validOrderCountList, ","));
        orderReportVO.setTotalOrderCount(totalOrderCount);
        orderReportVO.setValidOrderCount(totalValidOrderCount);
        orderReportVO.setOrderCompletionRate(orderCompletionRate);
        return orderReportVO;
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);
        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(names, ","))
                .numberList(StringUtils.join(numbers, ","))
                .build();
    }


}

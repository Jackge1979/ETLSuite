package com.cenrise.test;

import java.util.Date;

/*
 * 上传文件记录实体类
 */
public class UploadReportEntity {
	/**
	 * 自增id
	 */
	private Long id;
	/**
	 * 航司业务订单号
	 */
	private String airlineOrderId;
	/**
	 * 航司二字码
	 */
	private String flight2Code;
	/**
	 * 起始票号
	 */
	private String startTicketNumber;
	/**
	 * 结束票号
	 */
	private String endTicketNumber;
	/**
	 * 航程
	 */
	private String trip;
	/**
	 * 航班号
	 */
	private String flightNo;
	/**
	 * 舱位
	 */
	private String cabin;
	/**
	 * 起飞日期
	 */
	private Date departDate;
	/**
	 * 出票时间
	 */
	private Date ticketOutTime;

	/**
	 * pnr
	 */
	private String pnr;
	/**
	 * 入库人
	 */
	private String inputOpt;
	/**
	 * 出票人
	 */
	private String ticketOutOpt;
	/**
	 * 操作员
	 */
	private String operator;
	/**
	 * 支付途径(易宝，支付宝...)
	 */
	private String payBank;
	/**
	 * 支付流水号
	 */
	private String bankOrderId;
	/**
	 * 退款状态
	 */
	private String refundStatus;

	/**
	 * 大客户编码
	 */
	private String bigCustomerId;
	/**
	 * 订单状态
	 */
	private String orderStatus;
	/**
	 * 商户标识
	 */
	private String customerSign;

	/**
	 * 记录来源
	 */
	private String recordSource;
	/**
	 * 插入版本号
	 */
	private String insertSign;
	/**
	 * 对账状态init_record,compare_record,compare_finish(预留)
	 */
	private String recordStatus;
	/**
	 * 记录创建时间
	 */
	private Date createTime;
	/**
	 * 区域名称
	 */
	private String district;
	/**
	 * 收款方id
	 */
	private String receiverId;
	/**
	 * 创建日期
	 */
	private Date createDate;

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAirlineOrderId() {
		return airlineOrderId;
	}

	public void setAirlineOrderId(String airlineOrderId) {
		this.airlineOrderId = airlineOrderId;
	}

	public String getFlight2Code() {
		return flight2Code;
	}

	public void setFlight2Code(String flight2Code) {
		this.flight2Code = flight2Code;
	}

	public String getStartTicketNumber() {
		return startTicketNumber;
	}

	public void setStartTicketNumber(String startTicketNumber) {
		this.startTicketNumber = startTicketNumber;
	}

	public String getEndTicketNumber() {
		return endTicketNumber;
	}

	public void setEndTicketNumber(String endTicketNumber) {
		this.endTicketNumber = endTicketNumber;
	}

	public String getTrip() {
		return trip;
	}

	public void setTrip(String trip) {
		this.trip = trip;
	}

	public String getFlightNo() {
		return flightNo;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public String getCabin() {
		return cabin;
	}

	public void setCabin(String cabin) {
		this.cabin = cabin;
	}

	public Date getDepartDate() {
		return departDate;
	}

	public void setDepartDate(Date departDate) {
		this.departDate = departDate;
	}

	public Date getTicketOutTime() {
		return ticketOutTime;
	}

	public void setTicketOutTime(Date ticketOutTime) {
		this.ticketOutTime = ticketOutTime;
	}

	public String getPnr() {
		return pnr;
	}

	public void setPnr(String pnr) {
		this.pnr = pnr;
	}

	public String getInputOpt() {
		return inputOpt;
	}

	public void setInputOpt(String inputOpt) {
		this.inputOpt = inputOpt;
	}

	public String getTicketOutOpt() {
		return ticketOutOpt;
	}

	public void setTicketOutOpt(String ticketOutOpt) {
		this.ticketOutOpt = ticketOutOpt;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getPayBank() {
		return payBank;
	}

	public void setPayBank(String payBank) {
		this.payBank = payBank;
	}

	public String getBankOrderId() {
		return bankOrderId;
	}

	public void setBankOrderId(String bankOrderId) {
		this.bankOrderId = bankOrderId;
	}

	public String getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}

	public String getBigCustomerId() {
		return bigCustomerId;
	}

	public void setBigCustomerId(String bigCustomerId) {
		this.bigCustomerId = bigCustomerId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getCustomerSign() {
		return customerSign;
	}

	public void setCustomerSign(String customerSign) {
		this.customerSign = customerSign;
	}

	public String getRecordSource() {
		return recordSource;
	}

	public void setRecordSource(String recordSource) {
		this.recordSource = recordSource;
	}

	public String getInsertSign() {
		return insertSign;
	}

	public void setInsertSign(String insertSign) {
		this.insertSign = insertSign;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}

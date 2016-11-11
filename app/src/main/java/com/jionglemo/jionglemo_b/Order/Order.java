package com.jionglemo.jionglemo_b.Order;

import java.io.Serializable;

/**
 * Created by Mike on 2016/8/3.
 */
public class Order implements Serializable{

    private int id;
    private int store_id;//店铺id
    private int product_id;//产品id
    private String product_name;//产品名称
    private int order_status;//订单状态：1待付款；2待发货；3待收货；4待评价；5订单完成；6取消订单
    private String order_sn;//订单流水号
    private String store_name;//店铺名称
    private String norms_name;//规格名称
    private String thumb;//产品缩略图
    private int quantity;//购买产品数量
    private String unit_price;//单价
    private String total_price;//总价
    private String logistics_sn;//物流单号
    private int postage;//邮费
    private int postage_type;//包邮状态：0不包邮；1包邮
    private String name;//客户名称
    private String tel;//电话
    private String province_name;
    private String city_name;
    private String district_name;
    private String detail_address;
    private String seller_message;

    public Order() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getNorms_name() {
        return norms_name;
    }

    public void setNorms_name(String norms_name) {
        this.norms_name = norms_name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(String unit_price) {
        this.unit_price = unit_price;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getLogistics_sn() {
        return logistics_sn;
    }

    public void setLogistics_sn(String logistics_sn) {
        this.logistics_sn = logistics_sn;
    }

    public int getPostage() {
        return postage;
    }

    public void setPostage(int postage) {
        this.postage = postage;
    }

    public int getPostage_type() {
        return postage_type;
    }

    public void setPostage_type(int postage_type) {
        this.postage_type = postage_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getDistrict_name() {
        return district_name;
    }

    public void setDistrict_name(String district_name) {
        this.district_name = district_name;
    }

    public String getDetail_address() {
        return detail_address;
    }

    public void setDetail_address(String detail_address) {
        this.detail_address = detail_address;
    }

    public String getSeller_message() {
        return seller_message;
    }

    public void setSeller_message(String seller_message) {
        this.seller_message = seller_message;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}

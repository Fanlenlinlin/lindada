package com.jionglemo.jionglemo_b.HomePage;

/**
 * Created by Mike on 2016/7/28.
 */
public class Product {

    private int store_id;//商铺id
    private String store_name;//商铺名称
    private String product_name;//产品名称
    private String thumb;//产品缩略图
    private String content;//产品说明
    private int purchase;//购买人数
    private int concern_number;//关注人数
    private int praise;//好评数量
    private int product_id;//产品id
    private String norms_name;//规格名称
    private String present_price;//现价
    private String original_price;//原价
    private int postage;//邮费
    private int postage_type;//包邮状态：0不包邮；1包邮

    public Product() {
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPurchase() {
        return purchase;
    }

    public void setPurchase(int purchase) {
        this.purchase = purchase;
    }

    public int getConcern_number() {
        return concern_number;
    }

    public void setConcern_number(int concern_number) {
        this.concern_number = concern_number;
    }

    public int getPraise() {
        return praise;
    }

    public void setPraise(int praise) {
        this.praise = praise;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getNorms_name() {
        return norms_name;
    }

    public void setNorms_name(String norms_name) {
        this.norms_name = norms_name;
    }

    public String getPresent_price() {
        return present_price;
    }

    public void setPresent_price(String present_price) {
        this.present_price = present_price;
    }

    public String getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(String original_price) {
        this.original_price = original_price;
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
}

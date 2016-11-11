package com.jionglemo.jionglemo_b.ProductManager;

/**
 * Created by Mike on 2016/9/20.
 */
public class ProductManager {

    private String product_name;//产品名称
    private String thumb;//产品缩略图
    private int status;//产品状态：0上架；1下架
    private int purchase;//购买人数
    private int concern_number;//关注人数
    private String create_time;//生成时间
    private int product_id;
    private String present_price;
    private int repertory;//库存

    public ProductManager() {
    }

    public int getRepertory() {
        return repertory;
    }

    public void setRepertory(int repertory) {
        this.repertory = repertory;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getPresent_price() {
        return present_price;
    }

    public void setPresent_price(String present_price) {
        this.present_price = present_price;
    }
}

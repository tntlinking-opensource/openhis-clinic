package com.geeke.stock.entity;

/**
 * 入库Evt
 * @author txl
 * @version 2022-06-07
 */
public class DispensingDetailEvt {

    private static final long serialVersionUID = 1005591224273698910L;
    //  处方信息id
    private String recipelInfoId;
    //  药品/材料id
    private String id;
    //  0|1 药品|材料
    private String type;
    private Integer number;

    public String getRecipelInfoId() {
        return recipelInfoId;
    }

    public void setRecipelInfoId(String recipelInfoId) {
        this.recipelInfoId = recipelInfoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}

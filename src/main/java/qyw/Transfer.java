package qyw;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 转账信息
 */
public class Transfer implements Serializable {

    private static final long serialVersionUID = -5437378141013159805L;
    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 转账金额
     */
    private BigDecimal amount;


    /**
     * 账单类别
     */
    private String billingClassification;


    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 订单号
     */
    private String orderNo;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * 图片名称

     */
    private String imgName;

    private String productName;

    /**
     * 应用类别：
     * 支付宝、微信、微博
     */
    private String appType;

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBillingClassification() {
        return billingClassification;
    }

    public void setBillingClassification(String billingClassification) {
        this.billingClassification = billingClassification;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }
}

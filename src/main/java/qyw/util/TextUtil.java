package qyw.util;

import qyw.Transfer;

public class TextUtil {


    /**
     * 过滤文字
     * @param billType 账单类型：支付宝、微信、微博
     * @return
     */
    public static void filterBill(String text, String billType, Transfer transfer) {


        if(billType.equals("alipay")) {
            text = text.replaceAll("\\s*", "|");

            String[] str = text.split("|");

            if(str[0].contains("商品说明")) {
                transfer.setProductName(removeSpecialCharacters(str[1].trim()));
            }

            if(str[0].contains("创建时间")) {
                transfer.setCreateDate(removeSpecialCharacters(str[1]));
            }

            if(str[0].contains("订单号")) {
                transfer.setOrderNo(removeSpecialCharacters(str[1]));
            }

            if(str[0].contains("商户全称")) {
                transfer.setMerchantName(removeSpecialCharacters(str[1]));
            }

        } else if(billType.equals("weichat")) {

            String[] str = text.split("");

            if(str[0].contains("商品")) {
                transfer.setProductName(removeSpecialCharacters(str[1].trim()));
            }

            if(str[0].contains("支付时间")) {
                transfer.setCreateDate(removeSpecialCharacters(str[1]));
            }

            if(str[0].contains("交易单号")) {
                transfer.setOrderNo(removeSpecialCharacters(str[1]));
            }

            if(str[0].contains("商户全称")) {
                transfer.setMerchantName(removeSpecialCharacters(str[1]));
            }



        } else if(billType.equals("weibo")) {

        }

    }

    public static String removeSpecialCharacters(String str) {
       return str.replaceAll("[^0-9a-zA-Z\u4e00\u9fa5.，,。？“”》>]+"," ");
    }
}

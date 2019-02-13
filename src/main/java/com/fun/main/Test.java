package com.fun.main;

import net.sf.json.JSONObject;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.util.ArrayList;
import java.util.List;



public class Test {

    public static boolean sendTemplateMsg(String token, Template template) throws JSONException {

        boolean flag = false;

        String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", token);

        JSONObject jsonResult = CommonUtil.httpsRequest(requestUrl, "POST", template.toJSON());
        if (jsonResult != null) {
            int errorCode = jsonResult.getInt("errcode");
            String errorMessage = jsonResult.getString("errmsg");
            if (errorCode == 0) {
                flag = true;
            } else {
                System.out.println("模板消息发送失败:" + errorCode + "," + errorMessage);
                flag = false;
            }
        }
        return flag;

    }

    public static void main(String[] args) throws JSONException {
        String token ="18_K7jOsUhb_HdCWQozCSE4GYRCRm4Q-IuC4DMGz8GDSNNq4Gjop7Aufrf2ykPCFzJ6zsGtCt0Nc7tbmg5K2Di_Fh4-_Gi9ag3VCkekZmnK3WByc_xz_JpNZME4rzYKJCG1BbXcXj90PFyuvItgCEUgAGAGIY";
        Template tem=new Template();
        tem.setTemplateId("5r1_g4gV1p7Yu5K08boXPF1gd7uLCpVCqAwKhdjAIx0");
        tem.setTopColor("#00DD00");
        tem.setToUser("oMh056HzTKJd4wrqmNONdzHeKEHk");
        //tem.setToUser("oMh056JX_yrvd1WCtv27YtybkRpg");
        tem.setUrl("www.woaiguoziqi.我爱你/O2O/front/index");

        List<TemplateParam> paras=new ArrayList<TemplateParam>();
        paras.add(new TemplateParam("first","PS:微信模板接口,群发---我们已收到您的货款，开始为您打包商品，请耐心等待:点我跳转到商品页面 )","#0044BB"));
        paras.add(new TemplateParam("keyword1","¥120.80元","#0044BB"));
        paras.add(new TemplateParam("keyword2","连衣裙","#0044BB"));
        paras.add(new TemplateParam("remark","感谢你对我们平台的支持!!!","#FF3333"));

        tem.setTemplateParamList(paras);

        boolean result=sendTemplateMsg(token,tem);
        System.out.println(result);

    }

}

/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.hotcms.widget.noticeContentList;

import com.huotu.hotcms.service.entity.Category;
import com.huotu.hotcms.service.entity.Notice;
import com.huotu.hotcms.widget.CMSContext;
import com.huotu.hotcms.widget.ComponentProperties;
import com.huotu.hotcms.widget.PreProcessWidget;
import com.huotu.hotcms.widget.Widget;
import com.huotu.hotcms.widget.WidgetStyle;
import com.huotu.hotcms.widget.service.CMSDataSourceService;
import me.jiangcai.lib.resource.service.ResourceService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.NumberUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * @author CJ
 */
public class WidgetInfo implements Widget, PreProcessWidget {
    public static final String SERIAL = "serial";
    public static final String COUNT = "count";
    public static final String DATA_LIST = "dataList";


    @Override
    public String groupId() {
        return "com.huotu.hotcms.widget.noticeContentList";
    }

    @Override
    public String widgetId() {
        return "noticeContentList";
    }

    @Override
    public String name(Locale locale) {
        if (locale.equals(Locale.CHINA)) {
            return "公告列表";
        }
        return "noticeContentList";
    }


    @Override
    public String description(Locale locale) {
        if (locale.equals(Locale.CHINESE)) {
            return "这是一个 A custom Widget，你可以对组件进行自定义修改。";
        }
        return "This is a noticeContentList,  you can make custom change the component.";
    }

    @Override
    public String dependVersion() {
        return "1.0-SNAPSHOT";
    }

    @Override
    public WidgetStyle[] styles() {
        return new WidgetStyle[]{new DefaultWidgetStyle()};
    }

    @Override
    public Resource widgetDependencyContent(MediaType mediaType) {
        if (mediaType.equals(Widget.Javascript))
            return new ClassPathResource("js/widgetInfo.js", getClass().getClassLoader());
        return null;
    }

    @Override
    public Map<String, Resource> publicResources() {
        Map<String, Resource> map = new HashMap<>();
        map.put("thumbnail/defaultStyleThumbnail.png", new ClassPathResource("thumbnail/defaultStyleThumbnail.png", getClass().getClassLoader()));
        return map;
    }

    @Override
    public void valid(String styleId, ComponentProperties componentProperties) throws IllegalArgumentException {
        WidgetStyle style = WidgetStyle.styleByID(this, styleId);
        //加入控件独有的属性验证
        String serial = (String) componentProperties.get(SERIAL);
        int count = Integer.valueOf(componentProperties.get(COUNT).toString());

        if (serial == null || count < 1) {
            throw new IllegalArgumentException("参数错误");
        }
    }

    @Override
    public Class springConfigClass() {
        return null;
    }


    @Override
    public ComponentProperties defaultProperties(ResourceService resourceService) throws IOException, IllegalStateException {
        ComponentProperties properties = new ComponentProperties();
        // 随意找一个数据源,如果没有。那就没有。。
        CMSDataSourceService cmsDataSourceService = CMSContext.RequestContext().getWebApplicationContext()
                .getBean(CMSDataSourceService.class);

        List<Category> categories = cmsDataSourceService.findNoticeCategory();
        if (categories.isEmpty())
            throw new IllegalStateException("请至少添加一个数据源再使用这个控件。");
        properties.put(SERIAL, categories.get(0).getSerial());
        properties.put(COUNT, 10);
        return properties;
    }

    @Override
    public void prepareContext(WidgetStyle style, ComponentProperties properties, Map<String, Object> variables
            , Map<String, String> parameters) {
        String serial = (String) properties.get(SERIAL);
        CMSDataSourceService cmsDataSourceService = CMSContext.RequestContext().getWebApplicationContext()
                .getBean(CMSDataSourceService.class);
        int count = NumberUtils.parseNumber(variables.get(COUNT).toString(), Integer.class);
        List<Notice> list = cmsDataSourceService.findNoticeContent(serial, count);
        variables.put(DATA_LIST, list);

    }
}

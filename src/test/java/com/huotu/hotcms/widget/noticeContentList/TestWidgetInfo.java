/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.hotcms.widget.noticeContentList;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import com.huotu.hotcms.service.entity.Category;
import com.huotu.hotcms.service.entity.Notice;
import com.huotu.hotcms.widget.CMSContext;
import com.huotu.hotcms.widget.ComponentProperties;
import com.huotu.hotcms.widget.Widget;
import com.huotu.hotcms.widget.WidgetStyle;
import com.huotu.hotcms.widget.service.CMSDataSourceService;
import com.huotu.widget.test.WidgetTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class TestWidgetInfo extends WidgetTest {

    @Override
    protected boolean printPageSource() {
        return true;
    }

    @Override
    protected void editorWork(Widget widget, WebElement editor, Supplier<Map<String, Object>> currentWidgetProperties) {
        WebElement count = editor.findElement(By.name("count"));
        count.clear();
        Actions actions = new Actions(driver);
        actions.sendKeys(count, "20").build().perform();
        Map map = currentWidgetProperties.get();
        assertThat(map.get(WidgetInfo.COUNT)).isEqualTo("20");
    }

    @Override
    protected void browseWork(Widget widget, WidgetStyle style, Function<ComponentProperties, WebElement> uiChanger)
            throws IOException {
        ComponentProperties properties = widget.defaultProperties(resourceService);
        WebElement webElement = uiChanger.apply(properties);
        List<WebElement> lis = webElement.findElements(By.tagName("li"));
        CMSDataSourceService cmsDataSourceService = CMSContext.RequestContext().getWebApplicationContext()
                .getBean(CMSDataSourceService.class);
        List<Notice> list = cmsDataSourceService.findNoticeContent(properties.get(WidgetInfo.SERIAL).toString()
                ,Integer.valueOf(properties.get(WidgetInfo.COUNT).toString()));
        if (list!=null){
            assertThat(lis.size()).isEqualTo(list.size());
        }
    }

    @Override
    protected void editorBrowseWork(Widget widget, Function<ComponentProperties, WebElement> uiChanger
            , Supplier<Map<String, Object>> currentWidgetProperties) throws IOException {
        ComponentProperties properties = widget.defaultProperties(resourceService);
        WebElement webElement = uiChanger.apply(properties);
        List<WebElement> option = webElement.findElements(By.tagName("option"));
        CMSDataSourceService cmsDataSourceService = CMSContext.RequestContext().getWebApplicationContext()
                .getBean(CMSDataSourceService.class);
        List<Category> list = cmsDataSourceService.findNoticeCategory();
        if (list!=null){
            assertThat(option.size()).isEqualTo(list.size());
        }
        WebElement count = webElement.findElement(By.name(WidgetInfo.COUNT));
        assertThat(count.getAttribute("value")).isEqualTo(properties.get(WidgetInfo.COUNT).toString());
    }
}

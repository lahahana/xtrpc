package com.github.lahahana.xtrpc.common.config.spring;

import com.github.lahahana.xtrpc.common.config.api.ServiceConfig;
import com.github.lahahana.xtrpc.common.domain.Service;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class ServiceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return ServiceConfig.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String ref = element.getAttribute("ref");
        builder.addPropertyValue("ref", ref);
        String id = element.getAttribute("id");
        builder.addPropertyValue("id", id);
    }
}

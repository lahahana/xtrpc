package com.github.lahahana.xtrpc.common.config.spring;

import com.github.lahahana.xtrpc.common.config.api.*;
import com.github.lahahana.xtrpc.common.constant.Constraints;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class XTNameSpaceSupport extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser(Constraints.APPLICATION, new XTBeanDefinitionParser(Application.class));
        registerBeanDefinitionParser(Constraints.SERVICE, new XTBeanDefinitionParser(ServiceConfig.class));
        registerBeanDefinitionParser(Constraints.REGISTRY, new XTBeanDefinitionParser(Registry.class));
        registerBeanDefinitionParser(Constraints.REFERENCE, new XTBeanDefinitionParser(Reference.class));
        registerBeanDefinitionParser(Constraints.PROTOCOL, new XTBeanDefinitionParser(Protocol.class));
    }
}

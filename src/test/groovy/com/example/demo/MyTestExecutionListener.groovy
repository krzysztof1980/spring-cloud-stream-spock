package com.example.demo

import org.spockframework.spring.SpringMockTestExecutionListener
import org.spockframework.spring.SpringTestContext
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.core.annotation.Order

@Order(1)
class MyTestExecutionListener extends SpringMockTestExecutionListener {

    @Override
    void beforeTestClass(SpringTestContext testContext) throws Exception {
        super.beforeTestClass(testContext)
        def registry = (BeanDefinitionRegistry) testContext.getApplicationContext()
        // When using functions as message handlers, BindableFunctionProxyFactory is responsible for handling them.
        // It is a Spring FactoryBean, but seems not to be meant to create beans by using the method getObject() -
        // unfortunately exactly this tries to do the SpringMockTestExecutionListener in the method beforeTestMethod,
        // to find out which beans are mocks. The following is a hack to remove names of bindings from BeanDefinitionRegistry
        // (an alternative would be to use JUnit instead of Spock)
        registry.getBeanDefinitionNames()
                .findAll { it.endsWith('_binding') }
                .each { registry.removeBeanDefinition(it) }
    }
}

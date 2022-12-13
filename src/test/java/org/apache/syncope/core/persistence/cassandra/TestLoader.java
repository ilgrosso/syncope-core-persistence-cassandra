/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.apache.syncope.core.persistence.cassandra;

import org.apache.syncope.common.lib.SyncopeConstants;
import org.apache.syncope.core.persistence.api.DomainHolder;
import org.apache.syncope.core.persistence.api.content.ContentLoader;
import org.apache.syncope.core.spring.ApplicationContextProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

public class TestLoader implements InitializingBean {

    private final DomainHolder domainHolder;

    private final ContentLoader contentLoader;

    private final ConfigurableApplicationContext ctx;

    public TestLoader(
            final DomainHolder domainHolder,
            final ContentLoader contentLoader,
            final ConfigurableApplicationContext ctx) {

        this.domainHolder = domainHolder;
        this.contentLoader = contentLoader;
        this.ctx = ctx;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContextProvider.setApplicationContext(ctx);
        ApplicationContextProvider.setBeanFactory((DefaultListableBeanFactory) ctx.getBeanFactory());

        contentLoader.load(SyncopeConstants.MASTER_DOMAIN, null);
        if (domainHolder.getDomains().containsKey("Two")) {
            contentLoader.load("Two", null);
        }
    }
}

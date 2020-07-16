package org.example.genericcontroller.support.generic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

//@Configuration
//@RequiredArgsConstructor
public class GenericConfiguration extends AdviceModeImportSelector<EnabledGeneric> {
    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        return new String[0];
    }
//    @Override
//    public BeanDefinition parse(Element element, ParserContext parserContext) {
//        System.out.println("#################################");
//        return null;
//    }

//    private final ApplicationContext context;

//    @Bean
//    public String test() {
//        ConfigurableListableBeanFactory beanFactory = ((AnnotationConfigServletWebServerApplicationContext) context).getBeanFactory();
//        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
//        String[] candidateNames = registry.getBeanDefinitionNames();
//
//        for (String beanName : candidateNames) {
//            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
//            System.out.println(beanDef.getBeanClassName());
//            if (!StringUtils.isEmpty(beanDef.getBeanClassName()) &&
//                    RootBeanDefinition.class.isAssignableFrom(beanDef.getClass())) {
//                Class<?> beanClass = ((RootBeanDefinition) beanDef).getBeanClass();
//                if (AnnotatedElementUtils.hasAnnotation(beanClass, EnabledGeneric.class)) {
//                    System.out.println("DEEEE");
//                }
//            }
//            System.out.println("Asd");
//        }
//
//        return "ASd";
//    }
}

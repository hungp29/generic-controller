package org.example.genericcontroller.support.generic;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;

//@Configuration
//@RequiredArgsConstructor
public class GenericConfigurationSelector extends AdviceModeImportSelector<EnabledGeneric> {
    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        return new String[]{GenericConfiguration.class.getName()};
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

package com.jqdom.curriculum.ch03;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BeanController {

    private static final String BASE_PACKAGE = "com.jqdom.curriculum.ch03";

    private final ApplicationContext context;
    private final ConfigurableListableBeanFactory beanFactory;
    private final OrderService orderService;

    public BeanController(
            ApplicationContext context,
            ConfigurableListableBeanFactory beanFactory,
            OrderService orderService) {
        this.context = context;
        this.beanFactory = beanFactory;
        this.orderService = orderService;
    }

    /** GET /orders?item=book&urgent=true */
    @GetMapping("/orders")
    public String order(@RequestParam String item, @RequestParam(defaultValue = "false") boolean urgent) {
        return orderService.place(item, urgent);
    }

    /** GET /senders - List로 주입받은 NotificationSender 전부 */
    @GetMapping("/senders")
    public List<String> senders() {
        return orderService.channels();
    }

    /**
     * GET /beans - 컨테이너가 관리하는 Bean 중 이 챕터 패키지에서 온 것만 이름순으로.
     * {@code @Bean} 메서드로 만든 Bean은 타입이 라이브러리 클래스이므로, 그 메서드를 가진
     * {@code @Configuration} 클래스(팩토리 Bean)의 패키지로 판단한다.
     */
    @GetMapping("/beans")
    public List<String> beans() {
        return java.util.Arrays.stream(context.getBeanDefinitionNames())
                .filter(this::belongsToThisChapter)
                .sorted()
                .toList();
    }

    private boolean belongsToThisChapter(String beanName) {
        BeanDefinition definition = beanFactory.getBeanDefinition(beanName);
        String owner = definition.getFactoryBeanName();
        Class<?> type = owner != null ? context.getType(owner) : context.getType(beanName);
        return type != null && type.getName().startsWith(BASE_PACKAGE);
    }
}

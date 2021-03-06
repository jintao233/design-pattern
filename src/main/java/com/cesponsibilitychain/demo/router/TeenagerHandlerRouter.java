package com.cesponsibilitychain.demo.router;


import com.cesponsibilitychain.demo.dto.Person;
import com.cesponsibilitychain.demo.handler.StrategyHandler;
import com.cesponsibilitychain.demo.handler.impl.DefaultHandler;
import com.cesponsibilitychain.demo.handler.impl.FemaleHandler;
import com.cesponsibilitychain.demo.handler.impl.MaleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * @program demo
 * @description: 策略 + 责任链模式
 * @author: Jin
 * @create: 2021-04-12 10:58
 * @update: 2021-04-12 10:58
 * @intention: 子转发节点，需要同时继承Router抽象类和实现Handler接口。方法中首先进行一定异常入参拦截，
 * 遵循 fail-fast 原则，避免将这一层可以拦截的错误传递到下一层，同时也要避免“越权”做非本层职责的拦截校验，
 * 避免产生耦合，为后面业务拓展挖坑。
 * <p>
 * 在拦截逻辑后直接调用本身 Router 的 public R applyStrategy(T param) 方法路由给下游节点即可。
 */
@Component
public class TeenagerHandlerRouter extends AbstractStrategyRouter<Person, String>
        implements StrategyHandler<Person, String> {
    private static final Logger log = LoggerFactory.getLogger(TeenagerHandlerRouter.class);

    private final FemaleHandler femaleHandler;
    private final MaleHandler maleHandler;
    private HashMap<String, StrategyHandler<Person, String>> handlerMap;


    @Autowired
    public TeenagerHandlerRouter(FemaleHandler femaleHandler,
                                 MaleHandler maleHandler,
                                 DefaultHandler defaultHandler) {
        super(defaultHandler);
        this.femaleHandler = femaleHandler;
        this.maleHandler = maleHandler;
    }

    /**
     * 类初始化时注册路由信息
     */
    @PostConstruct
    private void handlerMapInit() {
        handlerMap = new HashMap<>();
        handlerMap.put("male", maleHandler);
        handlerMap.put("female", femaleHandler);
    }

    @Override
    public String apply(Person person) {
        return applyStrategy(person);
    }

    @Override
    protected StrategyMapper<Person, String> registerStrategyMapper() {
        return param -> handlerMap.get(param.getSex().toLowerCase());
    }

}

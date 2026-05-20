package com.zju.lease.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SentinelRuleConfig {

    @PostConstruct
    public void initFlowRules() {
        List<FlowRule> flowRules = new ArrayList<>();
        flowRules.add(buildFlowRule("POST:/app/login", 20));
        flowRules.add(buildFlowRule("GET:/app/login/getCode", 20));
        flowRules.add(buildFlowRule("GET:/app/apartment/list", 50));
        FlowRuleManager.loadRules(flowRules);

        List<DegradeRule> degradeRules = new ArrayList<>();
        degradeRules.add(buildSlowRatioDegrade("POST:/app/login", 0.5, 1000, 10));
        DegradeRuleManager.loadRules(degradeRules);
    }

    private FlowRule buildFlowRule(String resource, double qps) {
        FlowRule rule = new FlowRule();
        rule.setResource(resource);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(qps);
        return rule;
    }

    private DegradeRule buildSlowRatioDegrade(String resource, double threshold,
                                               int statIntervalMs, int recoveryTimeoutSec) {
        DegradeRule rule = new DegradeRule();
        rule.setResource(resource);
        rule.setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType());
        rule.setCount(threshold);
        rule.setStatIntervalMs(statIntervalMs);
        rule.setTimeWindow(recoveryTimeoutSec);
        rule.setMinRequestAmount(5);
        rule.setSlowRatioThreshold(3000);
        return rule;
    }
}

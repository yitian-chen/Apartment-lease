package com.zju.lease.agent.config;

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
    public void initRules() {
        List<FlowRule> flowRules = new ArrayList<>();
        flowRules.add(buildFlowRule("POST:/api/agent/search", 10));
        flowRules.add(buildFlowRule("POST:/api/agent/admin/reindex", 2));
        FlowRuleManager.loadRules(flowRules);

        List<DegradeRule> degradeRules = new ArrayList<>();
        // AI search: slow when 60% of requests exceed 3s
        DegradeRule slowDegrade = new DegradeRule();
        slowDegrade.setResource("POST:/api/agent/search");
        slowDegrade.setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType());
        slowDegrade.setCount(0.6);
        slowDegrade.setSlowRatioThreshold(3000);
        slowDegrade.setStatIntervalMs(5000);
        slowDegrade.setTimeWindow(10);
        slowDegrade.setMinRequestAmount(5);
        degradeRules.add(slowDegrade);

        // AI search: error when 50% of requests fail
        DegradeRule errorDegrade = new DegradeRule();
        errorDegrade.setResource("POST:/api/agent/search");
        errorDegrade.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
        errorDegrade.setCount(0.5);
        errorDegrade.setStatIntervalMs(5000);
        errorDegrade.setTimeWindow(10);
        errorDegrade.setMinRequestAmount(5);
        degradeRules.add(errorDegrade);

        DegradeRuleManager.loadRules(degradeRules);
    }

    private FlowRule buildFlowRule(String resource, double qps) {
        FlowRule rule = new FlowRule();
        rule.setResource(resource);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(qps);
        return rule;
    }
}

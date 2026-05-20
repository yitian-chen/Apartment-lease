package com.zju.lease.chat.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
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
        List<FlowRule> rules = new ArrayList<>();
        rules.add(buildFlowRule("GET:/app/chat/conversations", 30));
        rules.add(buildFlowRule("GET:/app/chat/users/search", 20));
        rules.add(buildFlowRule("POST:/app/chat/messages", 50));
        FlowRuleManager.loadRules(rules);
    }

    private FlowRule buildFlowRule(String resource, double qps) {
        FlowRule rule = new FlowRule();
        rule.setResource(resource);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(qps);
        return rule;
    }
}

package com.zju.lease.agent.controller.agent;

import com.zju.lease.agent.service.ApartmentDataIngestor;
import com.zju.lease.agent.service.ApartmentSearchAgent;
import com.zju.lease.agent.service.RoomSearcher;
import com.zju.lease.agent.vo.AgentSearchVo;
import com.zju.lease.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Tag(name = "AI助手")
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
@Slf4j
public class AgentSearchController {

    private final ApartmentSearchAgent apartmentSearchAgent;
    private final RoomSearcher roomSearcher;
    private final ApartmentDataIngestor apartmentDataIngestor;

    @Operation(summary = "AI智能搜索房间",
            description = "用户输入自然语言描述租房需求，AI分析后推荐最匹配的房源")
    @PostMapping("/search")
    public Result<AgentSearchVo> search(
            @Parameter(description = "自然语言查询，如「找个西湖区2000元以内朝南带独卫的房间」", required = true)
            @RequestParam String query) {
        log.info("Agent search query: {}", query);

        // 1. 向量搜索匹配房间
        RoomSearcher.SearchResult searchResult = roomSearcher.search(query, 5);
        List<AgentSearchVo.RoomItemVo> roomItems = roomSearcher.getRoomItems(searchResult.roomIds());

        // 2. 将用户查询和搜索结果发给 LLM
        String context = searchResult.contextText().isEmpty() ? "无匹配结果" : searchResult.contextText();
        String aiText = apartmentSearchAgent.search(query, context);

        // 3. 过滤 think 标签
        String cleaned = aiText.replaceAll("(?s)<think>.*?</think>", "").trim();

        // 4. 解析 LLM 推荐结果编号，只保留推荐房间的卡片
        List<AgentSearchVo.RoomItemVo> recommendedRooms = parseRecommendedRooms(cleaned, searchResult.roomIds(), roomItems);
        String displayText = stripRecommendPrefix(cleaned);

        return Result.ok(new AgentSearchVo(displayText, recommendedRooms));
    }

    /**
     * 解析 LLM 回复中的 [推荐:N1,N3] 格式，提取匹配结果编号
     */
    private List<AgentSearchVo.RoomItemVo> parseRecommendedRooms(
            String aiText, List<Long> allRoomIds, List<AgentSearchVo.RoomItemVo> allRoomItems) {
        Matcher m = Pattern.compile("\\[推荐:\\s*([^\\]]+)\\]").matcher(aiText);
        if (!m.find()) {
            // LLM 未按要求格式返回，降级展示全部
            return allRoomItems;
        }
        String ids = m.group(1).trim();
        if (ids.equals("无")) return List.of();

        Set<Integer> matchIndices = new HashSet<>();
        for (String s : ids.split("[,，\\s]+")) {
            try { matchIndices.add(Integer.parseInt(s.replaceAll("[^0-9]", ""))); } catch (NumberFormatException ignored) {}
        }

        List<AgentSearchVo.RoomItemVo> filtered = new ArrayList<>();
        for (Integer idx : matchIndices) {
            int i = idx - 1; // N1 → allRoomIds[0]
            if (i >= 0 && i < allRoomIds.size()) {
                Long roomId = allRoomIds.get(i);
                allRoomItems.stream()
                        .filter(r -> r.getId().equals(roomId))
                        .findFirst()
                        .ifPresent(filtered::add);
            }
        }
        return filtered.isEmpty() ? allRoomItems : filtered;
    }

    /** 去掉 [推荐:...] 前缀，展示纯文本 */
    private String stripRecommendPrefix(String text) {
        return text.replaceFirst("\\[推荐:\\s*[^\\]]+\\]\\s*", "").trim();
    }

    @Operation(summary = "手动触发房间数据重新索引")
    @PostMapping("/admin/reindex")
    public Result<String> reindex(
            @Parameter(description = "房间ID，传值则只重建该房间的索引，不传则全量重建")
            @RequestParam(required = false) Long roomId) {
        if (roomId != null) {
            apartmentDataIngestor.ingestRoomAsync(roomId);
            return Result.ok("房间 " + roomId + " 重新索引已触发");
        } else {
            apartmentDataIngestor.fullReindex();
            return Result.ok("全量重新索引已触发");
        }
    }
}

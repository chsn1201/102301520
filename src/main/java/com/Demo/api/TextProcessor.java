package com.Demo.api;


import com.huaban.analysis.jieba.JiebaSegmenter;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 功能：
 *  - 弹幕文本噪声过滤（如“666”、“点赞”等无信息弹幕、纯符号/数字、过短内容）
 *  - 基于关键词列表筛选出与“大语言模型”相关的弹幕（支持大小写和英文）
 *  - 使用 jieba 分词将弹幕拆分为词并统计词频
 */
public class TextProcessor {

    // 关键词列表：涵盖大模型、AI、ChatGPT 等常见相关词汇与变体
    private final List<String> keywords = Arrays.asList(
            //大模型相关
            "大模型", "大型语言模型", "大语言模型", "语言模型", "预训练模型", "Transformer",
            "AI大模型", "AI模型", "智能模型", "人工智能模型",
            "foundation model", "large model", "large language model", "LLM", "llm",
            "multimodal", "多模态", "多模态大模型", "视觉语言模型",

            //ChatGPT / OpenAI 系列
            "ChatGPT", "chatgpt", "GPT", "gpt", "GPT-3", "GPT-3.5", "GPT-4", "GPT4", "GPT5",
            "OpenAI", "openai", "ChatGPT4", "ChatGPT5",
            "Copilot", "copilot", "Claude", "claude", "Gemini", "gemini",
            "Sora", "sora", "文心一言", "ERNIE", "ERNIE Bot", "通义千问", "Qwen", "Qwen2",
            "星火大模型", "讯飞星火", "百度文心", "阿里通义", "智谱清言", "GLM", "ChatGLM", "chatglm",

            //通用AI、机器学习
            "AI", "ai", "人工智能", "智能助手", "机器学习", "深度学习", "神经网络", "算法模型",
            "训练数据", "参数量", "推理", "微调", "对齐", "RLHF", "强化学习", "Transformer架构",

            //编程、生成式AI
            "AIGC", "aigc", "生成式AI", "生成模型", "prompt", "提示词", "AI绘画", "AI写作",
            "Stable Diffusion", "Midjourney", "Diffusion", "text2image", "AI生成",
            "代码生成", "AI编程", "Code Interpreter", "代码助手",

            //研究与公司名相关
            "DeepMind", "Anthropic", "Google DeepMind", "Meta AI", "百度AI", "阿里AI",
            "智谱AI", "Moonshot", "MiniMax", "Kimi", "豆包", "通义", "SparkDesk",

            //相关技术词
            "embedding", "token", "向量数据库", "知识图谱", "检索增强生成", "RAG",
            "langchain", "LangChain", "Agent", "agents", "智能体", "自主智能体",
            "CoT", "chain of thought", "思维链", "推理链", "长文本", "上下文", "记忆机制"
    );

    // 常见噪声集合（可扩展）
    private final Set<String> noiseSet = new HashSet<>(Arrays.asList(
            "666", "233", "哈哈", "哈哈哈", "哈", "呵呵", "好", "好耶", "好好笑",
            "笑死", "笑晕", "真好", "太强了", "强", "牛", "牛逼", "牛b", "牛哔",
            "厉害", "赞", "点个赞", "加油", "冲", "冲冲冲", "前排", "打卡", "来了",
            "路过", "占楼", "沙发", "二楼", "三楼", "晚到", "哈哈哈哈", "嘿嘿", "呜呜",
            "卧槽", "哇", "哇塞", "啊这", "离谱", "好听", "好看", "帅", "漂亮", "美",
            "哭了", "笑了", "泪目", "无语", "真实", "淦", "妙啊", "好活", "绝了"
    ));

    // 噪声正则：纯标点、纯数字、纯空白等视为噪声
    private final Pattern noisePattern = Pattern.compile("^[\\\\p{Punct}\\\\s\\\\d]+$");

    // 分词器
    private final JiebaSegmenter segmenter = new JiebaSegmenter();

    /**
     *  过滤噪声弹幕：
     *  - 长度小于等于 1 的弹幕直接丢弃
     *  - 在 noiseSet 中的短语丢弃
     *  - 匹配 noisePattern（纯符号/数字）丢弃
     * @param raw 原始弹幕列表
     * @return 过滤后的弹幕列表
     */
    public List<String> filterNoise(List<String> raw){
        if(raw == null){
            return new ArrayList<>();
        }
        return raw.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> s.length() > 1)
                .filter(s -> !noiseSet.contains(s))
                .filter(s -> !noisePattern.matcher(s).matches())
                .collect(Collectors.toList());
    }

    /**
     * 在清洗后的弹幕中筛选包含关键词的条目
     * @param raw 已清洗过噪声的弹幕列表
     * @return 包含关键词的弹幕子集
     */
    public List<String> filterByKeyword(List<String> raw){
        if(raw == null){
            return new ArrayList<>();
        }
        return raw.stream()
                .filter(s -> {
                    String lower = s.toLowerCase();
                    for(String keyword : keywords){
                        if(lower.contains(keyword.toLowerCase())){
                            return true;
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
    }

    /**
     * 对文本列表进行分词统计词频
     * @param raw 待统计的文本列表
     * @return 词->频次
     */
    public Map<String, Integer> computeWordFrequency(List<String> raw){
        Map<String, Integer> freq = new HashMap<>();
        if(raw == null){
            return freq;
        }

        for(String s : raw){
            List<String> words = segmenter.sentenceProcess(s);
            for(String word : words){
                String w = word.trim();
                if(w.length() <= 1){
                    continue;
                }
                if(isStopword(w)){
                    continue;
                }
                freq.put(w, freq.getOrDefault(w, 0) + 1);
            }
        }
        return freq;
    }

    /**
     * 获取 topN 词频项（降序）
     */
    public List<Map.Entry<String, Integer>> topN(Map<String, Integer> freq, int n){
        return freq.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * 简易停用词判断（示例）
     * 可替换为更完善的停用词表文件加载
     */
    private boolean isStopword(String w) {
        String[] stops = {"的", "了", "是", "在", "我", "有", "和", "就", "也", "都", "吗", "你"};
        for (String s : stops) {
            if (w.equals(s)) return true;
        }
        return false;
    }
}

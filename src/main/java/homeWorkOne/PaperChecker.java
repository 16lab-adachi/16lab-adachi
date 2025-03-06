package homeWorkOne;

import java.io.*;
import java.util.*;

// 论文查重类
public class PaperChecker {

    // 原文文件路径
    private String originalFilePath;
    // 抄袭版文件路径
    private String plagiarizedFilePath;
    // 答案文件路径
    private String answerFilePath;

    /**
     * 构造函数，初始化文件路径
     * @param originalFilePath 原文文件路径
     * @param plagiarizedFilePath 抄袭版文件路径
     * @param answerFilePath 答案文件路径
     */
    public PaperChecker(String originalFilePath, String plagiarizedFilePath, String answerFilePath) {
        // 参数校验：检查文件路径是否为 null 或空
        if (originalFilePath == null || originalFilePath.isEmpty() ||
                plagiarizedFilePath == null || plagiarizedFilePath.isEmpty() ||
                answerFilePath == null || answerFilePath.isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        this.originalFilePath = originalFilePath;
        this.plagiarizedFilePath = plagiarizedFilePath;
        this.answerFilePath = answerFilePath;
    }

    /**
     * 计算相似度
     * @return 相似度 (0.0 到 1.0 之间)
     * @throws IOException 如果读取或写入文件时发生 I/O 错误
     * @throws IllegalArgumentException 如果文件路径为空
     */
    public double calculateSimilarity() throws IOException, IllegalArgumentException {
        // 参数校验
        if (originalFilePath == null || originalFilePath.isEmpty() ||
                plagiarizedFilePath == null || plagiarizedFilePath.isEmpty() ||
                answerFilePath == null || answerFilePath.isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        // 读取原文和抄袭版文件内容
        String originalText = readFileContent(originalFilePath);
        String plagiarizedText = readFileContent(plagiarizedFilePath);

        // 文本预处理（分词、去除停用词）
        List<String> originalWords = preprocessText(originalText);
        List<String> plagiarizedWords = preprocessText(plagiarizedText);

        // 使用 HashSet 存储原文的二元组，以提高查找效率
        Set<String> originalWordSet = new HashSet<>(originalWords);
        int commonWordCount = 0;
        // 遍历抄袭版文本的二元组，统计共同出现的二元组数量
        for (String word : plagiarizedWords) {
            if (originalWordSet.contains(word)) {
                commonWordCount++;
            }
        }

        // 计算相似度 (共同出现的二元组数量 / 原文和抄袭版文本中较长的二元组数量)
        double similarity = 0.0;
        if (originalWords.size() > 0 || plagiarizedWords.size() > 0) {
            similarity = (double) commonWordCount / Math.max(originalWords.size(), plagiarizedWords.size());
        }

        // 将相似度写入答案文件
        writeSimilarityToFile(similarity);
        return similarity;
    }

    /**
     * 读取文件内容
     * @param filePath 文件路径
     * @return 文件内容字符串
     * @throws IOException 如果读取文件时发生 I/O 错误, 如文件不存在，无法读取等。
     * @throws NullPointerException 如果文件路径为空
     */
    String readFileContent(String filePath) throws IOException {
        // 校验文件路径是否为空
        if (filePath == null) {
            throw new NullPointerException("文件路径为空");
        }

        File file = new File(filePath);

        // 检查文件是否存在
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        // 检查文件是否可读
        if (!file.canRead()) {
            throw new IOException("文件不可读: " + filePath);
        }


        // 使用 try-with-resources 确保资源正确关闭
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            // 逐行读取文件
            while ((line = reader.readLine()) != null) {
                // 添加换行符以保留原文格式
                content.append(line).append("\n");
            }
            return content.toString();

        }
    }
    /**
     * 将相似度写入文件
     * @param similarity 相似度
     * @throws IOException 如果写入文件时发生 I/O 错误
     * @throws IllegalArgumentException 如果写入文件路径为空
     */
    void writeSimilarityToFile(double similarity) throws IOException {
        //参数校验
        if (answerFilePath == null || answerFilePath.isEmpty())
        {
            throw new IllegalArgumentException("写入文件路径为空");
        }
        // 使用 try-with-resources 确保资源正确关闭
        try (PrintWriter writer = new PrintWriter(new FileWriter(answerFilePath))) {
            // 格式化输出，保留两位小数
            writer.printf("%.2f", similarity);
        }
    }

    /**
     * 文本预处理（二元分词、去除停用词）
     * @param text 要处理的文本
     * @return 处理后的词列表（二元组）
     * @throws NullPointerException 如果传入文本为空
     */
    List<String> preprocessText(String text) {
        //参数校验
        if (text == null)
        {
            throw new NullPointerException("待处理文本为空");
        }
        // 进行二元分词
        List<String> words = bigramSplit(text);

        // 去除停用词
        List<String> filteredWords = new ArrayList<>();
        String[] stopWords = {"的", "了", "是", "我", "你", "他", "她", "它", "我们", "你们", "他们", "今天", "要", "和", "与", "及", "之", "在", "中", "上", "下", "这", "那", "也", "就"};
        // 使用 HashSet 存储停用词，以提高查找效率
        Set<String> stopWordSet = new HashSet<>(Arrays.asList(stopWords));

        for (String word : words) {
            // 转小写
            String lowerCaseWord = word.toLowerCase();
            // 去除空字符串和停用词
            if (!lowerCaseWord.isEmpty() && !stopWordSet.contains(lowerCaseWord)) {
                filteredWords.add(lowerCaseWord);
            }
        }
        return filteredWords;
    }

    /**
     * 二元分词 (Bigram)
     * @param text 要分词的文本
     * @return 分词后的二元组列表
     * @throws NullPointerException 如果传入文本为空
     */
    private List<String> bigramSplit(String text) {
        //参数校验
        if(text == null)
        {
            throw new NullPointerException("待分词文本为空");
        }
        List<String> bigrams = new ArrayList<>();
        // 移除空格和标点符号
        text = text.replaceAll("[\\s\\p{Punct}]+", "");

        // 进行二元分词
        for (int i = 0; i < text.length() - 1; i++) {
            // 提取二元组
            String bigram = text.substring(i, i + 2);
            bigrams.add(bigram);
        }
        return bigrams;
    }

    /**
     * 主函数 (程序入口)
     * @param args 命令行参数：原文文件路径、抄袭版文件路径、答案文件路径
     */
    public static void main(String[] args) {
        // 检查命令行参数数量
        if (args.length != 3) {
            System.out.println("Usage: java PaperChecker <original_file_path> <plagiarized_file_path> <answer_file_path>");
            return;
        }

        // 获取命令行参数
        String originalFilePath = args[0];
        String plagiarizedFilePath = args[1];
        String answerFilePath = args[2];

        // 创建 PaperChecker 对象
        PaperChecker checker = new PaperChecker(originalFilePath, plagiarizedFilePath, answerFilePath);

        try {
            // 计算并输出相似度
            double similarity = checker.calculateSimilarity();
            System.out.println("Similarity: " + String.format("%.2f", similarity));
        }
        catch (IllegalArgumentException e) {
            System.err.println("参数错误: " + e.getMessage());
        }
        catch (IOException e) {
            System.err.println("IO错误: " + e.getMessage());
        }
        catch (Exception e)
        {
            System.err.println("发生未知错误: " + e.getMessage());
        }
    }
}
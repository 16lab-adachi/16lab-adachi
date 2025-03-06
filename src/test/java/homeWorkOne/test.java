package homeWorkOne;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;

public class test {

    // 辅助方法：创建测试文件
    private void createTestFile(String filePath, String content) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.print(content);
        }
    }

    // 辅助方法：删除测试文件
    private void deleteTestFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    // 测试用例1：正常情况下的相似度计算
    // 目的：验证程序在正常输入（两个非空、内容不同的文本文件）下能否正确计算相似度。
    // 预期结果：相似度值应在 0.0 到 1.0 之间，且不等于 0.0 或 1.0。
    @Test
    public void testCalculateSimilarity_normal() throws IOException {
        // 准备测试数据
        String originalText = "这是原始文本。";
        String plagiarizedText = "这是抄袭文本！";
        String originalFilePath = "original.txt";
        String plagiarizedFilePath = "plagiarized.txt";
        String answerFilePath = "answer.txt";
        createTestFile(originalFilePath, originalText);
        createTestFile(plagiarizedFilePath, plagiarizedText);

        // 创建 PaperChecker 对象
        PaperChecker checker = new PaperChecker(originalFilePath, plagiarizedFilePath, answerFilePath);

        // 调用 calculateSimilarity 方法
        double similarity = checker.calculateSimilarity();

        // 断言：相似度应大于 0 且小于 1
        assertTrue(similarity > 0.0);
        assertTrue(similarity < 1.0);

        // 清理测试文件
        deleteTestFile(originalFilePath);
        deleteTestFile(plagiarizedFilePath);
        deleteTestFile(answerFilePath);
    }

    // 测试用例2：两个空文件的相似度计算
    // 目的：验证程序在输入两个空文件时能否正确处理。
    // 预期结果：相似度值应为 0.0。
    @Test
    public void testCalculateSimilarity_emptyFiles() throws IOException {
        // 准备测试数据（空文件）
        String originalFilePath = "empty_original.txt";
        String plagiarizedFilePath = "empty_plagiarized.txt";
        String answerFilePath = "answer.txt";
        createTestFile(originalFilePath, "");
        createTestFile(plagiarizedFilePath, "");
        PaperChecker checker = new PaperChecker(originalFilePath, plagiarizedFilePath, answerFilePath);
        double similarity = checker.calculateSimilarity();
        assertEquals(0.0, similarity, 0.001); // 使用 delta 值处理浮点数比较
        deleteTestFile(originalFilePath);
        deleteTestFile(plagiarizedFilePath);
        deleteTestFile(answerFilePath);

    }

    // 测试用例3：两个相同文件的相似度计算
    // 目的：验证程序在输入两个完全相同的文件时能否正确处理。
    // 预期结果：相似度值应为 1.0。
    @Test
    public void testCalculateSimilarity_identicalFiles() throws IOException {
        // 准备测试数据（相同内容的文件）
        String text = "这是一段相同的文本。";
        String originalFilePath = "identical_original.txt";
        String plagiarizedFilePath = "identical_plagiarized.txt";
        String answerFilePath = "answer.txt";
        createTestFile(originalFilePath, text);
        createTestFile(plagiarizedFilePath, text);
        PaperChecker checker = new PaperChecker(originalFilePath, plagiarizedFilePath, answerFilePath);

        double similarity = checker.calculateSimilarity();
        assertEquals(1.0, similarity, 0.001);
        deleteTestFile(originalFilePath);
        deleteTestFile(plagiarizedFilePath);
        deleteTestFile(answerFilePath);
    }

    // 测试用例4：文件不存在时的异常处理
    // 目的：验证程序在尝试读取不存在的文件时能否正确抛出 FileNotFoundException。
    // 预期结果：应抛出 FileNotFoundException。
    @Test
    public void testReadFileContent_fileNotFound() {
        PaperChecker checker = new PaperChecker("", "", ""); // 构造函数参数不重要
        assertThrows(FileNotFoundException.class, () -> {
            checker.readFileContent("nonexistent_file.txt");
        });
    }

    // 测试用例5：文件路径为 null 时的异常处理
    // 目的：验证程序能否正确处理文件路径为null的情况
    //预期结果: 应该抛出NullPointerException
    @Test
    public void testReadFileContent_nullFilePath() {
        PaperChecker checker = new PaperChecker("", "", "");
        assertThrows(NullPointerException.class, () -> {
            checker.readFileContent(null);
        });
    }
    // 测试用例6：构造函数文件路径为 null 时的异常处理
    // 目的：验证构造函数能否正确处理文件路径为null的情况
    // 预期结果: 应该抛出IllegalArgumentException
    @Test
    public void testCalculateSimilarity_nullFilePath() {

        assertThrows(IllegalArgumentException.class, () -> {
            new PaperChecker(null, "plag.txt", "answer.txt");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new PaperChecker("orig.txt", null, "answer.txt");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PaperChecker("orig.txt", "plag.txt", null);
        });
    }
}
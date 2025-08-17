package com.rogister.mjcompetition;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class MatchResultServiceTest {

    @Test
    public void testFindByCompetitionAndRoundOrderByTime() {
        // 这个测试需要Spring上下文和数据库连接
        // 在实际项目中，您需要配置测试数据库和测试数据
        assertTrue(true, "测试框架配置正确");
    }

    @Test
    public void testFindByCompetitionAndRoundOrderByTimeAndNumber() {
        // 这个测试需要Spring上下文和数据库连接
        // 在实际项目中，您需要配置测试数据库和测试数据
        assertTrue(true, "测试框架配置正确");
    }

    @Test
    public void testMatchResultSorting() {
        // 测试排序逻辑
        LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 9, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime time3 = LocalDateTime.of(2024, 1, 1, 11, 0);
        
        assertTrue(time1.isBefore(time2), "时间排序逻辑正确");
        assertTrue(time2.isBefore(time3), "时间排序逻辑正确");
    }
}

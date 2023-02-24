package com.ufutao.account.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufutao.account.admin.data.AccountRecord;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AccountControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(AccountControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    private long account = 1L;

    @Test
    public void register() throws Exception {
        MvcResult ret = mockMvc.perform(MockMvcRequestBuilders.
                get("/account/register")
                .param("uid", Long.toString(account))
        ).andReturn();
        logger.info("注册测试账户:{{}}", ret.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    @Test
    public void queryMoney() throws Exception {
        MvcResult ret = mockMvc.perform(MockMvcRequestBuilders.
                get("/account/queryMoney")
                .param("uid", Long.toString(account))
        ).andReturn();
        logger.info("测试余额查询:{{}}", ret.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    private AccountRecord getRecordForTesting() {
        AccountRecord record = new AccountRecord();
        record.setAccount(account);
        record.setChannel("test");
        record.setOrderId(UUID.randomUUID().toString().replace("-", "").substring(0, 32));
        return record;
    }

    @Test
    public void addMoney() throws Exception {
        AccountRecord record = getRecordForTesting();
        record.setAction("退款");
        record.setNumber(50);

        MvcResult ret = mockMvc.perform(MockMvcRequestBuilders.
                post("/account/addMoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(record))
        ).andReturn();
        logger.info("测试余额入账:{{}}", ret.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    @Test
    public void reduceMoney() throws Exception {
        AccountRecord record = getRecordForTesting();
        record.setAction("消费");
        record.setNumber(100);

        MvcResult ret = mockMvc.perform(MockMvcRequestBuilders.
                post("/account/reduceMoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(record))
        ).andReturn();
        logger.info("测试余额出账:{{}}", ret.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    @Test
    public void queryHistory() throws Exception {
        MvcResult ret = mockMvc.perform(MockMvcRequestBuilders.
                get("/account/queryHistory")
                .param("uid", Long.toString(account))
                .param("start", new Date(new Date().getTime()- TimeUnit.DAYS.toMillis(30)).toString())
                .param("end", new Date().toString())
        ).andReturn();
        logger.info("测试余额历史:{{}}", ret.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }
}
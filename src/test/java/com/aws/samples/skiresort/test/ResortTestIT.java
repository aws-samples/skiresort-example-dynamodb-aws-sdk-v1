/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.aws.samples.skiresort.test;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.aws.samples.skiresort.domain.AvalancheDanger;
import com.aws.samples.skiresort.domain.Resort;
import com.aws.samples.skiresort.test.config.DynamoDBIntegrationTestConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Example of testing basic CRUD functionality
 */
@ExtendWith(LocalDBCreationExtension.class)
@SpringBootTest
public class ResortTestIT {

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private DynamoDBIntegrationTestConfiguration dynamoDBIntegrationTestConfiguration;

    @BeforeEach
    public void initDataModel() {
        dynamoDBIntegrationTestConfiguration.dynamoDBLocalSetup();
    }

    @AfterEach
    public void verifyEmptyDatabase() {
        assertEquals(0, dynamoDBIntegrationTestConfiguration.getItemCount());
    }

    @Test
    void testSaveResort() {
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);

        assertEquals(0, dynamoDBIntegrationTestConfiguration.getItemCount());
        LocalDate now = LocalDate.now();
        Resort resort = Resort.builder().date(now).build();
        mapper.save(resort);
        assertEquals(1, dynamoDBIntegrationTestConfiguration.getItemCount());
        // cleanup
        mapper.delete(resort);
    }

    @Test
    void testUpdateResort() {
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);

        // first save a resort
        assertEquals(0, dynamoDBIntegrationTestConfiguration.getItemCount());
        LocalDate now = LocalDate.now();
        Resort resort = Resort.builder().date(now).totalUniqueLiftRiders(1000).build();
        mapper.save(resort);

        Resort resortFromDatabaseBeforeUpdate = mapper.load(resort);
        assertEquals(1000, resortFromDatabaseBeforeUpdate.getTotalUniqueLiftRiders());

        // then update the resort
        resort.setTotalUniqueLiftRiders(2000);
        mapper.save(resort);

        Resort resortFromDatabaseAfterUpdate = mapper.load(resort);
        assertEquals(2000, resortFromDatabaseAfterUpdate.getTotalUniqueLiftRiders());

        // cleanup
        mapper.delete(resort);
    }
}

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
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.aws.samples.skiresort.domain.AvalancheDanger;
import com.aws.samples.skiresort.domain.LiftDynamicStats;
import com.aws.samples.skiresort.domain.LiftStaticStats;
import com.aws.samples.skiresort.test.config.DynamoDBIntegrationTestConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LocalDBCreationExtension.class)
@SpringBootTest
public class RetrieveAllDataTestIT {

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
    void testRetrieveStaticAndDynamicLiftData() {
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);

        assertEquals(0, dynamoDBIntegrationTestConfiguration.getItemCount());
        int liftNumber = 6789;

        // create static test data
        LiftStaticStats staticStats = LiftStaticStats.builder()
                .experiencedRidersOnly(true)
                .liftNumber(liftNumber)
                .liftTime("07:00")
                .verticalFeet(3000)
                .build();
        mapper.save(staticStats);

        // create dynamic test data
        LocalDate now = LocalDate.now();
        List<LiftDynamicStats> dynamicStatsList = new ArrayList<LiftDynamicStats>();
        for(int i = 0; i < 3; i++) {
            LiftDynamicStats dynamicStats = LiftDynamicStats.builder()
                    .avalancheDanger(AvalancheDanger.HIGH)
                    .averageSnowCoverageInches(234)
                    .date(now.minusDays(i))
                    .liftNumber(liftNumber)
                    .totalUniqueLiftRiders(1000)
                    .build();
            dynamicStatsList.add(dynamicStats);
            mapper.save(dynamicStats);
        }
        assertEquals(4, dynamoDBIntegrationTestConfiguration.getItemCount());

        // Cannot use DynamoDBMapper because of the heterogenous nature of this query
        // result can contain both LiftDynamicStats & LiftStaticStats
        AttributeValue liftPK = new AttributeValue("LIFT#" + liftNumber);
        QueryRequest queryRequest = new QueryRequest()
                .withTableName("SkiLifts")
                .withKeyConditionExpression("PK = :v_pk")
                .withExpressionAttributeValues(Map.of(":v_pk", liftPK));
        QueryResult queryResult = amazonDynamoDB.query(queryRequest);

        assertEquals(4, queryResult.getCount());

        // cleanup
        mapper.delete(staticStats);
        dynamicStatsList.forEach( dynamicStats -> mapper.delete(dynamicStats));
    }
}

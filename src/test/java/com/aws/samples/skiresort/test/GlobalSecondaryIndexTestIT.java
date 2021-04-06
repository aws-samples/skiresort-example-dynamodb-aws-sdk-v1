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
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.aws.samples.skiresort.domain.AvalancheDanger;
import com.aws.samples.skiresort.domain.LiftDynamicStats;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(LocalDBCreationExtension.class)
@SpringBootTest
public class GlobalSecondaryIndexTestIT {

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
    void testRetrieveDateOfLiftDataSortedByTotalUniqueLift() {

        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);

        assertEquals(0, dynamoDBIntegrationTestConfiguration.getItemCount());
        int lift1 = 6789;
        int lift2 = 5678;

        // create dynamic test data
        LocalDate now = LocalDate.now();
        List<LiftDynamicStats> dynamicStatsList = new ArrayList<LiftDynamicStats>();
        for(int i = 0; i < 3; i++) {
            LiftDynamicStats dynamicStatsLift1 = LiftDynamicStats.builder()
                    .avalancheDanger(AvalancheDanger.HIGH)
                    .averageSnowCoverageInches(234)
                    .date(now.minusDays(i))
                    .liftNumber(lift1)
                    .totalUniqueLiftRiders(1000)
                    .build();
            LiftDynamicStats dynamicStatsLift2 = LiftDynamicStats.builder()
                    .avalancheDanger(AvalancheDanger.HIGH)
                    .averageSnowCoverageInches(432)
                    .date(now.minusDays(i))
                    .liftNumber(lift2)
                    .totalUniqueLiftRiders(1500)
                    .build();
            dynamicStatsList.add(dynamicStatsLift1);
            dynamicStatsList.add(dynamicStatsLift2);
            mapper.save(dynamicStatsLift1);
            mapper.save(dynamicStatsLift2);
        }
        assertEquals(6, dynamoDBIntegrationTestConfiguration.getItemCount());

        // Query based on only the PK of the GSI: we want all data for a given Lift
        List<LiftDynamicStats> results = mapper.query(LiftDynamicStats.class,
                new DynamoDBQueryExpression<LiftDynamicStats>()
                        // reads from GSI can't be consistent
                        .withConsistentRead(false)
                        .withExpressionAttributeValues(
                                Map.of(":val1", new AttributeValue().withS("LIFT#" + lift1)))
                        .withIndexName("GSI_1")
                        .withKeyConditionExpression("GSI_1_PK = :val1"));

        assertEquals(3, results.size());
        results.forEach( result -> assertNotNull(result.getDate()));

        // cleanup
        dynamicStatsList.forEach( dynamicStats -> mapper.delete(dynamicStats));
    }
}

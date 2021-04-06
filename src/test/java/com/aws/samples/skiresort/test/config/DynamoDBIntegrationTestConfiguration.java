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
package com.aws.samples.skiresort.test.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.aws.samples.skiresort.domain.Resort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DynamoDBIntegrationTestConfiguration {

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    private DynamoDBMapper dynamoDBMapper;

    public void dynamoDBLocalSetup() {
        try {
            ListTablesResult tablesResult = amazonDynamoDB.listTables();
            if (!tablesResult.getTableNames().contains("SkiLifts")) {
                dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
                // Single table design: any of the domain class will contain the data needed to create the table
                CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(Resort.class);
                tableRequest.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L));

                tableRequest.getGlobalSecondaryIndexes().forEach(gsi -> {
                    gsi.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L));
                    // "Date" is a required attribute for the access pattern using this Global Secondary Index
                    // ProjectionType.KEYS_ONLY would miss that attribute
                    // ProjectionType.ALL would work but less efficient then ProjectionType.INCLUDE
                    gsi.getProjection().setNonKeyAttributes(List.of("Date"));
                    gsi.getProjection().setProjectionType(ProjectionType.INCLUDE);
                });
                amazonDynamoDB.createTable(tableRequest);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getItemCount() {
        return amazonDynamoDB.describeTable("SkiLifts").getTable().getItemCount();
    }
}

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
package com.aws.samples.skiresort.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.aws.samples.skiresort.convert.LocalDateConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
@Builder
@Data
@DynamoDBTable(tableName = "SkiLifts")
@NoArgsConstructor
public class Resort {

    @DynamoDBAttribute(attributeName = "Date")
    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S)
    private LocalDate date;

    @DynamoDBAttribute(attributeName = "TotalUniqueLiftRiders")
    private int totalUniqueLiftRiders;

    @DynamoDBAttribute(attributeName = "AverageSnowCoverageInches")
    private int averageSnowCoverageInches;

    @DynamoDBAttribute(attributeName = "AvalancheDanger")
    @DynamoDBTypeConvertedEnum
    private AvalancheDanger avalancheDanger;

    @DynamoDBAttribute(attributeName = "OpenLifts")
    private Set<String> openLifts;

    @DynamoDBHashKey(attributeName = "PK")
    public String getPK() {
        return "RESORT_DATA";
    }

    public void setPK(String pk) {
        // intentionally left blank: PK is static data
    }

    @DynamoDBRangeKey(attributeName = "SK")
    public String getSK() {
        return "DATE#" + date;
    }

    public void setSK(String sk) {
        // intentionally left blank: SK is set by setting the date attribute
    }

    @DynamoDBIndexHashKey(attributeName = "GSI_1_PK", globalSecondaryIndexName = "GSI_1")
    public String getGSI1PK() {
        return getPK();
    }

    public void setGSI1PK(String pk) {
        // intentionally left blank: PK is static data
    }

    @DynamoDBIndexRangeKey( attributeName = "GSI_1_SK", globalSecondaryIndexName = "GSI_1")
    public String getGSI1SK() {
        return "TOTAL_UNIQUE_LIFT_RIDERS#" + getTotalUniqueLiftRiders();
    }

    public void setGSI1SK(String sk) {
        // intentionally left blank: SK is set by setting the totalUniqueLiftRiders attribute
    }
}
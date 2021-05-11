## Ski Resort example using Amazon DynamoDB based on Single Table Design using AWS SDK for Java v1


This is the _**sample**_ code that comes together with [the blog post on Amazon DynamoDB Single Table Design using DynamoDBMapper & Spring Boot](https://aws.amazon.com/blogs/database/amazon-dynamodb-single-table-design-using-dynamodbmapper-and-spring-boot/).

The purpose of this sample code is to demonstrate how you can apply the Single Table Design concept in Java / Spring Boot based applications.

The sample code demonstrates some relevant access patterns using local integration tests using DynamoDB Local.

To run the tests locally when Apache Maven is already installed:

`mvn clean verify`


If Apache Maven is not installed the included Maven Wrapper can be used. Maven version used by the wrapper can be updated in `./mvn/wrapper/maven-wrapper.properties`.

To run the test locally using the Maven Wrapper:

`./mvnw clean verify`



### Single Table data model

The domain classes in this example store all data in the same table:

<table>
    <thead>
        <tr>
            <th colspan="11">Skilifts Table</th>
        </tr>        
        <tr>
            <th colspan="2">Primary key</th>
            <th colspan="9">Attributes</th>
        </tr>
        <tr>
            <th>PK</th>
            <th>SK</th>
            <th>Date</th>
            <th>TotalUniqueLiftRiders</th>
            <th>AverageSnowCoverageInches</th>
            <th>AvalancheDanger</th>
            <th>OpenLifts</th>
            <th>ExperiencedRidersOnly</th>
            <th>VerticalFeet</th>
            <th>LiftTime</th>
            <th>LiftNumber</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>RESORT_DATA</td>
            <td>DATE#07-03-2021</td>
            <td>07-03-2021</td>
            <td>7788</td>
            <td>50</td>
            <td>HIGH</td>
            <td>60</td>
            <td/>
            <td/>
            <td/>
            <td/>
        </tr>
        <tr>
            <td>RESORT_DATA</td>
            <td>DATE#08-03-2021</td>
            <td>08-03-2021</td>
            <td>6699</td>
            <td>40</td>
            <td>MODERATE</td>
            <td>60</td>
            <td/>
            <td/>
            <td/>
            <td/>
        </tr>
        <tr>
            <td>RESORT_DATA</td>
            <td>DATE#09-03-2021</td>
            <td>09-03-2021</td>
            <td>5678</td>
            <td>65</td>
            <td>EXTREME</td>
            <td>53</td>
            <td/>
            <td/>
            <td/>
            <td/>
        </tr>
        <tr>
            <td>LIFT#1234</td>
            <td>STATIC_DATA</td>
            <td/>
            <td/>
            <td/>
            <td/>
            <td/>
            <td>TRUE</td>
            <td>1230</td>
            <td>7:00</td>
            <td>4545</td>
        </tr>
        <tr>
            <td>LIFT#1234</td>
            <td>DATE#07-03-2021</td>
            <td>07-03-2021</td>
            <td>3000</td>
            <td>60</td>
            <td>HIGH</td>
            <td>OPEN</td>
            <td/>
            <td/>
            <td/>
            <td/>
        </tr>
    </tbody>
</table>

## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This library is licensed under the MIT-0 License. See the LICENSE file.


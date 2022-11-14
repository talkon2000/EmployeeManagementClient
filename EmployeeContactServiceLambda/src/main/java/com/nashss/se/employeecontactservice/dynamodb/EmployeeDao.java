package com.nashss.se.employeecontactservice.dynamodb;
import com.nashss.se.employeecontactservice.dynamodb.models.Employee;
import com.nashss.se.employeecontactservice.exceptions.EmployeeNotFoundException;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Accesses data for a playlist using {@link Employee} to represent the model in DynamoDB.
 */
public class EmployeeDao {

    private final DynamoDBMapper dynamoDBMapper;

    /**
     * Instantiates a EmployeeDao object.
     *
     * @param dynamoDBMapper the {@link DynamoDBMapper} used to interact with the employees table
     */
    @Inject
    public EmployeeDao(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    /**
     * Returns the {@link Employee} corresponding to the specified id.
     *
     * @param employeeId the Employee ID
     * @return the stored Employee, or null if none was found.
     */

    public Employee getEmployee(String employeeId) {
        Employee employee = dynamoDBMapper.load(Employee.class, employeeId);
        if (null == employee) {
            throw new EmployeeNotFoundException(String.format("Could not find Employee with ID '%s' ", employeeId)
            );

        }
        return employee;
    }

    /**
     * Returns the {@link Employee} corresponding to the specified id.
     *
     * @param employeeStartKey the Employee ID
     * @param forward boolean if true the page will go to the next if false it will go to previous.
     * @return the stored Employees, or null if none was found.
     */

    public List<Employee> getAllActiveEmployeesWithLimit(Employee employeeStartKey, Boolean forward) {
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":status", new AttributeValue().withS("Active"));
        valueMap.put("employeeId", new AttributeValue().withS(employeeStartKey.getEmployeeId()));
        DynamoDBQueryExpression<Employee> queryExpression = new DynamoDBQueryExpression<Employee>()
                .withIndexName(Employee.EMPLOYEE_STATUS)
                .withLimit(20)
                .withScanIndexForward(forward)
                .withConsistentRead(false)
                .withExclusiveStartKey(valueMap)
                .withKeyConditionExpression("status = :status")
                .withExpressionAttributeValues(valueMap);

        PaginatedQueryList<Employee> employeesList = dynamoDBMapper.query(Employee.class, queryExpression);

        return employeesList;
    }

    public List<Employee> getAllActiveEmployeesWithLimit() {
        return getAllActiveEmployeesWithLimit(null, true);
    }
}

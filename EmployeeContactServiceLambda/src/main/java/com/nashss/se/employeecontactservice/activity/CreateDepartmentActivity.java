package com.nashss.se.employeecontactservice.activity;

import com.nashss.se.employeecontactservice.activity.requests.CreateDepartmentRequest;
import com.nashss.se.employeecontactservice.activity.results.CreateDepartmentResult;
import com.nashss.se.employeecontactservice.dynamodb.DepartmentDao;
import com.nashss.se.employeecontactservice.dynamodb.models.Department;
import com.nashss.se.employeecontactservice.exceptions.DepartmentNotFoundException;
import com.nashss.se.employeecontactservice.exceptions.InvalidAttributeValueException;
import com.nashss.se.employeecontactservice.utils.EmployeeMgmtClientServiceUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class CreateDepartmentActivity {
    private final Logger log;

    private final DepartmentDao departmentDao;

    /**
     * Instantiates a new CreateDepartmentActivity object.
     *
     * @param departmentDao DepartmentDao to access the department table.
     */
    @Inject
    public CreateDepartmentActivity(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
        log = LogManager.getLogger();
    }

    /**
     * This method handles the incoming request by creating a department in the database.
     * <p>
     * It then returns the department.
     * <p>
     * If deptId and/or departmentName are invalid strings,
     * this should throw an InvalidAttributeValueException.
     *
     * @param request request object containing the department's information
     * @return CreateDepartmentResult result object containing the API defined {@link Department}
     */
    public CreateDepartmentResult handleRequest(CreateDepartmentRequest request) {
        log.info("Received Create Department Request {}", request);

        checkAttributes(request);

        Department dept = new Department();
        dept.setDeptId(request.getDeptId());
        dept.setDeptName(request.getDeptName());
        dept.setDeptStatus(request.getDeptStatus());

        departmentDao.saveDepartment(dept);

        return CreateDepartmentResult.builder()
                .withDepartment(dept)
                .build();
    }

    private void checkAttributes(CreateDepartmentRequest request) {
        // We want the department to not be found
        try {
            departmentDao.getDepartment(request.getDeptId());
            throw new InvalidAttributeValueException("Department ID \"" + request.getDeptId() + "\" is already taken.");
        } catch (DepartmentNotFoundException ignored) {}

        if (request.getDeptName() != null && !EmployeeMgmtClientServiceUtils.isValidString(request.getDeptName())) {
            throw new InvalidAttributeValueException("Department name \"" +
                    request.getDeptName() +
                    "\" contains invalid characters");
        }
    }
}

import EmployeeMgmtClient from '../api/employeeMgmtClient';
import Header from '../components/header';
import BindingClass from "../util/bindingClass";
import DataStore from "../util/DataStore";

/**
 * Logic needed for the view playlist page of the website.
 */
//Global variable to track the very first employee Id retrieved and the first page.

class ViewEmployeeDetail extends BindingClass {

    constructor() {
        super();
        this.bindClassMethods(['clientLoaded', 'mount', 'displayEmpDetails', 'update', 'loadDeptDropDown', 'redirectToViewEmployee'], this);
        this.dataStore = new DataStore();
        this.header = new Header(this.dataStore);
    }

 /**
     * Once the client is loaded, get the employee details.
     */
    async clientLoaded() {
        const urlParams = new URLSearchParams(window.location.search);
        const employeeId = urlParams.get('id');
        this.dataStore.set('employeeId', employeeId);
        const employeeDetail = await this.client.getEmployee(employeeId);
        this.dataStore.set('employeeDetail', employeeDetail);
        await this.loadDeptDropDown();
        this.displayEmpDetails();
    }

    /**
     * Add the header to the page and load the EmployeeMgmtClientClient.
     */
    async mount() {
        document.getElementById('save-employee').addEventListener('click', this.update);
        document.getElementById('depts').addEventListener('change', this.deptChange);
        this.header.addHeaderToPage();
        this.header.loadData();
        this.client = new EmployeeMgmtClient();
        await this.clientLoaded();

    }

deptChange() {
    const dept = document.getElementById('depts');
    const deptId = document.getElementById('depts').value;
    const deptName = dept.options[dept.selectedIndex].innerHTML;
    document.getElementById('deptId').innerHTML = deptId;
}

async loadDeptDropDown() {
       //Get all depts API
       const departments = await this.client.getAllDepartments();
       console.log(departments);
       const deptsDropDown = document.getElementById('depts');
       for (let key of departments) {
          let option = document.createElement("option");
          option.setAttribute('value', key.deptId);
          option.setAttribute('innerHTML', key.deptName);
          let optionText = document.createTextNode(key.deptName);
          option.appendChild(optionText);
          deptsDropDown.appendChild(option);
        }
    }



 /**
     * Display employee details  on the page.
     */
    displayEmpDetails() {
             const employeeDetail = this.dataStore.get('employeeDetail');

             if (!employeeDetail) {
                 return;
             }

             if (employeeDetail.firstName){
                 document.getElementById('fname').value = employeeDetail.firstName;
             }
             if (employeeDetail.lastName){
                 document.getElementById('lname').value = employeeDetail.lastName;
             }
             if (employeeDetail.jobTitle){
                 document.getElementById('jobtitle').value = employeeDetail.jobTitle;
             }
             if (employeeDetail.email){
                 document.getElementById('email').value = employeeDetail.email;
             }
             if (employeeDetail.phoneNumber){
                 document.getElementById('phone').value = employeeDetail.phoneNumber;
             }
             if (employeeDetail.dateOfBirth){
                 document.getElementById('dob').value = employeeDetail.dateOfBirth;
             }
             if (employeeDetail.hireDate){
                 document.getElementById('hireDate').value = employeeDetail.hireDate;
             }
             if (employeeDetail.deptName){
                 const dept = document.getElementById('depts');

                 document.getElementById('depts').value = employeeDetail.deptId;
             }
             if (employeeDetail.deptId){
                 document.getElementById('deptId').innerHTML = employeeDetail.deptId;
              }
         
    }

    async update() {

        const dept = document.getElementById('depts');
        const employeeId = this.dataStore.get('employeeId');
        const firstName = document.getElementById('fname').value;
        const lastName = document.getElementById('lname').value;
        const jobTitle = document.getElementById('jobtitle').value;
        const email = document.getElementById('email').value;
        const deptId = document.getElementById('depts').value;
        const deptName = dept.options[dept.selectedIndex].innerHTML;
        const hireDate = document.getElementById('hireDate').value;
        const phoneNumber = document.getElementById('phone').value;
        const dateOfBirth = document.getElementById('dob').value;
        const employeeStatus = empStatusBox.options[empStatusBox.selectedIndex].innerHTML;
        document.getElementById('save-employee').innerHTML = 'Saving Employee...';

        const employee = {
            employeeId,
            firstName,
            lastName,
            jobTitle,
            email,
            deptId,
            deptName,
            hireDate,
            dateOfBirth,
            employeeStatus};

        const employeeUpdated = await this.client.updateEmployee(employee);
        this.dataStore.set('employee', employeeUpdated);
        this.redirectToViewEmployee();
    }

    redirectToViewEmployee() {
        const employee = this.dataStore.get('employee');
        if (employee) {
            window.location.href = `/index.html`;
        }
    }
}

/**
 * Main method to run when the page contents have loaded.
 */
const main = async () => {
    const updateEmployee = new ViewEmployeeDetail();
    await updateEmployee.mount();
};

window.addEventListener('DOMContentLoaded', main);
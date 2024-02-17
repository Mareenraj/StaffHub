import { useEffect, useState } from "react";
import { listEmployees } from "../services/EmployeeService";

const ListEmployeeComponent = () => {
 const[employees,setEmployees] = useState([])

 useEffect(()=>{
   listEmployees().then((response)=>{
     setEmployees(response.data)
   }).catch((error)=>{
     console.error(error);
   })
 },[])

  return (
    <div>
      <h2 className="mb-4 text-4xl font-extrabold leading-none tracking-tight text-gray-900 md:text-5xl lg:text-6xl dark:text-white text-center">
        List of Employees
      </h2>
      <div className="relative overflow-x-auto shadow-md sm:rounded-lg mx-10 my-7">
        <table className="w-full text-lg text-left rtl:text-right text-gray-500 dark:text-gray-400">
          <thead className=" text-gray-700 uppercase bg-gray-400 dark:bg-gray-700 dark:text-gray-400">
            <tr>
              <th scope="col" className="px-6 py-3">
                Id
              </th>
              <th scope="col" className="px-6 py-3">
                First Name
              </th>
              <th scope="col" className="px-6 py-3">
                Last Name
              </th>
              <th scope="col" className="px-6 py-3">
                Email
              </th>
            </tr>
          </thead>
          <tbody>
            {employees.map((employee) => (
              <tr
                key={employee.id}
                className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 font-semibold"
              >
                <td className="px-6 py-4">{employee.id}</td>
                <td className="px-6 py-4">{employee.firstName}</td>
                <td className="px-6 py-4">{employee.lastName}</td>
                <td className="px-6 py-4">{employee.email}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ListEmployeeComponent;

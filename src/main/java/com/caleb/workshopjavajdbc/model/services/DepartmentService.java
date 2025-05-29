package com.caleb.workshopjavajdbc.model.services;

import com.caleb.workshopjavajdbc.model.dao.DaoFactory;
import com.caleb.workshopjavajdbc.model.dao.DepartmentDao;
import com.caleb.workshopjavajdbc.model.entities.Department;

import java.util.ArrayList;
import java.util.List;

public class DepartmentService {

    private DepartmentDao dao = DaoFactory.createDepartmentDao();

    public DepartmentService(){

    }

    public List<Department> findAll(){
        return dao.findAll();
    }

    public void saveOrUpdate(Department obj){
        if(obj.getId() == null){
            dao.insert(obj);
        }
        else {
            dao.update(obj);
        }
    }

    public void remove(Department obj){
        dao.deleteById(obj.getId());
    }
}

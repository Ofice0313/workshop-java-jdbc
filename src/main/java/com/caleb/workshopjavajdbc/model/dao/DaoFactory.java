package com.caleb.workshopjavajdbc.model.dao;


import com.caleb.workshopjavajdbc.db.DB;
import com.caleb.workshopjavajdbc.model.dao.impl.DepartmentDaoJDBC;
import com.caleb.workshopjavajdbc.model.dao.impl.SellerDaoJDBC;

public class DaoFactory {

	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC(DB.getConnection());
	}
	
	public static DepartmentDao createDepartmentDao() {
		return new DepartmentDaoJDBC(DB.getConnection());
	}
}

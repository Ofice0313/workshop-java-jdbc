package com.caleb.workshopjavajdbc.model.services;

import com.caleb.workshopjavajdbc.model.dao.DaoFactory;
import com.caleb.workshopjavajdbc.model.dao.SellerDao;
import com.caleb.workshopjavajdbc.model.entities.Seller;

import java.util.List;

public class SellerService {

    private SellerDao dao = DaoFactory.createSellerDao();

    public SellerService(){

    }

    public List<Seller> findAll(){
        return dao.findAll();
    }

    public void saveOrUpdate(Seller obj){
        if(obj.getId() == null){
            dao.insert(obj);
        }
        else {
            dao.update(obj);
        }
    }

    public void remove(Seller obj){
        dao.deleteById(obj.getId());
    }
}

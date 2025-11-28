package com.Proyecto.backEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.Proyecto.backEnd.model.PersonalModel;

public interface PersonalRepo extends JpaRepository<PersonalModel,Integer>, JpaSpecificationExecutor<PersonalModel>{

}

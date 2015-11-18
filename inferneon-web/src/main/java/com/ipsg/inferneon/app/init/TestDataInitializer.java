package com.ipsg.inferneon.app.init;


import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ipsg.inferneon.app.model.Attribute;
import com.ipsg.inferneon.app.model.Project;
import com.ipsg.inferneon.app.model.User;

/**
 *
 * This is a initializing bean that inserts some test data in the database. It is only active in
 * the development profile, to see the data login with user123 / PAssword2 and do a search starting on
 * 1st of January 2015.
 *
 */
@Component
public class TestDataInitializer {

    @Autowired
    private EntityManagerFactory entityManagerFactory;


    public void init() throws Exception {

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        User user = new User("test123", "$2a$10$x9vXeDsSC2109FZfIJz.pOZ4dJ056xBpbesuMJg3jZ.ThQkV119tS", "test@email.com", 1000L);
        Attribute empId = new Attribute("EmployeeId","ATT",null,0);
        Attribute empName = new Attribute("EmployeeName","ATT",null,1);
        Attribute gender = new Attribute("Gender","ATTV","Male,Female",2);
        Attribute empSal = new Attribute("EmployeeSalary","ATT",null,3);	
        Set<Attribute> empAttr = new HashSet<Attribute>();
        empAttr.add(empId);
        empAttr.add(empName);
        empAttr.add(empSal);
        empAttr.add(gender);
        Project project = new Project();
        empId.setProject(project);
        empName.setProject(project);
        gender.setProject(project);
        empSal.setProject(project);
        project.setProjectName("Employees");
        project.setAttributes(empAttr);
        project.setCreatedTS(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        project.setUser(user);
        session.persist(user);
        session.persist(project);
        transaction.commit();
    }
}
package com.inferneon.dao;

import com.inferneon.model.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.inferneon.model.User;
public class UserDataAccessObjectImplementation implements UserDataAccessObject {
	private SessionFactory sessionFactory;
	public void NewRegistration(User user) {
		sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.saveOrUpdate(user);
		session.getTransaction().commit();
	}

	public int login(User user) {
		sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		Query query = session
				.createSQLQuery(
						"select * from user where Email= :email and password= :password")
				.addEntity(User.class)
				.setParameter("email", user.getEmail())
				.setString("password", user.getPassword());
		List<User> result = query.list();
		int userId = 0;
		for (User users : result) {
			userId = users.getid();
		}

		return userId;
	}

	@SuppressWarnings("unchecked")
	public List<Project> getProjects(int userId) {
		sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		System.out.println("service userid=" + userId);
		Query query = session.createSQLQuery("select * from project where id="
				+ userId);
		List<Project> result = query.list();
		return result;

	}

	public void newProject(Project project) {
		sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.saveOrUpdate(project);
		session.getTransaction().commit();

	}

	public void newAttribute(Attributes attributes) {
		sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.saveOrUpdate(attributes);
		session.getTransaction().commit();

	}

	public void newAttributeName(attributeNames attributenames) {
		sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.saveOrUpdate(attributenames);
		session.getTransaction().commit();

	}

	public void newNominalValues(attributeNominalValues attributeNominalValues) {
		sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.saveOrUpdate(attributeNominalValues);
		session.getTransaction().commit();
	}
}

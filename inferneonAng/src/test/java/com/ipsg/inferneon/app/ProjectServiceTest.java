package com.ipsg.inferneon.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ipsg.inferneon.app.dto.ProjectDTO;
import com.ipsg.inferneon.app.model.Project;
import com.ipsg.inferneon.app.model.SearchResult;
import com.ipsg.inferneon.app.services.ProjectService;
import com.ipsg.inferneon.config.root.RootContextConfig;
import com.ipsg.inferneon.config.root.TestConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static com.ipsg.inferneon.app.TestUtils.date;
import static com.ipsg.inferneon.app.TestUtils.time;
import static com.ipsg.inferneon.app.dto.ProjectDTO.mapFromProjectEntity;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes={TestConfiguration.class, RootContextConfig.class})
public class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testFindProjectsByDate() {
        SearchResult<Project> result = projectService.findProjects(UserServiceTest.USERNAME, date(2015,1,1), date(2015,1,2), null ,null, 1);
        assertTrue("results not expected, total " + result.getResultsCount(), result.getResultsCount() == 4);
    }

    @Test
    public void testFindProjectsByDateTime() {
        SearchResult<Project> result = projectService.findProjects(UserServiceTest.USERNAME, date(2015,1,1), date(2015,1,2),
                time("11:00") ,time("14:00"), 1);
        assertTrue("results not expected, total " + result.getResultsCount(), result.getResultsCount() == 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromDateAfterToDate() {
        projectService.findProjects(UserServiceTest.USERNAME, date(2015,1,2), date(2015,1,1), null ,null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromTimeAfterToTime() {
        projectService.findProjects(UserServiceTest.USERNAME, date(2015,1,2), date(2015,1,1), time("12:00") ,time("11:00"), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromDateNull() {
        projectService.findProjects(UserServiceTest.USERNAME, null, date(2015,1,1), time("12:00") ,time("11:00"), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void toDateNull() {
        projectService.findProjects(UserServiceTest.USERNAME, date(2015,1,1), null, time("12:00") ,time("11:00"), 1);
    }

    @Test
    public void deleteProjects() {
        projectService.deleteProjects(Arrays.asList(15L));
        Project project = em.find(Project.class, 15L);
        assertNull("project was not deleted" , project);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteProjectsNull() {
        projectService.deleteProjects(null);
    }

    @Test
    public void saveProjects() {
        ProjectDTO project1 = mapFromProjectEntity(em.find(Project.class, 1L));
        ProjectDTO project2 = mapFromProjectEntity(em.find(Project.class, 2L));

        project1.setDescription("test1");
        project2.setNoOfProjects(10L);

        List<ProjectDTO> projects = Arrays.asList(project1, project2);

        projectService.saveProjects(UserServiceTest.USERNAME, projects);


        Project m1 = em.find(Project.class, 1L);
        assertTrue("description not as expected: " + m1.getDescription(), "test1".equals(m1.getDescription()));

        Project m2 = em.find(Project.class, 2L);
        assertTrue("noOfProjects not as expected: " + m2.getNoOfProjects(), m2.getNoOfProjects() == 10L);
    }


}

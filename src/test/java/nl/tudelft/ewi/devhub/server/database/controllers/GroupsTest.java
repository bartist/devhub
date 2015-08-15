package nl.tudelft.ewi.devhub.server.database.controllers;

import java.util.Random;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import nl.tudelft.ewi.devhub.server.database.entities.CourseEdition;
import nl.tudelft.ewi.devhub.server.database.entities.Group;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import nl.tudelft.ewi.devhub.server.database.entities.GroupRepository;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JukitoRunner.class)
@UseModules(TestDatabaseModule.class)
public class GroupsTest {
	
	@Inject
	private Random random;
	
	@Inject
	private Groups groups;
	
	@Inject
	private Courses courses;
	
	@Test(expected=ConstraintViolationException.class)
	public void testInsertGroupWithoutCourse() {
		Group group = new Group();
//		group.setRepositoryName("courses/ti1705/group-1");
		group.setGroupNumber(5l);
		groups.persist(group);
	}
	
	@Test(expected=ConstraintViolationException.class)
	public void testInsertGroupWithoutGroupNumber() {
		CourseEdition course = getTestCourse();
		Group group = new Group();
//		group.setRepositoryName("courses/ti1705/group-1");
		group.setCourseEdition(course);
		groups.persist(group);
	}
	
	@Test(expected=ConstraintViolationException.class)
	public void testInsertGroupWithoutRepositoryName() {
		CourseEdition course = getTestCourse();
		Group group = new Group();
		group.setGroupNumber(6l);
		group.setCourseEdition(course);
		groups.persist(group);
	}
	
	@Test(expected=PersistenceException.class)
	public void testUnableToInsertWithSameRepoName() {
		Group group = createGroup();
		groups.persist(group);
		
		Group otherGroup = createGroup();
		otherGroup.setCourseEdition(group.getCourseEdition());
		otherGroup.setGroupNumber(random.nextLong());
		otherGroup.getRepository().setRepositoryName(group.getRepository().getRepositoryName());
		groups.persist(otherGroup);
	}
	
	@Test(expected=PersistenceException.class)
	public void testUnableToInsertWithSameGroupNumber() {
		Group group = createGroup();
		groups.persist(group);
		
		Group otherGroup = createGroup();
		otherGroup.setCourseEdition(group.getCourseEdition());
		otherGroup.setGroupNumber(group.getGroupNumber());
		groups.persist(otherGroup);
	}
	
	@Test
	public void testPersistGroup() {
		Group group = createGroup();
		groups.persist(group);
	}
	
	@Test
	public void testListPersistedGroup() {
		Group group = createGroup();
		CourseEdition course = group.getCourse();
		groups.persist(group);
		assertThat(groups.find(course), hasItem(group));
	}

	@Test
	public void testFindByGroupNumber() {
		Group group = createGroup();
		CourseEdition course = group.getCourse();
		groups.persist(group);
		assertEquals(group, groups.find(course, group.getGroupNumber()));
	}
	
	@Test
	public void testFindByRepoName() {
		Group group = createGroup();
		String repoName = group.getRepository().getRepositoryName();
		groups.persist(group);
		assertEquals(group, groups.findByRepoName(repoName));
	}
	
	protected Group createGroup() {
		Group group = new Group();
		CourseEdition course = getTestCourse();
		group.setGroupNumber(random.nextLong());
		group.setCourseEdition(course);

		GroupRepository groupRepository = new GroupRepository();
		groupRepository.setRepositoryName(String.format("courses/%s/group-%s", group.getGroupNumber(), course.getName()));
		group.setRepository(groupRepository);
		return group;
	}
	
	protected CourseEdition getTestCourse() {
		return courses.find("TI1705");
	}
	
}

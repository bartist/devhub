package nl.tudelft.ewi.devhub.server.database.controllers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.tudelft.ewi.devhub.server.backend.IssueBackend;
import nl.tudelft.ewi.devhub.server.backend.PersistedBackendTest;
import nl.tudelft.ewi.devhub.server.database.entities.Group;
import nl.tudelft.ewi.devhub.server.database.entities.GroupRepository;
import nl.tudelft.ewi.devhub.server.database.entities.User;
import nl.tudelft.ewi.devhub.server.database.entities.issues.Issue;
import nl.tudelft.ewi.devhub.server.database.entities.issues.IssueLabel;
import nl.tudelft.ewi.devhub.webtests.rules.UnitOfWorkRule;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

import static nl.tudelft.ewi.devhub.webtests.utils.EntityEqualsMatcher.isEntity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

@RunWith(JukitoRunner.class)
@UseModules(TestDatabaseModule.class)
public class IssuesTest extends PersistedBackendTest {

	@Rule @Inject public UnitOfWorkRule unitOfWorkRule;
	@Inject private Provider<Groups> groupsProvider;
	@Inject private Provider<CourseEditions> coursesProvider;
	@Inject private Provider<Users> usersProvider;
	@Inject private Provider<Issues> issuesProvider;
	@Inject private Provider<IssueLabels> issueLabelsProvider;
	@Inject private Provider<EntityManager> entityManagerProvider;
	@Inject private Provider<IssueBackend> issueBackendProvider;

	private User user1;
	private User user2;
	private Group group;
	
	private Issue issue1;
	private Issue issue2;
	

	@Before
	public void setUpGroup() {
		user1 = createUser();
		user2 = createUser();
		group = createGroup(createCourseEdition(), user1, user2);

		issue1 = persistIssue(user1);		
		issue2 = persistIssue(user2);
	}
	

	@Test
	public void testCreateIssue() {
		GroupRepository groupRepository = group.getRepository();
		List<Issue> issueQueryResult = issuesProvider.get().findOpenIssues(groupRepository);
		
		assertEquals(2, issueQueryResult.size());
		assertThat(issueQueryResult.get(0), isEntity(issue1));
	}
	@Test
	public void testFindIssuesOfUser() {
		GroupRepository groupRepository = group.getRepository();
		
		List<Issue> issueQueryResult = issuesProvider.get().findAssignedIssues(groupRepository, user1);
		assertEquals(1, issueQueryResult.size());
		assertThat(issueQueryResult.get(0), isEntity(issue1));
	}

	@Test
	public void testFindOpenIssues() {
		GroupRepository groupRepository = group.getRepository();
		
		// Close issue 2
		issue2.setOpen(false);
		
		List<Issue> issueQueryResult = issuesProvider.get().findOpenIssues(groupRepository);
		assertEquals(1, issueQueryResult.size());
		assertThat(issueQueryResult.get(0), isEntity(issue1));
	}

	@Test
	public void testFindClosedIssues() {
		GroupRepository groupRepository = group.getRepository();
		
		// Close issue 2
		issue2.setClosed(new Date());
		issue2.setOpen(false);
		
		List<Issue> issueQueryResult = issuesProvider.get().findClosedIssues(groupRepository);
		assertEquals(1, issueQueryResult.size());
		assertThat(issueQueryResult.get(0), isEntity(issue2));
	}

	@Test
	public void testFindUnassignedIssues() {
		GroupRepository groupRepository = group.getRepository();
		issue1.setAssignee(null);
		
		List<Issue> issueQueryResult = issuesProvider.get().findUnassignedIssues(groupRepository);
		assertEquals(1, issueQueryResult.size());
		assertThat(issueQueryResult.get(0), isEntity(issue1));
	}
	
	@Test
	public void testFindIssuesById() {
		Issue actualIssue = issuesProvider.get()
			.findIssueById(group.getRepository(), issue1.getIssueId())
			.get();

		assertThat(actualIssue, isEntity(issue1));
	}

	@Test
	public void testAddLabel() {
		
		IssueLabel issueLabel = issueBackendProvider.get().addIssueLabelToRepository(group.getRepository(), "My Label", 0xcccccc);
		issue1.addLabel(issueLabel);

		issue1 = issuesProvider.get().findIssueById(group.getRepository(), issue1.getIssueId()).get();
		
		assertEquals(1, issue1.getLabels().size());
		
		IssueLabel label = issue1.getLabels().iterator().next();
		assertSame(issueLabel, label);
		
		assertEquals(1, group.getRepository().getLabels().size());
		
		label = group.getRepository().getLabels().iterator().next();
		assertSame(issueLabel, label);
		
	}
	
	private Issue persistIssue(User user){
		GroupRepository groupRepository = group.getRepository();
		
		Issue issue = new Issue();
		issue.setTitle("Issue x");
		issue.setRepository(groupRepository);
		issue.setOpen(true);
		issue.setAssignee(user);

		Issues issues = issuesProvider.get();
		issues.persist(issue);
		entityManagerProvider.get().refresh(issue);
		return issue;
	}

	@Override
	protected CourseEditions getCourses() {
		return coursesProvider.get();
	}

	@Override
	protected Users getUsers() {
		return usersProvider.get();
	}

	@Override
	protected Groups getGroups() {
		return groupsProvider.get();
	}

}

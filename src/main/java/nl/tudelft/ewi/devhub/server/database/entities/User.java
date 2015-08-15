package nl.tudelft.ewi.devhub.server.database.entities;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mindrot.jbcrypt.BCrypt;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

@Data
@Entity
@EqualsAndHashCode(of = { "netId" })
@ToString(of = { "netId" })
@Table(name = "users")
public class User {

	private static final Comparator<Group> GROUP_COMPARATOR = new Comparator<Group>() {
		@Override
		public int compare(Group group1, Group group2) {
			CourseEdition course1 = group1.getCourseEdition();
			CourseEdition course2 = group2.getCourseEdition();
			String code1 = course1.getCode();
			String code2 = course2.getCode();
			int compare = code1.compareTo(code2);
			if (compare != 0) {
				return compare;
			}
			
			return (int) (group1.getGroupNumber() - group2.getGroupNumber());
		}
	};

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@Size(max = 32)
	@Column(name = "net_id", length = 32, nullable = false, unique = true)
	private String netId;

	@Size(max = 128)
	@Column(name = "name", length = 128, nullable = true)
	private String name;

	@Size(max = 255)
	@Column(name = "email", length = 255, nullable = true)
	private String email;

	@Size(max = 20)
	@Column(name = "student_number", length = 20, nullable = true, unique = true)
	private String studentNumber;

	@Basic(fetch = FetchType.LAZY)
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Column(name = "password")
	private String password;
	
	@Column(name = "admin")
	private boolean admin;

	@ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
	private List<Group> groups;

	@ManyToMany(mappedBy = "assistants", fetch = FetchType.LAZY)
	private Set<CourseEdition> assists;

	@Deprecated
	public List<Group> listGroups() {
		return getGroups();
	}
	
	public List<Group> listAssistedGroups() {
		return getAssists().stream()
			.flatMap(edition -> edition.getGroups().stream())
			.sorted(GROUP_COMPARATOR)
			.collect(Collectors.toList());
	}

	public boolean isMemberOf(Group group) {
		return getGroups().contains(group);
	}

	public boolean isAssisting(CourseEdition course) {
		return getAssists().contains(course);
	}

	public boolean isParticipatingInCourse(CourseEdition course) {
		return getGroups().stream()
			.map(Group::getCourseEdition)
			.anyMatch(course::equals);
	}
	
	public void setPassword(String password) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(password));
		this.password = BCrypt.hashpw(password, BCrypt.gensalt());
	}
	
	public boolean isPasswordMatch(String password) {
		return password != null && this.password != null
				&& BCrypt.checkpw(password, this.password);
	}
	
}

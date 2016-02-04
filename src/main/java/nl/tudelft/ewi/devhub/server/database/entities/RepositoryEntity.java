package nl.tudelft.ewi.devhub.server.database.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.tudelft.ewi.devhub.server.database.Base;
import nl.tudelft.ewi.devhub.server.database.Configurable;
import nl.tudelft.ewi.devhub.server.database.entities.builds.BuildInstructionEntity;

import com.google.common.collect.ImmutableMap;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

/**
 * The {@code Repository} information was previously stored within the
 * {@link Group} entity. Now {@code Repository} has been separated from its {@code Group}.
 * This allows for the following future improvements:
 *
 * <ul>
 *     <li>Multiple repositories per group.</li>
 *     <li>Personal (not group nor course bound) repositories.</li>
 *     <li>Separate repository from group provisioning.</li>
 * </ul>
 *
 * The {@code RepositoryEntity} is currently implemented as an extendable {@code Entity}.
 * This is so foreign key relationships can be made to abstractions of repositories.
 */
@Data
@Entity
@Table(name = "repository")
@EqualsAndHashCode(of = {"id"})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER)
public abstract class RepositoryEntity implements Configurable, Base {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty
    @Column(name = "repository_name", unique = true)
    private String repositoryName;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "build_instruction", nullable = true)
    private BuildInstructionEntity buildInstruction;

    /**
     * @return a list of collaborators for this repository.
     * For a {@link GroupRepository} these are the {@link User Users} in the {@link Group}.
     */
    public abstract Collection<User> getCollaborators();

    /**
     * @return the title for the repository. This may be editable or managed.
     */
    public abstract String getTitle();

}
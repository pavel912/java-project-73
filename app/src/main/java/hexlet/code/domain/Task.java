package hexlet.code.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    @Lob
    private String description;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private TaskStatus taskStatus;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "executor_id")
    private User executor;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tasks_labels",
            joinColumns = {@JoinColumn(name = "tasks_id")},
            inverseJoinColumns = {@JoinColumn(name = "labels_id")})
    private List<Label> labels = new ArrayList<>();

    @CreationTimestamp
    private Instant createdAt;
}

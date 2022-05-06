package at.htlklu.spring.model;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.*;

@Entity																
@Table(name = "DEPARTMENT")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Department extends RepresentationModel<Department> implements Serializable
{
	//region static Properties
	private static final long serialVersionUID = -6936296833789537850L;

	public static final Comparator<Department> BY_NAME = Comparator.comparing(Department::getName);
	//endregion


	//region Properties
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)				
	@Column(name = "DEPARTMENT_ID")									
	private Integer departmentId;

	@NotBlank	
	@Column(name = "NAME")											
	private String name;
	@NotBlank	
	@Column(name = "NAME_SHORT")									
	private String nameShort;

	@JsonProperty("head")
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "TEACHER_ID")
//	@JsonIgnore							// Achtung: auch die @JsonProperty auskommentieren !!
	private Teacher teacher;

	@JsonIgnore
	@OneToMany(mappedBy = "department",  							// JPA (mappedBy gibt das Feld in der Klasse "SchoolClass" an)
			   cascade = CascadeType.MERGE,
			   orphanRemoval = true, 
			   fetch = FetchType.LAZY)
	@OrderBy("level")												// JPA (Sortierung nach dem Level)
	private Set<SchoolClass> schoolClasses = new HashSet<SchoolClass>();
	//endregion


	//region Constructors
	public Department()
	{
	}
	
	
	public Department(String name, 
					  String nameShort, 
					  Teacher teacher)
	{
		super();
		this.name = name;
		this.nameShort = nameShort;
		this.teacher = teacher;
	}
	//endregion


	//region Methods
	@Override
	public String toString()
	{
		return String.format("%s", this.name);
	}


	// 1AHBG:  1 Schülerin    -> 1.50
	// 1BHBG: 10 Schülerinnen -> 1.60
	// ...
	// Liste aller SchülerInnen der Abteilung
	public double avgStudentsHeight1()
	{
		return this.schoolClasses.stream()
								 .flatMap(sc -> sc.getStudents().stream())
								 .mapToInt(student -> student.getHeight())
								 .average()
								 .orElse(-1);
	}






	public long countStudents()
	{
		return this.getSchoolClasses().stream()
									  .mapToLong(sc -> sc.countStudents())
									  .sum();
	}

	public long countStudentsMale()
	{
		return this.getSchoolClasses().stream()
									  .mapToLong(sc -> sc.countStudentsMale())
									  .sum();
	}

//	public double avgStudentsMale()
//	{
//		return this.getSchoolClasses().stream()
//									  .mapToLong(sc -> sc.countStudentsMale())
//									  .average()
//									  .orElse(-1);
//	}

	public long countStudentsFemale()
	{
		return this.getSchoolClasses().stream()
									  .mapToLong(sc -> sc.countStudentsFemale())
									  .sum();
	}


	public double avgStudentsHeight()
	{
//		return this.schoolClasses.stream()
//								 .mapToDouble(schoolClass -> schoolClass.avgStudentsHeight())
//								 .average()
//								 .orElse(-1);

		// besser
		return this.schoolClasses.stream()
							     .flatMap(sc -> sc.getStudents().stream())
								 .filter(student -> student.getHeight() != null)
								 .mapToInt(student -> student.getHeight())
							     .average()
							     .orElse(-1);
	}

	public double avgStudentsWeight()
	{
//		return this.schoolClasses.stream()
//								 .mapToDouble(schoolClass -> schoolClass.avgStudentsWeight())
//								 .average()
//								 .orElse(-1);

		// besser
		return this.schoolClasses.stream()
							     .flatMap(sc -> sc.getStudents().stream())
								 .filter(student -> student.getWeight() != null)
								 .mapToDouble(student -> student.getWeight())
							     .average()
							     .orElse(-1);
	}

	public double avgStudentsBMI()
	{
		return this.schoolClasses.stream()
							     .flatMap(sc -> sc.getStudents().stream())
								 .filter(student -> student.getBMI() > 0)
								 .mapToDouble(student -> student.getBMI())
							     .average()
							     .orElse(-1);
	}


	public long countGrades(int grade)
	{
		return this.schoolClasses.stream()
								 .mapToLong(schoolClass -> schoolClass.countGrades(grade))
								 .sum();
	}
	//endregion


	//region Getter and Setter
	public Integer getDepartmentId()
	{
		return departmentId;
	}
	public void setDepartmentId(Integer departmentId)
	{
		this.departmentId = departmentId;
	}

	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}

	public String getNameShort()
	{
		return nameShort;
	}
	public void setNameShort(String nameShort)
	{
		this.nameShort = nameShort;
	}

	
	
	public Teacher getTeacher()
	{
		return teacher;
	}
	public void setTeacher(Teacher teacher)
	{
		this.teacher = teacher;
	}
		

	public Set<SchoolClass> getSchoolClasses()
	{
		return schoolClasses;
	}
	//endregion


	//region HashCode and Equals
	@Override
	public int hashCode() {
	    return getClass().hashCode();
	}
	
	@Override 
	public boolean equals(Object obj)
	{
		boolean equal;
		Department department1 = this;
		if (department1 == obj)
		{
			equal = true;
		}
		else if ((obj == null) || (!(obj instanceof Department)))
		{
			equal = false;
		}
		else
		{
			Department department2 = (Department)obj;
			equal = department1.departmentId != null && Objects.equals(department1.departmentId, department2.getDepartmentId());
		}
		return equal;
	}
	//endregion
	
}

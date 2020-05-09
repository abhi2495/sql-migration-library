package com.example;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.example.jpa.Student;
import com.example.jpa.StudentRepository;
import com.example.tenancy.TenancyContext;
import com.example.tenancy.TenancyContextHolder;
import com.example.tenancy.Tenant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
//@ActiveProfiles(Application.Profiles.NO_DEPENDENCIES)
public class StudentRepositoryTest {

  private static final String TENANT_ALIAS_1 = "foo";
  private static final String TENANT_ID_1 = "x-foo";

  private static final String TENANT_ALIAS_2 = "bar";
  private static final String TENANT_ID_2 = "x-bar";

  @Autowired
  private StudentRepository studentRepository;

  @Test
  public void studentRepository_thenAutoMigrationAndStudentRepositoryWorks() {
    Student student1 = new Student();
    student1.setName("foo");
    student1.setEmail("foo.bar@example.com");
    TenancyContextHolder.setContext(TenancyContext.newContext(new Tenant(TENANT_ID_1, TENANT_ALIAS_1, true)));
    studentRepository.save(student1);

    TenancyContextHolder.setContext(TenancyContext.newContext(new Tenant(TENANT_ID_2, TENANT_ALIAS_2, true)));
    Student student2 = new Student();
    student2.setName("foo");
    student2.setEmail("foo.bar@example.com");
    Student student3 = new Student();
    student3.setName("foo");
    student3.setEmail("foo.bar@example.com");

    studentRepository.save(student2);
    studentRepository.save(student3);

    List<Student> getBarStudents = (List) studentRepository.findAll();
    assertThat(getBarStudents.size(), is(2));
    studentRepository.deleteAll();

    TenancyContextHolder.setContext(TenancyContext.newContext(new Tenant(TENANT_ID_1, TENANT_ALIAS_1, true)));

    List<Student> getFooStudents = (List) studentRepository.findAll();
    assertThat(getFooStudents.size(), is(1));
    studentRepository.deleteAll();

    TenancyContextHolder.clearContext();
  }

}

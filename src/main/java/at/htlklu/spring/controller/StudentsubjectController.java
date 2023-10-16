package at.htlklu.spring.controller;

import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.Department;
import at.htlklu.spring.model.StudentSubject;
import at.htlklu.spring.model.Subject;
import at.htlklu.spring.model.Teacher;
import at.htlklu.spring.repository.StudentSubjectRepository;
import at.htlklu.spring.repository.SubjectRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//localhost:8082/mvc/teachers
//localhost:8082/mvc/teachers/1/departments
//localhost:8082/mvc/teachers/1/schoolClasses


@Controller
@RequestMapping(value ="mvc/Studentsubjects")
public class StudentsubjectController
{
	//region Properties
	private static Logger logger = LogManager.getLogger(StudentsubjectController.class);
	private static final String CLASS_NAME = "StudentsubjectController";
	public static final String FORM_NAME_SINGLE = "TeacherSingle";
	public static final String FORM_NAME_LIST = "StudentsubjectList";

	@Autowired
	StudentSubjectRepository studentSubjectRepository;
	//endregion


	// localhost:8082/mvc/teachers
	@GetMapping("")
	public ModelAndView show()
	{
		logger.info(LogUtils.info(CLASS_NAME, "show"));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(StudentsubjectController.FORM_NAME_LIST);

		List<StudentSubject> studentSubjects = studentSubjectRepository.findAll();
		mv.addObject("Studentsubjects", studentSubjects); //link zur HTML

	    return mv;
	}


}


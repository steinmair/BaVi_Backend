package at.htlklu.spring.controller;

import at.htlklu.spring.api.LogUtils;
import at.htlklu.spring.model.Department;
import at.htlklu.spring.model.Teacher;
import at.htlklu.spring.model.SchoolClass;
import at.htlklu.spring.repository.SchoolClassesRepository;
import at.htlklu.spring.repository.TeacherRepository;
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
@RequestMapping(value ="mvc/schoolClasses")
public class SchoolClassController
{
	//region Properties
	private static Logger logger = LogManager.getLogger(SchoolClassController.class);
	private static final String CLASS_NAME = "SchoolClassController";
	public static final String FORM_NAME_SINGLE = "TeacherSingle";
	public static final String FORM_NAME_LIST = "SchoolClassList";

	@Autowired
	SchoolClassesRepository schoolClassesRepository;
	//endregion


	// localhost:8082/mvc/teachers
	@GetMapping("")
	public ModelAndView show()
	{
		logger.info(LogUtils.info(CLASS_NAME, "show"));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(SchoolClassController.FORM_NAME_LIST);

		List<SchoolClass> schoolClasses = schoolClassesRepository.findAll();
		mv.addObject("schoolClass", schoolClasses); //link zur HTML

	    return mv;
	}

}

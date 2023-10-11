package at.htlklu.spring.controller;

import at.htlklu.spring.model.*;
import at.htlklu.spring.repository.*;

import at.htlklu.spring.api.LogUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.Set;

//localhost:8082/mvc/teachers
//localhost:8082/mvc/teachers/1/departments
//localhost:8082/mvc/teachers/1/schoolClasses


@Controller
@RequestMapping(value ="mvc/teachers")
public class TeacherController
{
	//region Properties
	private static Logger logger = LogManager.getLogger(TeacherController.class);
	private static final String CLASS_NAME = "TeacherController";
	public static final String FORM_NAME_SINGLE = "TeacherSingle";
	public static final String FORM_NAME_LIST = "TeacherList";

	@Autowired
	TeacherRepository teacherRepository;
	//endregion


	// localhost:8082/mvc/teachers
	@GetMapping("")
	public ModelAndView show()
	{
		logger.info(LogUtils.info(CLASS_NAME, "show"));

		ModelAndView mv = new ModelAndView();

		mv.setViewName(TeacherController.FORM_NAME_LIST);

		List<Teacher> teachers = teacherRepository.findAll();
		mv.addObject("teachers", teachers); //link zur HTML

	    return mv;
	}
	//localhost:8082/mvc/teachers/1/departments
	@GetMapping("{teacherId}/departments")
	public ModelAndView showDepartments(@PathVariable int  teacherId){

		logger.info(LogUtils.info(TeacherController.class.getSimpleName(), "showDepartments",String.format("%d",teacherId)));

		ModelAndView mv = new ModelAndView();
		mv.setViewName(DepartmentController.FORM_NAME_LIST);

		Optional<Teacher> optTeacher = teacherRepository.findById(teacherId);

		if(optTeacher.isPresent())
		{ //Teacher wurde gefunden, weil id in Teachertabelle vorhanden
			Teacher teacher = optTeacher.get();

			Set<Department> departments = teacher.getDepartments();

			mv.addObject("departments",departments);
		}else {

		}

		//to do

		return mv;
	}

}

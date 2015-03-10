package edu.uco.schambers;


import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Steve
 */
public class Utility {
	private final static List<SelectItem> cities;
	static{
		cities = new ArrayList<SelectItem>();	
		cities.add(new SelectItem("Choose one"));
		cities.add(new SelectItem("Lawton"));
		cities.add(new SelectItem("OKC"));
		cities.add(new SelectItem("Moore"));
		cities.add(new SelectItem("Norman"));
		cities.add(new SelectItem("Edmond"));
		
	}
	private final static List<SelectItem> genders;
	static
	{
		genders = new ArrayList<SelectItem>();	
		genders.add(new SelectItem("Male"));
		genders.add(new SelectItem("Female"));
	}
	private final static List<SelectItem> languages;
	static
	{

		languages = new ArrayList<SelectItem>();	
		languages.add(new SelectItem("C++"));
		languages.add(new SelectItem("Java"));
		languages.add(new SelectItem("C#"));
		languages.add(new SelectItem("Swift"));
		languages.add(new SelectItem("Python"));
	}
	public static List<SelectItem> cityList()
	{
		return cities;
	}
	public static List<SelectItem> genderList()
	{
		return genders;
	}
	public static List<SelectItem> languageList()
	{
		return languages;
	}
	static{
		
	}
}

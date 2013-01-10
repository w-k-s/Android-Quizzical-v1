package uk.ac.hw.waqqas.quiztime;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**Event Handler for the SAXParsser
 * 
 * @author Waqqas
 *
 */
public class XMLContentHandler extends DefaultHandler
{
	//ArrayList in which questions are stored
	private ArrayList<MCQuestion> questions;
	
	//Question being parced
	private MCQuestion question;
	
	//String being parsed.
	private String elementValueHolder;
	
	public XMLContentHandler()
	{
		//initialise questionsList.
		questions = new ArrayList<MCQuestion>();
	}
	

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{
		elementValueHolder = "";
		if(localName.equals("question"))
		{
			//if start tag is 'question', initialise new question object.
			question = new MCQuestion();
			//correct answer index is stored in XML attribute.
			int correctIndex = Integer.parseInt(attributes.getValue("correct"));
			//set correct answer of question object.
			question.setCorrectAnswer(correctIndex);
		}
			
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException
	{
		//store parsed string in elementValueHolder.
		elementValueHolder = new String(ch,start,length);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
	{
		if(localName.equalsIgnoreCase("question"))
			//if end tag is question, add question object to list.
			questions.add(question);
		else if (localName.equalsIgnoreCase("ask"))
			//if end tag is ask, set string as question
			question.setQuestion(elementValueHolder);
		else if (localName.equalsIgnoreCase("answer"))
			//if end tag is answer, add string to list of answers.
			question.addAnswer(elementValueHolder);
	}
	
	public ArrayList<MCQuestion> getQuestions()
	{
		//return parsed questions list.
		return questions;
	}
	
	
}

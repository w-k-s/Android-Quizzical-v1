package uk.ac.hw.waqqas.quiztime;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Event Handler for the SAXParsser
 * 
 * @author Waqqas
 * 
 */
public class XMLContentHandler extends DefaultHandler
{
    // ArrayList in which questions are stored
    private ArrayList<Question> questions;

    // Question being parsed
    private Question question;
    private String correctChoice;

    private boolean bAsk = false;
    private boolean bChoiceA = false;
    private boolean bChoiceB = false;
    private boolean bChoiceC = false;
    private boolean bChoiceD = false;

    public XMLContentHandler()
    {
	// initialise questionsList.
	questions = new ArrayList<Question>();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
	    Attributes attributes) throws SAXException
    {
	if (localName.equals("question"))
	{
	    // if start tag is 'question', initialise new question object.
	    question = new Question();
	    // correct answer index is stored in XML attribute.
	    this.correctChoice = attributes.getValue("correct");
	    // set correct answer of question object.
	} else if (localName.equalsIgnoreCase("ask"))
	{
	    this.bAsk = true;
	} else if (localName.equalsIgnoreCase("A"))
	{
	    this.bChoiceA = true;
	} else if (localName.equalsIgnoreCase("B"))
	{
	    this.bChoiceB = true;
	} else if (localName.equalsIgnoreCase("C"))
	{
	    this.bChoiceC = true;
	} else if (localName.equalsIgnoreCase("D"))
	{
	    this.bChoiceD = true;
	}

    }

    @Override
    public void characters(char[] ch, int start, int length)
	    throws SAXException
    {
	// store parsed string in elementValueHolder.
	String textContent = new String(ch, start, length);

	if (this.bAsk)
	{
	    this.question.setQuestion(textContent);
	} else if (this.bChoiceA)
	{
	    this.question.addAnswer(new Answer(textContent,this.correctChoice.equalsIgnoreCase("A")));
	} else if (this.bChoiceB)
	{
	    this.question.addAnswer(new Answer(textContent,this.correctChoice.equalsIgnoreCase("B")));
	} else if (this.bChoiceC)
	{
	    this.question.addAnswer(new Answer(textContent,this.correctChoice.equalsIgnoreCase("C")));
	} else if (this.bChoiceD)
	{
	   this.question.addAnswer(new Answer(textContent,this.correctChoice.equalsIgnoreCase("D")));
	}
    }

    @Override
    public void endElement(String uri, String localName, String qName)
    {
	if (localName.equalsIgnoreCase("question"))
	{
	    questions.add(question);
	} else if (localName.equalsIgnoreCase("ask"))
	{
	    this.bAsk = false;
	} else if (localName.equalsIgnoreCase("A"))
	{
	    this.bChoiceA = false;
	} else if (localName.equalsIgnoreCase("B"))
	{
	    this.bChoiceB = false;
	} else if (localName.equalsIgnoreCase("C"))
	{
	    this.bChoiceC = false;
	} else if (localName.equalsIgnoreCase("D"))
	{
	    this.bChoiceD = false;
	}
    }

    public ArrayList<Question> getQuestions()
    {
	// return parsed questions list.
	return questions;
    }

}

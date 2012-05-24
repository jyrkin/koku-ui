package fi.arcusys.koku.web;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.arcusys.koku.kv.model.KokuAnswer;
import fi.arcusys.koku.kv.model.KokuQuestion;
import fi.arcusys.koku.kv.model.KokuRequest;
import fi.arcusys.koku.kv.model.KokuResponse;
import fi.arcusys.koku.kv.requestservice.Answer;
import fi.arcusys.koku.kv.requestservice.Question;
import fi.arcusys.koku.kv.requestservice.QuestionType;
import fi.arcusys.koku.users.KokuUser;
import fi.arcusys.koku.web.exporter.csv.CSVExporter;

public class ExportFileControllerTest {

	public static final String CSV_EXPECTED_STRING_UNIQUE_SORTING = "\uFEFF\"Vastauksen yhteenveto\"\n"
			+ "\"Vastaaja\";\"Say five things which are not Jackie Chan\";;;\"Pick a date\";;;;;;;;\"Kommentti\"\n"
			+ "\"Kalle Kuntalainen\";\"Carrot\";\"Car\";\"Jackie Chan\";;;\"23.5.2012\";\"24.5.2012\";\"25.5.2012\";\"26.5.2012\";\"27.5.2012\";\"28.5.2012\";\"Another comment foo bar baz lol olo lol\"\n"
			+ "\"Kirsi Kuntalainen\";\"Jackie Chan\";\"Jackie Chan\";;\"21.5.2012\";\"22.5.2012\";\"23.5.2012\";\"24.5.2012\";\"25.5.2012\";;;;\"This is a comment string foo bar baz\"\n"
			+ "\n"
			+ "\"Vastaamattomat\"\n";


	public static final String CSV_EXPECTED_STRING_NO_UNIQUE_SORTING = "\uFEFF\"Vastauksen yhteenveto\"\n"
			+ "\"Vastaaja\";\"Say five things which are not Jackie Chan\";;;\"Pick a date\";;;;;;\"Kommentti\"\n"
			+ "\"Kalle Kuntalainen\";\"Carrot\";\"Car\";\"Jackie Chan\";\"23.5.2012\";\"24.5.2012\";\"25.5.2012\";\"26.5.2012\";\"27.5.2012\";\"28.5.2012\";\"Another comment foo bar baz lol olo lol\"\n"
			+ "\"Kirsi Kuntalainen\";\"Jackie Chan\";\"Jackie Chan\";;\"21.5.2012\";\"22.5.2012\";\"23.5.2012\";\"24.5.2012\";\"25.5.2012\";;\"This is a comment string foo bar baz\"\n"
			+ "\n"
			+ "\"Vastaamattomat\"\n";


	public KokuRequest request = null;


	@Before
	public void setUp() {
		request = createKokuRequestTest1();
	}

	@Test
	public void exporterTest() {
		CSVExporter exporter = new CSVExporter(request, null);
		String csv = exporter.getContents("Vastauksen yhteenveto", "Vastaaja", "Kommentti", "Vastaamattomat");
		assertEquals("CSV string doesn't match", CSV_EXPECTED_STRING_UNIQUE_SORTING, csv);
	}

	private KokuRequest createKokuRequestTest1() {
		KokuRequest r = new KokuRequest();
		r.setRequestId(403);
		r.setSender("veeti.virkamies");
		r.setSubject("CSV Export Test 3; Multiple Recipients and Multiple Questions");
		r.setContent("");
		r.setRespondedAmount(2);
		r.setMissedAmount(0);
		r.setCreationDate("21.5.2012");
		r.setEndDate("31.5.2012");
		r.setRequestType(null);
		r.setUnrespondedList(null);

		List<KokuResponse> respondedList = new ArrayList<KokuResponse>();

		KokuUser kalle = createKokuUser("Kalle Kuntalainen", "kalle.kuntalainen@kunpo.tpe.fi", "Kalle", "Kuntalainen", "0403333244", null);
		List<KokuAnswer> kalle_answers = new ArrayList<KokuAnswer>();
		kalle_answers.add(createKokuAnswer("Carrot, Car, Jackie Chan", "", 10));
		kalle_answers.add(createKokuAnswer(
					"23.5.2012, 24.5.2012, 25.5.2012, 26.5.2012, 27.5.2012, 28.5.2012", "", 11));
		respondedList.add(createKokuResponse(
				kalle_answers, "kalle.kuntalainen", "Another comment foo bar baz lol olo lol", kalle));

		KokuUser kirsi = createKokuUser("Kirsi Kuntalainen", "kirsi.kuntalainen@kunpo.tpe.fi", "Kirsi", "Kuntalainen", "0403333222", null);
		List<KokuAnswer> kirsi_answers = new ArrayList<KokuAnswer>();
		kirsi_answers.add(createKokuAnswer("Jackie Chan, Jackie Chan", "", 10));
		kirsi_answers.add(createKokuAnswer(
					"21.5.2012, 22.5.2012, 23.5.2012, 24.5.2012, 25.5.2012", "", 11));
		respondedList.add(createKokuResponse(
				kirsi_answers, "kirsi.kuntalainen", "This is a comment string foo bar baz", kirsi));

		r.setRespondedList(respondedList);

		List<KokuQuestion> questions = new ArrayList<KokuQuestion>();
		questions.add(createKokuQuestion(
					"Say five things which are not Jackie Chan", 10, QuestionType.MULTIPLE_CHOICE, null));
		questions.add(createKokuQuestion(
					"Pick a date", 11, QuestionType.CALENDAR, null));

		r.setQuestions(questions);

		return r;
	}

	private KokuAnswer createKokuAnswer(String answer, String comment, int questionNumber) {
		Answer a = new Answer();
		a.setAnswer(answer);
		a.setComment(comment);
		a.setQuestionNumber(questionNumber);
		return new KokuAnswer(a);
	}

	private KokuUser createKokuUser(String displayname, String email, String firstname, String lastname, String phoneNumber, String uid) {
		KokuUser u = new KokuUser();
		u.setDisplayname(displayname);
		u.setEmail(email);
		u.setFirstname(firstname);
		u.setLastname(lastname);
		u.setPhoneNumber(phoneNumber);
		u.setUid(uid);
		return u;
	}

	private KokuResponse createKokuResponse(
			List<KokuAnswer> answers, String name, String comment, KokuUser replierUser) {
		KokuResponse kr = new KokuResponse();
		kr.setAnswers(answers);
		kr.setName(name);
		kr.setComment(comment);
		kr.setReplierUser(replierUser);
		return kr;
	}

	private KokuQuestion createKokuQuestion(
			String description, int number, QuestionType type, KokuAnswer answer) {
		Question q = new Question();
		q.setDescription(description);
		q.setNumber(number);
		q.setType(type);
		KokuQuestion kq = new KokuQuestion(q);
		kq.setAnswer(answer);
		return kq;
	}

}

/*
 * KokuRequest [
 *     requestId=403,
 *     sender=veeti.virkamies,
 *     subject=CSV Export Test 3; Multiple Recipients and Multiple Questions,
 *     content=,
 *     respondedAmount=2,
 *     missedAmount=0,
 *     creationDate=21.5.2012,
 *     endDate=31.5.2012,
 *     requestType=null,
 *     respondedList=[
 *         KokuResponse [
 *             answers=[
 *                 KokuAnswer [
 *                     answer=Carrot, Car, Jackie Chan,
 *                     comment=,
 *                     questionNumber=10
 *                 ],
 *                 KokuAnswer [
 *                     answer=23.5.2012, 24.5.2012, 25.5.2012, 26.5.2012, 27.5.2012, 28.5.2012,
 *                     comment=,
 *                     questionNumber=11
 *                 ]
 *             ],
 *             name=kalle.kuntalainen,
 *             comment=Another comment foo bar baz lol olo lol
 *         ],
 *         KokuResponse [
 *             answers=[
 *                 KokuAnswer [
 *                     answer=Jackie Chan, Jackie Chan,
 *                     comment=,
 *                     questionNumber=10
 *                 ],
 *                 KokuAnswer [
 *                     answer=21.5.2012, 22.5.2012, 23.5.2012, 24.5.2012, 25.5.2012,
 *                     comment=,
 *                     questionNumber=11
 *                 ]
 *             ],
 *             name=kirsi.kuntalainen,
 *             comment=This is a comment string foo bar baz
 *         ]
 *     ],
 *     unrespondedList=null,
 *     questions=[
 *         KokuQuestion [
 *             description=Say five things which are not Jackie Chan,
 *             number=10,
 *             type=MULTIPLE_CHOICE,
 *             answer=null
 *         ],
 *         KokuQuestion [
 *             description=Pick a date,
 *             number=11,
 *             type=CALENDAR,
 *             answer=null
 *         ]
 *
 * KokuRequest [
 *     requestId=403,
 *     sender=veeti.virkamies,
 *     subject=CSV Export Test 3; Multiple Recipients and Multiple Questions,
 *     content=,
 *     respondedAmount=2,
 *     missedAmount=0,
 *     creationDate=21.5.2012,
 *     endDate=31.5.2012,
 *     requestType=null,
 *     respondedList=[
 *         KokuResponse [
 *             answers=[
 *                 KokuAnswer [
 *                     answer=Carrot, Car, Jackie Chan,
 *                     comment=,
 *                     questionNumber=10
 *                 ],
 *                 KokuAnswer [
 *                     answer=23.5.2012, 24.5.2012, 25.5.2012, 26.5.2012, 27.5.2012, 28.5.2012,
 *                     comment=,
 *                     questionNumber=11
 *                 ]
 *             ],
 *             name=kalle.kuntalainen,
 *             comment=Another comment foo bar baz lol olo lol
 *         ],
 *         KokuResponse [
 *             answers=[
 *                 KokuAnswer [
 *                     answer=Jackie Chan, Jackie Chan,
 *                     comment=,
 *                     questionNumber=10
 *                 ],
 *                 KokuAnswer [
 *                     answer=21.5.2012, 22.5.2012, 23.5.2012, 24.5.2012, 25.5.2012,
 *                     comment=,
 *                     questionNumber=11
 *                 ]
 *             ],
 *             name=kirsi.kuntalainen,
 *             comment=This is a comment string foo bar baz
 *         ]
 *     ],
 *     unrespondedList=null,
 *     questions=[
 *         KokuQuestion [
 *             description=Say five things which are not Jackie Chan,
 *             number=10,
 *             type=MULTIPLE_CHOICE,
 *             answer=null
 *         ],
 *         KokuQuestion [
 *             description=Pick a date,
 *             number=11,
 *             type=CALENDAR,
 *             answer=null
 *         ]
 *     ]
 * ]
 */

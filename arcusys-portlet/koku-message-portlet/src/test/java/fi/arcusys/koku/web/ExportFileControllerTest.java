package fi.arcusys.koku.web;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.arcusys.koku.common.services.requests.models.*;

import fi.arcusys.koku.kv.requestservice.Question;
import fi.arcusys.koku.kv.requestservice.QuestionType;
import fi.arcusys.koku.common.services.users.KokuUser;
import fi.arcusys.koku.web.exporter.csv.CSVExporter;

public class ExportFileControllerTest {

	public static final String CSV_EXPECTED_STRING_TEST_1 = "\"Vastauksen yhteenveto\"\n"
			+ "\"Vastaaja\";\"Say five things which are not Jackie Chan\";;;\"Pick a date\";;;;;;;;\"Kommentti\"\n"
			+ "\"Kalle Kuntalainen\";\"Carrot\";\"Car\";\"Jackie Chan\";;;\"23.5.2012\";\"24.5.2012\";\"25.5.2012\";\"26.5.2012\";\"27.5.2012\";\"28.5.2012\";\"Another comment foo bar baz lol olo lol\"\n"
			+ "\"Kirsi Kuntalainen\";;;\"Jackie Chan\";\"21.5.2012\";\"22.5.2012\";\"23.5.2012\";\"24.5.2012\";\"25.5.2012\";;;;\"This is a comment string foo bar baz\"\n"
			+ "\n"
			+ "\"Vastaamattomat\"\n";

	public static final String CSV_EXPECTED_STRING_TEST_2 = "\"Vastauksen yhteenveto\"\n"
			+ "\"Vastaaja\";\"Kyllä - Ei kysymys\";\"Vapaavalintaiset vaihtoehdot\";;;;;\"Kalenteri\";;;;\"Numero\";\"Kysymys joka vaatii vastaajalta vapaamuotoisen tekstikenttävastauksen\";\"Kommentti\"\n"
			+ "\"Kalle Kuntalainen\";\"Ei\";;\"Vaihtoehto 2\";\"Vaihtoehto 3\";\"Vaihtoehto 4\";\"Vaihtoehto 5\";;\"26.5.2012\";\"27.5.2012\";\"28.5.2012\";\"20\";\"Tässä toinen vapaamuotoinen vastaus\";\"Ei kommentoitavaa\"\n"
			+ "\"Kirsi Kuntalainen\";\"Kyllä\";\"Vaihtoehto 1\";\"Vaihtoehto 2\";;\"Vaihtoehto 4\";;\"25.5.2012\";\"26.5.2012\";\"27.5.2012\";;\"10\";\"Tässä käyttäjän vapaamuotoinen vastaus\";\"Kommentti on myös vapaamuotoinen vastaus, mutta vastaaja voi antaa kommentin missä tahansa kyselyssä se on aina läsnä, eikä sille ole kyselijän määrittelemää kysymystä.\"\n"
			+ "\n"
			+ "\"Vastaamattomat\"\n";


	public KokuRequest request1 = null;
	public KokuRequest request2 = null;


	@Before
	public void setUp() {
		request1 = createKokuRequestTest1();
		request2 = createKokuRequestTest2();
	}

	@Test
	public void exporterTest1() {
		CSVExporter exp = new CSVExporter(request1, null);
		String csv = exp.getContents("Vastauksen yhteenveto", "Vastaaja", "Kommentti", "Vastaamattomat");
		assertEquals("CSV string doesn't match", CSV_EXPECTED_STRING_TEST_1, csv);
	}

	@Test
	public void exporterTest2() {
		CSVExporter exp = new CSVExporter(request2, null);
		String csv = exp.getContents("Vastauksen yhteenveto", "Vastaaja", "Kommentti", "Vastaamattomat");
		assertEquals("CSV string doesn't match", CSV_EXPECTED_STRING_TEST_2, csv);
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

		KokuUser kalle = createKokuUser("Kalle Kuntalainen", "kalle.kuntalainen@kunpo.tpe.fi",
				"Kalle", "Kuntalainen", "0403333244", null);
		List<KokuAnswer> kalle_answers = createKokuAnswers("Carrot, Car, Jackie Chan",
				"23.5.2012, 24.5.2012, 25.5.2012, 26.5.2012, 27.5.2012, 28.5.2012");
		respondedList.add(createKokuResponse(
				kalle_answers, "kalle.kuntalainen", "Another comment foo bar baz lol olo lol", kalle));

		KokuUser kirsi = createKokuUser("Kirsi Kuntalainen", "kirsi.kuntalainen@kunpo.tpe.fi",
				"Kirsi", "Kuntalainen", "0403333222", null);
		List<KokuAnswer> kirsi_answers = createKokuAnswers("Jackie Chan, Jackie Chan",
				"21.5.2012, 22.5.2012, 23.5.2012, 24.5.2012, 25.5.2012");
		respondedList.add(createKokuResponse(
				kirsi_answers, "kirsi.kuntalainen", "This is a comment string foo bar baz", kirsi));

		r.setRespondedList(respondedList);

		List<KokuQuestion> questions = new ArrayList<KokuQuestion>();
		questions.add(createKokuQuestion(
					"Say five things which are not Jackie Chan", 0, QuestionType.MULTIPLE_CHOICE, null,
					createKokuPossibleAnswers("Potato", "Carrot", "Car", "Jackie Chan", "Jackie Chan")));
		questions.add(createKokuQuestion(
					"Pick a date", 1, QuestionType.CALENDAR, null,
					createKokuPossibleAnswers("21.5.2012", "22.5.2012", "23.5.2012", "24.5.2012",
							"25.5.2012", "26.5.2012", "27.5.2012", "28.5.2012")));

		r.setQuestions(questions);

		return r;
	}

	private KokuRequest createKokuRequestTest2() {
		KokuRequest r = new KokuRequest();
		r.setRequestId(415);
		r.setSender("veeti.virkamies");
		r.setSubject("csv export test 8");
		r.setContent("");
		r.setRespondedAmount(2);
		r.setMissedAmount(0);
		r.setCreationDate("25.5.2012");
		r.setEndDate("31.5.2012");
		r.setRequestType(null);
		r.setUnrespondedList(null);

		List<KokuResponse> respondedList = new ArrayList<KokuResponse>();

		KokuUser kalle = createKokuUser("Kalle Kuntalainen",
				"kalle.kuntalainen@kunpo.tpe.fi", "Kalle", "Kuntalainen", "0403333244", null);
		List<KokuAnswer> kalle_answers = createKokuAnswers("Ei", "Vaihtoehto 2, Vaihtoehto 3, Vaihtoehto 4, Vaihtoehto 5",
				"26.5.2012, 27.5.2012, 28.5.2012", "20", "Tässä toinen vapaamuotoinen vastaus");
		respondedList.add(createKokuResponse(kalle_answers, "kalle.kuntalainen", "Ei kommentoitavaa", kalle));

		KokuUser kirsi = createKokuUser("Kirsi Kuntalainen", "kirsi.kuntalainen@kunpo.tpe.fi",
				"Kirsi", "Kuntalainen", "0403333222", null);
		List<KokuAnswer> kirsi_answers = createKokuAnswers("Kyllä", "Vaihtoehto 1, Vaihtoehto 2, Vaihtoehto 4",
				"25.5.2012, 26.5.2012, 27.5.2012", "10", "Tässä käyttäjän vapaamuotoinen vastaus");
		respondedList.add(createKokuResponse(
				kirsi_answers, "kirsi.kuntalainen", "Kommentti on myös vapaamuotoinen vastaus,"
				+ " mutta vastaaja voi antaa kommentin missä tahansa kyselyssä;"
				+ " se on aina läsnä, eikä sille ole kyselijän määrittelemää kysymystä.", kirsi));

		r.setRespondedList(respondedList);

		List<KokuQuestion> questions = new ArrayList<KokuQuestion>();
		questions.add(createKokuQuestion( "Kyllä - Ei kysymys", 0, QuestionType.YES_NO, null, createKokuPossibleAnswers()));
		questions.add(createKokuQuestion(
				"Vapaavalintaiset vaihtoehdot", 1, QuestionType.MULTIPLE_CHOICE, null,
				createKokuPossibleAnswers("Vaihtoehto 1", "Vaihtoehto 2", "Vaihtoehto 3", "Vaihtoehto 4", "Vaihtoehto 5")));
		questions.add(createKokuQuestion( "Kalenteri", 2, QuestionType.CALENDAR, null,
				createKokuPossibleAnswers("25.5.2012", "26.5.2012", "27.5.2012", "28.5.2012")));
		questions.add(createKokuQuestion( "Numero", 3, QuestionType.NUMBER, null, createKokuPossibleAnswers()));
		questions.add(createKokuQuestion( "Kysymys joka vaatii vastaajalta vapaamuotoisen tekstikenttävastauksen", 4,
				QuestionType.FREE_TEXT, null, createKokuPossibleAnswers()));

		r.setQuestions(questions);

		return r;
	}

	private List<KokuAnswer> createKokuAnswers(String... answers) {
		String comment = "";
		int answerNumber = 0;
		List<KokuAnswer> kokuAnswers = new ArrayList<KokuAnswer>();
		for (String answer : answers) {
			kokuAnswers.add(new KokuAnswer(answer, comment, answerNumber));
			answerNumber++;
		}
		return kokuAnswers;
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
			String description, int number, QuestionType type, KokuAnswer answer, List<KokuPossibleAnswer> possibleAnswers) {
		Question q = new Question();
		q.setDescription(description);
		q.setNumber(number);
		q.setType(type);
		KokuQuestion kq = new KokuQuestion(q);
		kq.setAnswer(answer);
		kq.setPossibleAnswers(possibleAnswers);
		return kq;
	}
	
	private List<KokuPossibleAnswer> createKokuPossibleAnswers(String... parameter_answers) {
		List<KokuPossibleAnswer> return_answers = new ArrayList<KokuPossibleAnswer>();
		int i = 0;
		for (String answer : parameter_answers) {
			return_answers.add(new KokuPossibleAnswer(answer, i));
			i++;
		}
		return return_answers;
	}
}
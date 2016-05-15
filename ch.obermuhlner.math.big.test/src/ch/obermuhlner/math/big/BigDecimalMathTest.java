package ch.obermuhlner.math.big;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.Test;

public class BigDecimalMathTest {

	private static final MathContext MC = MathContext.DECIMAL128;

	private static final MathContext MC_CHECK_DOUBLE = MathContext.DECIMAL32;

	@Test
	public void testInternals() {
		assertEquals(toCheck(2.0), toCheck(BigDecimal.valueOf(2)));
		assertEquals(toCheck(2.0), toCheck(BigDecimal.valueOf(2.0)));
		
		assertEquals(null, toCheck(Double.NaN));
		assertEquals(null, toCheck(Double.NEGATIVE_INFINITY));
		assertEquals(null, toCheck(Double.POSITIVE_INFINITY));
	}
	
	@Test
	public void testIsIntValue() {
		assertEquals(true, BigDecimalMath.isIntValue(BigDecimal.valueOf(Integer.MIN_VALUE)));
		assertEquals(true, BigDecimalMath.isIntValue(BigDecimal.valueOf(Integer.MAX_VALUE)));
		assertEquals(true, BigDecimalMath.isIntValue(BigDecimal.valueOf(0)));
		assertEquals(true, BigDecimalMath.isIntValue(BigDecimal.valueOf(-55)));
		assertEquals(true, BigDecimalMath.isIntValue(BigDecimal.valueOf(33)));
		assertEquals(true, BigDecimalMath.isIntValue(BigDecimal.valueOf(-55.0)));
		assertEquals(true, BigDecimalMath.isIntValue(BigDecimal.valueOf(33.0)));

		assertEquals(false, BigDecimalMath.isIntValue(BigDecimal.valueOf(Integer.MIN_VALUE - 1L)));
		assertEquals(false, BigDecimalMath.isIntValue(BigDecimal.valueOf(Integer.MAX_VALUE + 1L)));
		
		assertEquals(false, BigDecimalMath.isIntValue(BigDecimal.valueOf(3.333)));
		assertEquals(false, BigDecimalMath.isIntValue(BigDecimal.valueOf(-5.555)));
	}
	
	@Test
	public void testFactorial() {
		assertEquals(new BigDecimal("1"), BigDecimalMath.factorial(0));
		assertEquals(new BigDecimal("1"), BigDecimalMath.factorial(1));
		assertEquals(new BigDecimal("2"), BigDecimalMath.factorial(2));
		assertEquals(new BigDecimal("6"), BigDecimalMath.factorial(3));
		assertEquals(new BigDecimal("24"), BigDecimalMath.factorial(4));
		assertEquals(new BigDecimal("120"), BigDecimalMath.factorial(5));
		
		assertEquals(
				new BigDecimal("9425947759838359420851623124482936749562312794702543768327889353416977599316221476503087861591808346911623490003549599583369706302603264000000000000000000000000"),
				BigDecimalMath.factorial(101));
	}

	@Test(expected = ArithmeticException.class)
	public void testPowIntZeroPowerNegative() {
		BigDecimalMath.pow(BigDecimal.valueOf(0), -5, MC);
	}

	@Test
	public void testPowIntPositiveY() {
		// positive exponents
		for(int x : new int[] { -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5 }) {
			for(int y : new int[] { 0, 1, 2, 3, 4, 5 }) {
				assertEquals(
						x + "^" + y,
						BigDecimal.valueOf((int) Math.pow(x, y)),
						BigDecimalMath.pow(BigDecimal.valueOf(x), y, MC));
			}
		}
	}
	
	@Test
	public void testPowIntHighAccuracy() {
		// Result from wolframalpha.com: 1.000000000000001 ^ 1234567
		String val = "1.0000000012345670007620772217746112884011264566574371750661936042203432730421791357400340579375261062151425984605455718643834831212687809215508627027381366482513893346638309647254328483125554030430209837119592796226273439855097892690164822394282109582106572606688508863981956571098445811521589634730079294115917257238821829137340388818182807197358582081813107978164190701238742379894183398009280170118101371420721038965387736053980576803168658232943601622524279972909569009054951992769572674935063940581972099846878996147233580891866876374475623810230198932136306920161303356757346458080393981632574418878114647836311205301451612892591304592483387202671500569971713254903439669992702668656944996771767101889159835990797016804271347502053715595561455746434955874842970156476866700704289785119355240166844949092583102028847019848438487206052262820785557574627974128427022802453099783875466674774383283633178630613523399806185908766812896743349684394795523513553454443796268439405430281375480024234032172402840564635266057234920659063946839453576870882295214918516855889289061559150620879201634277096704728220897344041618549870872138380388841708643468696894694958739051584506837702527545643699395947205334800543370866515060967536750156194684592206567524739086165295878406662557303580256110172236670067327095217480071365601062314705686844139397480994156197621687313833641789783258629317024951883457084359977886729599488232112988200551717307628303748345907910029990065217835915703110440740246602046742181454674636608252499671425052811702208797030657332754492225689850123854291480472732132658657813229027494239083970478001231283002517914471878332200542180147054941938310139493813524503325181756491235593304058711999763930240249546122995086505989026270701355781888675020326791938289147344430814703304780863155994800418441632244536";
		BigDecimal referenceResult = new BigDecimal(val);
		for(int precision : new int[] { 1, 3, 10, 20, 30, 40, 50, 60, 70, 80, 90, 200, 1000, 1800}) {
			MathContext mathContext = new MathContext(precision);
			assertEquals(
					"precision=" + precision, 
					referenceResult.round(mathContext).toString(),
					BigDecimalMath.pow(new BigDecimal("1.000000000000001"), 1234567, mathContext).toString());
		}
	}
	
	@Test
	public void testPowIntNegativeY() {
		// positive exponents
		for(int x : new int[] { -5, -4, -3, -2, -1, 1, 2, 3, 4, 5 }) { // no x=0 !
			for(int y : new int[] { -5, -4, -3, -2, -1}) {
				assertEquals(
						x + "^" + y,
						BigDecimal.ONE.divide(BigDecimal.valueOf((int) Math.pow(x, -y)), MC),
						BigDecimalMath.pow(BigDecimal.valueOf(x), y, MC));
			}
		}
	}

	@Test
	public void testPowIntSpecialCases() {
		// 0^0 = 1
		assertEquals(BigDecimal.valueOf(1), BigDecimalMath.pow(BigDecimal.valueOf(0), 0, MC));
		// 0^x = 0 for x > 0
		assertEquals(BigDecimal.valueOf(0), BigDecimalMath.pow(BigDecimal.valueOf(0), +5, MC));

		// x^0 = 1 for all x
		assertEquals(BigDecimal.valueOf(1), BigDecimalMath.pow(BigDecimal.valueOf(-5), 0, MC));
		assertEquals(BigDecimal.valueOf(1), BigDecimalMath.pow(BigDecimal.valueOf(+5), 0, MC));
	}

	@Test(expected = ArithmeticException.class)
	public void testPowInt0NegativeY() {
		// 0^x for x < 0 is undefined
		System.out.println(BigDecimalMath.pow(BigDecimal.valueOf(0), -5, MC));
	}
	
	@Test
	public void testPowPositiveX() {
		for(double x : new double[] { 1, 1.5, 2, 2.5, 3, 4, 5 }) {
			for(double y : new double[] { -5, -4, -3, -2.5, -2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2, 2.5, 3, 4, 5 }) {
				assertEquals(
						x + "^" + y,
						toCheck(Math.pow(x, y)),
						toCheck(BigDecimalMath.pow(BigDecimal.valueOf(x), BigDecimal.valueOf(y), MC)));
			}
		}
		for(double x : new double[] { 0 }) {
			for(double y : new double[] { 0, 0.5, 1, 1.5, 2, 2.5, 3, 4, 5 }) {
				assertEquals(
						x + "^" + y,
						toCheck(Math.pow(x, y)),
						toCheck(BigDecimalMath.pow(BigDecimal.valueOf(x), BigDecimal.valueOf(y), MC)));
			}
		}
	}

	@Test
	public void testPowNegativeX() {
		for(double x : new double[] { -2, -1 }) {
			for(double y : new double[] { -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5 }) {
				assertEquals(
						x + "^" + y,
						toCheck(Math.pow(x, y)),
						toCheck(BigDecimalMath.pow(BigDecimal.valueOf(x), BigDecimal.valueOf(y), MC)));
			}
		}
	}

	@Test
	public void testPowSpecialCases() {
		// 0^0 = 1
		assertEquals(BigDecimal.valueOf(1), BigDecimalMath.pow(BigDecimal.valueOf(0), BigDecimal.valueOf(0), MC));
		// 0^x = 0 for x > 0
		assertEquals(BigDecimal.valueOf(0), BigDecimalMath.pow(BigDecimal.valueOf(0), BigDecimal.valueOf(+5), MC));

		// x^0 = 1 for all x
		assertEquals(BigDecimal.valueOf(1), BigDecimalMath.pow(BigDecimal.valueOf(-5), BigDecimal.valueOf(0), MC));
		assertEquals(BigDecimal.valueOf(1), BigDecimalMath.pow(BigDecimal.valueOf(+5), BigDecimal.valueOf(0), MC));
	}

	@Test(expected = ArithmeticException.class)
	public void testPow0NegativeY() {
		// 0^x for x < 0 is undefined
		System.out.println(BigDecimalMath.pow(BigDecimal.valueOf(0), BigDecimal.valueOf(-5), MC));
	}

	@Test
	public void testPowHighAccuracy1() {
		// Result from wolframalpha.com: 0.12345 ^ 0.54321
		String val = "0.3209880595151945185125730942395290036641685516401211365668021036227236806558712414817507777010529315619538091221044550517779379562785777203521073317310721887789752732383195992338046561142233197839101366627988301068817528932856364705673996626318789438689474137773276533959617159796843289130492749319006030362443626367021658149242426847020379714749221060925227256780407031977051743109767225075035162749746755475404882675969237304723283707838724317900591364308593663647305926456586738661094577874745954912201392504732008960366344473904725152289010662196139662871362863747003357119301290791005303042638323919552042428899542474653695157843324029537490471818904797202183382709740019779991866183409872343305557416160635632389025962773383948534706993646814493361946320537133866646649868386696744314086907873844459873522561100570574729858449637845765912377361924716997579241434414109143219005616107946583880474580592369219885446517321145488945984700859989002667482906803702408431898991426975130215742273501237614632961770832706470833822137675136844301417148974010849402947454745491575337007331634736828408418815679059906104486027992986268232803807301917429934411578887225359031451001114134791114208050651494053415141140416237540583107162910153240598400275170478935634433997238593229553374738812677055332589568742194164880936765282391919077003882108791507606561409745897362292129423109172883116578263204383034775181118065757584408324046421493189442843977781400819942671602106042861790274528866034496106158048150133736995335643971391805690440083096190217018526827375909068556103532422317360304116327640562774302558829111893179295765516557567645385660500282213611503701490309520842280796017787286271212920387358249026225529459857528177686345102946488625734747525296711978764741913791309106485960272693462458335037929582834061232406160";
		BigDecimal referenceResult = new BigDecimal(val);
		for(int precision : new int[] { 20, 30, 40, 50, 60, 70, 80, 90, 200 }) {
			MathContext mathContext = new MathContext(precision);
			assertEquals(
					"precision=" + precision, 
					referenceResult.round(mathContext).toString(),
					BigDecimalMath.pow(new BigDecimal("0.12345"), new BigDecimal("0.54321"), mathContext).toString());
		}
	}
	
	//@Test
	public void testPowHighAccuracy2() {
		// Result from wolframalpha.com: 1234.5 ^ 5.4321
		String val = "62128200273178468.6677398330313037781753494560130835832101960387223758707669665725754895879107246310011029364211118269934534848627597104718365299707675269883473866053798863560099145230081124493870576780612499275723252481988188990085485417903685910250385975275407201318962063571641788853056193956632922391172489400257505790978314596080576631215805090936935676836971091464254857748180262699112027530753684170510323511798980747639116410705861310591624568227525136728034348718513230067867653958961909807085463366698897670703033966902227226026963721428348393842605660315775615215897171041744502317760375398468093874441545987214768846585209830041286071364933140664316884545264314137705612948991849327809564207354415319908754752255701802039139765434084951567836148382259822205056903343078315714330953561297888049627241752521508353126178543435267324563502039726903979264593590549404498146175495384414213014048644769191478319546475736458067346291095970042183567796890291583916374248166579807593334209446774446615766870268699990517113368293867016985423417705611330741518898131591089047503977721006889839010831321890964560951517989774344229913647667605138595803854678957098670003929907267918591145790413480904188741307063239101475728087298405926679231349800701106750462465201862628618772432920720630962325975002703818993580555861946571650399329644600854846155487513507946368829475408071100475344884929346742632438630083062705384305478596166582416332328006339035640924818942503261178020860473649223332292597947133883640686283632593820956826840942563265332271497540069352040396588314197259366049553760360493773149812879272759032356567261509967695159889106382819692093987902453799750689562469611095996225341555322139606462193260609916132372239906927497183040765412767764999503366952191218000245749101208123555266177028678838265168229";
		BigDecimal referenceResult = new BigDecimal(val);
		for(int precision : new int[] { 20, 30, 40, 50, 60, 70, 80, 90, 200 }) { // TODO need to optimize pow()
			MathContext mathContext = new MathContext(precision);
			assertEquals(
					"precision=" + precision, 
					referenceResult.round(mathContext).toString(),
					BigDecimalMath.pow(new BigDecimal("1234.5"), new BigDecimal("5.4321"), mathContext).toString());
		}
	}
	
	@Test
	public void testSqrt() {
		for(double value : new double[] { 0, 0.1, 2, 10, 33.3333 }) {
			assertEquals(
					"sqrt(" + value + ")",
					toCheck(Math.sqrt(value)),
					toCheck(BigDecimalMath.sqrt(BigDecimal.valueOf(value), MC)));
		}
	}

	@Test
	public void testSqrtHighAccuracy() {
		// Result from wolframalpha.com: sqrt(2)
		String val = "1.4142135623730950488016887242096980785696718753769480731766797379907324784621070388503875343276415727350138462309122970249248360558507372126441214970999358314132226659275055927557999505011527820605714701095599716059702745345968620147285174186408891986095523292304843087143214508397626036279952514079896872533965463318088296406206152583523950547457502877599617298355752203375318570113543746034084988471603868999706990048150305440277903164542478230684929369186215805784631115966687130130156185689872372352885092648612494977154218334204285686060146824720771435854874155657069677653720226485447015858801620758474922657226002085584466521458398893944370926591800311388246468157082630100594858704003186480342194897278290641045072636881313739855256117322040245091227700226941127573627280495738108967504018369868368450725799364729060762996941380475654823728997180326802474420629269124859052181004459842150591120249441341728531478105803603371077309182869314710171111683916581726889419758716582152128229518488472089694633862891562882765952635140542267653239694617511291602408715510135150455381287560052631468017127402653969470240300517495318862925631385188163478001569369176881852378684052287837629389214300655869568685964595155501644724509836896036887323114389415576651040883914292338113206052433629485317049915771756228549741438999188021762430965206564211827316726257539594717255934637238632261482742622208671155839599926521176252698917540988159348640083457085181472231814204070426509056532333398436457865796796519267292399875366617215982578860263363617827495994219403777753681426217738799194551397231274066898329989895386728822856378697749662519966583525776198939322845344735694794962952168891485492538904755828834526096524096542889394538646625744927556381964410316979833061852019379384940057156333720548068540575867999670121372239";
		BigDecimal referenceResult = new BigDecimal(val);
		for(int precision : new int[] { 20, 30, 40, 50, 60, 70, 80, 90, 200, 1000, 1800 }) {
			MathContext mathContext = new MathContext(precision);
			assertEquals(
					"precision=" + precision, 
					referenceResult.round(mathContext).toString(),
					BigDecimalMath.sqrt(new BigDecimal("2"), mathContext).toString());
		}
	}

	@Test
	public void testRoot() {
		for(double value : new double[] { 0.1, 2, 10, 33.3333 }) {
			assertEquals(
					"root(2," + value + ")",
					toCheck(Math.sqrt(value)),
					toCheck(BigDecimalMath.root(BigDecimal.valueOf(2), BigDecimal.valueOf(value), MC)));
			assertEquals(
					"root(3," + value + ")",
					toCheck(Math.cbrt(value)),
					toCheck(BigDecimalMath.root(BigDecimal.valueOf(3), BigDecimal.valueOf(value), MC)));
		}
	}

	@Test
	public void testRootHighAccuracy1() {
		// Result from wolframalpha.com: root(1.23, 123)
		String val = "50.016102539344819307741514415079435545110277821887074630242881493528776023690905378058352283823814945584087486290764920313665152884137840533937075179853255596515758851877960056849468879933122908090021571162427934915567330612627267701300492535817858361072169790783434196345863626810981153268939825893279523570322533446766188724600595265286542918045850353371520018451295635609248478721067200812355632099802713302132804777044107393832707173313768807959788098545050700242134577863569636367439867566923334792774940569273585734964008310245010584348384920574103306733020525390136397928777667088202296433541706175886006626333525007680397351405390927420825851036548474519239425298649420795296781692303253055152441850691276044546565109657012938963181532017974206315159305959543881191233733179735321461579808278383770345759408145745617032705494900390986476773247981270283533959979287340513398944113566999839889290733896874439682249327621463735375868408190435590094166575473967368412983975580104741004390308453023021214626015068027388545767003666342291064051883531202983476423138817666738346033272948508395214246047027012105246939488877506475824651688812245962816086719050192476878886543996441778751825677213412487177484703116405390741627076678284295993334231429145515176165808842776515287299275536932744066126348489439143701880784521312311735178716650919024092723485314329094064704170548551468318250179561508293077056611877488417962195965319219352314664764649802231780262169742484818333055713291103286608643184332535729978330383356321740509817475633105247757622805298711765784874873240679024286215940395303989612556865748135450980540945799394622053158729350598632915060818702520420240989908678141379300904169936776618861221839938283876222332124814830207073816864076428273177778788053613345444299361357958409716099682468768353446625063";
		BigDecimal referenceResult = new BigDecimal(val);
		for(int precision : new int[] { 20, 30, 40, 50, 60, 70, 80, 90 }) {
			MathContext mathContext = new MathContext(precision);
			assertEquals(
					"precision=" + precision, 
					referenceResult.round(mathContext).toString(),
					BigDecimalMath.root(BigDecimal.valueOf(1.23), BigDecimal.valueOf(123), mathContext).toString());
		}
	}

	@Test
	public void testRootHighAccuracy2() {
		// Result from wolframalpha.com: root(7.5, 123)
		String val = "1.8995643695815870676539369434054361726808105217886880103090875996194822396396255621113000403452538887419132729641364085738725440707944858433996644867599831080192362123855812595483776922496542428049642916664676504355648001147425299497362249152998433619265150901899608932149147324281944326398659053901429881376755786331063699786297852504541315337453993167176639520666006383001509553952974478682921524643975384790223822148525159295285828652242201443762216662072731709846657895992750535254286493842754491094463672629441270037173501058364079340866564365554529160216015597086145980711187711119750807640654996392084846441696711420521658760165363535215241687408369549643269709297427044177507157609035697648282875422321141920576120188389383509318979064825824777240151847818551071255436323480281154877997743553609520167536258202911691329853232693386770937694807506144279660147324316659333074620896627829029651910783066736606497262785345465872401993026696735802446138584306213230373571409591420951964537136053258998945471633936332983896917810023265095766395377592848121611444196796785031727740335105553348270077424620974061727975050161324060753928284759055040064976732991126510635738927993365006832681484889202649313814280125684525505938973967575274196130269615461251746873419445856759329916403947432038902141704646304799083820073914767560878449162496519826664715572693747490088659968040153989493366037393989012508491856761986732685422561958101646754270192269505879594808800416777471196270722586367363680538183391904535845392721112874375802640395545739073303112631715831096156004422381940090623765493332249827278090443678800852264922795299927727708248191560574252923342860845325222035245426918719153132138325983001330317244830727602810422542012322698940744820925849667642343510406965273569391887099540050259962759858771196756422007171";
		BigDecimal referenceResult = new BigDecimal(val);
		for(int precision : new int[] { 20, 30, 40, 50, 60, 70, 80, 90 }) {
			MathContext mathContext = new MathContext(precision);
			assertEquals(
					"precision=" + precision, 
					referenceResult.round(mathContext).toString(),
					BigDecimalMath.root(BigDecimal.valueOf(7.5), BigDecimal.valueOf(123), mathContext).toString());
		}
	}

	@Test
	public void testLog() {
		for(double value : new double[] { 0.1, 2, 10, 33.3333 }) {
			assertEquals("log(" + value + ")",
					toCheck(Math.log(value)),
					toCheck(BigDecimalMath.log(BigDecimal.valueOf(value), MC)));
		}
	}

	@Test
	public void testLogHighAccuracy1() {
		// Result from wolframalpha.com: ln(0.1)
		String val = "-2.30258509299404568401799145468436420760110148862877297603332790096757260967735248023599720508959829834196778404228624863340952546508280675666628736909878168948290720832555468084379989482623319852839350530896537773262884616336622228769821988674654366747440424327436515504893431493939147961940440022210510171417480036880840126470806855677432162283552201148046637156591213734507478569476834636167921018064450706480002775026849167465505868569356734206705811364292245544057589257242082413146956890167589402567763113569192920333765871416602301057030896345720754403708474699401682692828084811842893148485249486448719278096762712757753970276686059524967166741834857044225071979650047149510504922147765676369386629769795221107182645497347726624257094293225827985025855097852653832076067263171643095059950878075237103331011978575473315414218084275438635917781170543098274823850456480190956102992918243182375253577097505395651876975103749708886921802051893395072385392051446341972652872869651108625714921988499787488737713456862091670584980782805975119385444500997813114691593466624107184669231010759843831919129223079250374729865092900988039194170265441681633572755570315159611356484654619089704281976336583698371632898217440736600916217785054177927636773114504178213766011101073104239783252189489881759792179866639431952393685591644711824675324563091252877833096360426298215304087456092776072664135478757661626292656829870495795491395491804920906943858079003276301794150311786686209240853794986126493347935487173745167580953708828106745244010589244497647968607512027572418187498939597164310551884819528833074669931781463493000032120032776565413047262188397059679445794346834321839530441484480370130575367426215367557981477045803141363779323629156012818533649846694226146520645994207291711937060244492";
		BigDecimal referenceResult = new BigDecimal(val);
		for(int precision : new int[] { 20, 30, 40, 50, 60, 70, 80, 90, 200 }) { // TODO need to optimize log()
			MathContext mathContext = new MathContext(precision);
			assertEquals(
					"precision=" + precision, 
					referenceResult.round(mathContext).toString(),
					BigDecimalMath.log(new BigDecimal("0.1"), mathContext).toString());
		}
	}

	@Test
	public void testLogHighAccuracy2() {
		// Result from wolframalpha.com: ln(1.1)
		String val = "0.0953101798043248600439521232807650922206053653086441991852398081630010142358842328390575029130364930727479418458517498888460436935129806386890150217023263755687346983551204157456607731117050481406611584967219092627683199972666804124629171163211396201386277872575289851216418802049468841988934550053918259553296705084248072320206243393647990631942365020716424972582488628309770740635849277971589257686851592941134955982468458204470563781108676951416362518738052421687452698243540081779470585025890580291528650263570516836272082869034439007178525831485094480503205465208833580782304569935437696233763597527612962802332419887793490159262767738202097437296124304231269978317763387834500850947983607954894765663306829441000443449252110585597386446423305000249520642003351749383035733163887183863658864095987980592896922224719866617664086469438599082172014984648661016553883267832731905893594398418365160836037053676940083743785539126726302367554039807719021730407981203469520199824994506211545156995496539456365581027383589659382402015390419603824664083368873307873019384357785045824504691072378535575392646883979065139246126662251603763318447377681731632334250380687464278805888614468777887659631017437620270326399552535490068490417697909725326896790239468286121676873104226385183016443903673794887669845552057786043820598162664741719835262749471347084606772426040314789592161567246837020619602671610506695926435445325463039957620861253293473952704732964930764736250291219054949541518603372096218858644670199237818738241646938837142992083372427353696766016209216197009652464144415416340684821107427035544058078681627922043963452271529803892396332155037590445683916173953295983049207965617834301297873495901595044766960173144576650851894013006899406665176310040752323677741807454239794575425116685728529323731335086049670268306";
		BigDecimal referenceResult = new BigDecimal(val);
		for(int precision : new int[] { 20, 30, 40, 50, 60, 70, 80, 90, 200 }) { // TODO need to optimize log()
			MathContext mathContext = new MathContext(precision);
			assertEquals(
					"precision=" + precision, 
					referenceResult.round(mathContext).toString(),
					BigDecimalMath.log(new BigDecimal("1.1"), mathContext).toString());
		}
	}

	@Test
	public void testLogHighAccuracy3() {
		// Result from wolframalpha.com: ln(12345.6)
		String val = "9.42105500327135525114521501453525399237436111276300326386323534432727151942992511520562558738175320737219392933678106934681377640298797158131139323906361961480381516008415766949640011144295651380957422777114172279167654006534622812747920122075143832000303491928864417567534602811492095685408856581074035803357797631847469251006466446952382984400769172787795491275890878474305023861509824367243299385769279771744041937866552134975148449991501344008449686333627176197439283560717007769286520651804657135365525410547797134491863813264296599988480767570621877413992243488449252058389112464675521921368744908030643106093708139694498213865760209374231089223703469389057990578641477811580679006647361045368883126313166757159295044784734054746026667561208850147352459931288221690064827656007945926558137817955314752299200021125335319543610643148781413031739368946686197126231424703883123190210238015791369611214420726133482521541649129324232190740641049135517129893844376556993789191631768552752257796461172834352906322971133196717292014063557464657868471260257837864581817895933554699436597231519928906186824100551929174973211768975723220457184410041128885431823059460270296159512608527194960997843854276107619358871611335110158160499192067423059751567986373407423489599586293284362977309927604782683386482396609096117347165767675657470578510018397575185923185572052807175571518796143517238193372303027925460053807069802388627060672427272087223286476333683468229892546440731981947511457788744089944064466689422654892614083398427300212135529866471079161390374604296893598724751037581346990096479637907462110313260901383748633868418336284029147686046156013978973990920093756659785588328734878986910751799701679853456356654554727303139653731884939067754728654663370026652097310980166441905496504187282659704649813546716585697691";
		BigDecimal referenceResult = new BigDecimal(val);
		for(int precision : new int[] { 20 }) { // TODO need to optimize log() with high values !!
			MathContext mathContext = new MathContext(precision);
			assertEquals(
					"precision=" + precision, 
					referenceResult.round(mathContext).toString(),
					BigDecimalMath.log(new BigDecimal("12345.6"), mathContext).toString());
		}
	}

	@Test
	public void testExp() {
		for(double value : new double[] { -5, -1, 0.1, 2, 10 }) {
			assertEquals("exp(" + value + ")",
					toCheck(Math.exp(value)),
					toCheck(BigDecimalMath.exp(BigDecimal.valueOf(value), MC)));
		}
	}

	@Test
	public void testExpHighAccuracy1() {
		// Result from wolframalpha.com: exp(0.1)
		String val = "1.1051709180756476248117078264902466682245471947375187187928632894409679667476543029891433189707486536329171204854012445361537347145315787020068902997574505197515004866018321613310249357028047934586850494525645057122112661163770326284627042965573236001851138977093600284769443372730658853053002811154007820888910705403712481387499832879763074670691187054786420033729321209162792986139109713136202181843612999064371057442214441509033603625128922139492683515203569550353743656144372757405378395318324008280741587539066613515113982139135726893022699091000215648706791206777090283207508625041582515035160384730085864811589785637025471895631826720701700554046867490844416060621933317666818019314469778173494549497985045303406629427511807573756398858555866448811811806333247210364950515781422279735945226411105718464916466588898895425154437563356326922423993425668055030150187978568089290481077628854935380963680803086975643392286380110893491216896970405186147072881173903395370306903756052863966751655566156177044091023716763999613715961429909147602055822171056918247483370329310652377494326018131931115202583455695740577117305727325929270892586003078380276849851024733440526333630939768046873818746897979176031710638428538365444373036344477660068827517905394205724765809719068497652979331103372768988364106139063845834332444587680278142035133567220351279735997089196132184270510670193246409032174006524564495804123904224547124821906736781803247534842994079537510834190198353331683651574603364551464993636940684957076677363104098202444018343049556576017452467191522001230198866508508728780804296630956390659819928014152407848066718063601429519635764058390569704470217925967541099757148635387989599481795155282833193600584112822014656645896726556449347326910544815360769564296952628696236865028848565540573895707695598984577773238";
		BigDecimal referenceResult = new BigDecimal(val);
		for(int precision : new int[] { 20, 30, 40, 50, 60, 70, 80, 90, 200, 1000, 1800 }) {
			MathContext mathContext = new MathContext(precision);
			assertEquals(
					"precision=" + precision, 
					referenceResult.round(mathContext).toString(),
					BigDecimalMath.exp(new BigDecimal("0.1"), mathContext).toString());
		}
	}

	@Test
	public void testExpHighAccuracy2() {
		// Result from wolframalpha.com: exp(123.4)
		String val = "390786063200889154948155379410925404241701821048363382.932350954191939407875540538095053725850542917235991826991631549658381619846119064767940229694652504799690942074237719293556052198585602941442651814977379463173507703540164446248233994372649675083170661574855926134163163649067886904058135980414181563116455815478263535970747684634869846370078756117785925810367190913580101129012440848783613501818345221727921636036313301394206941005430607708535856550269918711349535144151578877168501672228271098301349906118292542981905746359989070403424693487891904874342086983801039403550584241691952868285098443443717286891245248589074794703961309335661643261592184482383775612097087066220605742406426487994296854782150419816494210555905079335674950579368212272414401633950719948812364415716009625682245889528799300726848267101515893741151833582331196187096157250772884710690932741239776061706938673734755604112474396879294621933875319320351790004373826158307559047749814106033538586272336832756712454484917457975827690460377128324949811226379546825509424852035625713328557508831082726245169380827972015037825516930075858966627188583168270036404466677106038985975116257215788600328710224325128935656214272530307376436037654248341541925033083450953659434992320670198187236379508778799056681080864264023524043718014105080505710276107680142887912693096434707224622405921182458722451547247803222237498053677146957211048297712875730899381841541047254172958887430559055751735318481711132216206915942752379320012433097749980476094039036829992786175018479140791048841069099146681433638254527364565199203683980587269493176948487694117499339581660653106481583097500412636413209554147009042448657752082659511080673300924304690964484273924800648584285968546527296722686071417123776801220060226116144242129928933422759721847194902947144831258";
		BigDecimal referenceResult = new BigDecimal(val);
		for(int precision : new int[] { 60, 80, 90, 1000, 1800 }) {
			MathContext mathContext = new MathContext(precision);
			assertEquals(
					"precision=" + precision, 
					referenceResult.round(mathContext).toString(),
					BigDecimalMath.exp(new BigDecimal("123.4"), mathContext).toString());
		}
	}

	@Test
	public void testSin() {
		for(double value : new double[] { -5, -1, -0.3, 0, 0.1, 2, 10 }) {
			assertEquals("sin(" + value + ")",
					toCheck(Math.sin(value)),
					toCheck(BigDecimalMath.sin(BigDecimal.valueOf(value), MC)));
		}
	}

	@Test
	public void testCos() {
		for(double value : new double[] { -5, -1, -0.3, 0, 0.1, 2, 10 }) {
			assertEquals("cos(" + value + ")",
					toCheck(Math.cos(value)),
					toCheck(BigDecimalMath.cos(BigDecimal.valueOf(value), MC)));
		}
	}

	private static BigDecimal toCheck(double value) {
		long longValue = (long) value;
		if (value == (double)longValue) {
			return toCheck(BigDecimal.valueOf(longValue));
		}
		
		if (Double.isFinite(value)) {
			return toCheck(BigDecimal.valueOf(value));
		}
		
		return null;
	}

	private static BigDecimal toCheck(BigDecimal value) {
		return value.setScale(MC_CHECK_DOUBLE.getPrecision(), MC_CHECK_DOUBLE.getRoundingMode());
	}
}
